package com.folioframe.domain.member.dto.response;

import com.folioframe.domain.member.enums.MemberType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class LoginResDTO {
    private String accessToken;
    private String refreshToken;
    private MemberType memberType;
}
