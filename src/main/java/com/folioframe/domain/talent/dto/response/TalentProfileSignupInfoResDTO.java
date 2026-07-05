package com.folioframe.domain.talent.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TalentProfileSignupInfoResDTO {
    private String name;
    private String phone;
    private Integer age;
}
