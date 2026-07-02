package com.folioframe.domain.portfolio.controller;

import com.folioframe.domain.portfolio.dto.response.PortfolioAiFeedbackResDTO;
import com.folioframe.domain.portfolio.exception.code.PortfolioSuccessCode;
import com.folioframe.domain.portfolio.service.PortfolioAiFeedbackService;
import com.folioframe.global.apiPayload.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/portfolios/{portfolioId}/ai-feedback")
@RequiredArgsConstructor
public class PortfolioAiFeedbackController implements PortfolioAiFeedbackControllerDocs {

    private final PortfolioAiFeedbackService aiFeedbackService;

    @Override
    @PostMapping
    public ResponseEntity<ApiResponse<PortfolioAiFeedbackResDTO>> generate(
            @PathVariable Long portfolioId,
            @RequestHeader("X-Member-Id") Long memberId) {
        return ResponseEntity.ok(
                ApiResponse.onSuccess(PortfolioSuccessCode.AI_FEEDBACK_GENERATED,
                        aiFeedbackService.generate(portfolioId, memberId)));
    }

    @Override
    @GetMapping("/latest")
    public ResponseEntity<ApiResponse<PortfolioAiFeedbackResDTO>> getLatest(
            @PathVariable Long portfolioId,
            @RequestHeader("X-Member-Id") Long memberId) {
        return ResponseEntity.ok(
                ApiResponse.onSuccess(PortfolioSuccessCode.AI_FEEDBACK_FOUND,
                        aiFeedbackService.getLatest(portfolioId, memberId)));
    }
}
