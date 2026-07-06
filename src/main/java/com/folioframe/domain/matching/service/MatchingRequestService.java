package com.folioframe.domain.matching.service;

import com.folioframe.domain.company.entity.CompanyProfile;
import com.folioframe.domain.company.repository.CompanyProfileRepository;
import com.folioframe.domain.job.entity.JobPosting;
import com.folioframe.domain.job.repository.JobPostingRepository;
import com.folioframe.domain.matching.dto.request.MatchingRequestCreateReqDTO;
import com.folioframe.domain.matching.dto.request.MatchingStatusUpdateReqDTO;
import com.folioframe.domain.matching.dto.response.MatchingRequestResDTO;
import com.folioframe.domain.matching.entity.MatchingRequest;
import com.folioframe.domain.matching.enums.MatchingStatus;
import com.folioframe.domain.matching.exception.code.MatchingErrorCode;
import com.folioframe.domain.matching.repository.MatchingRequestRepository;
import com.folioframe.domain.member.enums.NotificationType;
import com.folioframe.domain.portfolio.entity.Portfolio;
import com.folioframe.domain.portfolio.repository.PortfolioRepository;
import com.folioframe.global.apiPayload.exception.GeneralException;
import com.folioframe.domain.member.event.NotificationEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class MatchingRequestService {

    private final MatchingRequestRepository matchingRequestRepository;
    private final CompanyProfileRepository companyProfileRepository;
    private final PortfolioRepository portfolioRepository;
    private final JobPostingRepository jobPostingRepository;
    private final ApplicationEventPublisher eventPublisher;

    public MatchingRequestResDTO create(MatchingRequestCreateReqDTO dto) {

        CompanyProfile company = companyProfileRepository.findById(dto.getCompanyProfileId())
                .orElseThrow(() -> new GeneralException(MatchingErrorCode.FORBIDDEN_ACCESS));

        Portfolio portfolio = portfolioRepository.findById(dto.getPortfolioId())
                .orElseThrow(() -> new GeneralException(MatchingErrorCode.MATCHING_NOT_FOUND));

        JobPosting jobPosting = null;
        if (dto.getJobPostingId() != null) {
            jobPosting = jobPostingRepository.findById(dto.getJobPostingId())
                    .orElseThrow(() -> new GeneralException(MatchingErrorCode.JOB_POSTING_NOT_FOUND));
        }

        boolean exists = matchingRequestRepository
                .existsByCompanyProfileAndPortfolio(company, portfolio);

        if (exists) {
            throw new GeneralException(MatchingErrorCode.ALREADY_PROPOSED);
        }

        MatchingRequest request = MatchingRequest.builder()
                .companyProfile(company)
                .portfolio(portfolio)
                .talentProfile(portfolio.getTalentProfile())
                .jobPosting(jobPosting)
                .message(dto.getMessage())
                .status(MatchingStatus.PROPOSED)
                .build();

        MatchingRequest saved = matchingRequestRepository.save(request);

        Long talentMemberId = portfolio.getTalentProfile().getMember().getId();
        eventPublisher.publishEvent(new NotificationEvent(
                talentMemberId,
                NotificationType.COMPANY_PROPOSAL,
                "기업 매칭 제안이 도착했습니다.",
                company.getCompanyName() + "에서 매칭을 제안했습니다.",
                "/matchings/" + saved.getId() // TODO: 프론트엔드 라우팅 URL에 맞게 수정 필요
        ));

        return MatchingRequestResDTO.builder()
                .matchingRequestId(saved.getId())
                .status(saved.getStatus())
                .build();
    }

    public MatchingRequestResDTO updateStatus(Long id, MatchingStatusUpdateReqDTO dto) {

        MatchingRequest request = matchingRequestRepository.findById(id)
                .orElseThrow(() -> new GeneralException(MatchingErrorCode.MATCHING_NOT_FOUND));

        if (request.getStatus() == dto.getStatus()) {
            throw new GeneralException(MatchingErrorCode.INVALID_MATCHING_STATUS);
        }

        request.changeStatus(dto.getStatus());

        Long companyMemberId = request.getCompanyProfile().getMember().getId();
        eventPublisher.publishEvent(new NotificationEvent(
                companyMemberId,
                NotificationType.APPLICATION_RESULT,
                "매칭 제안 응답",
                request.getTalentProfile().getName() + "님이 매칭 제안을 " + dto.getStatus().name() + "했습니다.",
                "/matchings/" + request.getId() // TODO: 프론트엔드 라우팅 URL에 맞게 수정 필요
        ));

        return MatchingRequestResDTO.builder()
                .matchingRequestId(request.getId())
                .status(request.getStatus())
                .build();
    }
}