package com.aegisflow.api.transactions.service;

import com.aegisflow.api.common.exception.ValidationException;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class CsvFileValidator {

    private static final long MAX_FILE_SIZE_BYTES = 10 * 1024 * 1024;

    public void validate(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new ValidationException("CSV file is required");
        }
        if (file.getSize() > MAX_FILE_SIZE_BYTES) {
            throw new ValidationException("CSV file exceeds the maximum supported size of 10MB");
        }
        String filename = file.getOriginalFilename();
        if (filename == null || !filename.toLowerCase().endsWith(".csv")) {
            throw new ValidationException("Only .csv files are supported");
        }
    }
}
