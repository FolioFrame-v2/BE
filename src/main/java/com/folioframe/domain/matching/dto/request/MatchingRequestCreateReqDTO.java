package com.folioframe.domain.matching.dto.request;

import lombok.Getter;

@Getter
public class MatchingRequestCreateReqDTO {
    private Long companyProfileId;
    private Long portfolioId;
    private Long jobPostingId;
    private String message;
}
