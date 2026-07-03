package com.folioframe.domain.member.service;

import com.folioframe.domain.common.entity.Terms;
import com.folioframe.domain.common.enums.TermsType;
import com.folioframe.domain.common.repository.TermsRepository;
import com.folioframe.domain.company.entity.CompanyProfile;
import com.folioframe.domain.company.enums.VerificationStatus;
import com.folioframe.domain.company.repository.CompanyProfileRepository;
import com.folioframe.domain.member.dto.request.*;
import com.folioframe.domain.member.dto.response.*;
import com.folioframe.domain.member.entity.Member;
import com.folioframe.domain.member.entity.MemberAgreement;
import com.folioframe.domain.member.enums.MemberType;
import com.folioframe.domain.member.repository.MemberAgreementRepository;
import com.folioframe.domain.member.repository.MemberRepository;
import com.folioframe.domain.token.entity.RefreshToken;
import com.folioframe.domain.token.repository.RefreshTokenRepository;
import com.folioframe.global.auth.JwtUtil;
import com.folioframe.global.auth.exception.AuthException;
import com.folioframe.global.auth.exception.code.AuthErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class AuthService {

    // 영문 + 숫자 + 특수문자를 모두 포함한 8~20자
    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^(?=.*[A-Za-z])(?=.*\\d)(?=.*[^A-Za-z0-9]).{8,20}$");

    private final MemberRepository memberRepository;
    private final TermsRepository termsRepository;
    private final MemberAgreementRepository memberAgreementRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final CompanyProfileRepository companyProfileRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Transactional
    public SignupResDTO signup(SignupReqDTO req) {

        if (memberRepository.existsByLoginId(req.getLoginId())) {
            throw new AuthException(AuthErrorCode.DUPLICATE_ID);
        }

        if (!StringUtils.hasText(req.getPassword()) || !PASSWORD_PATTERN.matcher(req.getPassword()).matches()) {
            throw new AuthException(AuthErrorCode.INVALID_PASSWORD_FORMAT);
        }
        if (!req.getPassword().equals(req.getPasswordConfirm())) {
            throw new AuthException(AuthErrorCode.PASSWORD_MISMATCH);
        }

        if (!StringUtils.hasText(req.getPhone())) {
            throw new AuthException(AuthErrorCode.INVALID_INPUT);
        }
        if (memberRepository.existsByPhone(req.getPhone())) {
            throw new AuthException(AuthErrorCode.DUPLICATE_PHONE);
        }

        if (req.getMemberType() == MemberType.COMPANY) {
            if (!StringUtils.hasText(req.getBusinessNumber())) {
                throw new AuthException(AuthErrorCode.BUSINESS_NUMBER_REQUIRED);
            }
            if (companyProfileRepository.existsByBusinessNumber(req.getBusinessNumber())) {
                throw new AuthException(AuthErrorCode.DUPLICATE_BUSINESS_NUMBER);
            }
        }

        List<Terms> requiredTerms = termsRepository.findAllByRequiredTrue();
        Set<Long> agreedTermsIds = req.getAgreedTerms() == null ? Set.of() : new HashSet<>(req.getAgreedTerms());
        boolean allRequiredAgreed = requiredTerms.stream().allMatch(terms -> agreedTermsIds.contains(terms.getId()));
        if (!allRequiredAgreed) {
            throw new AuthException(AuthErrorCode.REQUIRED_TERMS_NOT_AGREED);
        }

        Member member = Member.builder()
                .loginId(req.getLoginId())
                .password(passwordEncoder.encode(req.getPassword()))
                .name(req.getName())
                .birthDate(req.getBirthDate())
                .phone(req.getPhone())
                .memberType(req.getMemberType())
                .build();
        memberRepository.save(member);

        if (req.getMemberType() == MemberType.COMPANY) {
            // 사업자번호는 여기서 받아두고, 관리자가 확인 후 VerificationStatus를 VERIFIED/REJECTED로 바꿔줌
            companyProfileRepository.save(CompanyProfile.builder()
                    .member(member)
                    .companyName(req.getName())
                    .businessNumber(req.getBusinessNumber())
                    .verificationStatus(VerificationStatus.PENDING)
                    .build());
        }

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

    @Transactional(readOnly = true)
    public CheckPhoneResDTO checkPhone(String phone) {
        return new CheckPhoneResDTO(!memberRepository.existsByPhone(phone));
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
        saveRefreshToken(member, refreshToken);

        return LoginResDTO.builder()
                .accessToken(accessToken).refreshToken(refreshToken).memberType(member.getMemberType()).build();
    }

    @Transactional
    public RefreshResDTO reissue(String refreshToken) {

        jwtUtil.validateToken(refreshToken);

        // 로그아웃 등으로 폐기된 refresh token은 서명은 유효해도 더 이상 재발급에 쓸 수 없어야 함
        RefreshToken stored = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new AuthException(AuthErrorCode.INVALID_TOKEN));
        Member member = stored.getMember();

        String newAccessToken = jwtUtil.createAccessToken(member.getLoginId(), member.getMemberType().name());
        String newRefreshToken = jwtUtil.createRefreshToken(member.getLoginId(), member.getMemberType().name());
        saveRefreshToken(member, newRefreshToken);

        return RefreshResDTO.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .build();
    }

    private void saveRefreshToken(Member member, String refreshToken) {
        Instant expiryDate = jwtUtil.getExpiryDate(refreshToken);
        refreshTokenRepository.findByMember(member)
                .ifPresentOrElse(
                        existing -> existing.updateToken(refreshToken, expiryDate),
                        () -> refreshTokenRepository.save(RefreshToken.builder()
                                .member(member).token(refreshToken).expiryDate(expiryDate).build())
                );
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