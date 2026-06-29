package com.folioframe.domain.portfolio.controller;

import com.folioframe.domain.portfolio.dto.response.TemplateDetailResDTO;
import com.folioframe.domain.portfolio.dto.response.TemplateResDTO;
import com.folioframe.global.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Tag(name = "Template", description = "포트폴리오 템플릿 조회 API")
public interface PortfolioTemplateControllerDocs {

    @Operation(
            summary = "템플릿 목록 조회",
            description = "사용 가능한 템플릿 목록을 사용 횟수 기준 내림차순으로 조회합니다. 포트폴리오 생성 전 템플릿 선택 화면에서 사용합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공")
    })
    ResponseEntity<ApiResponse<List<TemplateResDTO>>> getList();

    @Operation(
            summary = "템플릿 상세 조회",
            description = "템플릿의 상세 정보와 포함된 필드 목록(성장과정, 미래계획 등)을 displayOrder 순으로 조회합니다. 템플릿 선택 전 미리보기에 사용합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "템플릿을 찾을 수 없습니다.")
    })
    ResponseEntity<ApiResponse<TemplateDetailResDTO>> getDetail(
            @Parameter(description = "템플릿 ID", required = true) @PathVariable Long templateId
    );
}
