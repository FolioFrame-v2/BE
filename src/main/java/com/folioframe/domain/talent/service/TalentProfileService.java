package com.folioframe.domain.talent.service;

import com.folioframe.domain.common.dto.response.PartResDTO;
import com.folioframe.domain.common.entity.Part;
import com.folioframe.domain.common.entity.Region;
import com.folioframe.domain.common.entity.Techstack;
import com.folioframe.domain.common.repository.PartRepository;
import com.folioframe.domain.common.repository.RegionRepository;
import com.folioframe.domain.common.repository.TechstackRepository;
import com.folioframe.domain.member.entity.Member;
import com.folioframe.domain.member.repository.MemberRepository;
import com.folioframe.domain.talent.dto.request.TalentCareerReqDTO;
import com.folioframe.domain.talent.dto.request.TalentCertificateReqDTO;
import com.folioframe.domain.talent.dto.request.TalentEducationReqDTO;
import com.folioframe.domain.talent.dto.request.TalentProfileCreateRequest;
import com.folioframe.domain.talent.dto.request.TalentProfileUpdateRequest;
import com.folioframe.domain.talent.dto.response.*;
import com.folioframe.domain.talent.entity.TalentCareer;
import com.folioframe.domain.talent.entity.TalentCertificate;
import com.folioframe.domain.talent.entity.TalentEducation;
import com.folioframe.domain.talent.entity.TalentPart;
import com.folioframe.domain.talent.entity.TalentProfile;
import com.folioframe.domain.talent.entity.TalentTechstack;
import com.folioframe.domain.talent.exception.code.TalentProfileErrorCode;
import com.folioframe.domain.talent.repository.TalentCareerRepository;
import com.folioframe.domain.talent.repository.TalentCertificateRepository;
import com.folioframe.domain.talent.repository.TalentEducationRepository;
import com.folioframe.domain.talent.repository.TalentPartRepository;
import com.folioframe.domain.talent.repository.TalentProfileRepository;
import com.folioframe.domain.talent.repository.TalentTechstackRepository;
import com.folioframe.global.apiPayload.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TalentProfileService {

    private final TalentProfileRepository talentProfileRepository;
    private final MemberRepository memberRepository;
    private final RegionRepository regionRepository;
    private final TechstackRepository techstackRepository;
    private final TalentTechstackRepository talentTechstackRepository;
    private final PartRepository partRepository;
    private final TalentPartRepository talentPartRepository;
    private final TalentCareerRepository talentCareerRepository;
    private final TalentEducationRepository talentEducationRepository;
    private final TalentCertificateRepository talentCertificateRepository;

    @Transactional
    public Long createProfile(Long memberId, TalentProfileCreateRequest request) {
        if (talentProfileRepository.findByMemberId(memberId).isPresent()) {
            throw new GeneralException(TalentProfileErrorCode.PROFILE_ALREADY_EXISTS);
        }

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new GeneralException(TalentProfileErrorCode.MEMBER_NOT_FOUND));

        Region region = regionRepository.findById(request.getRegionId())
                .orElseThrow(() -> new GeneralException(TalentProfileErrorCode.REGION_NOT_FOUND));

        TalentProfile profile = TalentProfile.builder()
                .member(member)
                .name(request.getName())
                .region(region)
                .contactEmail(request.getContactEmail())
                .phoneNumber(request.getPhoneNumber())
                .age(request.getAge())
                .gender(request.getGender())
                .githubUrl(request.getGithubUrl())
                .portfolioWebsite(request.getPortfolioWebsite())
                .careerYears(request.getCareerYears())
                .oneLiner(request.getOneLiner())
                .build();

        TalentProfile savedProfile = talentProfileRepository.save(profile);

        saveTechStacks(savedProfile, request.getTechStackIds());
        saveParts(savedProfile, request.getPartIds());
        saveCareers(savedProfile, request.getCareers());
        saveEducations(savedProfile, request.getEducations());
        saveCertificates(savedProfile, request.getCertificates());

        return savedProfile.getId();
    }

    public TalentProfileSignupInfoResponse getSignupInfo(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new GeneralException(TalentProfileErrorCode.MEMBER_NOT_FOUND));

        Integer age = member.getBirthDate() != null
                ? Period.between(member.getBirthDate(), LocalDate.now()).getYears()
                : null;

        return TalentProfileSignupInfoResponse.builder()
                .name(member.getName())
                .phone(member.getPhone())
                .age(age)
                .build();
    }

    public TalentProfileResponse getMyProfile(Long memberId) {
        TalentProfile profile = talentProfileRepository.findByMemberId(memberId)
                .orElseThrow(() -> new GeneralException(TalentProfileErrorCode.PROFILE_NOT_FOUND));

        return convertToProfileResponse(profile);
    }

    @Transactional
    public TalentProfileResponse updateProfile(Long memberId, TalentProfileUpdateRequest request) {
        TalentProfile profile = talentProfileRepository.findByMemberId(memberId)
                .orElseThrow(() -> new GeneralException(TalentProfileErrorCode.PROFILE_NOT_FOUND));

        Region region = regionRepository.findById(request.getRegionId())
                .orElseThrow(() -> new GeneralException(TalentProfileErrorCode.REGION_NOT_FOUND));

        profile.updateProfile(request, region);

        updateTechStacks(profile, request.getTechStackIds());
        updateParts(profile, request.getPartIds());

        return convertToProfileResponse(profile);
    }

    private void saveTechStacks(TalentProfile profile, List<Long> techStackIds) {
        if (techStackIds == null || techStackIds.isEmpty()) return;

        List<TalentTechstack> talentTechstacks = techStackIds.stream()
                .map(id -> {
                    Techstack techstack = techstackRepository.findById(id)
                            .orElseThrow(() -> new GeneralException(TalentProfileErrorCode.INVALID_INPUT));
                    return TalentTechstack.builder()
                            .talentProfile(profile)
                            .techstack(techstack)
                            .build();
                })
                .collect(Collectors.toList());

        talentTechstackRepository.saveAll(talentTechstacks);
    }

    private void updateTechStacks(TalentProfile profile, List<Long> techStackIds) {
        talentTechstackRepository.deleteAllByTalentProfile(profile);
        saveTechStacks(profile, techStackIds);
    }

    private void saveParts(TalentProfile profile, List<Long> partIds) {
        if (partIds == null || partIds.isEmpty()) return;

        List<TalentPart> talentParts = partIds.stream()
                .map(id -> {
                    Part part = partRepository.findById(id)
                            .orElseThrow(() -> new GeneralException(TalentProfileErrorCode.INVALID_INPUT));
                    return TalentPart.builder()
                            .talentProfile(profile)
                            .part(part)
                            .build();
                })
                .collect(Collectors.toList());

        talentPartRepository.saveAll(talentParts);
    }

    private void updateParts(TalentProfile profile, List<Long> partIds) {
        talentPartRepository.deleteAllByTalentProfile(profile);
        saveParts(profile, partIds);
    }

    private void saveCareers(TalentProfile profile, List<TalentCareerReqDTO> careers) {
        if (careers == null || careers.isEmpty()) return;

        List<TalentCareer> talentCareers = careers.stream()
                .map(c -> TalentCareer.builder()
                        .talentProfile(profile)
                        .companyName(c.companyName())
                        .position(c.position())
                        .description(c.description())
                        .startedAt(c.startedAt())
                        .endedAt(c.endedAt())
                        .build())
                .collect(Collectors.toList());

        talentCareerRepository.saveAll(talentCareers);
    }

    private void saveEducations(TalentProfile profile, List<TalentEducationReqDTO> educations) {
        if (educations == null || educations.isEmpty()) return;

        List<TalentEducation> talentEducations = educations.stream()
                .map(e -> TalentEducation.builder()
                        .talentProfile(profile)
                        .schoolName(e.schoolName())
                        .major(e.major())
                        .degree(e.degree())
                        .startedAt(e.startedAt())
                        .endedAt(e.endedAt())
                        .status(e.status())
                        .build())
                .collect(Collectors.toList());

        talentEducationRepository.saveAll(talentEducations);
    }

    private void saveCertificates(TalentProfile profile, List<TalentCertificateReqDTO> certificates) {
        if (certificates == null || certificates.isEmpty()) return;

        List<TalentCertificate> talentCertificates = certificates.stream()
                .map(c -> TalentCertificate.builder()
                        .talentProfile(profile)
                        .name(c.name())
                        .issuer(c.issuer())
                        .issuedAt(c.issuedAt())
                        .expiresAt(c.expiresAt())
                        .credentialId(c.credentialId())
                        .build())
                .collect(Collectors.toList());

        talentCertificateRepository.saveAll(talentCertificates);
    }

    private TalentProfileResponse convertToProfileResponse(TalentProfile profile) {
        List<TalentTechStackResponse> techStacks = talentTechstackRepository.findAllByTalentProfile(profile)
                .stream().map(tt -> new TalentTechStackResponse(tt.getTechstack().getId(), tt.getTechstack().getName())).collect(Collectors.toList());

        List<PartResDTO> parts = talentPartRepository.findAllByTalentProfile(profile)
                .stream().map(tp -> PartResDTO.from(tp.getPart())).collect(Collectors.toList());

        List<TalentCareerResDTO> careers = talentCareerRepository.findAllByTalentProfileOrderByStartedAtDesc(profile)
                .stream().map(TalentCareerResDTO::from).collect(Collectors.toList());

        List<TalentEducationResDTO> educations = talentEducationRepository.findAllByTalentProfile(profile)
                .stream().map(TalentEducationResDTO::from).collect(Collectors.toList());

        List<TalentCertificateResDTO> certificates = talentCertificateRepository.findAllByTalentProfile(profile)
                .stream().map(TalentCertificateResDTO::from).collect(Collectors.toList());

        return TalentProfileResponse.builder()
                .talentProfileId(profile.getId())
                .name(profile.getName())
                .regionId(profile.getRegion().getId())
                .contactEmail(profile.getContactEmail())
                .phoneNumber(profile.getPhoneNumber())
                .age(profile.getAge())
                .gender(profile.getGender())
                .githubUrl(profile.getGithubUrl())
                .portfolioWebsite(profile.getPortfolioWebsite())
                .parts(parts)
                .careerYears(profile.getCareerYears())
                .oneLiner(profile.getOneLiner())
                .createdAt(profile.getCreatedAt() != null ? profile.getCreatedAt().toString() : null)
                .updatedAt(profile.getUpdatedAt() != null ? profile.getUpdatedAt().toString() : null)
                .techStacks(techStacks)
                .careers(careers)
                .educations(educations)
                .certificates(certificates)
                .build();
    }
}