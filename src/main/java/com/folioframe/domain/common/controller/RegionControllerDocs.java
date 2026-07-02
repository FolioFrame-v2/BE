package com.folioframe.domain.common.controller;

import com.folioframe.domain.common.dto.response.RegionResDTO;
import com.folioframe.global.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Tag(name = "Region", description = "지역 검색 API")
public interface RegionControllerDocs {

    @Operation(
            summary = "지역 검색",
            description = """
                    지역을 검색합니다(대소문자 무시, 부분 일치). keyword가 없으면 선택 가능한 전체 지역을 반환합니다.
                    상위 지역명("서울")과 하위 지역명("강남·서초·양재")을 합친 전체 경로(예: "서울 강남·서초·양재")를 기준으로 검색하므로,
                    "서울"만 입력해도 서울의 하위 지역이 모두 걸리고 "강남"까지 입력하면 좁혀집니다.
                    상위 지역 자체("서울", "경기" 등)는 선택 불가 항목이라 검색 결과에 나오지 않고, 항상 하위(리프) 지역만 반환됩니다.
                    새 지역을 등록하는 기능은 없습니다 — 관리자가 미리 정의한 목록 안에서만 선택 가능합니다.
                    """
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공")
    })
    ResponseEntity<ApiResponse<List<RegionResDTO>>> search(
            @Parameter(description = "검색어") @RequestParam(required = false) String keyword
    );
}
