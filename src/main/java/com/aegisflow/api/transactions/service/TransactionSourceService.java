package com.aegisflow.api.transactions.service;

import com.aegisflow.api.transactions.domain.FinancialRecordType;
import com.aegisflow.api.transactions.domain.TransactionSource;
import com.aegisflow.api.transactions.repository.TransactionSourceRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TransactionSourceService {

    private final TransactionSourceRepository sourceRepository;

    @Transactional
    public TransactionSource getOrCreate(UUID organizationId, FinancialRecordType recordType, String sourceName, String providerName) {
        return sourceRepository.findByOrganizationIdAndRecordTypeAndSourceName(organizationId, recordType, sourceName)
                .orElseGet(() -> create(organizationId, recordType, sourceName, providerName));
    }

    private TransactionSource create(UUID organizationId, FinancialRecordType recordType, String sourceName, String providerName) {
        TransactionSource source = new TransactionSource();
        source.setOrganizationId(organizationId);
        source.setRecordType(recordType);
        source.setSourceName(sourceName.trim());
        source.setProviderName(providerName == null ? null : providerName.trim());
        return sourceRepository.save(source);
    }
}
