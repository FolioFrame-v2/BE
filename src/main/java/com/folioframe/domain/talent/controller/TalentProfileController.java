package com.folioframe.domain.talent.controller;

import com.folioframe.domain.talent.dto.request.TalentProfileCreateReqDTO;
import com.folioframe.domain.talent.dto.request.TalentProfileUpdateReqDTO;
import com.folioframe.domain.talent.dto.response.TalentProfileResDTO;
import com.folioframe.domain.talent.dto.response.TalentProfileSignupInfoResDTO;
import com.folioframe.domain.talent.exception.code.TalentProfileErrorCode;
import com.folioframe.domain.talent.exception.code.TalentProfileSuccessCode;
import com.folioframe.domain.talent.service.TalentProfileService;
import com.folioframe.global.apiPayload.ApiResponse;
import com.folioframe.global.apiPayload.exception.GeneralException;
import com.folioframe.global.auth.CurrentMemberId;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "Talent Profile", description = "인재 프로필 등록, 조회, 수정 API")
@RestController
@RequestMapping("/api/v1/talent-profiles")
@RequiredArgsConstructor
public class TalentProfileController {

    private final TalentProfileService talentProfileService;

    @Operation(summary = "인재 프로필 등록 API", description = "새로운 인재 프로필을 등록합니다.")
    @PostMapping
    public ApiResponse<Map<String, Long>> createProfile(
            @CurrentMemberId Long memberId,
            @Valid @RequestBody TalentProfileCreateReqDTO request,
            BindingResult bindingResult) {

        // 에러 발생 시 무조건 콘솔에 출력
        if (bindingResult.hasErrors()) {
            bindingResult.getFieldErrors().forEach(error -> {
                System.out.println(">>> [에러 필드]: " + error.getField());
                System.out.println(">>> [에러 메시지]: " + error.getDefaultMessage());
                System.out.println(">>> [입력된 값]: " + error.getRejectedValue());
            });
            throw new GeneralException(TalentProfileErrorCode.INVALID_INPUT);
        }

        Long talentProfileId = talentProfileService.createProfile(memberId, request);

        return ApiResponse.onSuccess(TalentProfileSuccessCode.PROFILE_CREATED, Map.of("talentProfileId", talentProfileId));
    }

    @Operation(summary = "회원가입 정보 조회 API", description = "프로필 작성 화면에서 프리필할 회원가입 시 입력값(이름/휴대폰/나이)을 조회합니다.")
    @GetMapping("/signup-info")
    public ApiResponse<TalentProfileSignupInfoResDTO> getSignupInfo(@CurrentMemberId Long memberId) {

        TalentProfileSignupInfoResDTO signupInfo = talentProfileService.getSignupInfo(memberId);

        return ApiResponse.onSuccess(TalentProfileSuccessCode.PROFILE_READ_SUCCESS, signupInfo);
    }

    @Operation(summary = "내 인재 프로필 조회 API", description = "현재 로그인한 사용자의 인재 프로필 정보를 상세 조회합니다.")
    @GetMapping("/me")
    public ApiResponse<TalentProfileResDTO> getMyProfile(@CurrentMemberId Long memberId) {

        TalentProfileResDTO profile = talentProfileService.getMyProfile(memberId);

        return ApiResponse.onSuccess(TalentProfileSuccessCode.PROFILE_READ_SUCCESS, profile);
    }

    @Operation(summary = "내 인재 프로필 수정 API", description = "현재 로그인한 사용자의 인재 프로필 정보를 수정합니다.")
    @PatchMapping("/me")
    public ApiResponse<TalentProfileResDTO> updateProfile(
            @CurrentMemberId Long memberId,
            @RequestBody TalentProfileUpdateReqDTO request) {

        TalentProfileResDTO updatedProfile = talentProfileService.updateProfile(memberId, request);

        return ApiResponse.onSuccess(TalentProfileSuccessCode.PROFILE_UPDATE_SUCCESS, updatedProfile);
    }
}