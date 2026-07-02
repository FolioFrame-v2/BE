package com.folioframe.domain.portfolio.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.List;

public record ProjectReqDTO(

        @NotBlank(message = "프로젝트명은 필수입니다.")
        @Size(max = 200, message = "프로젝트명은 200자를 초과할 수 없습니다.")
        String title,

        @Size(max = 100, message = "역할은 100자를 초과할 수 없습니다.")
        String role,

        @Size(max = 500, message = "요약 설명은 500자를 초과할 수 없습니다.")
        String content,

        @Size(max = 500, message = "프로젝트 URL은 500자를 초과할 수 없습니다.")
        String projectUrl,

        // 연-월 단위로만 사용 (일자는 무시하고 매월 1일로 취급)
        LocalDate startedAt,

        LocalDate endedAt,

        // 프로젝트에 사용한 기술스택 ID 목록
        List<Long> techstackIds
) {}
