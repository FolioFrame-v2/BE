package com.folioframe.domain.portfolio.controller;

import com.folioframe.domain.portfolio.dto.response.TemplateDetailResDTO;
import com.folioframe.domain.portfolio.dto.response.TemplateResDTO;
import com.folioframe.domain.portfolio.exception.code.PortfolioSuccessCode;
import com.folioframe.domain.portfolio.service.PortfolioTemplateService;
import com.folioframe.global.apiPayload.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/templates")
@RequiredArgsConstructor
public class PortfolioTemplateController implements PortfolioTemplateControllerDocs {

    private final PortfolioTemplateService templateService;

    @Override
    @GetMapping
    public ResponseEntity<ApiResponse<List<TemplateResDTO>>> getList() {
        return ResponseEntity.ok(
                ApiResponse.onSuccess(PortfolioSuccessCode.TEMPLATE_LIST_FOUND, templateService.getList()));
    }

    @Override
    @GetMapping("/{templateId}")
    public ResponseEntity<ApiResponse<TemplateDetailResDTO>> getDetail(@PathVariable Long templateId) {
        return ResponseEntity.ok(
                ApiResponse.onSuccess(PortfolioSuccessCode.TEMPLATE_DETAIL_FOUND, templateService.getDetail(templateId)));
    }
}
