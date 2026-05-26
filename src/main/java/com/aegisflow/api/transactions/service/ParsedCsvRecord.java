package com.aegisflow.api.transactions.service;

import java.util.Map;

public record ParsedCsvRecord(long rowNumber, Map<String, String> values, String rawPayload) {
}
