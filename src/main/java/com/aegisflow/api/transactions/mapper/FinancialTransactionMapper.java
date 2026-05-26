package com.aegisflow.api.transactions.mapper;

import com.aegisflow.api.transactions.domain.FinancialTransaction;
import com.aegisflow.api.transactions.domain.TransactionMetadata;
import com.aegisflow.api.transactions.dto.response.FinancialTransactionResponse;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class FinancialTransactionMapper {

    public FinancialTransactionResponse toResponse(FinancialTransaction transaction) {
        Map<String, String> metadata = transaction.getMetadata().stream()
                .collect(Collectors.toMap(TransactionMetadata::getMetadataKey, TransactionMetadata::getMetadataValue, (left, right) -> right));
        return new FinancialTransactionResponse(
                transaction.getId(),
                transaction.getOrganizationId(),
                transaction.getSource().getId(),
                transaction.getRecordType(),
                transaction.getExternalReference(),
                transaction.getCounterpartyName(),
                transaction.getDescription(),
                transaction.getTransactionTimestamp(),
                transaction.getPostingTimestamp(),
                transaction.getCurrencyCode(),
                transaction.getAmount(),
                transaction.getDirection(),
                transaction.getReconciliationStatus(),
                metadata);
    }
}
