package com.folioframe.domain.portfolio.controller;

import com.folioframe.domain.portfolio.exception.code.PortfolioSuccessCode;
import com.folioframe.domain.portfolio.service.PortfolioBookmarkService;
import com.folioframe.global.apiPayload.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/portfolios/{portfolioId}/bookmarks")
@RequiredArgsConstructor
public class PortfolioBookmarkController implements PortfolioBookmarkControllerDocs {

    private final PortfolioBookmarkService bookmarkService;

    @Override
    @PostMapping
    public ResponseEntity<ApiResponse<Void>> bookmark(
            @PathVariable Long portfolioId,
            @RequestHeader("X-Member-Id") Long memberId) {
        bookmarkService.bookmark(portfolioId, memberId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.onSuccess(PortfolioSuccessCode.BOOKMARK_CREATED, null));
    }

    @Override
    @DeleteMapping
    public ResponseEntity<ApiResponse<Void>> cancelBookmark(
            @PathVariable Long portfolioId,
            @RequestHeader("X-Member-Id") Long memberId) {
        bookmarkService.cancelBookmark(portfolioId, memberId);
        return ResponseEntity.ok(ApiResponse.onSuccess(PortfolioSuccessCode.BOOKMARK_DELETED, null));
    }
}
