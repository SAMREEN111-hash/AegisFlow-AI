package com.aegisflow.api.transactions.service;

import com.aegisflow.api.identity.service.TokenHashService;
import com.aegisflow.api.transactions.domain.FinancialRecordType;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TransactionFingerprintService {

    private final TokenHashService tokenHashService;

    public String fingerprint(UUID organizationId, FinancialRecordType recordType, String externalReference, BigDecimal amount, String currencyCode, Instant timestamp) {
        String input = organizationId + "|" + recordType + "|" + externalReference + "|" + amount.stripTrailingZeros().toPlainString()
                + "|" + currencyCode + "|" + timestamp;
        return tokenHashService.sha256(input);
    }
}
