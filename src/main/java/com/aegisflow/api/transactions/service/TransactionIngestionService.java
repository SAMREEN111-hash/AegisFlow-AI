package com.aegisflow.api.transactions.service;

import com.aegisflow.api.common.exception.ResourceNotFoundException;
import com.aegisflow.api.common.api.PageResponse;
import com.aegisflow.api.common.pagination.PageRequestDto;
import com.aegisflow.api.common.pagination.PageRequestFactory;
import com.aegisflow.api.common.pagination.SortDirection;
import com.aegisflow.api.common.exception.ValidationException;
import com.aegisflow.api.transactions.domain.FinancialTransaction;
import com.aegisflow.api.transactions.domain.IngestionBatch;
import com.aegisflow.api.transactions.domain.IngestionBatchStatus;
import com.aegisflow.api.transactions.domain.IngestionError;
import com.aegisflow.api.transactions.domain.IngestionJob;
import com.aegisflow.api.transactions.domain.IngestionJobStatus;
import com.aegisflow.api.transactions.domain.TransactionMetadata;
import com.aegisflow.api.transactions.domain.TransactionSource;
import com.aegisflow.api.transactions.dto.request.CsvIngestionRequest;
import com.aegisflow.api.transactions.dto.response.CsvUploadResponse;
import com.aegisflow.api.transactions.dto.response.IngestionErrorResponse;
import com.aegisflow.api.transactions.mapper.IngestionMapper;
import com.aegisflow.api.transactions.repository.FinancialTransactionRepository;
import com.aegisflow.api.transactions.repository.IngestionBatchRepository;
import com.aegisflow.api.transactions.repository.IngestionErrorRepository;
import com.aegisflow.api.transactions.repository.IngestionJobRepository;
import com.aegisflow.api.transactions.validator.TransactionRecordValidator;
import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionIngestionService {

    private final CsvFileValidator fileValidator;
    private final CsvParsingService csvParsingService;
    private final TransactionSourceService sourceService;
    private final TransactionNormalizationService normalizationService;
    private final TransactionRecordValidator recordValidator;
    private final FinancialTransactionRepository transactionRepository;
    private final IngestionJobRepository jobRepository;
    private final IngestionBatchRepository batchRepository;
    private final IngestionErrorRepository errorRepository;
    private final IngestionMetricsService metricsService;
    private final IngestionMapper ingestionMapper;

    @Transactional
    public CsvUploadResponse ingestCsv(UUID organizationId, CsvIngestionRequest request, MultipartFile file) {
        fileValidator.validate(file);
        TransactionSource source = sourceService.getOrCreate(organizationId, request.recordType(), request.sourceName(), request.providerName());
        IngestionJob job = createJob(organizationId, source, file);

        try {
            List<ParsedCsvRecord> records = csvParsingService.parse(file);
            job.setTotalRecords(records.size());
            job.setStatus(IngestionJobStatus.PROCESSING);
            job.setStartedAt(Instant.now());
            job = jobRepository.save(job);

            IngestionBatch batch = createBatch(organizationId, job, records.size());
            int processed = 0;
            int failed = 0;
            int duplicates = 0;

            for (ParsedCsvRecord parsedRecord : records) {
                try {
                    NormalizedTransactionRecord normalizedRecord = normalizationService.normalize(organizationId, request.recordType(), parsedRecord);
                    recordValidator.validate(normalizedRecord);
                    if (transactionRepository.existsByOrganizationIdAndDuplicateFingerprint(organizationId, normalizedRecord.duplicateFingerprint())) {
                        duplicates++;
                        continue;
                    }
                    transactionRepository.save(toTransaction(organizationId, source, job, batch, normalizedRecord));
                    processed++;
                } catch (RuntimeException exception) {
                    failed++;
                    errorRepository.save(toIngestionError(organizationId, job, batch, parsedRecord, exception));
                }
            }

            batch.setRecordCount(processed);
            batch.setStatus(failed > 0 ? IngestionBatchStatus.FAILED : IngestionBatchStatus.PROCESSED);
            batchRepository.save(batch);

            job.setProcessedRecords(processed);
            job.setFailedRecords(failed);
            job.setDuplicateRecords(duplicates);
            job.setStatus(failed > 0 ? IngestionJobStatus.COMPLETED_WITH_ERRORS : IngestionJobStatus.COMPLETED);
            job.setCompletedAt(Instant.now());
            job = jobRepository.save(job);
            metricsService.recordCompleted(job);
            return new CsvUploadResponse(job.getId(), records.size(), processed, failed, duplicates);
        } catch (IOException exception) {
            job.setStatus(IngestionJobStatus.FAILED);
            job.setFailureReason("Unable to parse CSV file");
            job.setCompletedAt(Instant.now());
            jobRepository.save(job);
            throw new ValidationException("Unable to parse CSV file");
        }
    }

    @Transactional(readOnly = true)
    public IngestionJob getJob(UUID organizationId, UUID jobId) {
        return jobRepository.findByIdAndOrganizationId(jobId, organizationId)
                .orElseThrow(() -> new ResourceNotFoundException("IngestionJob", jobId));
    }

    @Transactional(readOnly = true)
    public PageResponse<IngestionErrorResponse> getErrors(UUID organizationId, UUID jobId, int page, int size) {
        getJob(organizationId, jobId);
        PageRequestDto pageRequest = new PageRequestDto(page, size, "rowNumber", SortDirection.ASC);
        return PageResponse.from(errorRepository
                .findByOrganizationIdAndJobId(organizationId, jobId, PageRequestFactory.from(pageRequest, "rowNumber"))
                .map(ingestionMapper::toResponse));
    }

    private IngestionJob createJob(UUID organizationId, TransactionSource source, MultipartFile file) {
        IngestionJob job = new IngestionJob();
        job.setOrganizationId(organizationId);
        job.setSource(source);
        job.setOriginalFilename(file.getOriginalFilename());
        job.setContentType(file.getContentType());
        job.setFileSizeBytes(file.getSize());
        return jobRepository.save(job);
    }

    private IngestionBatch createBatch(UUID organizationId, IngestionJob job, int recordCount) {
        IngestionBatch batch = new IngestionBatch();
        batch.setOrganizationId(organizationId);
        batch.setJob(job);
        batch.setBatchSequence(1);
        batch.setRecordCount(recordCount);
        batch.setStatus(IngestionBatchStatus.VALIDATED);
        return batchRepository.save(batch);
    }

    private FinancialTransaction toTransaction(UUID organizationId, TransactionSource source, IngestionJob job, IngestionBatch batch, NormalizedTransactionRecord record) {
        FinancialTransaction transaction = new FinancialTransaction();
        transaction.setOrganizationId(organizationId);
        transaction.setSource(source);
        transaction.setIngestionJob(job);
        transaction.setIngestionBatch(batch);
        transaction.setRecordType(record.recordType());
        transaction.setExternalReference(record.externalReference());
        transaction.setCounterpartyName(record.counterpartyName());
        transaction.setDescription(record.description());
        transaction.setTransactionTimestamp(record.transactionTimestamp());
        transaction.setPostingTimestamp(record.postingTimestamp());
        transaction.setCurrencyCode(record.currencyCode());
        transaction.setAmount(record.amount());
        transaction.setDirection(record.direction());
        transaction.setDuplicateFingerprint(record.duplicateFingerprint());
        transaction.setRawPayload(record.rawPayload());

        record.metadata().forEach((key, value) -> {
            TransactionMetadata metadata = new TransactionMetadata();
            metadata.setOrganizationId(organizationId);
            metadata.setTransaction(transaction);
            metadata.setMetadataKey(key);
            metadata.setMetadataValue(value);
            transaction.getMetadata().add(metadata);
        });
        return transaction;
    }

    private IngestionError toIngestionError(UUID organizationId, IngestionJob job, IngestionBatch batch, ParsedCsvRecord parsedRecord, RuntimeException exception) {
        IngestionError error = new IngestionError();
        error.setOrganizationId(organizationId);
        error.setJob(job);
        error.setBatch(batch);
        error.setRowNumber(parsedRecord.rowNumber());
        error.setErrorCode(exception instanceof ValidationException ? "VALIDATION_FAILED" : "INGESTION_RECORD_FAILED");
        error.setErrorMessage(exception.getMessage());
        error.setRawPayload(parsedRecord.rawPayload());
        return error;
    }
}
