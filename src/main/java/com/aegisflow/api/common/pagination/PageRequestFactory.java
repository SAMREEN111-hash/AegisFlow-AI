package com.aegisflow.api.common.pagination;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.util.StringUtils;

public final class PageRequestFactory {

    private PageRequestFactory() {
    }

    public static Pageable from(PageRequestDto request, String defaultSortBy) {
        String sortBy = StringUtils.hasText(request.sortBy()) ? request.sortBy() : defaultSortBy;
        Sort.Direction direction = request.direction() == SortDirection.ASC ? Sort.Direction.ASC : Sort.Direction.DESC;
        return PageRequest.of(request.page(), request.size(), Sort.by(direction, sortBy));
    }
}
