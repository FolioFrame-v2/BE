package com.folioframe.domain.company.service;

import com.folioframe.domain.common.dto.response.TechstackResDTO;
import com.folioframe.domain.common.entity.Region;
import com.folioframe.domain.common.entity.Techstack;
import com.folioframe.domain.common.repository.RegionRepository;
import com.folioframe.domain.common.repository.TechstackRepository;
import com.folioframe.domain.company.dto.request.CompanyProfileReqDTO;
import com.folioframe.domain.company.dto.response.CompanyProfileResDTO;
import com.folioframe.domain.company.dto.response.CompanyProfileSignupInfoResDTO;
import com.folioframe.domain.company.entity.CompanyProfile;
import com.folioframe.domain.company.entity.CompanyTechstack;
import com.folioframe.domain.company.enums.VerificationStatus;
import com.folioframe.domain.company.exception.code.CompanyErrorCode;
import com.folioframe.domain.company.repository.CompanyProfileRepository;
import com.folioframe.domain.company.repository.CompanyTechstackRepository;
import com.folioframe.domain.member.entity.Member;
import com.folioframe.domain.member.repository.MemberRepository;
import com.folioframe.global.apiPayload.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CompanyService {

    private final CompanyProfileRepository companyProfileRepository;
    private final MemberRepository memberRepository;
    private final RegionRepository regionRepository;
    private final TechstackRepository techstackRepository;
    private final CompanyTechstackRepository companyTechstackRepository;

    /**
     * 회원가입 정보 프리필 — 담당자 이름/연락처(Member)와 사업자번호(회원가입 시 임시 보관)를 내려준다
     */
    @Transactional(readOnly = true)
    public CompanyProfileSignupInfoResDTO getSignupInfo(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new GeneralException(CompanyErrorCode.MEMBER_NOT_FOUND));

        return CompanyProfileSignupInfoResDTO.builder()
                .contactName(member.getName())
                .contactPhone(member.getPhone())
                .businessNumber(member.getBusinessNumber())
                .build();
    }

    /**
     * 기업 프로필 등록
     */
    @Transactional
    public CompanyProfileResDTO createCompanyProfile(CompanyProfileReqDTO request, Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new GeneralException(CompanyErrorCode.MEMBER_NOT_FOUND));

        if (companyProfileRepository.existsByMemberId(memberId)) {
            throw new GeneralException(CompanyErrorCode.DUPLICATE_COMPANY_PROFILE);
        }

        Region region = regionRepository.findById(request.getRegionId())
                .orElseThrow(() -> new GeneralException(CompanyErrorCode.REGION_NOT_FOUND));

        CompanyProfile companyProfile = CompanyProfile.builder()
                .member(member)
                .companyName(request.getCompanyName())
                .businessNumber(member.getBusinessNumber())
                .industry(request.getIndustry())
                .websiteUrl(request.getWebsiteUrl())
                .companyIntro(request.getCompanyIntro())
                .employeeSize(request.getEmployeeSize())
                .region(region)
                .contactName(request.getContactName())
                .contactEmail(request.getContactEmail())
                .contactPhone(request.getContactPhone())
                .verificationStatus(VerificationStatus.PENDING)
                .build();

        CompanyProfile savedProfile = companyProfileRepository.save(companyProfile);

        saveTechStacks(savedProfile, request.getTechStackIds());

        return convertToResDTO(savedProfile);
    }

    /**
     * 내 기업 프로필 조회
     */
    @Transactional(readOnly = true)
    public CompanyProfileResDTO getMyCompanyProfile(Long memberId) {
        CompanyProfile companyProfile = companyProfileRepository.findByMemberId(memberId)
                .orElseThrow(() -> new GeneralException(CompanyErrorCode.PROFILE_NOT_FOUND));

        return convertToResDTO(companyProfile);
    }

    /**
     * 기업 프로필 수정
     */
    @Transactional
    public CompanyProfileResDTO updateCompanyProfile(Long profileId, CompanyProfileReqDTO request, Long memberId) {
        CompanyProfile companyProfile = companyProfileRepository.findById(profileId)
                .orElseThrow(() -> new GeneralException(CompanyErrorCode.PROFILE_NOT_FOUND));

        if (!companyProfile.getMember().getId().equals(memberId)) {
            throw new GeneralException(CompanyErrorCode.FORBIDDEN_UPDATE);
        }

        Region region = regionRepository.findById(request.getRegionId())
                .orElseThrow(() -> new GeneralException(CompanyErrorCode.REGION_NOT_FOUND));

        companyProfile.updateProfile(
                request.getCompanyName(),
                request.getIndustry(),
                request.getWebsiteUrl(),
                request.getCompanyIntro(),
                region,
                request.getEmployeeSize(),
                request.getContactName(),
                request.getContactEmail(),
                request.getContactPhone()
        );

        updateTechStacks(companyProfile, request.getTechStackIds());

        return convertToResDTO(companyProfile);
    }

    /**
     * 특정 기업 프로필 상세 조회 (외부 노출용 등)
     */
    @Transactional(readOnly = true)
    public CompanyProfileResDTO getCompanyProfile(Long profileId) {
        CompanyProfile companyProfile = companyProfileRepository.findById(profileId)
                .orElseThrow(() -> new GeneralException(CompanyErrorCode.COMPANY_NOT_FOUND));

        return convertToResDTO(companyProfile);
    }

    private void saveTechStacks(CompanyProfile companyProfile, List<Long> techStackIds) {
        if (techStackIds == null || techStackIds.isEmpty()) return;

        List<CompanyTechstack> companyTechstacks = techStackIds.stream()
                .map(id -> {
                    Techstack techstack = techstackRepository.findById(id)
                            .orElseThrow(() -> new GeneralException(CompanyErrorCode.INVALID_INPUT));
                    return CompanyTechstack.builder()
                            .companyProfile(companyProfile)
                            .techstack(techstack)
                            .build();
                })
                .toList();

        companyTechstackRepository.saveAll(companyTechstacks);
    }

    private void updateTechStacks(CompanyProfile companyProfile, List<Long> techStackIds) {
        companyTechstackRepository.deleteAllByCompanyProfile(companyProfile);
        saveTechStacks(companyProfile, techStackIds);
    }

    /**
     * 기업 사업자 인증 상태 변경 (관리자용)
     * TODO: 추후 관리자 권한 인증 로직 추가 필요
     */
    @Transactional
    public CompanyProfileResDTO updateVerificationStatus(Long profileId, VerificationStatus status) {
        CompanyProfile companyProfile = companyProfileRepository.findById(profileId)
                .orElseThrow(() -> new GeneralException(CompanyErrorCode.PROFILE_NOT_FOUND));

        companyProfile.updateVerificationStatus(status);

        return convertToResDTO(companyProfile);
    }

    /**
     * Entity -> Response DTO 변환 공통 메서드
     */
    private CompanyProfileResDTO convertToResDTO(CompanyProfile profile) {
        Long regionId = (profile.getRegion() != null) ? profile.getRegion().getId() : null;

        List<TechstackResDTO> techStacks = companyTechstackRepository.findAllByCompanyProfile(profile)
                .stream().map(ct -> TechstackResDTO.from(ct.getTechstack())).toList();

        return CompanyProfileResDTO.builder()
                .companyProfileId(profile.getId())
                .companyName(profile.getCompanyName())
                .businessNumber(profile.getBusinessNumber())
                .industry(profile.getIndustry())
                .websiteUrl(profile.getWebsiteUrl())
                .companyIntro(profile.getCompanyIntro())
                .employeeSize(profile.getEmployeeSize())
                .regionId(regionId)
                .contactName(profile.getContactName())
                .contactEmail(profile.getContactEmail())
                .contactPhone(profile.getContactPhone())
                .techStacks(techStacks)
                .verificationStatus(profile.getVerificationStatus())
                .createdAt(profile.getCreatedAt())
                .updatedAt(profile.getUpdatedAt())
                .build();
    }
}
