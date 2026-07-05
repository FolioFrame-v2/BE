package com.folioframe.domain.member.entity;

import com.folioframe.domain.member.enums.MemberStatus;
import com.folioframe.domain.member.enums.MemberType;
import com.folioframe.domain.member.enums.Provider;
import com.folioframe.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "member")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @Column(name = "member_name", nullable = false, length = 50)
    private String name;

    // LOCAL 회원은 아이디, GOOGLE 소셜 로그인 회원은 이메일이 그대로 들어감(로그인/JWT subject로 공용 사용)
    // 단, 소셜 로그인 회원 조회는 이 값이 아니라 provider+providerId로 함
    @Column(name = "login_id", unique = true, length = 255)
    private String loginId;

    @Column(name = "email", length = 100)
    private String email;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    // 소셜 로그인 회원은 null
    @Column(name = "password", length = 255)
    private String password;

    @Column(name = "phone", unique = true, length = 20)
    private String phone;

    // COMPANY 회원가입 시에만 값이 들어감. 회원가입 시점엔 회사명을 아직 모르므로
    // CompanyProfile을 만들지 못해, 사업자번호만 여기 임시로 보관해뒀다가
    // 기업 프로필 등록 화면에서 CompanyProfile.businessNumber로 옮겨간다.
    @Column(name = "business_number", unique = true, length = 100)
    private String businessNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "provider")
    private Provider provider;

    @Column(name = "provider_id", length = 100)
    private String providerId;

    @Enumerated(EnumType.STRING)
    @Column(name = "member_type", nullable = false)
    private MemberType memberType;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private MemberStatus status = MemberStatus.ACTIVE;
}
