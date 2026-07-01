package com.folioframe.domain.portfolio.controller;

import com.folioframe.domain.portfolio.dto.request.PortfolioCreateReqDTO;
import com.folioframe.domain.portfolio.dto.request.PortfolioUpdateReqDTO;
import com.folioframe.domain.portfolio.dto.request.PortfolioVisibilityReqDTO;
import com.folioframe.domain.portfolio.dto.response.PortfolioDetailResDTO;
import com.folioframe.domain.portfolio.dto.response.PortfolioResDTO;
import com.folioframe.domain.portfolio.dto.response.PortfolioSummaryResDTO;
import com.folioframe.domain.portfolio.enums.PortfolioSortType;
import com.folioframe.global.apiPayload.ApiResponse;
import com.folioframe.global.dto.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "Portfolio", description = "포트폴리오 생성·조회·수정·삭제 API")
public interface PortfolioControllerDocs {

    @Operation(
            summary = "포트폴리오 생성",
            description = "새 포트폴리오를 생성합니다. templateId는 필수이며 해당 템플릿의 필드가 자동으로 복사됩니다. 생성 시 공유용 publicSlug가 자동 발급됩니다. visibility를 지정하지 않으면 기본값 PRIVATE으로 생성됩니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "생성 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "요청 값이 올바르지 않습니다."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "회원, 탤런트 프로필 또는 템플릿을 찾을 수 없습니다.")
    })
    ResponseEntity<ApiResponse<PortfolioResDTO>> create(
            @Parameter(description = "인증된 회원 ID", required = true) @RequestHeader("X-Member-Id") Long memberId,
            @Valid @RequestBody PortfolioCreateReqDTO request
    );

    @Operation(
            summary = "내 포트폴리오 목록 조회 (마이페이지)",
            description = "본인이 작성한 포트폴리오 목록을 최근 수정순으로 페이지 단위 조회합니다. (2×2, 기본 4개/페이지)"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "회원 또는 탤런트 프로필을 찾을 수 없습니다.")
    })
    ResponseEntity<ApiResponse<PageResponse<PortfolioSummaryResDTO>>> getList(
            @Parameter(description = "인증된 회원 ID", required = true) @RequestHeader("X-Member-Id") Long memberId,
            @Parameter(description = "페이지 번호 (1부터 시작, 기본값: 1)") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "페이지 크기 (기본값: 4)") @RequestParam(defaultValue = "4") Integer size
    );

    @Operation(
            summary = "공개 포트폴리오 탐색 목록 조회",
            description = "공개(PUBLIC) + 게시(PUBLISHED) 상태인 포트폴리오 전체를 페이지 단위 조회합니다. (3×3, 기본 9개/페이지)\n\n" +
                    "- `sort`: LATEST(최신순, 기본값) / POPULAR(북마크 순) / MOST_VIEWED(조회순)\n" +
                    "- **비로그인 시**: 상위 3개만 반환 (`totalElements`는 실제 전체 개수 — 프론트에서 회원가입 유도 UI 표시)"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공")
    })
    ResponseEntity<ApiResponse<PageResponse<PortfolioSummaryResDTO>>> getPublicList(
            @Parameter(description = "정렬 (LATEST / POPULAR / MOST_VIEWED, 기본값: LATEST)") @RequestParam(required = false) PortfolioSortType sort,
            @Parameter(description = "페이지 번호 (1부터 시작, 기본값: 1)") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "페이지 크기 (기본값: 9)") @RequestParam(defaultValue = "9") Integer size,
            @Parameter(description = "인증된 회원 ID (선택 — 미전달 시 상위 3개만 반환)") @RequestHeader(value = "X-Member-Id", required = false) Long memberId
    );

    @Operation(
            summary = "포트폴리오 상세 조회 (ID)",
            description = "포트폴리오 ID로 상세 정보를 조회합니다. PUBLIC만 누구나 접근 가능하며, PRIVATE·LINK_ONLY는 본인만 접근 가능합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "해당 포트폴리오에 접근 권한이 없습니다."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "포트폴리오를 찾을 수 없습니다.")
    })
    ResponseEntity<ApiResponse<PortfolioDetailResDTO>> getDetail(
            @Parameter(description = "포트폴리오 ID", required = true) @PathVariable Long portfolioId,
            @Parameter(description = "인증된 회원 ID", required = true) @RequestHeader("X-Member-Id") Long memberId
    );

    @Operation(
            summary = "포트폴리오 링크 공유 조회 (Slug)",
            description = "공유 링크(publicSlug)로 포트폴리오를 조회합니다. PUBLIC·LINK_ONLY 포트폴리오만 접근 가능합니다. 비로그인 사용자도 접근할 수 있습니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "비공개 포트폴리오입니다."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "포트폴리오를 찾을 수 없습니다.")
    })
    ResponseEntity<ApiResponse<PortfolioDetailResDTO>> getBySlug(
            @Parameter(description = "공유 슬러그", required = true) @PathVariable String publicSlug
    );

    @Operation(
            summary = "포트폴리오 수정",
            description = "포트폴리오 기본 정보를 수정합니다. 본인 포트폴리오만 수정 가능합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "수정 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "요청 값이 올바르지 않습니다."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "해당 포트폴리오에 접근 권한이 없습니다."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "포트폴리오를 찾을 수 없습니다.")
    })
    ResponseEntity<ApiResponse<PortfolioResDTO>> update(
            @Parameter(description = "포트폴리오 ID", required = true) @PathVariable Long portfolioId,
            @Parameter(description = "인증된 회원 ID", required = true) @RequestHeader("X-Member-Id") Long memberId,
            @Valid @RequestBody PortfolioUpdateReqDTO request
    );

    @Operation(
            summary = "포트폴리오 공개 설정 변경",
            description = """
                    포트폴리오 공개 범위를 변경합니다. 본인 포트폴리오만 변경 가능합니다.
                    - PUBLIC: 전체 공개 — 누구나 접근 가능
                    - LINK_ONLY: 링크 공개 — publicSlug 링크를 가진 사람만 접근 가능
                    - PRIVATE: 비공개 — 본인만 접근 가능
                    """
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "변경 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "요청 값이 올바르지 않습니다."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "해당 포트폴리오에 접근 권한이 없습니다."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "포트폴리오를 찾을 수 없습니다.")
    })
    ResponseEntity<ApiResponse<PortfolioResDTO>> changeVisibility(
            @Parameter(description = "포트폴리오 ID", required = true) @PathVariable Long portfolioId,
            @Parameter(description = "인증된 회원 ID", required = true) @RequestHeader("X-Member-Id") Long memberId,
            @Valid @RequestBody PortfolioVisibilityReqDTO request
    );

    @Operation(
            summary = "포트폴리오 저장(발행)",
            description = "작성 완료된 포트폴리오를 저장합니다. EditStatus가 PUBLISHED로 변경되고 선택한 템플릿의 useCount가 1 증가합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "저장 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "해당 포트폴리오에 접근 권한이 없습니다."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "포트폴리오를 찾을 수 없습니다.")
    })
    ResponseEntity<ApiResponse<PortfolioResDTO>> publish(
            @Parameter(description = "포트폴리오 ID", required = true) @PathVariable Long portfolioId,
            @Parameter(description = "인증된 회원 ID", required = true) @RequestHeader("X-Member-Id") Long memberId
    );

    @Operation(
            summary = "포트폴리오 삭제",
            description = "포트폴리오를 삭제합니다. 본인 포트폴리오만 삭제 가능합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "삭제 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "해당 포트폴리오에 접근 권한이 없습니다."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "포트폴리오를 찾을 수 없습니다.")
    })
    ResponseEntity<ApiResponse<Void>> delete(
            @Parameter(description = "포트폴리오 ID", required = true) @PathVariable Long portfolioId,
            @Parameter(description = "인증된 회원 ID", required = true) @RequestHeader("X-Member-Id") Long memberId
    );
}
