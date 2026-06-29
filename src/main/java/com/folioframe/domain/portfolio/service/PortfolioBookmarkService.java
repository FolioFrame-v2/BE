package com.folioframe.domain.portfolio.service;

import com.folioframe.domain.portfolio.entity.Portfolio;
import com.folioframe.domain.portfolio.entity.PortfolioBookmark;
import com.folioframe.domain.portfolio.exception.PortfolioException;
import com.folioframe.domain.portfolio.exception.code.PortfolioErrorCode;
import com.folioframe.domain.portfolio.repository.PortfolioBookmarkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PortfolioBookmarkService {

    private final PortfolioBookmarkRepository bookmarkRepository;
    private final PortfolioService portfolioService;

    @Transactional
    public void bookmark(Long portfolioId, Long memberId) {
        Portfolio portfolio = portfolioService.findPortfolio(portfolioId);

        if (bookmarkRepository.existsByMemberIdAndPortfolio(memberId, portfolio)) {
            throw new PortfolioException(PortfolioErrorCode.BOOKMARK_ALREADY_EXISTS);
        }

        PortfolioBookmark bookmark = PortfolioBookmark.builder()
                .portfolio(portfolio)
                .memberId(memberId)
                .build();

        bookmarkRepository.save(bookmark);
        portfolio.increaseBookmarkCount();
    }

    @Transactional
    public void cancelBookmark(Long portfolioId, Long memberId) {
        Portfolio portfolio = portfolioService.findPortfolio(portfolioId);

        PortfolioBookmark bookmark = bookmarkRepository.findByMemberIdAndPortfolio(memberId, portfolio)
                .orElseThrow(() -> new PortfolioException(PortfolioErrorCode.BOOKMARK_NOT_FOUND));

        bookmarkRepository.delete(bookmark);
        portfolio.decreaseBookmarkCount();
    }
}
