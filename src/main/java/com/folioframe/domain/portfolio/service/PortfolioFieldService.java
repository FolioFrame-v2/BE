package com.folioframe.domain.portfolio.service;

import com.folioframe.domain.portfolio.dto.request.PortfolioFieldUpdateReqDTO;
import com.folioframe.domain.portfolio.dto.response.PortfolioFieldResDTO;
import com.folioframe.domain.portfolio.entity.Portfolio;
import com.folioframe.domain.portfolio.entity.PortfolioField;
import com.folioframe.domain.portfolio.exception.PortfolioException;
import com.folioframe.domain.portfolio.exception.code.PortfolioErrorCode;
import com.folioframe.domain.portfolio.repository.PortfolioFieldRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PortfolioFieldService {

    private final PortfolioFieldRepository fieldRepository;
    private final PortfolioService portfolioService;

    @Transactional(readOnly = true)
    public List<PortfolioFieldResDTO> getList(Long portfolioId) {
        Portfolio portfolio = portfolioService.findPortfolio(portfolioId);
        return fieldRepository.findAllByPortfolioOrderByDisplayOrder(portfolio)
                .stream()
                .map(PortfolioFieldResDTO::from)
                .toList();
    }

    @Transactional
    public PortfolioFieldResDTO updateContent(Long portfolioId, Long fieldId, Long memberId,
                                              PortfolioFieldUpdateReqDTO request) {
        Portfolio portfolio = portfolioService.findPortfolio(portfolioId);
        portfolioService.validateOwnership(portfolio, memberId);

        PortfolioField field = findField(fieldId);
        validateBelongsToPortfolio(field, portfolioId);

        field.updateContent(request.content());
        portfolio.markSaved();

        return PortfolioFieldResDTO.from(field);
    }

    private PortfolioField findField(Long fieldId) {
        return fieldRepository.findById(fieldId)
                .orElseThrow(() -> new PortfolioException(PortfolioErrorCode.PORTFOLIO_FIELD_NOT_FOUND));
    }

    private void validateBelongsToPortfolio(PortfolioField field, Long portfolioId) {
        if (!field.getPortfolio().getId().equals(portfolioId)) {
            throw new PortfolioException(PortfolioErrorCode.PORTFOLIO_FIELD_NOT_IN_PORTFOLIO);
        }
    }
}
