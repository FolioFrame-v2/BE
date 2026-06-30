package com.folioframe.domain.portfolio.controller;

import com.folioframe.domain.portfolio.dto.request.CareerReqDTO;
import com.folioframe.domain.portfolio.dto.response.CareerResDTO;
import com.folioframe.domain.portfolio.exception.code.PortfolioSuccessCode;
import com.folioframe.domain.portfolio.service.PortfolioCareerService;
import com.folioframe.global.apiPayload.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/portfolios/{portfolioId}/careers")
@RequiredArgsConstructor
public class PortfolioCareerController implements PortfolioCareerControllerDocs {

    private final PortfolioCareerService careerService;

    @Override
    @PostMapping
    public ResponseEntity<ApiResponse<CareerResDTO>> create(
            @PathVariable Long portfolioId,
            @RequestHeader("X-Member-Id") Long memberId,
            @Valid @RequestBody CareerReqDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.onSuccess(PortfolioSuccessCode.CAREER_CREATED,
                        careerService.create(portfolioId, memberId, request)));
    }

    @Override
    @GetMapping
    public ResponseEntity<ApiResponse<List<CareerResDTO>>> getList(
            @PathVariable Long portfolioId) {
        return ResponseEntity.ok(
                ApiResponse.onSuccess(PortfolioSuccessCode.CAREER_LIST_FOUND,
                        careerService.getList(portfolioId)));
    }

    @Override
    @PatchMapping("/{careerId}")
    public ResponseEntity<ApiResponse<CareerResDTO>> update(
            @PathVariable Long portfolioId,
            @PathVariable Long careerId,
            @RequestHeader("X-Member-Id") Long memberId,
            @Valid @RequestBody CareerReqDTO request) {
        return ResponseEntity.ok(
                ApiResponse.onSuccess(PortfolioSuccessCode.CAREER_UPDATED,
                        careerService.update(portfolioId, careerId, memberId, request)));
    }

    @Override
    @DeleteMapping("/{careerId}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable Long portfolioId,
            @PathVariable Long careerId,
            @RequestHeader("X-Member-Id") Long memberId) {
        careerService.delete(portfolioId, careerId, memberId);
        return ResponseEntity.ok(ApiResponse.onSuccess(PortfolioSuccessCode.CAREER_DELETED, null));
    }
}
