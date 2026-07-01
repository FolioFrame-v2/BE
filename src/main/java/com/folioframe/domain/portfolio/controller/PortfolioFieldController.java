package com.folioframe.domain.portfolio.controller;

import com.folioframe.domain.portfolio.dto.request.PortfolioFieldUpdateReqDTO;
import com.folioframe.domain.portfolio.dto.response.PortfolioFieldResDTO;
import com.folioframe.domain.portfolio.exception.code.PortfolioSuccessCode;
import com.folioframe.domain.portfolio.service.PortfolioFieldService;
import com.folioframe.global.apiPayload.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/portfolios/{portfolioId}/fields")
@RequiredArgsConstructor
public class PortfolioFieldController implements PortfolioFieldControllerDocs {

    private final PortfolioFieldService fieldService;

    @Override
    @GetMapping
    public ResponseEntity<ApiResponse<List<PortfolioFieldResDTO>>> getList(
            @PathVariable Long portfolioId) {
        return ResponseEntity.ok(
                ApiResponse.onSuccess(PortfolioSuccessCode.PORTFOLIO_FIELD_LIST_FOUND,
                        fieldService.getList(portfolioId)));
    }

    @Override
    @PatchMapping("/{fieldId}")
    public ResponseEntity<ApiResponse<PortfolioFieldResDTO>> updateContent(
            @PathVariable Long portfolioId,
            @PathVariable Long fieldId,
            @RequestHeader("X-Member-Id") Long memberId,
            @Valid @RequestBody PortfolioFieldUpdateReqDTO request) {
        return ResponseEntity.ok(
                ApiResponse.onSuccess(PortfolioSuccessCode.PORTFOLIO_FIELD_UPDATED,
                        fieldService.updateContent(portfolioId, fieldId, memberId, request)));
    }
}
