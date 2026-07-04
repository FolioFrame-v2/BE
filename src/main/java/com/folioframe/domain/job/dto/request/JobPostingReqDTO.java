package com.folioframe.domain.job.dto.request;

import com.folioframe.domain.common.enums.CareerLevel;
import com.folioframe.domain.common.enums.JobRole;
import com.folioframe.domain.job.enums.EmploymentType;
import com.folioframe.domain.job.enums.JobPostingStatus;
import com.folioframe.domain.job.enums.StackType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

public record JobPostingReqDTO(
        @NotBlank String positionName,
        @NotNull JobRole jobRole,
        @NotNull EmploymentType employmentType,
        CareerLevel careerLevel,
        Integer minCareerYear,
        Integer maxCareerYear,
        @NotNull Long regionId,
        String workLocation,
        Integer minSalary,
        Integer maxSalary,
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
        List<TechStackReqDto> techStacks
) {
    public record HiringProcessStepDto(
            Integer stepOrder,
            String stepName,
            String description
    ) {}

    public record TechStackReqDto(
            Long techStackId,
            StackType stackType
    ) {}
}