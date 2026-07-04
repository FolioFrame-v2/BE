package com.folioframe.domain.company.entity;

import com.folioframe.domain.common.entity.Region;
import com.folioframe.domain.company.enums.Industry;
import com.folioframe.domain.company.enums.VerificationStatus;
import com.folioframe.domain.member.entity.Member;
import com.folioframe.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "company_profile")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CompanyProfile extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "company_profile_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false, unique = true)
    private Member member;

    // 회원가입 시점엔 사업자번호만 받고, 지역은 이후 회사 프로필 완성 단계에서 채워짐
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "region_id")
    private Region region;

    @Column(name = "company_name", nullable = false, length = 100)
    private String companyName;

    @Column(name = "business_number", nullable = false, unique = true, length = 100)
    private String businessNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "industry", length = 50)
    private Industry industry;

    @Column(name = "website_url", length = 500)
    private String websiteUrl;

    @Column(name = "logo_url", length = 500)
    private String logoUrl;

    @Column(name = "company_intro", columnDefinition = "TEXT")
    private String companyIntro;

    @Column(name = "employee_size", length = 50)
    private String employeeSize;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "verification_status", nullable = false)
    private VerificationStatus verificationStatus = VerificationStatus.PENDING;

    public void updateVerificationStatus(VerificationStatus verificationStatus) {
        this.verificationStatus = verificationStatus;
    }

    public void updateProfile(String companyName, String businessNumber, Industry industry,
                              String websiteUrl, String logoUrl, String companyIntro, Region region, String employeeSize) {
        this.companyName = companyName;
        this.businessNumber = businessNumber;
        this.industry = industry;
        this.websiteUrl = websiteUrl;
        this.logoUrl = logoUrl;
        this.companyIntro = companyIntro;
        this.region = region;
        this.employeeSize = employeeSize;
    }
}