package com.folioframe.domain.member.controller;

import com.folioframe.domain.member.dto.request.*;
import com.folioframe.domain.member.dto.response.*;
import com.folioframe.domain.member.service.AuthService;
import com.folioframe.global.apiPayload.ApiResponse;
import com.folioframe.global.auth.exception.code.AuthSuccessCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Auth API", description = "인증 및 회원가입 관련 API")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "회원가입", description = "새로운 회원을 등록합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "회원가입이 완료되었습니다."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "입력 형식이 올바르지 않습니다."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "이미 존재하는 아이디입니다.")
    })
    @PostMapping("/signup")
    public ApiResponse<SignupResDTO> signup(@RequestBody SignupReqDTO request) {
        return ApiResponse.onSuccess(AuthSuccessCode.SIGNUP_SUCCESS, authService.signup(request));
    }

    @Operation(summary = "아이디 중복 확인", description = "입력한 아이디가 이미 사용 중인지 확인합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "사용 가능한 아이디입니다."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "아이디 형식이 올바르지 않거나 필수 값이 누락되었습니다."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "이미 사용 중인 아이디입니다.")
    })
    @PostMapping("/check-id")
    public ApiResponse<CheckIdResDTO> checkId(@RequestBody CheckIdReqDTO request) {
        return ApiResponse.onSuccess(AuthSuccessCode.CHECK_ID_SUCCESS, authService.checkId(request.getLoginId()));
    }

    @Operation(summary = "로그인", description = "이메일과 비밀번호로 로그인하고 토큰을 발급받습니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "로그인에 성공했습니다."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "아이디/비밀번호는 로그인에 필수 입력 값입니다."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "비밀번호가 일치하지 않습니다.")
    })
    @PostMapping("/login")
    public ApiResponse<LoginResDTO> login(@RequestBody LoginReqDTO request) {
        return ApiResponse.onSuccess(AuthSuccessCode.LOGIN_SUCCESS, authService.login(request));
    }

    @Operation(summary = "로그아웃", description = "현재 사용 중인 Access Token을 만료 처리합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "로그아웃에 성공하였습니다."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "유효하지 않거나 만료된 토큰입니다.")
    })
    @PostMapping("/logout")
    public ApiResponse<Void> logout(@RequestHeader("Authorization") String accessToken) {
        authService.logout(accessToken);
        return ApiResponse.onSuccess(AuthSuccessCode.LOGOUT_SUCCESS, null);
    }

    @Operation(summary = "토큰 재발급", description = "만료된 Access Token을 Refresh Token을 이용해 새로 발급받습니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "토큰 재발급에 성공하였습니다."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "만료된 토큰입니다.")
    })
    @PostMapping("/refresh")
    public ApiResponse<RefreshResDTO> reissue(@RequestHeader("Refresh-Token") String refreshToken) {
        return ApiResponse.onSuccess(AuthSuccessCode.REFRESH_SUCCESS, authService.reissue(refreshToken));
    }

    @Operation(summary = "약관 상세 조회", description = "특정 유형의 약관 상세 내용을 조회합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "약관 상세 조회가 완료되었습니다."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "요청하신 약관 정보를 찾을 수 없습니다.")
    })
    @GetMapping("/terms/{type}")
    public ApiResponse<TermsResDTO> getTerms(@PathVariable("type") String type) {
        return ApiResponse.onSuccess(AuthSuccessCode.TERMS_FOUND_SUCCESS, authService.getTermsByType(type));
    }
}