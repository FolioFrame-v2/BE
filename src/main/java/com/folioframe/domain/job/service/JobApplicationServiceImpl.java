package com.folioframe.domain.job.service;

import com.folioframe.domain.job.dto.request.JobApplicationCreateReqDTO;
import com.folioframe.domain.job.dto.request.JobApplicationStatusUpdateReqDTO;
import com.folioframe.domain.job.dto.response.*;
import com.folioframe.domain.job.entity.JobApplication;
import com.folioframe.domain.job.entity.JobPosting;
import com.folioframe.domain.job.exception.JobException;
import com.folioframe.domain.job.exception.code.JobErrorCode;
import com.folioframe.domain.job.repository.JobApplicationRepository;
import com.folioframe.domain.job.repository.JobPostingRepository;
import com.folioframe.domain.portfolio.entity.Portfolio;
import com.folioframe.domain.portfolio.repository.PortfolioRepository;
import com.folioframe.domain.talent.entity.TalentProfile;
import com.folioframe.domain.talent.repository.TalentProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class JobApplicationServiceImpl implements JobApplicationService {

    private final JobApplicationRepository jobApplicationRepository;
    private final JobPostingRepository jobPostingRepository;
    private final PortfolioRepository portfolioRepository;
    private final TalentProfileRepository talentProfileRepository;

    @Override
    @Transactional
    public JobApplicationCreateResDTO createApplication(Long memberId, JobApplicationCreateReqDTO request) {
        TalentProfile talentProfile = getTalentProfileByMemberId(memberId);

        JobPosting jobPosting = jobPostingRepository.findById(request.getJobPostingId())
                .orElseThrow(() -> new JobException(JobErrorCode.APP_RESOURCE_NOT_FOUND));

        Portfolio portfolio = portfolioRepository.findById(request.getPortfolioId())
                .orElseThrow(() -> new JobException(JobErrorCode.APP_RESOURCE_NOT_FOUND));

        if (jobApplicationRepository.existsByTalentProfileIdAndJobPostingId(talentProfile.getId(), jobPosting.getId())) {
            throw new JobException(JobErrorCode.DUPLICATE_APPLICATION);
        }

        JobApplication application = JobApplication.builder()
                .talentProfile(talentProfile)
                .jobPosting(jobPosting)
                .portfolio(portfolio)
                .message(request.getMessage())
                .build();

        JobApplication savedApplication = jobApplicationRepository.save(application);

        return new JobApplicationCreateResDTO(savedApplication.getId(), savedApplication.getStatus());
    }

    @Override
    @Transactional
    public JobApplicationStatusUpdateResDTO updateApplicationStatus(Long memberId, Long applicationId, JobApplicationStatusUpdateReqDTO request) {
        JobApplication application = jobApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new JobException(JobErrorCode.APPLICATION_NOT_FOUND));

        Long postingOwnerId = application.getJobPosting().getCompanyProfile().getMember().getId();
        if (!postingOwnerId.equals(memberId)) {
            throw new JobException(JobErrorCode.APP_FORBIDDEN_ACCESS);
        }

        if (request.status() == null) {
            throw new JobException(JobErrorCode.INVALID_STATUS_VALUE);
        }

        application.updateStatus(request.status());

        return new JobApplicationStatusUpdateResDTO(
                application.getId(),
                application.getStatus(),
                application.getUpdatedAt()
        );
    }

    @Override
    public JobApplicationResDTO getApplications(Long memberId, Pageable pageable) {
        TalentProfile talentProfile = getTalentProfileByMemberId(memberId);

        Page<JobApplication> applicationPage = jobApplicationRepository.findAllByTalentProfileId(talentProfile.getId(), pageable);

        List<JobApplicationResDTO.ApplicationDetail> details = applicationPage.getContent().stream()
                .map(app -> new JobApplicationResDTO.ApplicationDetail(
                        app.getId(),
                        app.getJobPosting().getCompanyProfile().getCompanyName(),
                        app.getJobPosting().getPositionName(),
                        app.getMessage(),
                        app.getStatus(),
                        app.getCreatedAt()
                ))
                .collect(Collectors.toList());

        JobApplicationResDTO.PageInfo pageInfo = new JobApplicationResDTO.PageInfo(
                applicationPage.getNumber(),
                applicationPage.getSize(),
                applicationPage.getTotalElements(),
                applicationPage.getTotalPages()
        );

        return new JobApplicationResDTO(details, pageInfo);
    }

    @Override
    public JobApplicationDetailResDTO getApplicationDetail(Long memberId, Long applicationId) {
        JobApplication application = jobApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new JobException(JobErrorCode.APPLICATION_NOT_FOUND));

        TalentProfile talentProfile = getTalentProfileByMemberId(memberId);

        // 지원자 본인 권한 검증
        if (!application.getTalentProfile().getId().equals(talentProfile.getId())) {
            throw new JobException(JobErrorCode.APP_FORBIDDEN_ACCESS);
        }

        return new JobApplicationDetailResDTO(
                application.getId(),
                application.getJobPosting().getId(),
                application.getPortfolio().getId(),
                application.getMessage(),
                application.getStatus(),
                application.getCreatedAt()
        );
    }

    private TalentProfile getTalentProfileByMemberId(Long memberId) {
        return talentProfileRepository.findByMemberId(memberId)
                .orElseThrow(() -> new JobException(JobErrorCode.APP_UNAUTHORIZED));
    }
}