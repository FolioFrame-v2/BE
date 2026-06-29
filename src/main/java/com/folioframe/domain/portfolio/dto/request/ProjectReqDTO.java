package com.folioframe.domain.portfolio.dto.request;

import com.folioframe.domain.portfolio.enums.ProjectDuration;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ProjectReqDTO(

        @NotBlank(message = "프로젝트명은 필수입니다.")
        @Size(max = 200, message = "프로젝트명은 200자를 초과할 수 없습니다.")
        String title,

        @Size(max = 100, message = "역할은 100자를 초과할 수 없습니다.")
        String role,

        String content,

        @Size(max = 500, message = "썸네일 URL은 500자를 초과할 수 없습니다.")
        String thumbnailUrl,

        @Size(max = 500, message = "프로젝트 URL은 500자를 초과할 수 없습니다.")
        String projectUrl,

        ProjectDuration durationRange
) {}
