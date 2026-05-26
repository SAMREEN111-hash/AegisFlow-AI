package com.aegisflow.api.transactions.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.charset.StandardCharsets;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

class CsvParsingServiceTest {

    private final CsvParsingService csvParsingService = new CsvParsingService();

    @Test
    void parsesHeaderBasedCsvRows() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "transactions.csv",
                "text/csv",
                "external_reference,amount,currency_code,transaction_timestamp,direction\nTX-1,10.00,USD,2026-05-25T00:00:00Z,CREDIT\n"
                        .getBytes(StandardCharsets.UTF_8));

        List<ParsedCsvRecord> records = csvParsingService.parse(file);

        assertThat(records).hasSize(1);
        assertThat(records.getFirst().values()).containsEntry("external_reference", "TX-1");
        assertThat(records.getFirst().rawPayload()).contains("TX-1");
    }
}
