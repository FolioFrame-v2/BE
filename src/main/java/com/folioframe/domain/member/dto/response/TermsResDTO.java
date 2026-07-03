package com.folioframe.domain.member.dto.response;

import com.folioframe.domain.common.enums.TermsType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class TermsResDTO {
    private Long termsId;
    private TermsType type;
    private String title;
    private String content;
    private boolean required;
}
