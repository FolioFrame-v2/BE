package com.folioframe.domain.talent.controller;

import com.folioframe.domain.talent.dto.request.TalentCareerReqDTO;
import com.folioframe.domain.talent.dto.response.TalentCareerResDTO;
import com.folioframe.domain.talent.exception.code.TalentProfileSuccessCode;
import com.folioframe.domain.talent.service.TalentCareerService;
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

@Tag(name = "Talent Career", description = "인재 프로필의 경력 등록/조회/수정/삭제 API")
@RestController
@RequestMapping("/api/v1/talent-profiles/me/careers")
@RequiredArgsConstructor
public class TalentCareerController {

    private final TalentCareerService careerService;

    @Operation(summary = "경력 등록 API", description = "내 인재 프로필에 경력을 등록합니다.")
    @PostMapping
    public ResponseEntity<ApiResponse<TalentCareerResDTO>> create(
            @CurrentMemberId Long memberId,
            @Valid @RequestBody TalentCareerReqDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.onSuccess(TalentProfileSuccessCode.CAREER_CREATED,
                        careerService.create(memberId, request)));
    }

    @Operation(summary = "경력 목록 조회 API", description = "내 인재 프로필의 경력 목록을 조회합니다.")
    @GetMapping
    public ResponseEntity<ApiResponse<List<TalentCareerResDTO>>> getList(
            @CurrentMemberId Long memberId) {
        return ResponseEntity.ok(
                ApiResponse.onSuccess(TalentProfileSuccessCode.CAREER_LIST_FOUND,
                        careerService.getList(memberId)));
    }

    @Operation(summary = "경력 수정 API", description = "내 인재 프로필의 경력을 수정합니다.")
    @PatchMapping("/{careerId}")
    public ResponseEntity<ApiResponse<TalentCareerResDTO>> update(
            @CurrentMemberId Long memberId,
            @PathVariable Long careerId,
            @Valid @RequestBody TalentCareerReqDTO request) {
        return ResponseEntity.ok(
                ApiResponse.onSuccess(TalentProfileSuccessCode.CAREER_UPDATED,
                        careerService.update(memberId, careerId, request)));
    }

    @Operation(summary = "경력 삭제 API", description = "내 인재 프로필의 경력을 삭제합니다.")
    @DeleteMapping("/{careerId}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @CurrentMemberId Long memberId,
            @PathVariable Long careerId) {
        careerService.delete(memberId, careerId);
        return ResponseEntity.ok(ApiResponse.onSuccess(TalentProfileSuccessCode.CAREER_DELETED, null));
    }
}
