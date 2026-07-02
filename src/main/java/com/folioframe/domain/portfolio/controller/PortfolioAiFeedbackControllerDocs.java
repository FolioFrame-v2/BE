package com.folioframe.domain.portfolio.controller;

import com.folioframe.domain.portfolio.dto.response.PortfolioAiFeedbackResDTO;
import com.folioframe.global.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

@Tag(name = "Portfolio AI Feedback", description = "포트폴리오 AI 첨삭 요청·조회 API")
public interface PortfolioAiFeedbackControllerDocs {

    @Operation(
            summary = "AI 첨삭 요청",
            description = "포트폴리오 한줄소개/상세설명/프로필 소개/프로젝트 요약/커스텀 필드를 FolioFrame_AI로 보내 첨삭을 생성합니다. " +
                    "포트폴리오당 ai_check_max_count(기본 3회)를 초과하면 실패합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "생성 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "첨삭할 내용이 없습니다."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "접근 권한이 없거나 AI 첨삭 가능 횟수를 모두 사용했습니다."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "포트폴리오를 찾을 수 없습니다."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "429", description = "AI 서비스 사용량이 많아 잠시 후 다시 시도해야 합니다."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "502", description = "AI 첨삭 서비스 호출에 실패했습니다.")
    })
    ResponseEntity<ApiResponse<PortfolioAiFeedbackResDTO>> generate(
            @Parameter(description = "포트폴리오 ID", required = true) @PathVariable Long portfolioId,
            @Parameter(description = "인증된 회원 ID", required = true) @RequestHeader("X-Member-Id") Long memberId
    );

    @Operation(
            summary = "최근 AI 첨삭 결과 조회",
            description = "포트폴리오에 대해 가장 최근에 생성된 AI 첨삭 결과를 조회합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "해당 포트폴리오에 접근 권한이 없습니다."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "포트폴리오 또는 AI 첨삭 결과를 찾을 수 없습니다.")
    })
    ResponseEntity<ApiResponse<PortfolioAiFeedbackResDTO>> getLatest(
            @Parameter(description = "포트폴리오 ID", required = true) @PathVariable Long portfolioId,
            @Parameter(description = "인증된 회원 ID", required = true) @RequestHeader("X-Member-Id") Long memberId
    );
}
