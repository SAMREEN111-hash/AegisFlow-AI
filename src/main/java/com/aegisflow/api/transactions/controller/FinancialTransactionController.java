package com.aegisflow.api.transactions.controller;

import com.aegisflow.api.common.api.ApiResponse;
import com.aegisflow.api.common.api.PageResponse;
import com.aegisflow.api.common.security.RequireTransactionRead;
import com.aegisflow.api.common.security.SecurityUtils;
import com.aegisflow.api.transactions.domain.FinancialRecordType;
import com.aegisflow.api.transactions.domain.ReconciliationStatus;
import com.aegisflow.api.transactions.dto.request.TransactionSearchRequest;
import com.aegisflow.api.transactions.dto.response.FinancialTransactionResponse;
import com.aegisflow.api.transactions.service.FinancialTransactionQueryService;
import java.time.Instant;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/transactions")
public class FinancialTransactionController {

    private final FinancialTransactionQueryService queryService;

    @RequireTransactionRead
    @GetMapping
    ApiResponse<PageResponse<FinancialTransactionResponse>> search(
            @RequestParam(value = "recordType", required = false) FinancialRecordType recordType,
            @RequestParam(value = "reconciliationStatus", required = false) ReconciliationStatus reconciliationStatus,
            @RequestParam(value = "currencyCode", required = false) String currencyCode,
            @RequestParam(value = "from", required = false) Instant from,
            @RequestParam(value = "to", required = false) Instant to,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "size", required = false) Integer size,
            @RequestParam(value = "sortBy", required = false) String sortBy) {
        TransactionSearchRequest request = new TransactionSearchRequest(recordType, reconciliationStatus, currencyCode, from, to, page, size, sortBy, null);
        return ApiResponse.success(queryService.search(SecurityUtils.currentOrganizationId(), request));
    }

    @RequireTransactionRead
    @GetMapping("/{transactionId}")
    ApiResponse<FinancialTransactionResponse> getById(@PathVariable UUID transactionId) {
        return ApiResponse.success(queryService.getById(SecurityUtils.currentOrganizationId(), transactionId));
    }
}
