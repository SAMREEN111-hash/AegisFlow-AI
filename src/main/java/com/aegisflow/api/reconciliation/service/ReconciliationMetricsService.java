package com.aegisflow.api.reconciliation.service;

import com.aegisflow.api.reconciliation.domain.ReconciliationJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ReconciliationMetricsService {
    public void recordCompleted(ReconciliationJob job) {
        log.info("reconciliation.job.completed jobId={} organizationId={} status={} candidates={} matches={} exceptions={}",
                job.getId(), job.getOrganizationId(), job.getStatus(), job.getCandidateCount(), job.getMatchedCount(), job.getExceptionCount());
    }
}
