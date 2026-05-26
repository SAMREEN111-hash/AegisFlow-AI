package com.aegisflow.api.reconciliation.mapper;

import com.aegisflow.api.reconciliation.domain.*;
import com.aegisflow.api.reconciliation.dto.response.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import org.springframework.stereotype.Component;

@Component
public class ReconciliationMapper {
    public ReconciliationJobResponse toJobResponse(ReconciliationJob job) {
        return new ReconciliationJobResponse(job.getId(), job.getRule().getId(), job.getName(), job.getStatus(),
                job.getCandidateCount(), job.getMatchedCount(), job.getExceptionCount(), job.getStartedAt(), job.getCompletedAt(), job.getFailureReason());
    }

    public ReconciliationRuleResponse toRuleResponse(ReconciliationRule rule) {
        return new ReconciliationRuleResponse(rule.getId(), rule.getName(), rule.getPrimaryRecordType(), rule.getCandidateRecordType(),
                rule.getAmountTolerance(), rule.getTimestampToleranceHours(), rule.getReferenceSimilarityThreshold(),
                rule.getAutoMatchConfidenceThreshold(), rule.isRequireCurrencyMatch(), rule.isActive());
    }

    public ReconciliationMatchResponse toMatchResponse(ReconciliationMatch match) {
        return new ReconciliationMatchResponse(match.getId(), match.getPrimaryTransaction().getId(), match.getCandidateTransaction().getId(),
                match.getMatchType(), match.getStatus(), match.getConfidenceScore(), match.getAmountVariance(), match.getMatchedByStrategy(), match.getExplanation());
    }

    public ReconciliationExceptionResponse toExceptionResponse(ReconciliationException exception) {
        return new ReconciliationExceptionResponse(exception.getId(), exception.getJob().getId(), exception.getTransaction().getId(),
                exception.getExceptionType(), exception.getStatus(), exception.getReason());
    }

    public ReconciliationStatisticsResponse toStatistics(ReconciliationJob job) {
        BigDecimal rate = job.getCandidateCount() == 0 ? BigDecimal.ZERO :
                BigDecimal.valueOf(job.getMatchedCount()).divide(BigDecimal.valueOf(job.getCandidateCount()), 4, RoundingMode.HALF_UP);
        return new ReconciliationStatisticsResponse(job.getId(), job.getCandidateCount(), job.getMatchedCount(), job.getExceptionCount(), rate);
    }
}
