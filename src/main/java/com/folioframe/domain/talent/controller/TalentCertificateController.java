package com.folioframe.domain.talent.controller;

import com.folioframe.domain.talent.dto.request.TalentCertificateReqDTO;
import com.folioframe.domain.talent.dto.response.TalentCertificateResDTO;
import com.folioframe.domain.talent.exception.code.TalentProfileSuccessCode;
import com.folioframe.domain.talent.service.TalentCertificateService;
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

@Tag(name = "Talent Certificate", description = "인재 프로필의 자격증 등록/조회/수정/삭제 API")
@RestController
@RequestMapping("/api/v1/talent-profiles/me/certificates")
@RequiredArgsConstructor
public class TalentCertificateController {

    private final TalentCertificateService certificateService;

    @Operation(summary = "자격증 등록 API", description = "내 인재 프로필에 자격증을 등록합니다.")
    @PostMapping
    public ResponseEntity<ApiResponse<TalentCertificateResDTO>> create(
            @CurrentMemberId Long memberId,
            @Valid @RequestBody TalentCertificateReqDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.onSuccess(TalentProfileSuccessCode.CERTIFICATE_CREATED,
                        certificateService.create(memberId, request)));
    }

    @Operation(summary = "자격증 목록 조회 API", description = "내 인재 프로필의 자격증 목록을 조회합니다.")
    @GetMapping
    public ResponseEntity<ApiResponse<List<TalentCertificateResDTO>>> getList(
            @CurrentMemberId Long memberId) {
        return ResponseEntity.ok(
                ApiResponse.onSuccess(TalentProfileSuccessCode.CERTIFICATE_LIST_FOUND,
                        certificateService.getList(memberId)));
    }

    @Operation(summary = "자격증 수정 API", description = "내 인재 프로필의 자격증을 수정합니다.")
    @PatchMapping("/{certificateId}")
    public ResponseEntity<ApiResponse<TalentCertificateResDTO>> update(
            @CurrentMemberId Long memberId,
            @PathVariable Long certificateId,
            @Valid @RequestBody TalentCertificateReqDTO request) {
        return ResponseEntity.ok(
                ApiResponse.onSuccess(TalentProfileSuccessCode.CERTIFICATE_UPDATED,
                        certificateService.update(memberId, certificateId, request)));
    }

    @Operation(summary = "자격증 삭제 API", description = "내 인재 프로필의 자격증을 삭제합니다.")
    @DeleteMapping("/{certificateId}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @CurrentMemberId Long memberId,
            @PathVariable Long certificateId) {
        certificateService.delete(memberId, certificateId);
        return ResponseEntity.ok(ApiResponse.onSuccess(TalentProfileSuccessCode.CERTIFICATE_DELETED, null));
    }
}
