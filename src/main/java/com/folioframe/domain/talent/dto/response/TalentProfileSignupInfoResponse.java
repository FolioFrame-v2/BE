package com.folioframe.domain.talent.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TalentProfileSignupInfoResponse {
    private String name;
    private String phone;
    private Integer age;
}
