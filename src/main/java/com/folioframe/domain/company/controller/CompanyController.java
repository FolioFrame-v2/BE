package com.folioframe.domain.company.controller;

import com.folioframe.domain.company.dto.request.CompanyProfileReqDTO;
import com.folioframe.domain.company.dto.request.CompanyVerificationReqDTO;
import com.folioframe.domain.company.dto.response.CompanyProfileResDTO;
import com.folioframe.domain.company.exception.code.CompanySuccessCode;
import com.folioframe.domain.company.service.CompanyService;
import com.folioframe.global.apiPayload.ApiResponse;
import com.folioframe.global.auth.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Company API", description = "기업 프로필 관련 API")
@RestController
@RequestMapping("/api/v1/company-profiles")
@RequiredArgsConstructor
public class CompanyController {

    private final CompanyService companyService;

    @Operation(
            summary = "기업 프로필 등록 API",
            description = "현재 로그인한 기업 회원의 기업 프로필을 등록합니다."
    )
    @PostMapping
    public ResponseEntity<ApiResponse<CompanyProfileResDTO>> createCompanyProfile(
            @Valid @RequestBody CompanyProfileReqDTO request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        Long currentMemberId = userDetails.member().getId();

        CompanyProfileResDTO response = companyService.createCompanyProfile(request, currentMemberId);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.onSuccess(CompanySuccessCode.PROFILE_CREATED, response));
    }

    @Operation(
            summary = "내 기업 프로필 조회 API",
            description = "현재 로그인한 기업 회원의 기업 프로필 정보를 조회합니다."
    )
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<CompanyProfileResDTO>> getMyCompanyProfile(
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        Long currentMemberId = userDetails.member().getId();

        CompanyProfileResDTO response = companyService.getMyCompanyProfile(currentMemberId);

        return ResponseEntity.ok(
                ApiResponse.onSuccess(CompanySuccessCode.PROFILE_FETCHED, response)
        );
    }

    @Operation(
            summary = "기업 프로필 수정 API",
            description = "현재 로그인한 기업 회원이 자신의 기업 프로필 정보를 수정합니다."
    )
    @PatchMapping("/{profileId}")
    public ResponseEntity<ApiResponse<CompanyProfileResDTO>> updateCompanyProfile(
            @PathVariable Long profileId,
            @Valid @RequestBody CompanyProfileReqDTO request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        Long currentMemberId = userDetails.member().getId();

        CompanyProfileResDTO response = companyService.updateCompanyProfile(profileId, request, currentMemberId);

        return ResponseEntity.ok(
                ApiResponse.onSuccess(CompanySuccessCode.PROFILE_UPDATED, response)
        );
    }

    @Operation(
            summary = "기업 프로필 상세 조회 API",
            description = "특정 기업 프로필의 상세 정보를 조회합니다."
    )
    @GetMapping("/{profileId}")
    public ResponseEntity<ApiResponse<CompanyProfileResDTO>> getCompanyProfile(
            @PathVariable Long profileId) {

        CompanyProfileResDTO response = companyService.getCompanyProfile(profileId);

        return ResponseEntity.ok(
                ApiResponse.onSuccess(CompanySuccessCode.COMPANY_FETCHED, response)
        );
    }

    /**
     * 기업 사업자 인증 상태 변경(관리자용)
     */
    @Operation(summary = "기업 사업자 인증 상태 변경(관리자용)", description = "관리자가 사업자번호 확인 후 기업 프로필의 인증 상태를 VERIFIED/REJECTED로 변경합니다.")
    @PatchMapping("/{profileId}/verification")
    public ResponseEntity<ApiResponse<CompanyProfileResDTO>> updateVerification(
            @PathVariable Long profileId,
            @Valid @RequestBody CompanyVerificationReqDTO request) {

        CompanyProfileResDTO response = companyService.updateVerificationStatus(profileId, request.getVerificationStatus());

        return ResponseEntity.ok(
                ApiResponse.onSuccess(CompanySuccessCode.COMPANY_VERIFICATION_UPDATED, response)
        );
    }
}