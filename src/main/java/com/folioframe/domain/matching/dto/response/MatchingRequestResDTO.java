package com.folioframe.domain.matching.dto.response;

import com.folioframe.domain.matching.enums.MatchingStatus;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MatchingRequestResDTO {
    private Long matchingRequestId;
    private MatchingStatus status;
}
