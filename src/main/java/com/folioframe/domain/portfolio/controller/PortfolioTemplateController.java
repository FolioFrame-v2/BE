package com.folioframe.domain.portfolio.controller;

import com.folioframe.domain.portfolio.dto.response.TemplateDetailResDTO;
import com.folioframe.domain.portfolio.dto.response.TemplateResDTO;
import com.folioframe.domain.portfolio.exception.code.PortfolioSuccessCode;
import com.folioframe.domain.portfolio.service.PortfolioTemplateService;
import com.folioframe.global.apiPayload.ApiResponse;
import com.folioframe.global.dto.PageRequest;
import com.folioframe.global.dto.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/portfolio-templates")
@RequiredArgsConstructor
public class PortfolioTemplateController implements PortfolioTemplateControllerDocs {

    private final PortfolioTemplateService templateService;

    @Override
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<TemplateResDTO>>> getList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "4") Integer size) {
        return ResponseEntity.ok(
                ApiResponse.onSuccess(PortfolioSuccessCode.TEMPLATE_LIST_FOUND,
                        templateService.getList(PageRequest.of(page, size))));
    }

    @Override
    @GetMapping("/{templateId}")
    public ResponseEntity<ApiResponse<TemplateDetailResDTO>> getDetail(@PathVariable Long templateId) {
        return ResponseEntity.ok(
                ApiResponse.onSuccess(PortfolioSuccessCode.TEMPLATE_DETAIL_FOUND,
                        templateService.getDetail(templateId)));
    }
}
