package com.folioframe.domain.talent.entity;

import com.folioframe.domain.common.entity.Region;
import com.folioframe.domain.common.enums.CareerLevel;
import com.folioframe.domain.common.enums.Gender;
import com.folioframe.domain.common.enums.JobRole;
import com.folioframe.domain.job.enums.EmploymentType;
import com.folioframe.domain.job.enums.JobSeekingStatus;
import com.folioframe.domain.member.entity.Member;
import com.folioframe.domain.talent.dto.request.TalentProfileUpdateRequest;
import com.folioframe.domain.talent.enums.ProfileVisibility;
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

    @Column(name = "linkedin_url", length = 500)
    private String linkedinUrl;

    @Column(name = "application_field", length = 50, nullable = false)
    private String applicationField;

    @Enumerated(EnumType.STRING)
    @Column(name = "job_role", nullable = false)
    private JobRole jobRole;

    @Enumerated(EnumType.STRING)
    @Column(name = "career_level", nullable = false)
    private CareerLevel careerLevel;

    @Enumerated(EnumType.STRING)
    @Column(name = "employment_type")
    private EmploymentType employmentType;

    @Column(name = "one_liner", length = 500)
    private String oneLiner;

    @Column(name = "introduction", length = 1000)
    private String introduction;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "profile_visibility", nullable = false)
    private ProfileVisibility profileVisibility = ProfileVisibility.PRIVATE;

    @Enumerated(EnumType.STRING)
    @Column(name = "job_seeking_status", nullable = false)
    private JobSeekingStatus jobSeekingStatus;

    @Builder.Default
    @Column(name = "view_count")
    private Integer viewCount = 0;

    @Builder.Default
    @Column(name = "bookmark_count")
    private Integer bookmarkCount = 0;

    public void updateProfile(TalentProfileUpdateRequest request, Region region) {
        this.name = request.getName();
        this.region = region;
        this.contactEmail = request.getContactEmail();
        this.phoneNumber = request.getPhoneNumber();
        this.age = request.getAge();
        this.gender = request.getGender();

        this.githubUrl = request.getGithubUrl();
        this.portfolioWebsite = request.getPortfolioWebsite();
        this.linkedinUrl = request.getLinkedinUrl();

        this.applicationField = request.getApplicationField();
        this.jobRole = request.getJobRole();
        this.careerLevel = request.getCareerLevel();
        this.employmentType = request.getEmploymentType();

        this.oneLiner = request.getOneLiner();
        this.introduction = request.getIntroduction();

        this.profileVisibility = request.getProfileVisibility();
        this.jobSeekingStatus = request.getJobSeekingStatus();
    }

    public void updateOneLiner(String oneLiner) {
        this.oneLiner = oneLiner;
    }
}