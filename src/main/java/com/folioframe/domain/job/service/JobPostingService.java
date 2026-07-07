package com.folioframe.domain.job.service;

import com.folioframe.domain.common.entity.Region;
import com.folioframe.domain.common.entity.Techstack;
import com.folioframe.domain.common.enums.CareerLevel;
import com.folioframe.domain.common.repository.RegionRepository;
import com.folioframe.domain.common.repository.TechstackRepository;
import com.folioframe.domain.common.service.RegionService;
import com.folioframe.domain.company.entity.CompanyProfile;
import com.folioframe.domain.company.repository.CompanyProfileRepository;
import com.folioframe.domain.job.dto.request.JobPostingReqDTO;
import com.folioframe.domain.job.dto.request.JobPostingSearchCond;
import com.folioframe.domain.job.dto.response.JobPostingDetailResDTO;
import com.folioframe.domain.job.dto.response.JobPostingListResDTO;
import com.folioframe.domain.job.entity.HiringProcessStep;
import com.folioframe.domain.job.entity.JobPosting;
import com.folioframe.domain.job.entity.JobPostingBookmark;
import com.folioframe.domain.job.entity.JobPostingTechstack;
import com.folioframe.domain.job.enums.JobPostingStatus;
import com.folioframe.domain.job.exception.code.JobErrorCode;
import com.folioframe.domain.job.repository.JobApplicationRepository;
import com.folioframe.domain.job.repository.JobPostingBookmarkRepository;
import com.folioframe.domain.job.repository.JobPostingRepository;
import com.folioframe.domain.job.repository.JobPostingTechStackRepository;
import com.folioframe.domain.member.entity.Member;
import com.folioframe.domain.member.repository.MemberRepository;
import com.folioframe.global.apiPayload.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class JobPostingService {

    private final JobPostingRepository jobPostingRepository;
    private final CompanyProfileRepository companyProfileRepository;
    private final RegionRepository regionRepository;
    private final RegionService regionService;
    private final JobApplicationRepository jobApplicationRepository;
    private final JobPostingBookmarkRepository jobPostingBookmarkRepository;
    private final MemberRepository memberRepository;
    private final TechstackRepository techstackRepository;
    private final JobPostingTechStackRepository jobPostingTechStackRepository;

    @Transactional
    public Long createJobPosting(JobPostingReqDTO request, Long memberId) {

        CompanyProfile company = companyProfileRepository.findByMemberId(memberId)
                .orElseThrow(() -> new GeneralException(JobErrorCode.FORBIDDEN_ACCESS));

        Region region = regionRepository.findById(request.regionId())
                .orElseThrow(() -> new GeneralException(JobErrorCode.INVALID_QUERY_PARAM));

        JobPosting jobPosting = JobPosting.builder()
                .companyProfile(company)
                .title(request.title())
                .positionName(request.positionName())
                .jobRole(request.jobRole())
                .employmentType(request.employmentType())
                .careerLevel(request.careerLevel())
                .minCareerYear(request.minCareerYear())
                .maxCareerYear(request.maxCareerYear())
                .region(region)
                .workLocation(request.workLocation())
                .minSalary(request.minSalary())
                .maxSalary(request.maxSalary())
                .fieldDescription(request.fieldDescription())
                .responsibilities(request.responsibilities())
                .qualifications(request.qualifications())
                .preferredQualifications(request.preferredQualifications())
                .preferredConditions(request.preferredConditions())
                .preferredTalents(request.preferredTalents())
                .hiringProcess(mapToHiringProcessSteps(request.hiringProcess()))
                .additionalNotes(request.additionalNotes())
                .deadline(request.deadline())
                .status(request.status())
                .build();

        JobPosting savedJobPosting = jobPostingRepository.save(jobPosting);

        saveTechStacks(request.techStacks(), savedJobPosting);

        return savedJobPosting.getId();
    }

    public Page<JobPostingListResDTO> getJobPostings(JobPostingSearchCond cond) {
        RegionService.RegionFilter regionFilter = regionService.resolveRegionFilter(cond.getRegionId());
        Page<JobPosting> jobPostings = jobPostingRepository.findByCondition(cond, regionFilter.exactRegionId(), regionFilter.provinceRegionId());

        List<Long> jobPostingIds = jobPostings.getContent().stream().map(JobPosting::getId).toList();

        final Map<Long, List<JobPostingTechstack>> techStackMap;
        if (!jobPostingIds.isEmpty()) {
            List<JobPostingTechstack> allTechStacks = jobPostingTechStackRepository.findByJobPostingIdIn(jobPostingIds);
            techStackMap = allTechStacks.stream()
                    .collect(Collectors.groupingBy(ts -> ts.getJobPosting().getId()));
        } else {
            techStackMap = Map.of();
        }

        return jobPostings.map(posting -> JobPostingListResDTO.builder()
                .jobPostingId(posting.getId())
                .companyName(posting.getCompanyProfile().getCompanyName())
                .careerLevel(posting.getCareerLevel())
                .status(JobPostingStatus.resolve(posting.getStatus(), posting.getDeadline()))
                .jobRole(posting.getJobRole())
                .title(posting.getTitle())
                .positionName(posting.getPositionName())
                .shortDescription(posting.getFieldDescription() != null && posting.getFieldDescription().length() > 50
                        ? posting.getFieldDescription().substring(0, 50) + "..."
                        : posting.getFieldDescription())
                .locationName(posting.getRegion().getName())
                .techStacks(mapToTechStackDto(techStackMap.getOrDefault(posting.getId(), List.of())))
                .build()
        );
    }

    @Transactional
    public JobPostingDetailResDTO getJobPostingDetail(Long jobPostingId, Long memberId) {

        if (!jobPostingRepository.existsById(jobPostingId)) {
            throw new GeneralException(JobErrorCode.JOB_POSTING_NOT_FOUND);
        }

        // DB에 직접 조회수 증가 쿼리를 날림
        jobPostingRepository.incrementViewCount(jobPostingId);

        // 업데이트된 최신 데이터를 가져옴
        JobPosting jobPosting = jobPostingRepository.findById(jobPostingId)
                .orElseThrow(() -> new GeneralException(JobErrorCode.JOB_POSTING_NOT_FOUND));

        boolean isBookmarked = false;
        if (memberId != null) {
            isBookmarked = jobPostingBookmarkRepository.existsByJobPostingIdAndMemberId(jobPostingId, memberId);
        }

        List<JobPostingTechstack> techStacks = jobPostingTechStackRepository.findByJobPostingId(jobPostingId);

        JobPostingDetailResDTO.CompanyProfileDto companyProfileDto = JobPostingDetailResDTO.CompanyProfileDto.builder()
                .companyId(jobPosting.getCompanyProfile().getId())
                .companyName(jobPosting.getCompanyProfile().getCompanyName())
                .industry(jobPosting.getCompanyProfile().getIndustry())
                .employeeSize(jobPosting.getCompanyProfile().getEmployeeSize())
                .websiteUrl(jobPosting.getCompanyProfile().getWebsiteUrl())
                .build();

        JobPostingDetailResDTO.HeaderInfoDto headerInfoDto = JobPostingDetailResDTO.HeaderInfoDto.builder()
                .dDay(java.time.temporal.ChronoUnit.DAYS.between(java.time.LocalDate.now(), jobPosting.getDeadline()))
                .employmentType(jobPosting.getEmploymentType())
                .careerLevel(jobPosting.getCareerLevel())
                .jobRole(jobPosting.getJobRole())
                .build();

        return JobPostingDetailResDTO.builder()
                .jobPostingId(jobPosting.getId())
                .companyProfile(companyProfileDto)
                .headerInfo(headerInfoDto)
                .title(jobPosting.getTitle())
                .positionName(jobPosting.getPositionName())
                .workLocation(jobPosting.getWorkLocation())
                .salaryString(formatSalary(jobPosting.getMinSalary(), jobPosting.getMaxSalary()))
                .viewCount(jobPosting.getViewCount())
                .bookmarkCount(jobPosting.getBookmarkCount())
                .isBookmarked(isBookmarked)
                .fieldDescription(jobPosting.getFieldDescription())
                .responsibilities(jobPosting.getResponsibilities())
                .qualifications(jobPosting.getQualifications())
                .preferredQualifications(jobPosting.getPreferredQualifications())
                .preferredConditions(jobPosting.getPreferredConditions())
                .preferredTalents(jobPosting.getPreferredTalents())
                .hiringProcess(mapToHiringProcessDto(jobPosting.getHiringProcess()))
                .additionalNotes(jobPosting.getAdditionalNotes())
                .deadline(jobPosting.getDeadline())
                .status(JobPostingStatus.resolve(jobPosting.getStatus(), jobPosting.getDeadline()))
                .techStacks(mapToTechStackDto(techStacks))
                .createdAt(jobPosting.getCreatedAt())
                .updatedAt(jobPosting.getUpdatedAt())
                .build();
    }

    @Transactional
    public LocalDateTime updateJobPosting(Long jobPostingId, JobPostingReqDTO request, Long memberId) {

        CompanyProfile company = companyProfileRepository.findByMemberId(memberId)
                .orElseThrow(() -> new GeneralException(JobErrorCode.FORBIDDEN_ACCESS));

        JobPosting jobPosting = jobPostingRepository.findById(jobPostingId)
                .orElseThrow(() -> new GeneralException(JobErrorCode.JOB_POSTING_NOT_FOUND));

        if (!jobPosting.getCompanyProfile().getId().equals(company.getId())) {
            throw new GeneralException(JobErrorCode.FORBIDDEN_ACCESS);
        }

        Region newRegion = regionRepository.findById(request.regionId())
                .orElseThrow(() -> new GeneralException(JobErrorCode.INVALID_QUERY_PARAM));

        jobPosting.update(request, newRegion);
        jobPosting.updateHiringProcess(mapToHiringProcessSteps(request.hiringProcess()));

        jobPostingTechStackRepository.deleteByJobPostingId(jobPosting.getId());
        saveTechStacks(request.techStacks(), jobPosting);

        return jobPosting.getUpdatedAt();
    }

    @Transactional
    public void deleteJobPosting(Long jobPostingId, Long memberId) {

        CompanyProfile company = companyProfileRepository.findByMemberId(memberId)
                .orElseThrow(() -> new GeneralException(JobErrorCode.FORBIDDEN_ACCESS));

        JobPosting jobPosting = jobPostingRepository.findById(jobPostingId)
                .orElseThrow(() -> new GeneralException(JobErrorCode.JOB_POSTING_NOT_FOUND));

        if (!jobPosting.getCompanyProfile().getId().equals(company.getId())) {
            throw new GeneralException(JobErrorCode.FORBIDDEN_ACCESS);
        }

        if (jobApplicationRepository.existsByJobPostingId(jobPostingId)) {
            throw new GeneralException(JobErrorCode.JOB_POSTING_CANNOT_DELETE);
        }

        jobPostingTechStackRepository.deleteByJobPostingId(jobPostingId);
        jobPostingBookmarkRepository.deleteByJobPostingId(jobPostingId);
        jobPostingRepository.delete(jobPosting);
    }

    @Transactional
    public Map<String, Object> toggleBookmark(Long jobPostingId, Long memberId) {
        JobPosting jobPosting = jobPostingRepository.findById(jobPostingId)
                .orElseThrow(() -> new GeneralException(JobErrorCode.JOB_POSTING_NOT_FOUND));

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new GeneralException(JobErrorCode.UNAUTHORIZED));

        Optional<JobPostingBookmark> existingBookmark = jobPostingBookmarkRepository.findByJobPostingIdAndMemberId(jobPostingId, memberId);

        boolean isBookmarked;
        if (existingBookmark.isPresent()) {
            jobPostingBookmarkRepository.delete(existingBookmark.get());
            jobPosting.decreaseBookmarkCount();
            isBookmarked = false;
        } else {
            JobPostingBookmark bookmark = JobPostingBookmark.builder()
                    .jobPosting(jobPosting)
                    .member(member)
                    .build();
            jobPostingBookmarkRepository.save(bookmark);
            jobPosting.increaseBookmarkCount();
            isBookmarked = true;
        }

        return Map.of("jobPostingId", jobPostingId, "isBookmarked", isBookmarked);
    }

    // 예: (4800, 7200) -> "4,800만원 ~ 7,200만원"
    private String formatSalary(Integer minSalary, Integer maxSalary) {
        if (minSalary == null && maxSalary == null) return null;

        String min = minSalary != null ? "%,d만원".formatted(minSalary) : "";
        String max = maxSalary != null ? "%,d만원".formatted(maxSalary) : "";
        if (minSalary == null) return max;
        if (maxSalary == null) return min;
        return min + " ~ " + max;
    }

    private void saveTechStacks(List<JobPostingReqDTO.TechStackReqDto> techStackDtos, JobPosting jobPosting) {
        if (techStackDtos == null || techStackDtos.isEmpty()) return;

        List<JobPostingTechstack> newTechStacks = techStackDtos.stream()
                .map(dto -> {
                    Techstack techstack = techstackRepository.findById(dto.techStackId())
                            .orElseThrow(() -> new GeneralException(JobErrorCode.INVALID_QUERY_PARAM));
                    return JobPostingTechstack.builder()
                            .jobPosting(jobPosting)
                            .techstack(techstack)
                            .stackType(dto.stackType())
                            .build();
                }).toList();

        jobPostingTechStackRepository.saveAll(newTechStacks);
    }

    private List<JobPostingListResDTO.TechStackDto> mapToTechStackDto(List<JobPostingTechstack> techStacks) {
        if (techStacks == null) return List.of();
        return techStacks.stream()
                .map(ts -> new JobPostingListResDTO.TechStackDto(ts.getTechstack().getId(), ts.getTechstack().getName()))
                .toList();
    }

    private List<HiringProcessStep> mapToHiringProcessSteps(List<JobPostingReqDTO.HiringProcessStepDto> dtos) {
        if (dtos == null) return List.of();
        return dtos.stream()
                .map(dto -> new HiringProcessStep(dto.stepOrder(), dto.stepName(), dto.description()))
                .toList();
    }

    private List<JobPostingReqDTO.HiringProcessStepDto> mapToHiringProcessDto(List<HiringProcessStep> steps) {
        if (steps == null) return List.of();
        return steps.stream()
                .map(step -> new JobPostingReqDTO.HiringProcessStepDto(step.getStepOrder(), step.getStepName(), step.getDescription()))
                .toList();
    }
}