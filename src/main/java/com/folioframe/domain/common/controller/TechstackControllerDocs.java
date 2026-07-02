package com.folioframe.domain.common.controller;

import com.folioframe.domain.common.dto.request.TechstackReqDTO;
import com.folioframe.domain.common.dto.response.TechstackResDTO;
import com.folioframe.global.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Tag(name = "Techstack", description = "기술스택 검색·등록 API")
public interface TechstackControllerDocs {

    @Operation(
            summary = "기술스택 검색",
            description = "이름으로 기술스택을 검색합니다(대소문자 무시, 부분 일치). keyword가 없으면 전체 목록을 반환합니다. 프론트 검색창의 자동완성 목록으로 사용합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공")
    })
    ResponseEntity<ApiResponse<List<TechstackResDTO>>> search(
            @Parameter(description = "검색어") @RequestParam(required = false) String keyword
    );

    @Operation(
            summary = "기술스택 등록(존재하면 재사용)",
            description = "이름으로 기술스택을 찾아서 반환하고, 없으면 새로 생성합니다. 대소문자·공백 차이(예: \"Spring\"/\"spring\", \"java script\"/\"javascript\")는 같은 기술스택으로 취급해 중복 생성하지 않습니다. 검색 후 원하는 이름이 없을 때 엔터로 새 기술스택을 등록하는 용도로 사용합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 또는 생성 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "요청 값이 올바르지 않습니다.")
    })
    ResponseEntity<ApiResponse<TechstackResDTO>> findOrCreate(
            @Valid @RequestBody TechstackReqDTO request
    );
}
