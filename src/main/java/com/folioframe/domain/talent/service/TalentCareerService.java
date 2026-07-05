package com.folioframe.domain.talent.service;

import com.folioframe.domain.talent.dto.request.TalentCareerReqDTO;
import com.folioframe.domain.talent.dto.response.TalentCareerResDTO;
import com.folioframe.domain.talent.entity.TalentCareer;
import com.folioframe.domain.talent.entity.TalentProfile;
import com.folioframe.domain.talent.exception.code.TalentProfileErrorCode;
import com.folioframe.domain.talent.repository.TalentCareerRepository;
import com.folioframe.domain.talent.repository.TalentProfileRepository;
import com.folioframe.global.apiPayload.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TalentCareerService {

    private final TalentCareerRepository careerRepository;
    private final TalentProfileRepository talentProfileRepository;

    @Transactional
    public TalentCareerResDTO create(Long memberId, TalentCareerReqDTO request) {
        TalentProfile profile = findMyProfile(memberId);

        TalentCareer career = TalentCareer.builder()
                .talentProfile(profile)
                .companyName(request.companyName())
                .position(request.position())
                .description(request.description())
                .startedAt(request.startedAt())
                .endedAt(request.endedAt())
                .build();
        careerRepository.save(career);

        return TalentCareerResDTO.from(career);
    }

    @Transactional(readOnly = true)
    public List<TalentCareerResDTO> getList(Long memberId) {
        TalentProfile profile = findMyProfile(memberId);
        return careerRepository.findAllByTalentProfileOrderByStartedAtDesc(profile)
                .stream()
                .map(TalentCareerResDTO::from)
                .toList();
    }

    @Transactional
    public TalentCareerResDTO update(Long memberId, Long careerId, TalentCareerReqDTO request) {
        TalentProfile profile = findMyProfile(memberId);
        TalentCareer career = findCareer(careerId);
        validateBelongsToProfile(career, profile);

        career.update(
                request.companyName(),
                request.position(),
                request.description(),
                request.startedAt(),
                request.endedAt()
        );

        return TalentCareerResDTO.from(career);
    }

    @Transactional
    public void delete(Long memberId, Long careerId) {
        TalentProfile profile = findMyProfile(memberId);
        TalentCareer career = findCareer(careerId);
        validateBelongsToProfile(career, profile);

        careerRepository.delete(career);
    }

    private TalentProfile findMyProfile(Long memberId) {
        return talentProfileRepository.findByMemberId(memberId)
                .orElseThrow(() -> new GeneralException(TalentProfileErrorCode.PROFILE_NOT_FOUND));
    }

    private TalentCareer findCareer(Long careerId) {
        return careerRepository.findById(careerId)
                .orElseThrow(() -> new GeneralException(TalentProfileErrorCode.CAREER_NOT_FOUND));
    }

    private void validateBelongsToProfile(TalentCareer career, TalentProfile profile) {
        if (!career.getTalentProfile().getId().equals(profile.getId())) {
            throw new GeneralException(TalentProfileErrorCode.CAREER_NOT_IN_PROFILE);
        }
    }
}
