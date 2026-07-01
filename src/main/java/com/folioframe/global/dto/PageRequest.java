package com.folioframe.global.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.data.domain.Sort;

@Schema(description = "페이징 요청")
public record PageRequest(
        @Schema(description = "페이지 번호 (1부터 시작)", example = "1")
        Integer page,

        @Schema(description = "페이지 크기", example = "9")
        Integer size
) {
    public PageRequest {
        if (page == null || page < 1) page = 1;
        if (size == null || size < 1) size = 9;
    }

    public static PageRequest of(Integer page, Integer size) {
        return new PageRequest(page, size);
    }

    public org.springframework.data.domain.PageRequest toPageable() {
        return org.springframework.data.domain.PageRequest.of(page - 1, size);
    }

    public org.springframework.data.domain.PageRequest toPageable(Sort sort) {
        return org.springframework.data.domain.PageRequest.of(page - 1, size, sort);
    }
}
