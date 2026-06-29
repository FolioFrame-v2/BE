package com.folioframe.domain.portfolio.service;

import com.folioframe.domain.portfolio.dto.request.EducationReqDTO;
import com.folioframe.domain.portfolio.dto.response.EducationResDTO;
import com.folioframe.domain.portfolio.entity.Portfolio;
import com.folioframe.domain.portfolio.entity.PortfolioEducation;
import com.folioframe.domain.portfolio.exception.PortfolioException;
import com.folioframe.domain.portfolio.exception.code.PortfolioErrorCode;
import com.folioframe.domain.portfolio.repository.PortfolioEducationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PortfolioEducationService {

    private final PortfolioEducationRepository educationRepository;
    private final PortfolioService portfolioService;

    @Transactional
    public EducationResDTO create(Long portfolioId, Long memberId, EducationReqDTO request) {
        Portfolio portfolio = portfolioService.findPortfolio(portfolioId);
        portfolioService.validateOwnership(portfolio, memberId);

        PortfolioEducation education = PortfolioEducation.builder()
                .portfolio(portfolio)
                .schoolName(request.schoolName())
                .major(request.major())
                .degree(request.degree())
                .startedAt(request.startedAt())
                .endedAt(request.endedAt())
                .graduated(request.graduated())
                .build();

        return EducationResDTO.from(educationRepository.save(education));
    }

    @Transactional(readOnly = true)
    public List<EducationResDTO> getList(Long portfolioId) {
        Portfolio portfolio = portfolioService.findPortfolio(portfolioId);
        return educationRepository.findAllByPortfolioOrderByStartedAtDesc(portfolio)
                .stream()
                .map(EducationResDTO::from)
                .toList();
    }

    @Transactional
    public EducationResDTO update(Long portfolioId, Long educationId, Long memberId, EducationReqDTO request) {
        Portfolio portfolio = portfolioService.findPortfolio(portfolioId);
        portfolioService.validateOwnership(portfolio, memberId);

        PortfolioEducation education = findEducation(educationId);
        validateBelongsToPortfolio(education, portfolioId);

        education.update(
                request.schoolName(),
                request.major(),
                request.degree(),
                request.startedAt(),
                request.endedAt(),
                request.graduated()
        );

        return EducationResDTO.from(education);
    }

    @Transactional
    public void delete(Long portfolioId, Long educationId, Long memberId) {
        Portfolio portfolio = portfolioService.findPortfolio(portfolioId);
        portfolioService.validateOwnership(portfolio, memberId);

        PortfolioEducation education = findEducation(educationId);
        validateBelongsToPortfolio(education, portfolioId);

        educationRepository.delete(education);
    }

    private PortfolioEducation findEducation(Long educationId) {
        return educationRepository.findById(educationId)
                .orElseThrow(() -> new PortfolioException(PortfolioErrorCode.EDUCATION_NOT_FOUND));
    }

    private void validateBelongsToPortfolio(PortfolioEducation education, Long portfolioId) {
        if (!education.getPortfolio().getId().equals(portfolioId)) {
            throw new PortfolioException(PortfolioErrorCode.EDUCATION_NOT_IN_PORTFOLIO);
        }
    }
}
