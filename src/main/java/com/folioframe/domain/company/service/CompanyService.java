package com.folioframe.domain.company.service;

import com.folioframe.domain.common.entity.Region;
import com.folioframe.domain.common.repository.RegionRepository;
import com.folioframe.domain.company.dto.request.CompanyProfileReqDTO;
import com.folioframe.domain.company.dto.response.CompanyProfileResDTO;
import com.folioframe.domain.company.entity.CompanyProfile;
import com.folioframe.domain.company.enums.VerificationStatus;
import com.folioframe.domain.company.exception.code.CompanyErrorCode;
import com.folioframe.domain.company.repository.CompanyProfileRepository;
import com.folioframe.domain.member.entity.Member;
import com.folioframe.domain.member.repository.MemberRepository;
import com.folioframe.global.apiPayload.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CompanyService {

    private final CompanyProfileRepository companyProfileRepository;
    private final MemberRepository memberRepository;
    private final RegionRepository regionRepository;

    /**
     * 기업 프로필 등록
     */
    @Transactional
    public CompanyProfileResDTO createCompanyProfile(CompanyProfileReqDTO request, Long memberId) {
        // 1. 사용자 조회
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new GeneralException(CompanyErrorCode.MEMBER_NOT_FOUND));

        // 2. 해당 계정에 연결된 기업 프로필이 이미 존재하는지 확인 (409)
        if (companyProfileRepository.existsByMemberId(memberId)) {
            throw new GeneralException(CompanyErrorCode.DUPLICATE_COMPANY_PROFILE);
        }

        // 3. 이미 등록된 사업자 등록번호인지 확인 (409)
        if (companyProfileRepository.existsByBusinessNumber(request.getBusinessNumber())) {
            throw new GeneralException(CompanyErrorCode.DUPLICATE_BUSINESS_NUMBER);
        }

        // 4. 지역 조회 (404)
        Region region = regionRepository.findById(request.getRegionId())
                .orElseThrow(() -> new GeneralException(CompanyErrorCode.REGION_NOT_FOUND));

        // 5. Entity 생성 및 연관관계 매핑 (초기 상태는 PENDING)
        CompanyProfile companyProfile = CompanyProfile.builder()
                .member(member)
                .companyName(request.getCompanyName())
                .businessNumber(request.getBusinessNumber())
                .industry(request.getIndustry())
                .websiteUrl(request.getWebsiteUrl())
                .logoUrl(request.getLogoUrl())
                .companyIntro(request.getCompanyIntro())
                .region(region)
                .verificationStatus(VerificationStatus.PENDING)
                .build();

        // 6. 저장
        CompanyProfile savedProfile = companyProfileRepository.save(companyProfile);

        return convertToResDTO(savedProfile);
    }

    /**
     * 내 기업 프로필 조회
     */
    @Transactional(readOnly = true)
    public CompanyProfileResDTO getMyCompanyProfile(Long memberId) {
        // 1. 내 프로필 조회, 없으면 예외 발생 (404)
        CompanyProfile companyProfile = companyProfileRepository.findByMemberId(memberId)
                .orElseThrow(() -> new GeneralException(CompanyErrorCode.PROFILE_NOT_FOUND));

        return convertToResDTO(companyProfile);
    }

    /**
     * 기업 프로필 수정
     */
    @Transactional
    public CompanyProfileResDTO updateCompanyProfile(Long profileId, CompanyProfileReqDTO request, Long memberId) {
        // 1. 프로필 존재 여부 확인 (404)
        CompanyProfile companyProfile = companyProfileRepository.findById(profileId)
                .orElseThrow(() -> new GeneralException(CompanyErrorCode.PROFILE_NOT_FOUND));

        // 2. 수정 권한 확인: 현재 로그인한 회원의 프로필인지 (403)
        if (!companyProfile.getMember().getId().equals(memberId)) {
            throw new GeneralException(CompanyErrorCode.FORBIDDEN_UPDATE);
        }

        // 3. 다른 기업이 사용 중인 사업자 등록번호인지 중복 확인 (409)
        if (companyProfileRepository.existsByBusinessNumberAndIdNot(request.getBusinessNumber(), profileId)) {
            throw new GeneralException(CompanyErrorCode.DUPLICATE_BUSINESS_NUMBER);
        }

        // 4. 수정할 지역 조회 (404)
        Region region = regionRepository.findById(request.getRegionId())
                .orElseThrow(() -> new GeneralException(CompanyErrorCode.REGION_NOT_FOUND));

        // 5. 엔티티 수정 (JPA 더티 체킹 적용)
        companyProfile.updateProfile(
                request.getCompanyName(),
                request.getBusinessNumber(),
                request.getIndustry(),
                request.getWebsiteUrl(),
                request.getLogoUrl(),
                request.getCompanyIntro(),
                region
        );

        return convertToResDTO(companyProfile);
    }

    /**
     * 특정 기업 프로필 상세 조회 (외부 노출용 등)
     */
    @Transactional(readOnly = true)
    public CompanyProfileResDTO getCompanyProfile(Long profileId) {
        // 1. 프로필 조회 (404)
        CompanyProfile companyProfile = companyProfileRepository.findById(profileId)
                .orElseThrow(() -> new GeneralException(CompanyErrorCode.COMPANY_NOT_FOUND));

        return convertToResDTO(companyProfile);
    }

    /**
     * Entity -> Response DTO 변환 공통 메서드
     */
    private CompanyProfileResDTO convertToResDTO(CompanyProfile profile) {
        Long regionId = (profile.getRegion() != null) ? profile.getRegion().getId() : null;

        return CompanyProfileResDTO.builder()
                .companyProfileId(profile.getId())
                .companyName(profile.getCompanyName())
                .businessNumber(profile.getBusinessNumber())
                .industry(profile.getIndustry())
                .websiteUrl(profile.getWebsiteUrl())
                .logoUrl(profile.getLogoUrl())
                .companyIntro(profile.getCompanyIntro())
                .regionId(regionId)
                .verificationStatus(profile.getVerificationStatus())
                .createdAt(profile.getCreatedAt())
                .updatedAt(profile.getUpdatedAt())
                .build();
    }

    /**
     * 기업 사업자 인증 상태 변경 (관리자용)
     * TODO: 추후 관리자 권한 인증 로직 추가 필요
     */
    @Transactional
    public CompanyProfileResDTO updateVerificationStatus(Long profileId, VerificationStatus status) {
        // 1. 프로필 조회 (없을 경우 404 에러)
        CompanyProfile companyProfile = companyProfileRepository.findById(profileId)
                .orElseThrow(() -> new GeneralException(CompanyErrorCode.PROFILE_NOT_FOUND));

        // 2. 인증 상태 업데이트 (JPA 더티 체킹)
        companyProfile.updateVerificationStatus(status);

        // 3. 응답 DTO로 변환하여 반환
        return convertToResDTO(companyProfile);
    }
}