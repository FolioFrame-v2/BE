package com.folioframe.domain.talent.entity;

import com.folioframe.domain.common.entity.Region;
import com.folioframe.domain.common.enums.CareerLevel;
import com.folioframe.domain.member.entity.Member;
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

    @Column(name = "profile_image_url", length = 500)
    private String profileImageUrl;

    @Column(name = "job_title", length = 100)
    private String jobTitle;

    @Column(name = "one_liner", length = 200)
    private String oneLiner;

    @Column(name = "contact_email", length = 100)
    private String contactEmail;

    @Column(name = "github_url", length = 500)
    private String githubUrl;

    @Column(name = "portfolio_website", length = 500)
    private String portfolioWebsite;

    @Column(name = "current_company", length = 100)
    private String currentCompany;

    @Column(name = "current_position", length = 100)
    private String currentPosition;

    @Builder.Default
    @Column(name = "career_years")
    private Integer careerYears = 0;

    @Enumerated(EnumType.STRING)
    @Column(name = "career_level")
    private CareerLevel careerLevel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "region_id", nullable = false)
    private Region region;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "profile_visibility", nullable = false)
    private ProfileVisibility profileVisibility = ProfileVisibility.PRIVATE;
}
