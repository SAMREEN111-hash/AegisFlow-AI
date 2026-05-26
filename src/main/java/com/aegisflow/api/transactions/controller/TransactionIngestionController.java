package com.aegisflow.api.transactions.controller;

import com.aegisflow.api.common.api.ApiResponse;
import com.aegisflow.api.common.api.PageResponse;
import com.aegisflow.api.common.security.RequireTransactionIngest;
import com.aegisflow.api.common.security.RequireTransactionRead;
import com.aegisflow.api.common.security.SecurityUtils;
import com.aegisflow.api.transactions.domain.FinancialRecordType;
import com.aegisflow.api.transactions.dto.request.CsvIngestionRequest;
import com.aegisflow.api.transactions.dto.response.CsvUploadResponse;
import com.aegisflow.api.transactions.dto.response.IngestionJobResponse;
import com.aegisflow.api.transactions.dto.response.IngestionErrorResponse;
import com.aegisflow.api.transactions.mapper.IngestionMapper;
import com.aegisflow.api.transactions.service.TransactionIngestionService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/transaction-ingestions")
public class TransactionIngestionController {

    private final TransactionIngestionService ingestionService;
    private final IngestionMapper ingestionMapper;

    @RequireTransactionIngest
    @PostMapping("/csv")
    ApiResponse<CsvUploadResponse> uploadCsv(
            @RequestParam("file") MultipartFile file,
            @RequestParam("recordType") @NotNull FinancialRecordType recordType,
            @RequestParam("sourceName") @NotBlank String sourceName,
            @RequestParam(value = "providerName", required = false) String providerName) {
        CsvIngestionRequest request = new CsvIngestionRequest(recordType, sourceName, providerName);
        return ApiResponse.success("CSV ingestion completed", ingestionService.ingestCsv(SecurityUtils.currentOrganizationId(), request, file));
    }

    @RequireTransactionRead
    @GetMapping("/{jobId}")
    ApiResponse<IngestionJobResponse> getJob(@PathVariable UUID jobId) {
        return ApiResponse.success(ingestionMapper.toResponse(ingestionService.getJob(SecurityUtils.currentOrganizationId(), jobId)));
    }

    @RequireTransactionRead
    @GetMapping("/{jobId}/errors")
    ApiResponse<PageResponse<IngestionErrorResponse>> getJobErrors(
            @PathVariable UUID jobId,
            @RequestParam(value = "page", required = false, defaultValue = "0") int page,
            @RequestParam(value = "size", required = false, defaultValue = "25") int size) {
        return ApiResponse.success(ingestionService.getErrors(SecurityUtils.currentOrganizationId(), jobId, page, size));
    }
}
