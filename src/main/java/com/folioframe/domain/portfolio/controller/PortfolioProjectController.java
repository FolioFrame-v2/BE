package com.folioframe.domain.portfolio.controller;

import com.folioframe.domain.portfolio.dto.request.ProjectReqDTO;
import com.folioframe.domain.portfolio.dto.response.ProjectResDTO;
import com.folioframe.domain.portfolio.exception.code.PortfolioSuccessCode;
import com.folioframe.domain.portfolio.service.PortfolioProjectService;
import com.folioframe.global.apiPayload.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/portfolios/{portfolioId}/projects")
@RequiredArgsConstructor
public class PortfolioProjectController implements PortfolioProjectControllerDocs {

    private final PortfolioProjectService projectService;

    @Override
    @PostMapping
    public ResponseEntity<ApiResponse<ProjectResDTO>> create(
            @PathVariable Long portfolioId,
            @RequestHeader("X-Member-Id") Long memberId,
            @Valid @RequestBody ProjectReqDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.onSuccess(PortfolioSuccessCode.PROJECT_CREATED,
                        projectService.create(portfolioId, memberId, request)));
    }

    @Override
    @GetMapping
    public ResponseEntity<ApiResponse<List<ProjectResDTO>>> getList(
            @PathVariable Long portfolioId) {
        return ResponseEntity.ok(
                ApiResponse.onSuccess(PortfolioSuccessCode.PROJECT_LIST_FOUND,
                        projectService.getList(portfolioId)));
    }

    @Override
    @PatchMapping("/{projectId}")
    public ResponseEntity<ApiResponse<ProjectResDTO>> update(
            @PathVariable Long portfolioId,
            @PathVariable Long projectId,
            @RequestHeader("X-Member-Id") Long memberId,
            @Valid @RequestBody ProjectReqDTO request) {
        return ResponseEntity.ok(
                ApiResponse.onSuccess(PortfolioSuccessCode.PROJECT_UPDATED,
                        projectService.update(portfolioId, projectId, memberId, request)));
    }

    @Override
    @DeleteMapping("/{projectId}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable Long portfolioId,
            @PathVariable Long projectId,
            @RequestHeader("X-Member-Id") Long memberId) {
        projectService.delete(portfolioId, projectId, memberId);
        return ResponseEntity.ok(ApiResponse.onSuccess(PortfolioSuccessCode.PROJECT_DELETED, null));
    }
}
