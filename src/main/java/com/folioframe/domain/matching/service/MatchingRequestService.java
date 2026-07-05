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
import com.folioframe.domain.matching.exception.code.MatchingSuccessCode;
import com.folioframe.domain.matching.repository.MatchingRequestRepository;
import com.folioframe.domain.portfolio.entity.Portfolio;
import com.folioframe.domain.portfolio.repository.PortfolioRepository;
import com.folioframe.global.apiPayload.exception.GeneralException;
import lombok.RequiredArgsConstructor;
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

        return MatchingRequestResDTO.builder()
                .matchingRequestId(request.getId())
                .status(request.getStatus())
                .build();
    }
}