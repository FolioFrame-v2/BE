package com.folioframe.domain.portfolio.controller;

import com.folioframe.domain.portfolio.dto.request.AiFieldChooseReqDTO;
import com.folioframe.domain.portfolio.dto.response.AiFieldResultDTO;
import com.folioframe.domain.portfolio.dto.response.PortfolioAiFeedbackResDTO;
import com.folioframe.domain.portfolio.dto.response.PortfolioAiFeedbackVersionResDTO;
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

@Tag(name = "Portfolio AI Feedback", description = "포트폴리오 AI 첨삭 요청·조회·버전 관리 API")
public interface PortfolioAiFeedbackControllerDocs {

    @Operation(
            summary = "AI 첨삭 요청",
            description = "포트폴리오 한줄소개/상세설명/프로필 소개/프로젝트 요약/커스텀 필드를 FolioFrame_AI로 보내 첨삭을 생성합니다. " +
                    "생성과 동시에 모든 필드에 AI 수정본이 기본으로 즉시 반영됩니다(실제 content가 바로 교체됨). " +
                    "특정 필드를 원본으로 되돌리고 싶으면 이후 필드 선택 API(chosen=ORIGINAL)를 호출해야 합니다. " +
                    "이 API를 다시 호출하면, 직전 버전에서 그동안 선택·수동 수정한 실제 내용이 그 버전의 최종 확정본(resolvedText)으로 " +
                    "동결되고 새 버전이 시작됩니다. 호출할 때마다 새 버전(version)이 하나씩 늘어나며, " +
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
            description = "포트폴리오에 대해 가장 최근에 생성된(버전 번호가 가장 큰) AI 첨삭 결과를 조회합니다. " +
                    "아직 확정(finalized)되지 않은 버전이면 각 필드의 resolvedText를 그 순간의 실제 콘텐츠로 실시간 계산해서 돌려줍니다."
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

    @Operation(
            summary = "AI 첨삭 버전 목록 조회",
            description = "포트폴리오 수정 화면 우측 버전 관리 패널에 쓰이는 목록입니다. 맨 앞에 '원본'(version=0, 첫 AI 첨삭 요청 " +
                    "직전 상태) 항목을 추가하고, 그 뒤로 지금까지 생성된 모든 AI 첨삭 버전(버전 번호·점수·생성일시)을 오래된 순으로 " +
                    "조회합니다. AI 첨삭을 한 번도 생성한 적이 없으면 빈 목록을 반환합니다(원본 항목도 없음)."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "해당 포트폴리오에 접근 권한이 없습니다."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "포트폴리오를 찾을 수 없습니다.")
    })
    ResponseEntity<ApiResponse<List<PortfolioAiFeedbackVersionResDTO>>> getVersions(
            @Parameter(description = "포트폴리오 ID", required = true) @PathVariable Long portfolioId,
            @Parameter(description = "인증된 회원 ID", required = true) @RequestHeader("X-Member-Id") Long memberId
    );

    @Operation(
            summary = "원본(첫 AI 첨삭 요청 직전 상태) 조회",
            description = "버전 관리 패널에서 버전1 위에 표시되는 '원본' 항목을 클릭했을 때 조회합니다. 첫 AI 첨삭 생성 시점에 " +
                    "각 필드에 남겨둔 originalText만 읽기 전용으로 보여주며, AI 수정본·선택 상태·총평·점수는 없습니다. " +
                    "AI 첨삭을 한 번도 생성한 적이 없으면 404를 반환합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "해당 포트폴리오에 접근 권한이 없습니다."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "포트폴리오를 찾을 수 없거나 AI 첨삭을 생성한 적이 없습니다.")
    })
    ResponseEntity<ApiResponse<PortfolioAiFeedbackResDTO>> getOriginal(
            @Parameter(description = "포트폴리오 ID", required = true) @PathVariable Long portfolioId,
            @Parameter(description = "인증된 회원 ID", required = true) @RequestHeader("X-Member-Id") Long memberId
    );

    @Operation(
            summary = "특정 버전 AI 첨삭 결과 조회",
            description = "버전 관리 패널에서 특정 버전을 클릭했을 때, 그 버전의 필드별 첨삭 결과와 총평·점수를 조회합니다. " +
                    "이미 확정(finalized=true)된 버전은 선택 UX가 더 이상 의미가 없으므로 originalText/aiRevisedText/chosen 없이 " +
                    "필드별 최종 확정 내용(resolvedText, 그 버전이 닫히는 시점에 동결된 텍스트)만 돌려주고, 아직 확정 전" +
                    "(finalized=false, 즉 최신 진행 중 버전)이면 원본·AI 수정본·선택 상태를 포함해 그 순간의 실제 콘텐츠를 " +
                    "실시간으로 계산해서 돌려줍니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "해당 포트폴리오에 접근 권한이 없습니다."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "포트폴리오 또는 해당 버전의 AI 첨삭 결과를 찾을 수 없습니다.")
    })
    ResponseEntity<ApiResponse<PortfolioAiFeedbackResDTO>> getByVersion(
            @Parameter(description = "포트폴리오 ID", required = true) @PathVariable Long portfolioId,
            @Parameter(description = "조회할 버전 번호", required = true) @PathVariable Integer version,
            @Parameter(description = "인증된 회원 ID", required = true) @RequestHeader("X-Member-Id") Long memberId
    );

    @Operation(
            summary = "AI 첨삭 필드 선택 반영",
            description = "AI 첨삭 생성 시 모든 필드는 기본적으로 AI 수정본이 이미 반영된 상태입니다. 이 API로 특정 필드를 ORIGINAL로 " +
                    "선택하면 해당 필드/프로젝트/포트폴리오/프로필의 실제 내용이 첨삭 요청 시점의 원본으로 즉시 되돌아가고, " +
                    "다시 AI로 선택하면 AI 수정본으로 다시 교체됩니다. 아직 확정되지 않은(finalized=false) 최신 버전의 " +
                    "필드만 선택할 수 있고, 이미 확정된 과거 버전의 필드는 다시 선택할 수 없습니다(400)."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "선택 반영 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "요청 값이 올바르지 않거나, 해당 필드가 이 포트폴리오에 속하지 않거나, 이미 확정된 버전입니다."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "해당 포트폴리오에 접근 권한이 없습니다."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "포트폴리오 또는 AI 첨삭 필드를 찾을 수 없습니다.")
    })
    ResponseEntity<ApiResponse<AiFieldResultDTO>> chooseField(
            @Parameter(description = "포트폴리오 ID", required = true) @PathVariable Long portfolioId,
            @Parameter(description = "AI 첨삭 필드 ID (PortfolioAiField.id)", required = true) @PathVariable Long aiFieldId,
            @Parameter(description = "인증된 회원 ID", required = true) @RequestHeader("X-Member-Id") Long memberId,
            @Valid @RequestBody AiFieldChooseReqDTO request
    );
}
