package com.aegisflow.api.transactions.mapper;

import com.aegisflow.api.transactions.domain.IngestionError;
import com.aegisflow.api.transactions.domain.IngestionJob;
import com.aegisflow.api.transactions.dto.response.IngestionErrorResponse;
import com.aegisflow.api.transactions.dto.response.IngestionJobResponse;
import org.springframework.stereotype.Component;

@Component
public class IngestionMapper {

    public IngestionJobResponse toResponse(IngestionJob job) {
        return new IngestionJobResponse(
                job.getId(),
                job.getOrganizationId(),
                job.getSource().getId(),
                job.getSource().getRecordType(),
                job.getSource().getSourceName(),
                job.getStatus(),
                job.getOriginalFilename(),
                job.getTotalRecords(),
                job.getProcessedRecords(),
                job.getFailedRecords(),
                job.getDuplicateRecords(),
                job.getStartedAt(),
                job.getCompletedAt(),
                job.getFailureReason());
    }

    public IngestionErrorResponse toResponse(IngestionError error) {
        return new IngestionErrorResponse(
                error.getId(),
                error.getJob().getId(),
                error.getRowNumber(),
                error.getErrorCode(),
                error.getErrorMessage(),
                error.getRawPayload());
    }
}
