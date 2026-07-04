package com.folioframe.domain.portfolio.controller;

import com.folioframe.domain.portfolio.dto.request.AiFeedbackRenameReqDTO;
import com.folioframe.domain.portfolio.dto.request.AiFieldChooseReqDTO;
import com.folioframe.domain.portfolio.dto.request.AiFieldEditReqDTO;
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
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Tag(name = "Portfolio AI Feedback", description = "포트폴리오 AI 첨삭 요청·조회·버전 관리 API")
public interface PortfolioAiFeedbackControllerDocs {

    @Operation(
            summary = "AI 첨삭 요청",
            description = "지금 편집 화면에 불러와 있는(선택된) 버전 — sourceVersion/sourceSubVersion으로 지정 — 의 내용을 " +
                    "FolioFrame_AI로 보내 첨삭을 생성합니다. **게시(publish) 중인 콘텐츠나 라이브 콘텐츠가 아니라 그 " +
                    "버전 자신의 확정/초안 내용(resolvedText)을 기준으로 합니다.** 두 파라미터를 생략하면 원본(version=0) " +
                    "을 기준으로 하며, 포트폴리오에 대해 최초로 호출되는 경우 그 순간의 라이브 콘텐츠를 원본 스냅샷으로 " +
                    "먼저 만들어둔 뒤 그것을 사용합니다. 결과는 어떤 버전을 기준으로 했든 항상 새로운 최상위 버전으로 " +
                    "생성됩니다(자식 버전을 기준으로 호출해도 그 자식 밑이 아니라 새 최상위 버전이 생김). 생성된 버전의 " +
                    "각 필드는 기본으로 AI 수정본이 채택된 상태로 시작하지만, 이는 그 버전 자신의 초안일 뿐 실제 라이브 " +
                    "콘텐츠는 전혀 바뀌지 않습니다 — 게시(publish) API를 호출해야만 라이브에 반영됩니다. 호출할 때마다 " +
                    "새로운 최상위 버전(version)이 하나씩 늘어나며, 포트폴리오당 ai_check_max_count(기본 3회)를 " +
                    "초과하면 실패합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "생성 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "첨삭할 내용이 없습니다."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "접근 권한이 없거나 AI 첨삭 가능 횟수를 모두 사용했습니다."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "포트폴리오 또는 지정한 sourceVersion/sourceSubVersion을 찾을 수 없습니다."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "429", description = "AI 서비스 사용량이 많아 잠시 후 다시 시도해야 합니다."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "502", description = "AI 첨삭 서비스 호출에 실패했습니다.")
    })
    ResponseEntity<ApiResponse<PortfolioAiFeedbackResDTO>> generate(
            @Parameter(description = "포트폴리오 ID", required = true) @PathVariable Long portfolioId,
            @Parameter(description = "첨삭 기준이 될 최상위 버전 번호(원본은 0). 생략하면 0(원본)") @RequestParam(required = false) Integer sourceVersion,
            @Parameter(description = "첨삭 기준이 될 자식(수정본) 버전 번호. 없으면 최상위 버전을 기준으로 함") @RequestParam(required = false) Integer sourceSubVersion,
            @Parameter(hidden = true) Long memberId
    );

    @Operation(
            summary = "최근 AI 첨삭 결과 조회",
            description = "포트폴리오 편집 화면 진입 시 기본으로 불러올 버전을 조회합니다. \"가장 최근에 생성된\" 버전이 " +
                    "아니라 \"가장 최근에 실제로 수정/저장된\" 버전(원본, 최상위 버전, 자식 수정본 통틀어)을 반환합니다 — " +
                    "AI 첨삭을 새로 받았는지와 무관하게, 필드 선택/직접수정으로 마지막에 손댄 버전을 그대로 이어서 보여주기 " +
                    "위함입니다. AI 첨삭을 한 번도 요청한 적 없으면 지금 라이브 콘텐츠를 읽기 전용으로 보여줍니다(항상 200). " +
                    "응답의 published는 이 버전이 지금 실제로 게시 중인지를 나타냅니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "해당 포트폴리오에 접근 권한이 없습니다."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "포트폴리오를 찾을 수 없습니다.")
    })
    ResponseEntity<ApiResponse<PortfolioAiFeedbackResDTO>> getLatest(
            @Parameter(description = "포트폴리오 ID", required = true) @PathVariable Long portfolioId,
            @Parameter(hidden = true) Long memberId
    );

    @Operation(
            summary = "AI 첨삭 버전 목록 조회",
            description = "포트폴리오 수정 화면 우측 버전 관리 패널에 쓰이는 트리 형태의 목록입니다. AI 첨삭을 한 번도 " +
                    "생성한 적이 없으면 라이브 콘텐츠 기준 가상 '원본' 항목 하나만 담긴 목록을 반환하고, 한 번이라도 " +
                    "생성했다면 실제 저장된 '원본'(version=0)부터 지금까지 생성된 모든 최상위 AI 첨삭 버전을 오래된 순으로 " +
                    "돌려주며, 각 최상위 버전 항목의 revisions에는 '수정본 만들기'로 생성된 자식 버전들이 subVersion " +
                    "오름차순으로 담깁니다. 각 항목의 published는 그 버전이 지금 실제로 게시 중인지를 나타내며(FE에서 파란 " +
                    "테두리로 표시할 때 사용), 한 포트폴리오에는 항상 최대 하나의 항목만 published=true입니다. label은 " +
                    "사용자가 이름 변경 API로 직접 지정한 표시 이름이며, null이면 FE가 번호로부터 기본 이름을 생성해서 보여줘야 합니다. " +
                    "createdAt은 그 버전이 처음 생성된 시각이고, lastModifiedAt은 AI 첨삭 수신 여부와 무관하게 필드 선택/직접수정 " +
                    "등으로 실제 내용이 마지막으로 바뀐 시각입니다(한 번도 안 바뀌었으면 createdAt과 동일). FE는 이 lastModifiedAt " +
                    "하나만 표시하면 됩니다 — 처음엔 생성 시각과 같다가 수정 시 자동으로 갱신됩니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "해당 포트폴리오에 접근 권한이 없습니다."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "포트폴리오를 찾을 수 없습니다.")
    })
    ResponseEntity<ApiResponse<List<PortfolioAiFeedbackVersionResDTO>>> getVersions(
            @Parameter(description = "포트폴리오 ID", required = true) @PathVariable Long portfolioId,
            @Parameter(hidden = true) Long memberId
    );

    @Operation(
            summary = "원본 조회",
            description = "버전 관리 패널에서 버전1 위에 표시되는 '원본' 항목을 클릭했을 때 조회합니다. AI 첨삭을 이미 " +
                    "시작했다면 최초 요청 직전에 스냅샷 떠둔 독립 버전(version=0)을 다른 버전과 동일하게 돌려주며, 직접수정· " +
                    "게시가 모두 가능합니다. 아직 한 번도 생성한 적이 없다면 지금 라이브 필드 콘텐츠를 읽기 전용으로 보여주고 " +
                    "총평·점수는 없습니다. 어느 경우든 항상 200을 반환합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "해당 포트폴리오에 접근 권한이 없습니다."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "포트폴리오를 찾을 수 없습니다.")
    })
    ResponseEntity<ApiResponse<PortfolioAiFeedbackResDTO>> getOriginal(
            @Parameter(description = "포트폴리오 ID", required = true) @PathVariable Long portfolioId,
            @Parameter(hidden = true) Long memberId
    );

    @Operation(
            summary = "특정 버전 AI 첨삭 결과 조회",
            description = "버전 관리 패널에서 특정 버전을 클릭했을 때, 그 버전의 필드별 첨삭 결과와 총평·점수를 조회합니다. " +
                    "subVersion을 주지 않으면 최상위 버전을, subVersion을 주면 그 최상위 버전 아래의 자식(수정본) 버전을 " +
                    "조회합니다(원본은 version=0). 이미 확정(finalized=true)된 버전(자식 버전은 항상 확정 상태)은 선택 UX가 " +
                    "더 이상 의미가 없으므로 originalText/aiRevisedText/chosen 없이 필드별 최종 초안 내용(resolvedText)만 " +
                    "돌려주고, 아직 확정 전(finalized=false)이면 원본·AI 수정본·선택 상태를 포함해 돌려줍니다. published는 " +
                    "이 버전이 지금 실제로 게시 중인지를 나타냅니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "해당 포트폴리오에 접근 권한이 없습니다."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "포트폴리오 또는 해당 버전의 AI 첨삭 결과를 찾을 수 없습니다.")
    })
    ResponseEntity<ApiResponse<PortfolioAiFeedbackResDTO>> getByVersion(
            @Parameter(description = "포트폴리오 ID", required = true) @PathVariable Long portfolioId,
            @Parameter(description = "조회할 최상위 버전 번호", required = true) @PathVariable Integer version,
            @Parameter(description = "조회할 자식(수정본) 버전 번호. 없으면 최상위 버전을 조회") @RequestParam(required = false) Integer subVersion,
            @Parameter(hidden = true) Long memberId
    );

    @Operation(
            summary = "AI 첨삭 버전 저장(확정)",
            description = "버전 관리 패널에서 '저장' 버튼을 눌렀을 때 호출합니다. 대상 버전이 아직 확정 전(finalized=false)이면 " +
                    "지금까지 선택/직접수정한 초안 내용을 그대로 최종본으로 확정합니다(원본/AI 선택 UX가 이후 사라짐). 이미 " +
                    "확정된 버전(또는 자식 수정본)에 대한 호출은 상태 변화 없이 그대로 응답합니다(직접수정 자체는 별도의 " +
                    "필드 직접수정 API로 바로 반영되므로 이 API가 추가로 할 일은 없습니다). subVersion을 주면 자식(수정본) " +
                    "버전을 대상으로 합니다. 이 API는 라이브 콘텐츠에 전혀 영향을 주지 않습니다 — 게시(publish) API만 라이브를 바꿉니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "저장 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "해당 포트폴리오에 접근 권한이 없습니다."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "포트폴리오 또는 해당 버전의 AI 첨삭 결과를 찾을 수 없습니다.")
    })
    ResponseEntity<ApiResponse<PortfolioAiFeedbackResDTO>> saveVersion(
            @Parameter(description = "포트폴리오 ID", required = true) @PathVariable Long portfolioId,
            @Parameter(description = "저장할 최상위 버전 번호", required = true) @PathVariable Integer version,
            @Parameter(description = "저장할 자식(수정본) 버전 번호. 없으면 최상위 버전을 저장") @RequestParam(required = false) Integer subVersion,
            @Parameter(hidden = true) Long memberId
    );

    @Operation(
            summary = "수정본 만들기",
            description = "확정된 최상위 버전 바로 아래에 자식 버전(예: 버전2 아래 버전2-1, 버전2-2 ...)을 새로 만듭니다. " +
                    "자식은 항상 최상위 버전 바로 밑에 1단계로만 붙습니다(자식의 자식은 없음). subVersion을 주지 않으면 " +
                    "최상위 버전(예: 버전2)의 확정 콘텐츠에서 시작하고, 이미 존재하는 형제 자식의 subVersion을 주면(예: " +
                    "버전2-1을 보다가 호출) 그 형제의 최신 내용을 이어받아 다음 자식(버전2-2)을 만듭니다. AI를 다시 " +
                    "호출하지 않고 그대로 복사해서 시작하며, 생성 즉시 확정 상태이므로 원본/AI 선택 UX 없이 바로 직접 " +
                    "수정만 가능합니다(필드 직접수정 API로 편집). 대상 최상위 버전이 아직 확정 전이면 400을 반환합니다. " +
                    "라이브 콘텐츠에는 영향이 없으며, 게시(publish)해야 반영됩니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "생성 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "아직 확정되지 않은 버전에는 수정본을 만들 수 없습니다."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "해당 포트폴리오에 접근 권한이 없습니다."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "포트폴리오 또는 해당 버전의 AI 첨삭 결과를 찾을 수 없습니다.")
    })
    ResponseEntity<ApiResponse<PortfolioAiFeedbackResDTO>> createRevision(
            @Parameter(description = "포트폴리오 ID", required = true) @PathVariable Long portfolioId,
            @Parameter(description = "수정본을 만들 최상위 버전 번호", required = true) @PathVariable Integer version,
            @Parameter(description = "내용을 이어받을 기존 형제 자식의 subVersion. 없으면 최상위 버전에서 시작") @RequestParam(required = false) Integer subVersion,
            @Parameter(hidden = true) Long memberId
    );

    @Operation(
            summary = "AI 첨삭 버전 게시",
            description = "버전 관리 패널에서 공개/비공개를 선택한 뒤 '게시하기' 버튼을 눌렀을 때 호출합니다. 포트폴리오를 " +
                    "게시하는 유일한 API입니다(기존 '포트폴리오 저장(발행)' PATCH /portfolios/{portfolioId}/publish는 " +
                    "폐지됨). **이 API는 visibility를 강제로 바꾸지 않습니다** — 그 직전에 PATCH " +
                    "/portfolios/{portfolioId}/visibility로 사용자가 이미 선택해둔 공개/비공개 값을 그대로 따르므로, " +
                    "비공개를 선택한 채로도 게시할 수 있습니다(이 경우 라이브 콘텐츠는 갱신되지만 여전히 비공개입니다). " +
                    "대상 버전의 지금 내용(resolvedText — 선택/직접수정 즉시 반영되므로 저장 여부와 무관하게 항상 최신 " +
                    "값)을 실제 라이브 엔티티(포트폴리오 한줄소개/상세설명, 프로필 소개, 커스텀 필드, 프로젝트 요약)에 " +
                    "복사하고 이 버전을 '지금 게시 중인 버전'으로 기록합니다. 대상 버전이 아직 확정 전(finalized=false, " +
                    "원본/AI 선택 UX가 떠 있는 오픈 상태)이면 **게시와 동시에 자동으로 확정도 함께 처리합니다**(게시=확정) " +
                    "— 별도로 저장 API를 먼저 호출할 필요가 없습니다. 아직 한 번도 저장/AI첨삭/게시를 하지 않은 초안은 " +
                    "이 API를 호출해야 비로소 확정되어 마이페이지 목록에 노출되고 방치 정리 대상에서 제외됩니다. AI 첨삭을 " +
                    "한 번도 요청한 적 없는 포트폴리오는 version=0(원본) row 자체가 없는데, 이때 version=0으로 게시를 " +
                    "호출하면 복사할 대상 없이 지금 라이브 콘텐츠를 그대로 확정합니다. 라이브 콘텐츠가 바뀌는 유일한 " +
                    "경로이며, 게시 전까지는 어떤 버전을 편집해도 실제 라이브 포트폴리오는 바뀌지 않습니다. subVersion을 " +
                    "주면 자식(수정본) 버전을 게시합니다. 이 포트폴리오가 처음 게시(확정)되는 순간에만 선택한 템플릿의 " +
                    "useCount가 1 증가합니다(버전을 바꿔가며 재게시해도 추가로 늘지 않음)."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "게시 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "해당 포트폴리오에 접근 권한이 없습니다."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "포트폴리오 또는 해당 버전의 AI 첨삭 결과를 찾을 수 없습니다.")
    })
    ResponseEntity<ApiResponse<PortfolioAiFeedbackResDTO>> publishVersion(
            @Parameter(description = "포트폴리오 ID", required = true) @PathVariable Long portfolioId,
            @Parameter(description = "게시할 최상위 버전 번호(원본은 0)", required = true) @PathVariable Integer version,
            @Parameter(description = "게시할 자식(수정본) 버전 번호. 없으면 최상위 버전을 게시") @RequestParam(required = false) Integer subVersion,
            @Parameter(hidden = true) Long memberId
    );

    @Operation(
            summary = "AI 첨삭 버전 이름 변경",
            description = "버전 관리 패널에서 특정 버전(원본 포함, version=0)의 표시 이름을 사용자가 직접 지정하거나 " +
                    "수정합니다. label을 비우거나(null/공백) 보내면 다시 기본 이름('버전1', '버전1-1' 등, FE가 " +
                    "번호로부터 생성)으로 되돌아갑니다. 확정 여부와 무관하게 언제나 가능하며 라이브 콘텐츠에는 영향이 " +
                    "없습니다. AI 첨삭을 한 번도 요청한 적 없는 포트폴리오의 가상 '원본'(실제 row 없음)은 이름을 저장할 " +
                    "곳이 없어 404를 반환합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "변경 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "이름이 너무 깁니다."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "해당 포트폴리오에 접근 권한이 없습니다."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "포트폴리오 또는 해당 버전의 AI 첨삭 결과를 찾을 수 없습니다.")
    })
    ResponseEntity<ApiResponse<PortfolioAiFeedbackResDTO>> renameVersion(
            @Parameter(description = "포트폴리오 ID", required = true) @PathVariable Long portfolioId,
            @Parameter(description = "이름을 바꿀 최상위 버전 번호(원본은 0)", required = true) @PathVariable Integer version,
            @Parameter(description = "이름을 바꿀 자식(수정본) 버전 번호. 없으면 최상위 버전을 변경") @RequestParam(required = false) Integer subVersion,
            @Parameter(hidden = true) Long memberId,
            @Valid @RequestBody AiFeedbackRenameReqDTO request
    );

    @Operation(
            summary = "AI 첨삭 버전 삭제",
            description = "버전 관리 패널에서 특정 버전을 삭제합니다. 원본(version=0)은 삭제할 수 없습니다. 최상위 버전을 " +
                    "삭제하면(subVersion 없음) 그 아래 자식(수정본) 버전까지 모두 함께 삭제되고(그룹 삭제), subVersion을 " +
                    "지정해 자식 버전만 삭제하면 그 자식만 삭제됩니다. 삭제 대상이 지금 게시 중인 버전(또는 그 부모, 즉 " +
                    "게시 중인 자식이 함께 사라지는 경우)이라면, 그 버전이 라이브에 올려뒀던 필드 내용을 전부 빈 값으로 " +
                    "지우고 portfolio.visibility를 PRIVATE로 전환합니다(게시 중이 아니었던 버전을 삭제할 때는 라이브 " +
                    "콘텐츠에 아무 영향이 없습니다)."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "삭제 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "원본은 삭제할 수 없습니다."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "해당 포트폴리오에 접근 권한이 없습니다."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "포트폴리오 또는 해당 버전의 AI 첨삭 결과를 찾을 수 없습니다.")
    })
    ResponseEntity<ApiResponse<Void>> deleteVersion(
            @Parameter(description = "포트폴리오 ID", required = true) @PathVariable Long portfolioId,
            @Parameter(description = "삭제할 최상위 버전 번호", required = true) @PathVariable Integer version,
            @Parameter(description = "삭제할 자식(수정본) 버전 번호. 없으면 최상위 버전(+ 모든 자식)을 삭제") @RequestParam(required = false) Integer subVersion,
            @Parameter(hidden = true) Long memberId
    );

    @Operation(
            summary = "AI 첨삭 필드 선택 반영",
            description = "AI 첨삭 생성 시 모든 필드는 기본적으로 AI 수정본이 이미 채택된 상태입니다. 이 API로 특정 필드를 " +
                    "ORIGINAL로 선택하면 그 버전 자신의 초안(resolvedText)이 첨삭 요청 시점의 원본으로 바뀌고, 다시 AI로 " +
                    "선택하면 AI 수정본으로 다시 바뀝니다. 실제 라이브 콘텐츠는 전혀 바뀌지 않으며, 게시(publish) API를 " +
                    "호출해야 반영됩니다. 아직 확정되지 않은(finalized=false) 버전의 필드만 선택할 수 있고, 이미 확정된 " +
                    "버전의 필드는 다시 선택할 수 없습니다(400) — 확정된 버전은 대신 필드 직접수정 API를 사용하세요."
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
            @Parameter(hidden = true) Long memberId,
            @Valid @RequestBody AiFieldChooseReqDTO request
    );

    @Operation(
            summary = "AI 첨삭 필드 직접 수정",
            description = "버전 관리 패널에서 특정 필드를 직접 타이핑해서 수정할 때 호출합니다. 확정 여부와 무관하게 항상 " +
                    "허용되며(확정된 버전도 직접수정 가능), 그 버전 자신의 초안(resolvedText)만 덮어씁니다. 실제 라이브 " +
                    "콘텐츠는 바뀌지 않으며, 게시(publish) API를 호출해야 반영됩니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "수정 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "해당 필드가 이 포트폴리오에 속하지 않습니다."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "해당 포트폴리오에 접근 권한이 없습니다."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "포트폴리오 또는 AI 첨삭 필드를 찾을 수 없습니다.")
    })
    ResponseEntity<ApiResponse<AiFieldResultDTO>> editField(
            @Parameter(description = "포트폴리오 ID", required = true) @PathVariable Long portfolioId,
            @Parameter(description = "AI 첨삭 필드 ID (PortfolioAiField.id)", required = true) @PathVariable Long aiFieldId,
            @Parameter(hidden = true) Long memberId,
            @Valid @RequestBody AiFieldEditReqDTO request
    );
}
