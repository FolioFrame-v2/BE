package com.folioframe.domain.job.dto.response;

import com.folioframe.domain.common.enums.CareerLevel;
import com.folioframe.domain.common.enums.JobRole;
import com.folioframe.domain.company.enums.Industry;
import com.folioframe.domain.job.dto.request.JobPostingReqDTO.HiringProcessStepDto;
import com.folioframe.domain.job.enums.EmploymentType;
import com.folioframe.domain.job.enums.JobPostingStatus;
import lombok.Builder;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Builder
public record JobPostingDetailResDTO(
        Long jobPostingId,
        CompanyProfileDto companyProfile,
        HeaderInfoDto headerInfo,
        String positionName,
        String workLocation,
        String salaryString,
        int viewCount,
        int bookmarkCount,
        boolean isBookmarked,
        String fieldDescription,
        List<String> responsibilities,
        List<String> qualifications,
        List<String> preferredQualifications,
        List<String> preferredConditions,
        List<String> preferredTalents,
        List<HiringProcessStepDto> hiringProcess,
        String additionalNotes,
        LocalDate deadline,
        JobPostingStatus status,
        List<JobPostingListResDTO.TechStackDto> techStacks,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    @Builder
    public record CompanyProfileDto(
            Long companyId,
            String companyName,
            Industry industry,
            String employeeSize,
            String websiteUrl
    ) {}

    @Builder
    public record HeaderInfoDto(
            long dDay,
            EmploymentType employmentType,
            CareerLevel careerLevel,
            JobRole jobRole
    ) {}
}