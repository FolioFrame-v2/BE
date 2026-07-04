package com.folioframe.domain.portfolio.controller;

import com.folioframe.domain.portfolio.dto.request.PortfolioCreateReqDTO;
import com.folioframe.domain.portfolio.dto.request.PortfolioUpdateReqDTO;
import com.folioframe.domain.portfolio.dto.request.PortfolioVisibilityReqDTO;
import com.folioframe.domain.portfolio.dto.request.TechstackIdsReqDTO;
import com.folioframe.domain.portfolio.dto.response.PortfolioDetailResDTO;
import com.folioframe.domain.portfolio.dto.response.PortfolioMyListResDTO;
import com.folioframe.domain.portfolio.dto.response.PortfolioPublicListResDTO;
import com.folioframe.domain.portfolio.dto.response.PortfolioResDTO;
import com.folioframe.domain.common.dto.response.TechstackResDTO;
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
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Tag(name = "Portfolio", description = "포트폴리오 생성·조회·수정·삭제 API")
public interface PortfolioControllerDocs {

    @Operation(
            summary = "포트폴리오 생성",
            description = "새 포트폴리오를 생성합니다. templateId는 필수이며 해당 템플릿의 필드가 자동으로 복사됩니다. 생성 시 공유용 publicSlug가 자동 발급됩니다. visibility를 지정하지 않으면 기본값 PRIVATE으로 생성됩니다. techstackIds로 포트폴리오 전체에서 사용해본 기술스택을 함께 등록할 수 있습니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "생성 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "요청 값이 올바르지 않습니다."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "회원, 탤런트 프로필, 템플릿 또는 기술스택을 찾을 수 없습니다.")
    })
    ResponseEntity<ApiResponse<PortfolioResDTO>> create(
            @Parameter(hidden = true) Long memberId,
            @Valid @RequestBody PortfolioCreateReqDTO request
    );

    @Operation(
            summary = "내 포트폴리오 목록 조회 (마이페이지)",
            description = "본인이 작성한 포트폴리오 목록을 최근 수정순으로 페이지 단위 조회합니다. (2×2, 기본 4개/페이지)\n\n" +
                    "제목, 마지막 수정일(updatedAt), 조회수, 공개/비공개 여부만 반환합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "회원 또는 탤런트 프로필을 찾을 수 없습니다.")
    })
    ResponseEntity<ApiResponse<PageResponse<PortfolioMyListResDTO>>> getList(
            @Parameter(hidden = true) Long memberId,
            @Parameter(description = "페이지 번호 (1부터 시작, 기본값: 1)") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "페이지 크기 (기본값: 4)") @RequestParam(defaultValue = "4") Integer size
    );

    @Operation(
            summary = "공개 포트폴리오 탐색 목록 조회",
            description = "공개(PUBLIC) + 게시(PUBLISHED) 상태인 포트폴리오 전체를 페이지 단위 조회합니다. (3×3, 기본 9개/페이지)\n\n" +
                    "- `sort`: LATEST(최신순, 기본값) / POPULAR(북마크 순) / MOST_VIEWED(조회순)\n" +
                    "- **비로그인 시**: 상위 3개만 반환 (`totalElements`는 실제 전체 개수 — 프론트에서 회원가입 유도 UI 표시)\n" +
                    "- 포트폴리오 제목, 작성자 프로필 사진/이름/지역, 경력 연차(careerLevel), 직군(jobRole), 보유 기술스택, 북마크수, 조회수를 반환합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공")
    })
    ResponseEntity<ApiResponse<PageResponse<PortfolioPublicListResDTO>>> getPublicList(
            @Parameter(description = "정렬 (LATEST / POPULAR / MOST_VIEWED, 기본값: LATEST)") @RequestParam(required = false) PortfolioSortType sort,
            @Parameter(description = "페이지 번호 (1부터 시작, 기본값: 1)") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "페이지 크기 (기본값: 9)") @RequestParam(defaultValue = "9") Integer size,
            @Parameter(hidden = true) Long memberId
    );

    @Operation(
            summary = "포트폴리오 상세 조회 (ID)",
            description = "포트폴리오 ID로 상세 정보를 조회합니다. PUBLIC만 누구나 접근 가능하며, PRIVATE는 본인만 접근 가능합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "해당 포트폴리오에 접근 권한이 없습니다."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "포트폴리오를 찾을 수 없습니다.")
    })
    ResponseEntity<ApiResponse<PortfolioDetailResDTO>> getDetail(
            @Parameter(description = "포트폴리오 ID", required = true) @PathVariable Long portfolioId,
            @Parameter(hidden = true) Long memberId
    );

    @Operation(
            summary = "포트폴리오 링크 공유 조회 (Slug)",
            description = "공유 링크(publicSlug)로 포트폴리오를 조회합니다. PUBLIC 포트폴리오만 접근 가능합니다. 비로그인 사용자도 접근할 수 있습니다."
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
            description = "포트폴리오 기본 정보를 수정합니다. 본인 포트폴리오만 수정 가능합니다. techstackIds는 매번 전체 목록으로 교체됩니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "수정 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "요청 값이 올바르지 않습니다."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "해당 포트폴리오에 접근 권한이 없습니다."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "포트폴리오 또는 기술스택을 찾을 수 없습니다.")
    })
    ResponseEntity<ApiResponse<PortfolioResDTO>> update(
            @Parameter(description = "포트폴리오 ID", required = true) @PathVariable Long portfolioId,
            @Parameter(hidden = true) Long memberId,
            @Valid @RequestBody PortfolioUpdateReqDTO request
    );

    @Operation(
            summary = "포트폴리오 저장 확정",
            description = "편집 화면의 '저장' 버튼이 호출합니다. 아직 확정된 적 없는 초안을 확정 상태로 전환하고, " +
                    "원본(v0) 스냅샷이 없으면 지금 라이브 콘텐츠 기준으로 함께 생성합니다. 확정되지 않은 포트폴리오는 " +
                    "마이페이지 목록에 노출되지 않고, 일정 시간이 지나면 서버가 자동으로 정리합니다 — 이 API를 호출해야 " +
                    "그 대상에서 제외됩니다. 이미 확정된 포트폴리오(또는 AI 첨삭을 먼저 요청했거나 게시한 적이 있는 " +
                    "포트폴리오)에 다시 호출해도 상태 변화 없이 그대로 응답합니다(멱등)."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "저장 확정 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "해당 포트폴리오에 접근 권한이 없습니다."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "포트폴리오를 찾을 수 없습니다.")
    })
    ResponseEntity<ApiResponse<PortfolioResDTO>> confirmSave(
            @Parameter(description = "포트폴리오 ID", required = true) @PathVariable Long portfolioId,
            @Parameter(hidden = true) Long memberId
    );

    @Operation(
            summary = "포트폴리오 공개 설정 변경",
            description = """
                    포트폴리오 공개 범위를 변경합니다. 본인 포트폴리오만 변경 가능합니다.
                    - PUBLIC: 전체 공개 — 누구나 접근 가능
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
            @Parameter(hidden = true) Long memberId,
            @Valid @RequestBody PortfolioVisibilityReqDTO request
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
            @Parameter(hidden = true) Long memberId
    );

    @Operation(
            summary = "포트폴리오 기술스택 수정",
            description = "포트폴리오 전체에서 사용해본 기술스택만 가볍게 교체합니다. techstackIds에 남길 항목만 담아 보내면 되며(예: 태그 하나 제거), 다른 필드는 건드리지 않습니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "수정 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "해당 포트폴리오에 접근 권한이 없습니다."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "포트폴리오 또는 기술스택을 찾을 수 없습니다.")
    })
    ResponseEntity<ApiResponse<List<TechstackResDTO>>> updateTechstacks(
            @Parameter(description = "포트폴리오 ID", required = true) @PathVariable Long portfolioId,
            @Parameter(hidden = true) Long memberId,
            @Valid @RequestBody TechstackIdsReqDTO request
    );
}
