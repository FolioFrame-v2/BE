package com.folioframe.domain.portfolio.service;

import com.folioframe.domain.portfolio.dto.request.CareerReqDTO;
import com.folioframe.domain.portfolio.dto.response.CareerResDTO;
import com.folioframe.domain.portfolio.entity.Portfolio;
import com.folioframe.domain.portfolio.entity.PortfolioCareer;
import com.folioframe.domain.portfolio.exception.PortfolioException;
import com.folioframe.domain.portfolio.exception.code.PortfolioErrorCode;
import com.folioframe.domain.portfolio.repository.PortfolioCareerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PortfolioCareerService {

    private final PortfolioCareerRepository careerRepository;
    private final PortfolioService portfolioService;

    @Transactional
    public CareerResDTO create(Long portfolioId, Long memberId, CareerReqDTO request) {
        Portfolio portfolio = portfolioService.findPortfolio(portfolioId);
        portfolioService.validateOwnership(portfolio, memberId);

        PortfolioCareer career = PortfolioCareer.builder()
                .portfolio(portfolio)
                .companyName(request.companyName())
                .position(request.position())
                .description(request.description())
                .startedAt(request.startedAt())
                .endedAt(request.endedAt())
                .current(request.current())
                .build();

        return CareerResDTO.from(careerRepository.save(career));
    }

    @Transactional(readOnly = true)
    public List<CareerResDTO> getList(Long portfolioId) {
        Portfolio portfolio = portfolioService.findPortfolio(portfolioId);
        return careerRepository.findAllByPortfolioOrderByStartedAtDesc(portfolio)
                .stream()
                .map(CareerResDTO::from)
                .toList();
    }

    @Transactional
    public CareerResDTO update(Long portfolioId, Long careerId, Long memberId, CareerReqDTO request) {
        Portfolio portfolio = portfolioService.findPortfolio(portfolioId);
        portfolioService.validateOwnership(portfolio, memberId);

        PortfolioCareer career = findCareer(careerId);
        validateBelongsToPortfolio(career, portfolioId);

        career.update(
                request.companyName(),
                request.position(),
                request.description(),
                request.startedAt(),
                request.endedAt(),
                request.current()
        );

        return CareerResDTO.from(career);
    }

    @Transactional
    public void delete(Long portfolioId, Long careerId, Long memberId) {
        Portfolio portfolio = portfolioService.findPortfolio(portfolioId);
        portfolioService.validateOwnership(portfolio, memberId);

        PortfolioCareer career = findCareer(careerId);
        validateBelongsToPortfolio(career, portfolioId);

        careerRepository.delete(career);
    }

    private PortfolioCareer findCareer(Long careerId) {
        return careerRepository.findById(careerId)
                .orElseThrow(() -> new PortfolioException(PortfolioErrorCode.CAREER_NOT_FOUND));
    }

    private void validateBelongsToPortfolio(PortfolioCareer career, Long portfolioId) {
        if (!career.getPortfolio().getId().equals(portfolioId)) {
            throw new PortfolioException(PortfolioErrorCode.CAREER_NOT_IN_PORTFOLIO);
        }
    }
}
