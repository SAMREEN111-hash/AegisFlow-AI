package com.aegisflow.api.reconciliation.service;

import com.aegisflow.api.common.api.PageResponse;
import com.aegisflow.api.common.exception.ResourceNotFoundException;
import com.aegisflow.api.common.pagination.PageRequestDto;
import com.aegisflow.api.common.pagination.PageRequestFactory;
import com.aegisflow.api.common.pagination.SortDirection;
import com.aegisflow.api.reconciliation.domain.*;
import com.aegisflow.api.reconciliation.dto.request.RunReconciliationRequest;
import com.aegisflow.api.reconciliation.dto.response.*;
import com.aegisflow.api.reconciliation.mapper.ReconciliationMapper;
import com.aegisflow.api.reconciliation.matching.MatchCandidate;
import com.aegisflow.api.reconciliation.matching.ReconciliationMatchingEngine;
import com.aegisflow.api.reconciliation.repository.*;
import com.aegisflow.api.transactions.domain.FinancialTransaction;
import com.aegisflow.api.transactions.domain.ReconciliationStatus;
import com.aegisflow.api.transactions.repository.FinancialTransactionRepository;
import java.time.Instant;
import java.util.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReconciliationExecutionService {
    private final ReconciliationRuleService ruleService;
    private final ReconciliationMatchingEngine matchingEngine;
    private final FinancialTransactionRepository transactionRepository;
    private final ReconciliationJobRepository jobRepository;
    private final ReconciliationExecutionRepository executionRepository;
    private final ReconciliationMatchRepository matchRepository;
    private final ReconciliationExceptionRepository exceptionRepository;
    private final ReconciliationDecisionAuditRepository auditRepository;
    private final ReconciliationMapper mapper;
    private final ReconciliationMetricsService metricsService;

    @Transactional
    public ReconciliationJobResponse run(UUID organizationId, RunReconciliationRequest request) {
        ReconciliationRule rule = ruleService.get(organizationId, request.ruleId());
        ReconciliationJob job = createJob(organizationId, rule, request.jobName());
        ReconciliationExecution execution = createExecution(organizationId, job);
        try {
            List<FinancialTransaction> primaries = loadUnreconciled(organizationId, rule.getPrimaryRecordType());
            List<FinancialTransaction> candidates = loadUnreconciled(organizationId, rule.getCandidateRecordType());
            Set<UUID> consumedCandidates = new HashSet<>();
            int matched = 0;
            int exceptions = 0;

            for (FinancialTransaction primary : primaries) {
                List<FinancialTransaction> available = candidates.stream()
                        .filter(candidate -> !consumedCandidates.contains(candidate.getId()))
                        .toList();
                Optional<MatchCandidate> best = matchingEngine.findBestMatch(primary, available, rule);
                if (best.isPresent() && best.get().confidenceScore().compareTo(rule.getAutoMatchConfidenceThreshold()) >= 0) {
                    saveMatch(organizationId, job, best.get());
                    primary.setReconciliationStatus(ReconciliationStatus.MATCHED);
                    best.get().candidate().setReconciliationStatus(ReconciliationStatus.MATCHED);
                    transactionRepository.save(primary);
                    transactionRepository.save(best.get().candidate());
                    consumedCandidates.add(best.get().candidate().getId());
                    matched++;
                } else {
                    saveException(organizationId, job, primary, best);
                    primary.setReconciliationStatus(ReconciliationStatus.EXCEPTION);
                    transactionRepository.save(primary);
                    exceptions++;
                }
            }

            job.setCandidateCount(primaries.size());
            job.setMatchedCount(matched);
            job.setExceptionCount(exceptions);
            job.setStatus(exceptions > 0 ? ReconciliationJobStatus.COMPLETED_WITH_EXCEPTIONS : ReconciliationJobStatus.COMPLETED);
            job.setCompletedAt(Instant.now());
            execution.setStatus(ReconciliationExecutionStatus.COMPLETED);
            execution.setCompletedAt(Instant.now());
            execution.setExecutionSummary("{\"matched\":" + matched + ",\"exceptions\":" + exceptions + "}");
            executionRepository.save(execution);
            job = jobRepository.save(job);
            metricsService.recordCompleted(job);
            return mapper.toJobResponse(job);
        } catch (RuntimeException exception) {
            job.setStatus(ReconciliationJobStatus.FAILED);
            job.setFailureReason(exception.getMessage());
            job.setCompletedAt(Instant.now());
            execution.setStatus(ReconciliationExecutionStatus.FAILED);
            execution.setCompletedAt(Instant.now());
            executionRepository.save(execution);
            jobRepository.save(job);
            throw exception;
        }
    }

    @Transactional(readOnly = true)
    public ReconciliationJob getJob(UUID organizationId, UUID jobId) {
        return jobRepository.findByIdAndOrganizationId(jobId, organizationId)
                .orElseThrow(() -> new ResourceNotFoundException("ReconciliationJob", jobId));
    }

    @Transactional(readOnly = true)
    public PageResponse<ReconciliationJobResponse> searchJobs(UUID organizationId, int page, int size) {
        PageRequestDto request = new PageRequestDto(page, size, "createdAt", SortDirection.DESC);
        return PageResponse.from(jobRepository.findByOrganizationId(organizationId, PageRequestFactory.from(request, "createdAt")).map(mapper::toJobResponse));
    }

    @Transactional(readOnly = true)
    public PageResponse<ReconciliationMatchResponse> matches(UUID organizationId, UUID jobId, int page, int size) {
        getJob(organizationId, jobId);
        return PageResponse.from(matchRepository.findByOrganizationIdAndJobId(organizationId, jobId,
                PageRequestFactory.from(new PageRequestDto(page, size, "createdAt", SortDirection.DESC), "createdAt")).map(mapper::toMatchResponse));
    }

    @Transactional(readOnly = true)
    public PageResponse<ReconciliationExceptionResponse> exceptions(UUID organizationId, UUID jobId, int page, int size) {
        getJob(organizationId, jobId);
        return PageResponse.from(exceptionRepository.findByOrganizationIdAndJobId(organizationId, jobId,
                PageRequestFactory.from(new PageRequestDto(page, size, "createdAt", SortDirection.DESC), "createdAt")).map(mapper::toExceptionResponse));
    }

    @Transactional(readOnly = true)
    public ReconciliationStatisticsResponse statistics(UUID organizationId, UUID jobId) {
        return mapper.toStatistics(getJob(organizationId, jobId));
    }

    private ReconciliationJob createJob(UUID organizationId, ReconciliationRule rule, String name) {
        ReconciliationJob job = new ReconciliationJob();
        job.setOrganizationId(organizationId);
        job.setRule(rule);
        job.setName(name.trim());
        job.setStatus(ReconciliationJobStatus.RUNNING);
        job.setStartedAt(Instant.now());
        return jobRepository.save(job);
    }

    private ReconciliationExecution createExecution(UUID organizationId, ReconciliationJob job) {
        ReconciliationExecution execution = new ReconciliationExecution();
        execution.setOrganizationId(organizationId);
        execution.setJob(job);
        return executionRepository.save(execution);
    }

    private List<FinancialTransaction> loadUnreconciled(UUID organizationId, com.aegisflow.api.transactions.domain.FinancialRecordType recordType) {
        Specification<FinancialTransaction> spec = FinancialTransactionRepository.organizationEquals(organizationId)
                .and(FinancialTransactionRepository.recordTypeEquals(recordType))
                .and(FinancialTransactionRepository.statusEquals(ReconciliationStatus.UNRECONCILED));
        return transactionRepository.findAll(spec);
    }

    private void saveMatch(UUID organizationId, ReconciliationJob job, MatchCandidate candidate) {
        ReconciliationMatch match = new ReconciliationMatch();
        match.setOrganizationId(organizationId);
        match.setJob(job);
        match.setPrimaryTransaction(candidate.primary());
        match.setCandidateTransaction(candidate.candidate());
        match.setStatus(ReconciliationMatchStatus.AUTO_MATCHED);
        match.setConfidenceScore(candidate.confidenceScore());
        match.setAmountVariance(candidate.amountVariance());
        match.setMatchedByStrategy(candidate.strategy());
        match.setExplanation(candidate.explanation());
        matchRepository.save(match);
        saveAudit(organizationId, job, "AUTO_MATCH", candidate.explanation());
    }

    private void saveException(UUID organizationId, ReconciliationJob job, FinancialTransaction transaction, Optional<MatchCandidate> best) {
        ReconciliationException exception = new ReconciliationException();
        exception.setOrganizationId(organizationId);
        exception.setJob(job);
        exception.setTransaction(transaction);
        exception.setExceptionType(best.isPresent() ? ReconciliationExceptionType.LOW_CONFIDENCE : ReconciliationExceptionType.NO_CANDIDATE_FOUND);
        exception.setReason(best.map(candidate -> "Candidate found below confidence threshold: " + candidate.confidenceScore()).orElse("No eligible candidate found"));
        exceptionRepository.save(exception);
        saveAudit(organizationId, job, "EXCEPTION_CREATED", "{\"transactionId\":\"" + transaction.getId() + "\"}");
    }

    private void saveAudit(UUID organizationId, ReconciliationJob job, String type, String payload) {
        ReconciliationDecisionAudit audit = new ReconciliationDecisionAudit();
        audit.setOrganizationId(organizationId);
        audit.setJob(job);
        audit.setDecisionType(type);
        audit.setDecisionPayload(payload);
        auditRepository.save(audit);
    }
}
