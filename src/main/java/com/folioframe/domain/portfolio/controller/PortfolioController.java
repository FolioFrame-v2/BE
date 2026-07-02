package com.folioframe.domain.portfolio.controller;

import com.folioframe.domain.portfolio.dto.request.PortfolioCreateReqDTO;
import com.folioframe.domain.portfolio.dto.request.PortfolioUpdateReqDTO;
import com.folioframe.domain.portfolio.dto.request.PortfolioVisibilityReqDTO;
import com.folioframe.domain.portfolio.dto.request.TechstackIdsReqDTO;
import com.folioframe.domain.portfolio.dto.response.PortfolioDetailResDTO;
import com.folioframe.domain.portfolio.dto.response.PortfolioResDTO;
import com.folioframe.domain.portfolio.dto.response.PortfolioSummaryResDTO;
import com.folioframe.domain.common.dto.response.TechstackResDTO;
import com.folioframe.domain.portfolio.exception.code.PortfolioSuccessCode;
import com.folioframe.domain.portfolio.service.PortfolioService;
import com.folioframe.domain.portfolio.enums.PortfolioSortType;
import com.folioframe.global.apiPayload.ApiResponse;
import com.folioframe.global.dto.PageRequest;
import com.folioframe.global.dto.PageResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/portfolios")
@RequiredArgsConstructor
public class PortfolioController implements PortfolioControllerDocs {

    private final PortfolioService portfolioService;

    @Override
    @PostMapping
    public ResponseEntity<ApiResponse<PortfolioResDTO>> create(
            @RequestHeader("X-Member-Id") Long memberId,
            @Valid @RequestBody PortfolioCreateReqDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.onSuccess(PortfolioSuccessCode.PORTFOLIO_CREATED,
                        portfolioService.create(memberId, request)));
    }

    @Override
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<PortfolioSummaryResDTO>>> getList(
            @RequestHeader("X-Member-Id") Long memberId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "4") Integer size) {
        return ResponseEntity.ok(
                ApiResponse.onSuccess(PortfolioSuccessCode.PORTFOLIO_LIST_FOUND,
                        portfolioService.getList(memberId, PageRequest.of(page, size))));
    }

    @Override
    @GetMapping("/public")
    public ResponseEntity<ApiResponse<PageResponse<PortfolioSummaryResDTO>>> getPublicList(
            @RequestParam(required = false) PortfolioSortType sort,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "9") Integer size,
            @RequestHeader(value = "X-Member-Id", required = false) Long memberId) {
        return ResponseEntity.ok(
                ApiResponse.onSuccess(PortfolioSuccessCode.PORTFOLIO_LIST_FOUND,
                        portfolioService.getPublicList(sort, PageRequest.of(page, size), memberId)));
    }

    @Override
    @GetMapping("/{portfolioId}")
    public ResponseEntity<ApiResponse<PortfolioDetailResDTO>> getDetail(
            @PathVariable Long portfolioId,
            @RequestHeader("X-Member-Id") Long memberId) {
        return ResponseEntity.ok(
                ApiResponse.onSuccess(PortfolioSuccessCode.PORTFOLIO_DETAIL_FOUND,
                        portfolioService.getDetail(portfolioId, memberId)));
    }

    @Override
    @GetMapping("/slug/{publicSlug}")
    public ResponseEntity<ApiResponse<PortfolioDetailResDTO>> getBySlug(
            @PathVariable String publicSlug) {
        return ResponseEntity.ok(
                ApiResponse.onSuccess(PortfolioSuccessCode.PORTFOLIO_DETAIL_FOUND,
                        portfolioService.getBySlug(publicSlug)));
    }

    @Override
    @PatchMapping("/{portfolioId}")
    public ResponseEntity<ApiResponse<PortfolioResDTO>> update(
            @PathVariable Long portfolioId,
            @RequestHeader("X-Member-Id") Long memberId,
            @Valid @RequestBody PortfolioUpdateReqDTO request) {
        return ResponseEntity.ok(
                ApiResponse.onSuccess(PortfolioSuccessCode.PORTFOLIO_UPDATED,
                        portfolioService.update(portfolioId, memberId, request)));
    }

    @Override
    @PatchMapping("/{portfolioId}/visibility")
    public ResponseEntity<ApiResponse<PortfolioResDTO>> changeVisibility(
            @PathVariable Long portfolioId,
            @RequestHeader("X-Member-Id") Long memberId,
            @Valid @RequestBody PortfolioVisibilityReqDTO request) {
        return ResponseEntity.ok(
                ApiResponse.onSuccess(PortfolioSuccessCode.PORTFOLIO_VISIBILITY_CHANGED,
                        portfolioService.changeVisibility(portfolioId, memberId, request)));
    }

    @Override
    @PatchMapping("/{portfolioId}/publish")
    public ResponseEntity<ApiResponse<PortfolioResDTO>> publish(
            @PathVariable Long portfolioId,
            @RequestHeader("X-Member-Id") Long memberId) {
        return ResponseEntity.ok(
                ApiResponse.onSuccess(PortfolioSuccessCode.PORTFOLIO_PUBLISHED,
                        portfolioService.publish(portfolioId, memberId)));
    }

    @Override
    @DeleteMapping("/{portfolioId}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable Long portfolioId,
            @RequestHeader("X-Member-Id") Long memberId) {
        portfolioService.delete(portfolioId, memberId);
        return ResponseEntity.ok(ApiResponse.onSuccess(PortfolioSuccessCode.PORTFOLIO_DELETED, null));
    }

    @Override
    @PatchMapping("/{portfolioId}/techstacks")
    public ResponseEntity<ApiResponse<List<TechstackResDTO>>> updateTechstacks(
            @PathVariable Long portfolioId,
            @RequestHeader("X-Member-Id") Long memberId,
            @Valid @RequestBody TechstackIdsReqDTO request) {
        return ResponseEntity.ok(
                ApiResponse.onSuccess(PortfolioSuccessCode.PORTFOLIO_TECHSTACKS_UPDATED,
                        portfolioService.updateTechstacks(portfolioId, memberId, request.techstackIds())));
    }
}
