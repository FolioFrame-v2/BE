package com.folioframe.domain.portfolio.controller;

import com.folioframe.domain.portfolio.dto.request.PortfolioCreateReqDTO;
import com.folioframe.domain.portfolio.dto.request.PortfolioUpdateReqDTO;
import com.folioframe.domain.portfolio.dto.request.PortfolioVisibilityReqDTO;
import com.folioframe.domain.portfolio.dto.request.TechstackIdsReqDTO;
import com.folioframe.domain.portfolio.dto.response.PortfolioDetailResDTO;
import com.folioframe.domain.portfolio.dto.response.PortfolioJobCategoryResDTO;
import com.folioframe.domain.portfolio.dto.response.PortfolioMyListResDTO;
import com.folioframe.domain.portfolio.dto.response.PortfolioPublicListResDTO;
import com.folioframe.domain.portfolio.dto.response.PortfolioResDTO;
import com.folioframe.domain.common.dto.response.TechstackResDTO;
import com.folioframe.domain.common.enums.CareerLevel;
import com.folioframe.domain.common.enums.PortfolioJobCategory;
import com.folioframe.domain.portfolio.exception.code.PortfolioSuccessCode;
import com.folioframe.domain.portfolio.service.PortfolioService;
import com.folioframe.domain.portfolio.enums.PortfolioSortType;
import com.folioframe.global.apiPayload.ApiResponse;
import com.folioframe.global.auth.CurrentMemberId;
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
            @CurrentMemberId Long memberId,
            @Valid @RequestBody PortfolioCreateReqDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.onSuccess(PortfolioSuccessCode.PORTFOLIO_CREATED,
                        portfolioService.create(memberId, request)));
    }

    @Override
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<PortfolioMyListResDTO>>> getList(
            @CurrentMemberId Long memberId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "4") Integer size) {
        return ResponseEntity.ok(
                ApiResponse.onSuccess(PortfolioSuccessCode.PORTFOLIO_LIST_FOUND,
                        portfolioService.getList(memberId, PageRequest.of(page, size))));
    }

    @Override
    @GetMapping("/job-categories")
    public ResponseEntity<ApiResponse<List<PortfolioJobCategoryResDTO>>> getJobCategories() {
        return ResponseEntity.ok(
                ApiResponse.onSuccess(PortfolioSuccessCode.PORTFOLIO_JOB_CATEGORY_LIST_FOUND,
                        portfolioService.getJobCategories()));
    }

    @Override
    @GetMapping("/public")
    public ResponseEntity<ApiResponse<PageResponse<PortfolioPublicListResDTO>>> getPublicList(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long regionId,
            @RequestParam(required = false) PortfolioSortType sort,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "9") Integer size,
            @RequestParam(required = false) CareerLevel career,
            @RequestParam(required = false) PortfolioJobCategory category,
            @CurrentMemberId(required = false) Long memberId) {
        return ResponseEntity.ok(
                ApiResponse.onSuccess(PortfolioSuccessCode.PORTFOLIO_LIST_FOUND,
                        portfolioService.getPublicList(keyword, regionId, sort, PageRequest.of(page, size), memberId, career, category)));
    }

    @Override
    @GetMapping("/{portfolioId}")
    public ResponseEntity<ApiResponse<PortfolioDetailResDTO>> getDetail(
            @PathVariable Long portfolioId,
            @CurrentMemberId(required = false) Long memberId) {
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
            @CurrentMemberId Long memberId,
            @Valid @RequestBody PortfolioUpdateReqDTO request) {
        return ResponseEntity.ok(
                ApiResponse.onSuccess(PortfolioSuccessCode.PORTFOLIO_UPDATED,
                        portfolioService.update(portfolioId, memberId, request)));
    }

    @Override
    @PostMapping("/{portfolioId}/save")
    public ResponseEntity<ApiResponse<PortfolioResDTO>> confirmSave(
            @PathVariable Long portfolioId,
            @CurrentMemberId Long memberId) {
        return ResponseEntity.ok(
                ApiResponse.onSuccess(PortfolioSuccessCode.PORTFOLIO_SAVE_CONFIRMED,
                        portfolioService.confirmSave(portfolioId, memberId)));
    }

    @Override
    @PatchMapping("/{portfolioId}/visibility")
    public ResponseEntity<ApiResponse<PortfolioResDTO>> changeVisibility(
            @PathVariable Long portfolioId,
            @CurrentMemberId Long memberId,
            @Valid @RequestBody PortfolioVisibilityReqDTO request) {
        return ResponseEntity.ok(
                ApiResponse.onSuccess(PortfolioSuccessCode.PORTFOLIO_VISIBILITY_CHANGED,
                        portfolioService.changeVisibility(portfolioId, memberId, request)));
    }

    @Override
    @DeleteMapping("/{portfolioId}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable Long portfolioId,
            @CurrentMemberId Long memberId) {
        portfolioService.delete(portfolioId, memberId);
        return ResponseEntity.ok(ApiResponse.onSuccess(PortfolioSuccessCode.PORTFOLIO_DELETED, null));
    }

    @Override
    @PatchMapping("/{portfolioId}/techstacks")
    public ResponseEntity<ApiResponse<List<TechstackResDTO>>> updateTechstacks(
            @PathVariable Long portfolioId,
            @CurrentMemberId Long memberId,
            @Valid @RequestBody TechstackIdsReqDTO request) {
        return ResponseEntity.ok(
                ApiResponse.onSuccess(PortfolioSuccessCode.PORTFOLIO_TECHSTACKS_UPDATED,
                        portfolioService.updateTechstacks(portfolioId, memberId, request.techstackIds())));
    }
}
