package com.folioframe.domain.portfolio.controller;

import com.folioframe.domain.portfolio.dto.request.PortfolioFieldUpdateReqDTO;
import com.folioframe.domain.portfolio.dto.response.PortfolioFieldResDTO;
import com.folioframe.global.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;

@Tag(name = "Portfolio Field", description = "포트폴리오 템플릿 필드 조회·수정 API")
public interface PortfolioFieldControllerDocs {

    @Operation(
            summary = "포트폴리오 필드 목록 조회",
            description = "포트폴리오에 포함된 템플릿 필드 목록과 현재 작성된 내용을 displayOrder 순으로 조회합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "포트폴리오를 찾을 수 없습니다.")
    })
    ResponseEntity<ApiResponse<List<PortfolioFieldResDTO>>> getList(
            @Parameter(description = "포트폴리오 ID", required = true) @PathVariable Long portfolioId
    );

    @Operation(
            summary = "포트폴리오 필드 내용 저장",
            description = "특정 필드에 작성한 내용을 저장합니다. 본인 포트폴리오의 필드만 수정 가능합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "저장 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "해당 필드가 이 포트폴리오에 속하지 않습니다."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "해당 포트폴리오에 접근 권한이 없습니다."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "포트폴리오 또는 필드를 찾을 수 없습니다.")
    })
    ResponseEntity<ApiResponse<PortfolioFieldResDTO>> updateContent(
            @Parameter(description = "포트폴리오 ID", required = true) @PathVariable Long portfolioId,
            @Parameter(description = "필드 ID", required = true) @PathVariable Long fieldId,
            @Parameter(description = "인증된 회원 ID", required = true) @RequestHeader("X-Member-Id") Long memberId,
            @RequestBody PortfolioFieldUpdateReqDTO request
    );
}
