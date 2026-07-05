package com.folioframe.domain.talent.entity;

import com.folioframe.domain.common.entity.Region;
import com.folioframe.domain.common.enums.Gender;
import com.folioframe.domain.member.entity.Member;
import com.folioframe.domain.talent.dto.request.TalentProfileUpdateReqDTO;
import com.folioframe.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "talent_profile")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class TalentProfile extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "talent_profile_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false, unique = true)
    private Member member;

    @Column(name = "name", length = 50, nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "region_id", nullable = false)
    private Region region;

    @Column(name = "contact_email", length = 100, nullable = false)
    private String contactEmail;

    @Column(name = "phone_number", length = 20)
    private String phoneNumber;

    @Column(name = "age")
    private Integer age;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender", length = 20)
    private Gender gender;

    @Column(name = "github_url", length = 500, nullable = false)
    private String githubUrl;

    @Column(name = "portfolio_website", length = 500)
    private String portfolioWebsite;

    @Column(name = "career_years", nullable = false)
    private Integer careerYears;

    @Column(name = "one_liner", length = 500)
    private String oneLiner;

    @Builder.Default
    @Column(name = "view_count")
    private Integer viewCount = 0;

    @Builder.Default
    @Column(name = "bookmark_count")
    private Integer bookmarkCount = 0;

    public void updateProfile(TalentProfileUpdateReqDTO request, Region region) {
        this.name = request.getName();
        this.region = region;
        this.contactEmail = request.getContactEmail();
        this.phoneNumber = request.getPhoneNumber();
        this.age = request.getAge();
        this.gender = request.getGender();

        this.githubUrl = request.getGithubUrl();
        this.portfolioWebsite = request.getPortfolioWebsite();

        this.careerYears = request.getCareerYears();

        this.oneLiner = request.getOneLiner();
    }

    public void updateOneLiner(String oneLiner) {
        this.oneLiner = oneLiner;
    }
}