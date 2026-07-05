package com.folioframe.domain.job.entity;

import com.folioframe.domain.common.entity.Region;
import com.folioframe.domain.common.enums.CareerLevel;
import com.folioframe.domain.common.enums.JobRole;
import com.folioframe.domain.company.entity.CompanyProfile;
import com.folioframe.domain.job.dto.request.JobPostingReqDTO;
import com.folioframe.domain.job.enums.EmploymentType;
import com.folioframe.domain.job.enums.JobPostingStatus;
import com.folioframe.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "job_posting")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class JobPosting extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "job_posting_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_profile_id", nullable = false)
    private CompanyProfile companyProfile;

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Column(name = "position_name", nullable = false, length = 200)
    private String positionName;

    @Enumerated(EnumType.STRING)
    @Column(name = "job_role", nullable = false)
    private JobRole jobRole;

    @Enumerated(EnumType.STRING)
    @Column(name = "employment_type", nullable = false)
    private EmploymentType employmentType;

    @Enumerated(EnumType.STRING)
    @Column(name = "career_level")
    private CareerLevel careerLevel;

    @Column(name = "min_career_year")
    private Integer minCareerYear;

    @Column(name = "max_career_year")
    private Integer maxCareerYear;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "region_id", nullable = false)
    private Region region;

    @Column(name = "work_location", length = 255)
    private String workLocation;

    @Column(name = "min_salary")
    private Integer minSalary;

    @Column(name = "max_salary")
    private Integer maxSalary;

    @Column(name = "field_description", columnDefinition = "TEXT")
    private String fieldDescription;

    @Builder.Default
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "job_posting_responsibility", joinColumns = @JoinColumn(name = "job_posting_id"))
    @Column(name = "responsibility", columnDefinition = "TEXT")
    private List<String> responsibilities = new ArrayList<>();

    @Builder.Default
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "job_posting_qualification", joinColumns = @JoinColumn(name = "job_posting_id"))
    @Column(name = "qualification", columnDefinition = "TEXT")
    private List<String> qualifications = new ArrayList<>();

    @Builder.Default
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "job_posting_preferred_qualification", joinColumns = @JoinColumn(name = "job_posting_id"))
    @Column(name = "preferred_qualification", columnDefinition = "TEXT")
    private List<String> preferredQualifications = new ArrayList<>();

    @Builder.Default
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "job_posting_preferred_condition", joinColumns = @JoinColumn(name = "job_posting_id"))
    @Column(name = "preferred_condition", columnDefinition = "TEXT")
    private List<String> preferredConditions = new ArrayList<>();

    @Builder.Default
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "job_posting_preferred_talent", joinColumns = @JoinColumn(name = "job_posting_id"))
    @Column(name = "preferred_talent", columnDefinition = "TEXT")
    private List<String> preferredTalents = new ArrayList<>();

    @Builder.Default
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "job_posting_hiring_process", joinColumns = @JoinColumn(name = "job_posting_id"))
    @OrderBy("stepOrder ASC")
    private List<HiringProcessStep> hiringProcess = new ArrayList<>();

    @Column(name = "additional_notes", columnDefinition = "TEXT")
    private String additionalNotes;

    @Column(name = "deadline")
    private LocalDate deadline;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private JobPostingStatus status = JobPostingStatus.ACTIVE;

    @Builder.Default
    @Column(name = "view_count", nullable = false)
    private int viewCount = 0;

    @Builder.Default
    @Column(name = "bookmark_count", nullable = false)
    private int bookmarkCount = 0;

    public void update(JobPostingReqDTO request, Region newRegion) {
        this.title = request.title();
        this.positionName = request.positionName();
        this.jobRole = request.jobRole();
        this.employmentType = request.employmentType();
        this.careerLevel = request.careerLevel();
        this.minCareerYear = request.minCareerYear();
        this.maxCareerYear = request.maxCareerYear();
        this.region = newRegion;
        this.workLocation = request.workLocation();
        this.minSalary = request.minSalary();
        this.maxSalary = request.maxSalary();
        this.fieldDescription = request.fieldDescription();
        this.additionalNotes = request.additionalNotes();
        this.deadline = request.deadline();
        this.status = request.status();

        this.responsibilities.clear();
        if (request.responsibilities() != null) this.responsibilities.addAll(request.responsibilities());

        this.qualifications.clear();
        if (request.qualifications() != null) this.qualifications.addAll(request.qualifications());

        this.preferredQualifications.clear();
        if (request.preferredQualifications() != null) this.preferredQualifications.addAll(request.preferredQualifications());

        this.preferredConditions.clear();
        if (request.preferredConditions() != null) this.preferredConditions.addAll(request.preferredConditions());

        this.preferredTalents.clear();
        if (request.preferredTalents() != null) this.preferredTalents.addAll(request.preferredTalents());
    }

    public void updateHiringProcess(List<HiringProcessStep> newSteps) {
        this.hiringProcess.clear();
        if (newSteps != null) this.hiringProcess.addAll(newSteps);
    }

    public void increaseViewCount() {
        this.viewCount++;
    }

    public void increaseBookmarkCount() {
        this.bookmarkCount++;
    }

    public void decreaseBookmarkCount() {
        if (this.bookmarkCount > 0) this.bookmarkCount--;
    }
}