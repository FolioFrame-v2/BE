package com.folioframe.domain.portfolio.ai.controller;

import com.folioframe.domain.portfolio.ai.dto.request.AiFeedbackRenameReqDTO;
import com.folioframe.domain.portfolio.ai.dto.request.AiFieldChooseReqDTO;
import com.folioframe.domain.portfolio.ai.dto.request.AiFieldEditReqDTO;
import com.folioframe.domain.portfolio.ai.dto.response.AiFieldResultDTO;
import com.folioframe.domain.portfolio.ai.dto.response.PortfolioAiFeedbackResDTO;
import com.folioframe.domain.portfolio.ai.dto.response.PortfolioAiFeedbackVersionResDTO;
import com.folioframe.domain.portfolio.ai.service.PortfolioAiFeedbackService;
import com.folioframe.domain.portfolio.exception.code.PortfolioSuccessCode;
import com.folioframe.global.apiPayload.ApiResponse;
import com.folioframe.global.auth.CurrentMemberId;
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
            @RequestParam(required = false) Integer sourceVersion,
            @RequestParam(required = false) Integer sourceSubVersion,
            @CurrentMemberId Long memberId) {
        return ResponseEntity.ok(
                ApiResponse.onSuccess(PortfolioSuccessCode.AI_FEEDBACK_GENERATED,
                        aiFeedbackService.generate(portfolioId, sourceVersion, sourceSubVersion, memberId)));
    }

    @Override
    @GetMapping("/latest")
    public ResponseEntity<ApiResponse<PortfolioAiFeedbackResDTO>> getLatest(
            @PathVariable Long portfolioId,
            @CurrentMemberId Long memberId) {
        return ResponseEntity.ok(
                ApiResponse.onSuccess(PortfolioSuccessCode.AI_FEEDBACK_FOUND,
                        aiFeedbackService.getLatest(portfolioId, memberId)));
    }

    @Override
    @GetMapping("/versions")
    public ResponseEntity<ApiResponse<List<PortfolioAiFeedbackVersionResDTO>>> getVersions(
            @PathVariable Long portfolioId,
            @CurrentMemberId Long memberId) {
        return ResponseEntity.ok(
                ApiResponse.onSuccess(PortfolioSuccessCode.AI_FEEDBACK_VERSION_LIST_FOUND,
                        aiFeedbackService.getVersions(portfolioId, memberId)));
    }

    @Override
    @GetMapping("/original")
    public ResponseEntity<ApiResponse<PortfolioAiFeedbackResDTO>> getOriginal(
            @PathVariable Long portfolioId,
            @CurrentMemberId Long memberId) {
        return ResponseEntity.ok(
                ApiResponse.onSuccess(PortfolioSuccessCode.AI_FEEDBACK_FOUND,
                        aiFeedbackService.getOriginal(portfolioId, memberId)));
    }

    @Override
    @GetMapping("/{version}")
    public ResponseEntity<ApiResponse<PortfolioAiFeedbackResDTO>> getByVersion(
            @PathVariable Long portfolioId,
            @PathVariable Integer version,
            @RequestParam(required = false) Integer subVersion,
            @CurrentMemberId Long memberId) {
        return ResponseEntity.ok(
                ApiResponse.onSuccess(PortfolioSuccessCode.AI_FEEDBACK_FOUND,
                        aiFeedbackService.getByVersion(portfolioId, version, subVersion, memberId)));
    }

    @Override
    @PostMapping("/{version}/save")
    public ResponseEntity<ApiResponse<PortfolioAiFeedbackResDTO>> saveVersion(
            @PathVariable Long portfolioId,
            @PathVariable Integer version,
            @RequestParam(required = false) Integer subVersion,
            @CurrentMemberId Long memberId) {
        return ResponseEntity.ok(
                ApiResponse.onSuccess(PortfolioSuccessCode.AI_FEEDBACK_VERSION_SAVED,
                        aiFeedbackService.saveVersion(portfolioId, version, subVersion, memberId)));
    }

    @Override
    @PostMapping("/{version}/revisions")
    public ResponseEntity<ApiResponse<PortfolioAiFeedbackResDTO>> createRevision(
            @PathVariable Long portfolioId,
            @PathVariable Integer version,
            @RequestParam(required = false) Integer subVersion,
            @CurrentMemberId Long memberId) {
        return ResponseEntity.ok(
                ApiResponse.onSuccess(PortfolioSuccessCode.AI_FEEDBACK_REVISION_CREATED,
                        aiFeedbackService.createRevision(portfolioId, version, subVersion, memberId)));
    }

    @Override
    @PostMapping("/{version}/publish")
    public ResponseEntity<ApiResponse<PortfolioAiFeedbackResDTO>> publishVersion(
            @PathVariable Long portfolioId,
            @PathVariable Integer version,
            @RequestParam(required = false) Integer subVersion,
            @CurrentMemberId Long memberId) {
        return ResponseEntity.ok(
                ApiResponse.onSuccess(PortfolioSuccessCode.AI_FEEDBACK_VERSION_PUBLISHED,
                        aiFeedbackService.publishVersion(portfolioId, version, subVersion, memberId)));
    }

    @Override
    @PatchMapping("/{version}/label")
    public ResponseEntity<ApiResponse<PortfolioAiFeedbackResDTO>> renameVersion(
            @PathVariable Long portfolioId,
            @PathVariable Integer version,
            @RequestParam(required = false) Integer subVersion,
            @CurrentMemberId Long memberId,
            @Valid @RequestBody AiFeedbackRenameReqDTO request) {
        return ResponseEntity.ok(
                ApiResponse.onSuccess(PortfolioSuccessCode.AI_FEEDBACK_VERSION_RENAMED,
                        aiFeedbackService.renameVersion(portfolioId, version, subVersion, memberId, request.label())));
    }

    @Override
    @DeleteMapping("/{version}")
    public ResponseEntity<ApiResponse<Void>> deleteVersion(
            @PathVariable Long portfolioId,
            @PathVariable Integer version,
            @RequestParam(required = false) Integer subVersion,
            @CurrentMemberId Long memberId) {
        aiFeedbackService.deleteVersion(portfolioId, version, subVersion, memberId);
        return ResponseEntity.ok(ApiResponse.onSuccess(PortfolioSuccessCode.AI_FEEDBACK_VERSION_DELETED, null));
    }

    @Override
    @PatchMapping("/fields/{aiFieldId}")
    public ResponseEntity<ApiResponse<AiFieldResultDTO>> chooseField(
            @PathVariable Long portfolioId,
            @PathVariable Long aiFieldId,
            @CurrentMemberId Long memberId,
            @Valid @RequestBody AiFieldChooseReqDTO request) {
        return ResponseEntity.ok(
                ApiResponse.onSuccess(PortfolioSuccessCode.AI_FEEDBACK_FIELD_CHOSEN,
                        aiFeedbackService.chooseField(portfolioId, aiFieldId, memberId, request.chosen())));
    }

    @Override
    @PatchMapping("/fields/{aiFieldId}/content")
    public ResponseEntity<ApiResponse<AiFieldResultDTO>> editField(
            @PathVariable Long portfolioId,
            @PathVariable Long aiFieldId,
            @CurrentMemberId Long memberId,
            @Valid @RequestBody AiFieldEditReqDTO request) {
        return ResponseEntity.ok(
                ApiResponse.onSuccess(PortfolioSuccessCode.AI_FEEDBACK_FIELD_EDITED,
                        aiFeedbackService.editField(portfolioId, aiFieldId, memberId, request.content())));
    }

}
