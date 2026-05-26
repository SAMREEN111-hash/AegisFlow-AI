package com.aegisflow.api.reconciliation.service;

import com.aegisflow.api.common.exception.ResourceNotFoundException;
import com.aegisflow.api.reconciliation.domain.ReconciliationRule;
import com.aegisflow.api.reconciliation.dto.request.CreateReconciliationRuleRequest;
import com.aegisflow.api.reconciliation.repository.ReconciliationRuleRepository;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReconciliationRuleService {
    private final ReconciliationRuleRepository ruleRepository;

    @Transactional
    public ReconciliationRule create(UUID organizationId, CreateReconciliationRuleRequest request) {
        ReconciliationRule rule = new ReconciliationRule();
        rule.setOrganizationId(organizationId);
        rule.setName(request.name().trim());
        rule.setPrimaryRecordType(request.primaryRecordType());
        rule.setCandidateRecordType(request.candidateRecordType());
        rule.setAmountTolerance(request.amountTolerance());
        rule.setTimestampToleranceHours(request.timestampToleranceHours());
        rule.setReferenceSimilarityThreshold(request.referenceSimilarityThreshold());
        rule.setAutoMatchConfidenceThreshold(request.autoMatchConfidenceThreshold());
        rule.setRequireCurrencyMatch(request.requireCurrencyMatch());
        return ruleRepository.save(rule);
    }

    @Transactional(readOnly = true)
    public ReconciliationRule get(UUID organizationId, UUID ruleId) {
        return ruleRepository.findByIdAndOrganizationId(ruleId, organizationId)
                .orElseThrow(() -> new ResourceNotFoundException("ReconciliationRule", ruleId));
    }

    @Transactional(readOnly = true)
    public List<ReconciliationRule> list(UUID organizationId) {
        return ruleRepository.findByOrganizationIdAndActiveTrue(organizationId);
    }
}
