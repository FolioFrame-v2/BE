package com.folioframe.domain.matching.dto.request;

import com.folioframe.domain.matching.enums.MatchingStatus;
import lombok.Getter;

@Getter
public class MatchingStatusUpdateReqDTO {
    private MatchingStatus status;
}