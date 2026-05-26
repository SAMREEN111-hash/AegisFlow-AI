package com.aegisflow.api.common.pagination;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public record PageRequestDto(
        @Min(0) int page,
        @Min(1) @Max(200) int size,
        String sortBy,
        SortDirection direction
) {

    public PageRequestDto {
        if (size == 0) {
            size = 25;
        }
        if (direction == null) {
            direction = SortDirection.DESC;
        }
    }
}
