package com.folioframe.domain.portfolio.controller;

import com.folioframe.domain.portfolio.dto.request.CertificateReqDTO;
import com.folioframe.domain.portfolio.dto.response.CertificateResDTO;
import com.folioframe.domain.portfolio.exception.code.PortfolioSuccessCode;
import com.folioframe.domain.portfolio.service.PortfolioCertificateService;
import com.folioframe.global.apiPayload.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/portfolios/{portfolioId}/certificates")
@RequiredArgsConstructor
public class PortfolioCertificateController implements PortfolioCertificateControllerDocs {

    private final PortfolioCertificateService certificateService;

    @Override
    @PostMapping
    public ResponseEntity<ApiResponse<CertificateResDTO>> create(
            @PathVariable Long portfolioId,
            @RequestHeader("X-Member-Id") Long memberId,
            @Valid @RequestBody CertificateReqDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.onSuccess(PortfolioSuccessCode.CERTIFICATE_CREATED,
                        certificateService.create(portfolioId, memberId, request)));
    }

    @Override
    @GetMapping
    public ResponseEntity<ApiResponse<List<CertificateResDTO>>> getList(
            @PathVariable Long portfolioId) {
        return ResponseEntity.ok(
                ApiResponse.onSuccess(PortfolioSuccessCode.CERTIFICATE_LIST_FOUND,
                        certificateService.getList(portfolioId)));
    }

    @Override
    @PatchMapping("/{certificateId}")
    public ResponseEntity<ApiResponse<CertificateResDTO>> update(
            @PathVariable Long portfolioId,
            @PathVariable Long certificateId,
            @RequestHeader("X-Member-Id") Long memberId,
            @Valid @RequestBody CertificateReqDTO request) {
        return ResponseEntity.ok(
                ApiResponse.onSuccess(PortfolioSuccessCode.CERTIFICATE_UPDATED,
                        certificateService.update(portfolioId, certificateId, memberId, request)));
    }

    @Override
    @DeleteMapping("/{certificateId}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable Long portfolioId,
            @PathVariable Long certificateId,
            @RequestHeader("X-Member-Id") Long memberId) {
        certificateService.delete(portfolioId, certificateId, memberId);
        return ResponseEntity.ok(ApiResponse.onSuccess(PortfolioSuccessCode.CERTIFICATE_DELETED, null));
    }
}
