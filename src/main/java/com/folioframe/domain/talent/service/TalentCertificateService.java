package com.folioframe.domain.talent.service;

import com.folioframe.domain.talent.dto.request.TalentCertificateReqDTO;
import com.folioframe.domain.talent.dto.response.TalentCertificateResDTO;
import com.folioframe.domain.talent.entity.TalentCertificate;
import com.folioframe.domain.talent.entity.TalentProfile;
import com.folioframe.domain.talent.exception.code.TalentProfileErrorCode;
import com.folioframe.domain.talent.repository.TalentCertificateRepository;
import com.folioframe.domain.talent.repository.TalentProfileRepository;
import com.folioframe.global.apiPayload.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TalentCertificateService {

    private final TalentCertificateRepository certificateRepository;
    private final TalentProfileRepository talentProfileRepository;

    @Transactional
    public TalentCertificateResDTO create(Long memberId, TalentCertificateReqDTO request) {
        TalentProfile profile = findMyProfile(memberId);

        TalentCertificate certificate = TalentCertificate.builder()
                .talentProfile(profile)
                .name(request.name())
                .issuer(request.issuer())
                .issuedAt(request.issuedAt())
                .expiresAt(request.expiresAt())
                .credentialId(request.credentialId())
                .build();
        certificateRepository.save(certificate);

        return TalentCertificateResDTO.from(certificate);
    }

    @Transactional(readOnly = true)
    public List<TalentCertificateResDTO> getList(Long memberId) {
        TalentProfile profile = findMyProfile(memberId);
        return certificateRepository.findAllByTalentProfile(profile)
                .stream()
                .map(TalentCertificateResDTO::from)
                .toList();
    }

    @Transactional
    public TalentCertificateResDTO update(Long memberId, Long certificateId, TalentCertificateReqDTO request) {
        TalentProfile profile = findMyProfile(memberId);
        TalentCertificate certificate = findCertificate(certificateId);
        validateBelongsToProfile(certificate, profile);

        certificate.update(
                request.name(),
                request.issuer(),
                request.issuedAt(),
                request.expiresAt(),
                request.credentialId()
        );

        return TalentCertificateResDTO.from(certificate);
    }

    @Transactional
    public void delete(Long memberId, Long certificateId) {
        TalentProfile profile = findMyProfile(memberId);
        TalentCertificate certificate = findCertificate(certificateId);
        validateBelongsToProfile(certificate, profile);

        certificateRepository.delete(certificate);
    }

    private TalentProfile findMyProfile(Long memberId) {
        return talentProfileRepository.findByMemberId(memberId)
                .orElseThrow(() -> new GeneralException(TalentProfileErrorCode.PROFILE_NOT_FOUND));
    }

    private TalentCertificate findCertificate(Long certificateId) {
        return certificateRepository.findById(certificateId)
                .orElseThrow(() -> new GeneralException(TalentProfileErrorCode.CERTIFICATE_NOT_FOUND));
    }

    private void validateBelongsToProfile(TalentCertificate certificate, TalentProfile profile) {
        if (!certificate.getTalentProfile().getId().equals(profile.getId())) {
            throw new GeneralException(TalentProfileErrorCode.CERTIFICATE_NOT_IN_PROFILE);
        }
    }
}
