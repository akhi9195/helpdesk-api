package com.portfolio.helpdesk.common.web;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import org.springframework.data.domain.Page;

/**
 * Stable JSON shape for paged results:
 * { content, page, size, totalElements, totalPages }.
 */
@Schema(description = "One page of results")
public record PageResponse<T>(
        List<T> content,
        @Schema(description = "Zero-based page number", example = "0") int page,
        @Schema(example = "20") int size,
        @Schema(example = "57") long totalElements,
        @Schema(example = "3") int totalPages
) {

    public static <T> PageResponse<T> from(Page<T> page) {
        return new PageResponse<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages());
    }
}