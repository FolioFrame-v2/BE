package com.folioframe.domain.company.controller;

import com.folioframe.domain.company.dto.request.CompanyVerificationReqDTO;
import com.folioframe.domain.company.dto.response.CompanyProfileResDTO;
import com.folioframe.domain.company.exception.code.CompanySuccessCode;
import com.folioframe.domain.company.service.CompanyService;
import com.folioframe.global.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

// TODO: 관리자 권한 체크 미적용 상태. 현재 시스템에 ADMIN 역할/인증 체계가 없어 로그인한 회원이면 누구나 호출 가능함.
// 관리자 인증 도입 시 이 엔드포인트에 권한 검증 추가 필요.
@Tag(name = "Company API", description = "기업 프로필 관련 API")
@RestController
@RequestMapping("/api/companies")
@RequiredArgsConstructor
public class CompanyController {

    private final CompanyService companyService;

    @Operation(summary = "기업 사업자 인증 상태 변경(관리자용)", description = "관리자가 사업자번호 확인 후 기업 프로필의 인증 상태를 VERIFIED/REJECTED로 변경합니다.")
    @PatchMapping("/{companyProfileId}/verification")
    public ApiResponse<CompanyProfileResDTO> updateVerification(
            @PathVariable Long companyProfileId,
            @RequestBody CompanyVerificationReqDTO request) {
        return ApiResponse.onSuccess(CompanySuccessCode.COMPANY_VERIFICATION_UPDATED,
                companyService.updateVerificationStatus(companyProfileId, request.getVerificationStatus()));
    }
}
