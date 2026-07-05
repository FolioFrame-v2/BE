package com.folioframe.domain.job.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class JobApplicationCreateReqDTO {

    @NotNull(message = "채용 공고 ID는 필수입니다.")
    private Long jobPostingId;

    @NotNull(message = "포트폴리오 ID는 필수입니다.")
    private Long portfolioId;

    private String message;
}