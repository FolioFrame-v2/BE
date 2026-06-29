package com.folioframe.domain.portfolio.controller;

import com.folioframe.domain.portfolio.dto.request.EducationReqDTO;
import com.folioframe.domain.portfolio.dto.response.EducationResDTO;
import com.folioframe.global.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;

@Tag(name = "Portfolio Education", description = "포트폴리오 학력 등록·조회·수정·삭제 API")
public interface PortfolioEducationControllerDocs {

    @Operation(
            summary = "학력 등록",
            description = "포트폴리오에 학력을 등록합니다. endedAt이 null이면 재학 중으로 처리됩니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "등록 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "요청 값이 올바르지 않습니다."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "해당 포트폴리오에 접근 권한이 없습니다."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "포트폴리오를 찾을 수 없습니다.")
    })
    ResponseEntity<ApiResponse<EducationResDTO>> create(
            @Parameter(description = "포트폴리오 ID", required = true) @PathVariable Long portfolioId,
            @Parameter(description = "인증된 회원 ID", required = true) @RequestHeader("X-Member-Id") Long memberId,
            @Valid @RequestBody EducationReqDTO request
    );

    @Operation(
            summary = "학력 목록 조회",
            description = "포트폴리오에 등록된 학력 목록을 입학일 기준 최신순으로 조회합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "포트폴리오를 찾을 수 없습니다.")
    })
    ResponseEntity<ApiResponse<List<EducationResDTO>>> getList(
            @Parameter(description = "포트폴리오 ID", required = true) @PathVariable Long portfolioId
    );

    @Operation(
            summary = "학력 수정",
            description = "등록된 학력 정보를 수정합니다. 본인 포트폴리오의 학력만 수정 가능합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "수정 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "요청 값이 올바르지 않거나 해당 학력이 이 포트폴리오에 속하지 않습니다."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "해당 포트폴리오에 접근 권한이 없습니다."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "포트폴리오 또는 학력을 찾을 수 없습니다.")
    })
    ResponseEntity<ApiResponse<EducationResDTO>> update(
            @Parameter(description = "포트폴리오 ID", required = true) @PathVariable Long portfolioId,
            @Parameter(description = "학력 ID", required = true) @PathVariable Long educationId,
            @Parameter(description = "인증된 회원 ID", required = true) @RequestHeader("X-Member-Id") Long memberId,
            @Valid @RequestBody EducationReqDTO request
    );

    @Operation(
            summary = "학력 삭제",
            description = "등록된 학력을 삭제합니다. 본인 포트폴리오의 학력만 삭제 가능합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "삭제 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "해당 학력이 이 포트폴리오에 속하지 않습니다."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "해당 포트폴리오에 접근 권한이 없습니다."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "포트폴리오 또는 학력을 찾을 수 없습니다.")
    })
    ResponseEntity<ApiResponse<Void>> delete(
            @Parameter(description = "포트폴리오 ID", required = true) @PathVariable Long portfolioId,
            @Parameter(description = "학력 ID", required = true) @PathVariable Long educationId,
            @Parameter(description = "인증된 회원 ID", required = true) @RequestHeader("X-Member-Id") Long memberId
    );
}
