package com.folioframe.domain.member.dto.response;

import com.folioframe.domain.member.enums.MemberType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class SignupResDTO {
    private Long id;
    private String loginId;
    private MemberType memberType;
}
