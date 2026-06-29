package com.folioframe.domain.portfolio.controller;

import com.folioframe.domain.portfolio.dto.request.EducationReqDTO;
import com.folioframe.domain.portfolio.dto.response.EducationResDTO;
import com.folioframe.domain.portfolio.exception.code.PortfolioSuccessCode;
import com.folioframe.domain.portfolio.service.PortfolioEducationService;
import com.folioframe.global.apiPayload.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/portfolios/{portfolioId}/educations")
@RequiredArgsConstructor
public class PortfolioEducationController implements PortfolioEducationControllerDocs {

    private final PortfolioEducationService educationService;

    @Override
    @PostMapping
    public ResponseEntity<ApiResponse<EducationResDTO>> create(
            @PathVariable Long portfolioId,
            @RequestHeader("X-Member-Id") Long memberId,
            @Valid @RequestBody EducationReqDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.onSuccess(PortfolioSuccessCode.EDUCATION_CREATED,
                        educationService.create(portfolioId, memberId, request)));
    }

    @Override
    @GetMapping
    public ResponseEntity<ApiResponse<List<EducationResDTO>>> getList(
            @PathVariable Long portfolioId) {
        return ResponseEntity.ok(
                ApiResponse.onSuccess(PortfolioSuccessCode.EDUCATION_LIST_FOUND,
                        educationService.getList(portfolioId)));
    }

    @Override
    @PatchMapping("/{educationId}")
    public ResponseEntity<ApiResponse<EducationResDTO>> update(
            @PathVariable Long portfolioId,
            @PathVariable Long educationId,
            @RequestHeader("X-Member-Id") Long memberId,
            @Valid @RequestBody EducationReqDTO request) {
        return ResponseEntity.ok(
                ApiResponse.onSuccess(PortfolioSuccessCode.EDUCATION_UPDATED,
                        educationService.update(portfolioId, educationId, memberId, request)));
    }

    @Override
    @DeleteMapping("/{educationId}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable Long portfolioId,
            @PathVariable Long educationId,
            @RequestHeader("X-Member-Id") Long memberId) {
        educationService.delete(portfolioId, educationId, memberId);
        return ResponseEntity.ok(ApiResponse.onSuccess(PortfolioSuccessCode.EDUCATION_DELETED, null));
    }
}
