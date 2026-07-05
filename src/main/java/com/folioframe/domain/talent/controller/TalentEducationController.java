package com.folioframe.domain.talent.controller;

import com.folioframe.domain.talent.dto.request.TalentEducationReqDTO;
import com.folioframe.domain.talent.dto.response.TalentEducationResDTO;
import com.folioframe.domain.talent.exception.code.TalentProfileSuccessCode;
import com.folioframe.domain.talent.service.TalentEducationService;
import com.folioframe.global.apiPayload.ApiResponse;
import com.folioframe.global.auth.CurrentMemberId;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Talent Education", description = "인재 프로필의 학력 등록/조회/수정/삭제 API")
@RestController
@RequestMapping("/api/v1/talent-profiles/me/educations")
@RequiredArgsConstructor
public class TalentEducationController {

    private final TalentEducationService educationService;

    @Operation(summary = "학력 등록 API", description = "내 인재 프로필에 학력을 등록합니다.")
    @PostMapping
    public ResponseEntity<ApiResponse<TalentEducationResDTO>> create(
            @CurrentMemberId Long memberId,
            @Valid @RequestBody TalentEducationReqDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.onSuccess(TalentProfileSuccessCode.EDUCATION_CREATED,
                        educationService.create(memberId, request)));
    }

    @Operation(summary = "학력 목록 조회 API", description = "내 인재 프로필의 학력 목록을 조회합니다.")
    @GetMapping
    public ResponseEntity<ApiResponse<List<TalentEducationResDTO>>> getList(
            @CurrentMemberId Long memberId) {
        return ResponseEntity.ok(
                ApiResponse.onSuccess(TalentProfileSuccessCode.EDUCATION_LIST_FOUND,
                        educationService.getList(memberId)));
    }

    @Operation(summary = "학력 수정 API", description = "내 인재 프로필의 학력을 수정합니다.")
    @PatchMapping("/{educationId}")
    public ResponseEntity<ApiResponse<TalentEducationResDTO>> update(
            @CurrentMemberId Long memberId,
            @PathVariable Long educationId,
            @Valid @RequestBody TalentEducationReqDTO request) {
        return ResponseEntity.ok(
                ApiResponse.onSuccess(TalentProfileSuccessCode.EDUCATION_UPDATED,
                        educationService.update(memberId, educationId, request)));
    }

    @Operation(summary = "학력 삭제 API", description = "내 인재 프로필의 학력을 삭제합니다.")
    @DeleteMapping("/{educationId}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @CurrentMemberId Long memberId,
            @PathVariable Long educationId) {
        educationService.delete(memberId, educationId);
        return ResponseEntity.ok(ApiResponse.onSuccess(TalentProfileSuccessCode.EDUCATION_DELETED, null));
    }
}
