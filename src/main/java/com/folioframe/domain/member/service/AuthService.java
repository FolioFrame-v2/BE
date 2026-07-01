package com.folioframe.domain.member.service;

import com.folioframe.domain.common.entity.Terms;
import com.folioframe.domain.common.enums.TermsType;
import com.folioframe.domain.common.repository.TermsRepository;
import com.folioframe.domain.member.dto.request.*;
import com.folioframe.domain.member.dto.response.*;
import com.folioframe.domain.member.entity.Member;
import com.folioframe.domain.member.entity.MemberAgreement;
import com.folioframe.domain.member.repository.MemberAgreementRepository;
import com.folioframe.domain.member.repository.MemberRepository;
import com.folioframe.global.auth.JwtUtil;
import com.folioframe.global.auth.exception.AuthException;
import com.folioframe.global.auth.exception.code.AuthErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final MemberRepository memberRepository;
    private final TermsRepository termsRepository;
    private final MemberAgreementRepository memberAgreementRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Transactional
    public SignupResDTO signup(SignupReqDTO req) {
        if (memberRepository.existsByLoginId(req.getLoginId())) {
            throw new AuthException(AuthErrorCode.DUPLICATE_ID);
        }

        Member member = Member.builder()
                .loginId(req.getLoginId())
                .password(passwordEncoder.encode(req.getPassword()))
                .memberType(req.getMemberType())
                .build();
        memberRepository.save(member);

        if (req.getAgreedTerms() != null) {
            for (Long termsId : req.getAgreedTerms()) {
                Terms terms = termsRepository.findById(termsId)
                        .orElseThrow(() -> new AuthException(AuthErrorCode.TERMS_NOT_FOUND));
                memberAgreementRepository.save(MemberAgreement.builder()
                        .member(member).terms(terms).agreed(true).build());
            }
        }
        return SignupResDTO.builder().id(member.getId()).loginId(member.getLoginId()).memberType(member.getMemberType()).build();
    }

    @Transactional(readOnly = true)
    public CheckIdResDTO checkId(String loginId) {
        return new CheckIdResDTO(!memberRepository.existsByLoginId(loginId));
    }

    @Transactional
    public LoginResDTO login(LoginReqDTO req) {
        Member member = memberRepository.findByLoginId(req.getLoginId())
                .orElseThrow(() -> new AuthException(AuthErrorCode.USER_NOT_FOUND));

        if (!passwordEncoder.matches(req.getPassword(), member.getPassword())) {
            throw new AuthException(AuthErrorCode.INVALID_PASSWORD);
        }

        String accessToken = jwtUtil.createAccessToken(member.getLoginId(), member.getMemberType().name());
        String refreshToken = jwtUtil.createRefreshToken(member.getLoginId(), member.getMemberType().name());

        return LoginResDTO.builder()
                .accessToken(accessToken).refreshToken(refreshToken).memberType(member.getMemberType()).build();
    }

    public void logout(String accessToken) { /* TODO: 블랙리스트 처리 로직 */ }

    @Transactional
    public RefreshResDTO reissue(String refreshToken) {

        jwtUtil.validateToken(refreshToken);

        String loginId = jwtUtil.getUserId(refreshToken);

        Member member = memberRepository.findByLoginId(loginId)
                .orElseThrow(() -> new AuthException(AuthErrorCode.USER_NOT_FOUND));

        String newAccessToken = jwtUtil.createAccessToken(member.getLoginId(), member.getMemberType().name());
        String newRefreshToken = jwtUtil.createRefreshToken(member.getLoginId(), member.getMemberType().name());

        // TODO: 추후 Redis 도입 시 기존 Refresh Token을 블랙리스트 처리하거나 지워주는 로직 추가

        return RefreshResDTO.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .build();
    }

    @Transactional(readOnly = true)
    public TermsResDTO getTermsByType(String typeStr) {
        TermsType type = TermsType.valueOf(typeStr.toUpperCase());
        Terms terms = termsRepository.findByType(type)
                .orElseThrow(() -> new AuthException(AuthErrorCode.TERMS_NOT_FOUND));

        return TermsResDTO.builder()
                .termsId(terms.getId()).type(terms.getType()).title(terms.getTitle())
                .content(terms.getContent()).required(terms.isRequired()).build();
    }
}