package com.folioframe.domain.talent.controller;

import com.folioframe.domain.common.enums.CareerLevel;
import com.folioframe.domain.job.enums.EmploymentType;
import com.folioframe.domain.common.enums.JobRole;
import com.folioframe.domain.talent.dto.request.TalentProfileCreateRequest;
import com.folioframe.domain.talent.dto.request.TalentProfileUpdateRequest;
import com.folioframe.domain.talent.dto.response.TalentProfileResponse;
import com.folioframe.domain.talent.dto.response.TalentProfileSearchResponse;
import com.folioframe.domain.talent.exception.code.TalentProfileErrorCode;
import com.folioframe.domain.talent.exception.code.TalentProfileSuccessCode;
import com.folioframe.domain.talent.service.TalentProfileService;
import com.folioframe.global.apiPayload.ApiResponse;
import com.folioframe.global.apiPayload.exception.GeneralException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "Talent Profile", description = "인재 프로필 등록, 조회, 수정 및 검색 API")
@RestController
@RequestMapping("/api/v1/talent-profiles")
@RequiredArgsConstructor
public class TalentProfileController {

    private final TalentProfileService talentProfileService;

    @Operation(summary = "인재 프로필 등록 API", description = "새로운 인재 프로필을 등록합니다.")
    @PostMapping
    public ApiResponse<Map<String, Long>> createProfile(
            @Valid @RequestBody TalentProfileCreateRequest request,
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

        Long memberId = 1L;
        Long talentProfileId = talentProfileService.createProfile(memberId, request);

        return ApiResponse.onSuccess(TalentProfileSuccessCode.PROFILE_CREATED, Map.of("talentProfileId", talentProfileId));
    }

    @Operation(summary = "내 인재 프로필 조회 API", description = "현재 로그인한 사용자의 인재 프로필 정보를 상세 조회합니다.")
    @GetMapping("/me")
    public ApiResponse<TalentProfileResponse> getMyProfile() {

        Long memberId = 1L;
        TalentProfileResponse profile = talentProfileService.getMyProfile(memberId);

        return ApiResponse.onSuccess(TalentProfileSuccessCode.PROFILE_READ_SUCCESS, profile);
    }

    @Operation(summary = "내 인재 프로필 수정 API", description = "현재 로그인한 사용자의 인재 프로필 정보를 수정합니다.")
    @PatchMapping("/me")
    public ApiResponse<TalentProfileResponse> updateProfile(
            @RequestBody TalentProfileUpdateRequest request) {

        Long memberId = 1L;
        TalentProfileResponse updatedProfile = talentProfileService.updateProfile(memberId, request);

        return ApiResponse.onSuccess(TalentProfileSuccessCode.PROFILE_UPDATE_SUCCESS, updatedProfile);
    }

    @Operation(summary = "인재풀 목록 검색 API", description = "경력, 직무, 기술스택 등의 조건으로 인재 목록을 페이징하여 조회합니다.")
    @GetMapping
    public ApiResponse<TalentProfileSearchResponse> searchProfiles(
            @RequestParam(required = false, defaultValue = "LATEST") String sort,
            @RequestParam(required = false) CareerLevel career,
            @RequestParam(required = false) EmploymentType employment,
            @RequestParam(required = false) String techStack,
            @RequestParam(required = false) JobRole job,
            Pageable pageable) {

        TalentProfileSearchResponse searchResult = talentProfileService.searchProfiles(sort, career, employment, techStack, job, pageable);

        return ApiResponse.onSuccess(TalentProfileSuccessCode.PROFILE_SEARCH_SUCCESS, searchResult);
    }
}