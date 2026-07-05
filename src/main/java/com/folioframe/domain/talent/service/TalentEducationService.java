package com.folioframe.domain.talent.service;

import com.folioframe.domain.talent.dto.request.TalentEducationReqDTO;
import com.folioframe.domain.talent.dto.response.TalentEducationResDTO;
import com.folioframe.domain.talent.entity.TalentEducation;
import com.folioframe.domain.talent.entity.TalentProfile;
import com.folioframe.domain.talent.exception.code.TalentProfileErrorCode;
import com.folioframe.domain.talent.repository.TalentEducationRepository;
import com.folioframe.domain.talent.repository.TalentProfileRepository;
import com.folioframe.global.apiPayload.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TalentEducationService {

    private final TalentEducationRepository educationRepository;
    private final TalentProfileRepository talentProfileRepository;

    @Transactional
    public TalentEducationResDTO create(Long memberId, TalentEducationReqDTO request) {
        TalentProfile profile = findMyProfile(memberId);

        TalentEducation education = TalentEducation.builder()
                .talentProfile(profile)
                .schoolName(request.schoolName())
                .major(request.major())
                .degree(request.degree())
                .startedAt(request.startedAt())
                .endedAt(request.endedAt())
                .status(request.status())
                .build();
        educationRepository.save(education);

        return TalentEducationResDTO.from(education);
    }

    @Transactional(readOnly = true)
    public List<TalentEducationResDTO> getList(Long memberId) {
        TalentProfile profile = findMyProfile(memberId);
        return educationRepository.findAllByTalentProfile(profile)
                .stream()
                .map(TalentEducationResDTO::from)
                .toList();
    }

    @Transactional
    public TalentEducationResDTO update(Long memberId, Long educationId, TalentEducationReqDTO request) {
        TalentProfile profile = findMyProfile(memberId);
        TalentEducation education = findEducation(educationId);
        validateBelongsToProfile(education, profile);

        education.update(
                request.schoolName(),
                request.major(),
                request.degree(),
                request.startedAt(),
                request.endedAt(),
                request.status()
        );

        return TalentEducationResDTO.from(education);
    }

    @Transactional
    public void delete(Long memberId, Long educationId) {
        TalentProfile profile = findMyProfile(memberId);
        TalentEducation education = findEducation(educationId);
        validateBelongsToProfile(education, profile);

        educationRepository.delete(education);
    }

    private TalentProfile findMyProfile(Long memberId) {
        return talentProfileRepository.findByMemberId(memberId)
                .orElseThrow(() -> new GeneralException(TalentProfileErrorCode.PROFILE_NOT_FOUND));
    }

    private TalentEducation findEducation(Long educationId) {
        return educationRepository.findById(educationId)
                .orElseThrow(() -> new GeneralException(TalentProfileErrorCode.EDUCATION_NOT_FOUND));
    }

    private void validateBelongsToProfile(TalentEducation education, TalentProfile profile) {
        if (!education.getTalentProfile().getId().equals(profile.getId())) {
            throw new GeneralException(TalentProfileErrorCode.EDUCATION_NOT_IN_PROFILE);
        }
    }
}
