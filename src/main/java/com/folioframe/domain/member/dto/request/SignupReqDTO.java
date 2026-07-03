package com.folioframe.domain.member.dto.request;

import com.folioframe.domain.member.enums.MemberType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.List;

@Getter
@NoArgsConstructor
public class SignupReqDTO {
    private String loginId;
    private String password;
    private String name;
    private MemberType memberType;
    private List<Long> agreedTerms;
}
