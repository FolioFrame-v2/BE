package com.folioframe.domain.member.dto.request;

import com.folioframe.domain.member.enums.MemberType;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Getter
@NoArgsConstructor
public class SignupReqDTO {
    private String loginId;
    private String password;
    private String passwordConfirm;
    private String name;
    private LocalDate birthDate;
    private String phone;
    private MemberType memberType;
    private List<Long> agreedTerms;

    // COMPANY 회원가입 시에만 사용 — 관리자가 확인 후 확정(VerificationStatus)
    private String businessNumber;
}
