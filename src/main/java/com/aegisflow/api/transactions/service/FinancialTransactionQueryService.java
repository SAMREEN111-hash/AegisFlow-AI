package com.aegisflow.api.transactions.service;

import com.aegisflow.api.common.api.PageResponse;
import com.aegisflow.api.common.exception.ResourceNotFoundException;
import com.aegisflow.api.common.pagination.PageRequestDto;
import com.aegisflow.api.common.pagination.PageRequestFactory;
import com.aegisflow.api.common.pagination.SortDirection;
import com.aegisflow.api.transactions.domain.FinancialTransaction;
import com.aegisflow.api.transactions.dto.request.TransactionSearchRequest;
import com.aegisflow.api.transactions.dto.response.FinancialTransactionResponse;
import com.aegisflow.api.transactions.mapper.FinancialTransactionMapper;
import com.aegisflow.api.transactions.repository.FinancialTransactionRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FinancialTransactionQueryService {

    private final FinancialTransactionRepository transactionRepository;
    private final FinancialTransactionMapper transactionMapper;

    @Transactional(readOnly = true)
    public PageResponse<FinancialTransactionResponse> search(UUID organizationId, TransactionSearchRequest request) {
        PageRequestDto pageRequest = new PageRequestDto(
                request.page() == null ? 0 : request.page(),
                request.size() == null ? 25 : request.size(),
                request.sortBy(),
                request.direction() == null ? SortDirection.DESC : request.direction());

        Specification<FinancialTransaction> specification = FinancialTransactionRepository.organizationEquals(organizationId)
                .and(FinancialTransactionRepository.recordTypeEquals(request.recordType()))
                .and(FinancialTransactionRepository.statusEquals(request.reconciliationStatus()))
                .and(FinancialTransactionRepository.currencyEquals(request.currencyCode()))
                .and(FinancialTransactionRepository.transactionTimestampFrom(request.from()))
                .and(FinancialTransactionRepository.transactionTimestampTo(request.to()));

        Page<FinancialTransactionResponse> page = transactionRepository
                .findAll(specification, PageRequestFactory.from(pageRequest, "transactionTimestamp"))
                .map(transactionMapper::toResponse);
        return PageResponse.from(page);
    }

    @Transactional(readOnly = true)
    public FinancialTransactionResponse getById(UUID organizationId, UUID transactionId) {
        return transactionRepository.findByIdAndOrganizationId(transactionId, organizationId)
                .map(transactionMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("FinancialTransaction", transactionId));
    }
}
