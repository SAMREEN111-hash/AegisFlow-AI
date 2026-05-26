package com.aegisflow.api.transactions.service;

import com.aegisflow.api.transactions.domain.IngestionJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class IngestionMetricsService {

    public void recordCompleted(IngestionJob job) {
        log.info("transaction.ingestion.completed jobId={} organizationId={} status={} total={} processed={} failed={} duplicates={}",
                job.getId(),
                job.getOrganizationId(),
                job.getStatus(),
                job.getTotalRecords(),
                job.getProcessedRecords(),
                job.getFailedRecords(),
                job.getDuplicateRecords());
    }
}
