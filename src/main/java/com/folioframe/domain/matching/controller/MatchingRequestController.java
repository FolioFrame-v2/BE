package com.folioframe.domain.matching.controller;

import com.folioframe.domain.matching.dto.request.MatchingRequestCreateReqDTO;
import com.folioframe.domain.matching.dto.request.MatchingStatusUpdateReqDTO;
import com.folioframe.domain.matching.dto.response.MatchingRequestResDTO;
import com.folioframe.domain.matching.exception.code.MatchingSuccessCode;
import com.folioframe.domain.matching.service.MatchingRequestService;
import com.folioframe.global.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/matchings-requests")
@RequiredArgsConstructor
@Tag(name = "Matching Request", description = "기업 ↔ 인재 매칭 제안 및 상태 관리 API")
public class MatchingRequestController {

    private final MatchingRequestService matchingRequestService;

    @Operation(
            summary = "매칭 제안 생성",
            description = "기업이 포트폴리오 기반으로 인재에게 매칭 제안을 보냅니다."
    )
    @PostMapping
    public ApiResponse<MatchingRequestResDTO> create(
            @RequestBody MatchingRequestCreateReqDTO dto
    ) {
        return ApiResponse.onSuccess(
                MatchingSuccessCode.MATCHING_PROPOSED,
                matchingRequestService.create(dto)
        );
    }

    @Operation(
            summary = "매칭 상태 변경",
            description = "매칭 제안의 상태(PROPOSED → ACCEPTED / REJECTED)를 변경합니다."
    )
    @PatchMapping("/{matchingRequestId}/status")
    public ResponseEntity<ApiResponse<MatchingRequestResDTO>> updateStatus(
            @PathVariable Long matchingRequestId,
            @RequestBody MatchingStatusUpdateReqDTO dto
    ) {
        return ResponseEntity.ok(
                ApiResponse.onSuccess(
                        MatchingSuccessCode.MATCHING_ACCEPTED,
                        matchingRequestService.updateStatus(matchingRequestId, dto)
                )
        );
    }
}