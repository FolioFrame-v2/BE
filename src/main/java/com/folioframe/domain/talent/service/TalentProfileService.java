package com.folioframe.domain.talent.service;

import com.folioframe.domain.common.entity.Region;
import com.folioframe.domain.common.entity.Tag;
import com.folioframe.domain.common.entity.Techstack;
import com.folioframe.domain.common.enums.CareerLevel;
import com.folioframe.domain.common.enums.JobRole;
import com.folioframe.domain.common.repository.RegionRepository;
import com.folioframe.domain.common.repository.TagRepository;
import com.folioframe.domain.common.repository.TechstackRepository;
import com.folioframe.domain.job.enums.EmploymentType;
import com.folioframe.domain.member.entity.Member;
import com.folioframe.domain.member.repository.MemberRepository;
import com.folioframe.domain.talent.dto.request.TalentProfileCreateRequest;
import com.folioframe.domain.talent.dto.request.TalentProfileUpdateRequest;
import com.folioframe.domain.talent.dto.response.*;
import com.folioframe.domain.talent.entity.TalentProfile;
import com.folioframe.domain.talent.entity.TalentTag;
import com.folioframe.domain.talent.entity.TalentTechstack;
import com.folioframe.domain.talent.exception.code.TalentProfileErrorCode;
import com.folioframe.domain.talent.repository.TalentProfileRepository;
import com.folioframe.domain.talent.repository.TalentTagRepository;
import com.folioframe.domain.talent.repository.TalentTechstackRepository;
import com.folioframe.global.apiPayload.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final TagRepository tagRepository;
    private final TalentTagRepository talentTagRepository;

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
                .linkedinUrl(request.getLinkedinUrl())
                .applicationField(request.getApplicationField())
                .jobRole(request.getJobRole())
                .careerLevel(request.getCareerLevel())
                .employmentType(request.getEmploymentType())
                .oneLiner(request.getOneLiner())
                .introduction(request.getIntroduction())
                .profileVisibility(request.getProfileVisibility())
                .profileImageUrl(request.getProfileImageUrl())
                .jobSeekingStatus(request.getJobSeekingStatus())
                .build();

        TalentProfile savedProfile = talentProfileRepository.save(profile);

        saveTechStacks(savedProfile, request.getTechStackIds());
        saveTags(savedProfile, request.getTagIds());

        return savedProfile.getId();
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
        updateTags(profile, request.getTagIds());

        return convertToProfileResponse(profile);
    }

    public TalentProfileSearchResponse searchProfiles(String sort, CareerLevel career, EmploymentType employment, String techStack, JobRole job, Pageable pageable) {
        Page<TalentProfileSimpleResponse> pageResult = talentProfileRepository.searchDynamic(sort, career, employment, techStack, job, pageable);

        return TalentProfileSearchResponse.builder()
                .searchCondition(TalentProfileSearchResponse.SearchConditionResponse.builder()
                        .sort(sort)
                        .career(career != null ? career.name() : null)
                        .job(job != null ? job.name() : null)
                        .employment(employment != null ? employment.name() : null)
                        .build())
                .content(pageResult.getContent())
                .pageable(TalentProfileSearchResponse.PageableResponse.builder()
                        .pageNumber(pageResult.getNumber())
                        .pageSize(pageResult.getSize())
                        .totalElements(pageResult.getTotalElements())
                        .build())
                .build();
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

    private void saveTags(TalentProfile profile, List<Long> tagIds) {
        if (tagIds == null || tagIds.isEmpty()) return;

        List<TalentTag> talentTags = tagIds.stream()
                .map(id -> {
                    Tag tag = tagRepository.findById(id)
                            .orElseThrow(() -> new GeneralException(TalentProfileErrorCode.INVALID_INPUT));
                    return TalentTag.builder()
                            .talentProfile(profile)
                            .tag(tag)
                            .build();
                })
                .collect(Collectors.toList());

        talentTagRepository.saveAll(talentTags);
    }

    private void updateTags(TalentProfile profile, List<Long> tagIds) {
        talentTagRepository.deleteAllByTalentProfile(profile);
        saveTags(profile, tagIds);
    }

    private TalentProfileResponse convertToProfileResponse(TalentProfile profile) {
        List<TalentTechStackResponse> techStacks = talentTechstackRepository.findAllByTalentProfile(profile)
                .stream().map(tt -> new TalentTechStackResponse(tt.getTechstack().getId(), tt.getTechstack().getName())).collect(Collectors.toList());

        List<TalentTagResponse> tags = talentTagRepository.findAllByTalentProfile(profile)
                .stream().map(tt -> new TalentTagResponse(tt.getTag().getId(), tt.getTag().getName())).collect(Collectors.toList());

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
                .linkedinUrl(profile.getLinkedinUrl())
                .applicationField(profile.getApplicationField())
                .jobRole(profile.getJobRole())
                .careerLevel(profile.getCareerLevel())
                .employmentType(profile.getEmploymentType())
                .oneLiner(profile.getOneLiner())
                .introduction(profile.getIntroduction())
                .profileVisibility(profile.getProfileVisibility())
                .profileImageUrl(profile.getProfileImageUrl())
                .jobSeekingStatus(profile.getJobSeekingStatus())
                .createdAt(profile.getCreatedAt() != null ? profile.getCreatedAt().toString() : null)
                .updatedAt(profile.getUpdatedAt() != null ? profile.getUpdatedAt().toString() : null)
                .techStacks(techStacks)
                .tags(tags)
                .build();
    }
}