package com.folioframe.domain.portfolio.controller;

import com.folioframe.global.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

@Tag(name = "Portfolio Bookmark", description = "포트폴리오 북마크 등록·취소 API")
public interface PortfolioBookmarkControllerDocs {

    @Operation(
            summary = "포트폴리오 북마크",
            description = "포트폴리오를 북마크합니다. 이미 북마크된 포트폴리오는 409 에러를 반환합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "북마크 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "포트폴리오 또는 회원을 찾을 수 없습니다."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "이미 북마크된 포트폴리오입니다.")
    })
    ResponseEntity<ApiResponse<Void>> bookmark(
            @Parameter(description = "포트폴리오 ID", required = true) @PathVariable Long portfolioId,
            @Parameter(description = "인증된 회원 ID", required = true) @RequestHeader("X-Member-Id") Long memberId
    );

    @Operation(
            summary = "포트폴리오 북마크 취소",
            description = "북마크된 포트폴리오의 북마크를 취소합니다. 북마크되지 않은 포트폴리오는 404 에러를 반환합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "북마크 취소 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "포트폴리오, 회원, 또는 북마크를 찾을 수 없습니다.")
    })
    ResponseEntity<ApiResponse<Void>> cancelBookmark(
            @Parameter(description = "포트폴리오 ID", required = true) @PathVariable Long portfolioId,
            @Parameter(description = "인증된 회원 ID", required = true) @RequestHeader("X-Member-Id") Long memberId
    );
}
