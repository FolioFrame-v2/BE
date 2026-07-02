package com.folioframe.domain.portfolio.controller;

import com.folioframe.domain.portfolio.dto.request.AiFieldChooseReqDTO;
import com.folioframe.domain.portfolio.dto.response.AiFieldResultDTO;
import com.folioframe.domain.portfolio.dto.response.PortfolioAiFeedbackResDTO;
import com.folioframe.domain.portfolio.dto.response.PortfolioAiFeedbackVersionResDTO;
import com.folioframe.domain.portfolio.exception.code.PortfolioSuccessCode;
import com.folioframe.domain.portfolio.service.PortfolioAiFeedbackService;
import com.folioframe.global.apiPayload.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @Override
    @GetMapping("/versions")
    public ResponseEntity<ApiResponse<List<PortfolioAiFeedbackVersionResDTO>>> getVersions(
            @PathVariable Long portfolioId,
            @RequestHeader("X-Member-Id") Long memberId) {
        return ResponseEntity.ok(
                ApiResponse.onSuccess(PortfolioSuccessCode.AI_FEEDBACK_VERSION_LIST_FOUND,
                        aiFeedbackService.getVersions(portfolioId, memberId)));
    }

    @Override
    @GetMapping("/original")
    public ResponseEntity<ApiResponse<PortfolioAiFeedbackResDTO>> getOriginal(
            @PathVariable Long portfolioId,
            @RequestHeader("X-Member-Id") Long memberId) {
        return ResponseEntity.ok(
                ApiResponse.onSuccess(PortfolioSuccessCode.AI_FEEDBACK_FOUND,
                        aiFeedbackService.getOriginal(portfolioId, memberId)));
    }

    @Override
    @GetMapping("/{version}")
    public ResponseEntity<ApiResponse<PortfolioAiFeedbackResDTO>> getByVersion(
            @PathVariable Long portfolioId,
            @PathVariable Integer version,
            @RequestHeader("X-Member-Id") Long memberId) {
        return ResponseEntity.ok(
                ApiResponse.onSuccess(PortfolioSuccessCode.AI_FEEDBACK_FOUND,
                        aiFeedbackService.getByVersion(portfolioId, version, memberId)));
    }

    @Override
    @PatchMapping("/fields/{aiFieldId}")
    public ResponseEntity<ApiResponse<AiFieldResultDTO>> chooseField(
            @PathVariable Long portfolioId,
            @PathVariable Long aiFieldId,
            @RequestHeader("X-Member-Id") Long memberId,
            @Valid @RequestBody AiFieldChooseReqDTO request) {
        return ResponseEntity.ok(
                ApiResponse.onSuccess(PortfolioSuccessCode.AI_FEEDBACK_FIELD_CHOSEN,
                        aiFeedbackService.chooseField(portfolioId, aiFieldId, memberId, request.chosen())));
    }

}
