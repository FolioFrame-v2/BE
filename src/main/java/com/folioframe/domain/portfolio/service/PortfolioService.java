package com.folioframe.domain.portfolio.service;

import com.folioframe.domain.portfolio.dto.request.PortfolioCreateReqDTO;
import com.folioframe.domain.portfolio.dto.request.PortfolioUpdateReqDTO;
import com.folioframe.domain.portfolio.dto.request.PortfolioVisibilityReqDTO;
import com.folioframe.domain.portfolio.dto.response.PortfolioDetailResDTO;
import com.folioframe.domain.portfolio.dto.response.PortfolioResDTO;
import com.folioframe.domain.portfolio.dto.response.PortfolioSummaryResDTO;
import com.folioframe.domain.portfolio.enums.EditStatus;
import com.folioframe.domain.portfolio.enums.PortfolioSortType;
import com.folioframe.global.dto.PageRequest;
import com.folioframe.global.dto.PageResponse;
import com.folioframe.domain.portfolio.entity.Portfolio;
import com.folioframe.domain.portfolio.entity.PortfolioField;
import com.folioframe.domain.portfolio.entity.PortfolioTemplate;
import com.folioframe.domain.portfolio.entity.TemplateField;
import com.folioframe.domain.portfolio.enums.PortfolioVisibility;
import com.folioframe.domain.portfolio.exception.PortfolioException;
import com.folioframe.domain.portfolio.exception.code.PortfolioErrorCode;
import com.folioframe.domain.portfolio.repository.PortfolioCareerRepository;
import com.folioframe.domain.portfolio.repository.PortfolioCertificateRepository;
import com.folioframe.domain.portfolio.repository.PortfolioEducationRepository;
import com.folioframe.domain.portfolio.repository.PortfolioFieldRepository;
import com.folioframe.domain.portfolio.repository.PortfolioProjectRepository;
import com.folioframe.domain.portfolio.repository.PortfolioRepository;
import com.folioframe.domain.portfolio.repository.PortfolioTemplateRepository;
import com.folioframe.domain.portfolio.repository.TemplateFieldRepository;
import com.folioframe.domain.talent.entity.TalentProfile;
import com.folioframe.domain.talent.repository.TalentProfileRepository;
import com.folioframe.global.apiPayload.code.GeneralErrorCode;
import com.folioframe.global.apiPayload.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PortfolioService {

    private final PortfolioRepository portfolioRepository;
    private final TalentProfileRepository talentProfileRepository;
    private final PortfolioTemplateRepository templateRepository;
    private final TemplateFieldRepository templateFieldRepository;
    private final PortfolioFieldRepository portfolioFieldRepository;
    private final PortfolioEducationRepository educationRepository;
    private final PortfolioCareerRepository careerRepository;
    private final PortfolioCertificateRepository certificateRepository;
    private final PortfolioProjectRepository projectRepository;

    @Transactional
    public PortfolioResDTO create(Long memberId, PortfolioCreateReqDTO request) {
        TalentProfile talentProfile = findTalentProfile(memberId);

        PortfolioTemplate template = templateRepository.findById(request.templateId())
                .orElseThrow(() -> new PortfolioException(PortfolioErrorCode.TEMPLATE_NOT_FOUND));

        Portfolio portfolio = Portfolio.builder()
                .talentProfile(talentProfile)
                .template(template)
                .title(request.title())
                .jobRole(request.jobRole())
                .careerSummary(request.careerSummary())
                .contactEmail(request.contactEmail())
                .githubUrl(request.githubUrl())
                .personalWebsite(request.personalWebsite())
                .oneLiner(request.oneLiner())
                .description(request.description())
                .visibility(request.visibility() != null ? request.visibility() : PortfolioVisibility.PRIVATE)
                .publicSlug(UUID.randomUUID().toString())
                .build();

        portfolioRepository.save(portfolio);

        List<TemplateField> templateFields = templateFieldRepository
                .findAllByTemplateOrderByDisplayOrder(template);

        List<PortfolioField> portfolioFields = templateFields.stream()
                .map(tf -> PortfolioField.builder()
                        .portfolio(portfolio)
                        .title(tf.getTitle())
                        .content("")
                        .displayOrder(tf.getDisplayOrder())
                        .build())
                .toList();

        portfolioFieldRepository.saveAll(portfolioFields);

        return PortfolioResDTO.from(portfolio);
    }

    @Transactional(readOnly = true)
    public PageResponse<PortfolioSummaryResDTO> getList(Long memberId, PageRequest pageRequest) {
        TalentProfile talentProfile = findTalentProfile(memberId);
        return PageResponse.of(
                portfolioRepository.findAllByTalentProfileOrderByUpdatedAtDesc(talentProfile, pageRequest.toPageable())
                        .map(PortfolioSummaryResDTO::from)
        );
    }

    @Transactional(readOnly = true)
    public PageResponse<PortfolioSummaryResDTO> getPublicList(PortfolioSortType sortType, PageRequest pageRequest, Long memberId) {
        if (sortType == null) sortType = PortfolioSortType.LATEST;
        // 비로그인 시 상위 3개만 반환 (프론트에서 회원가입 유도)
        PageRequest effectiveRequest = (memberId == null) ? PageRequest.of(1, 3) : pageRequest;
        return PageResponse.of(
                portfolioRepository.findAllByVisibilityAndEditStatus(
                                PortfolioVisibility.PUBLIC, EditStatus.PUBLISHED,
                                effectiveRequest.toPageable(sortType.getSort()))
                        .map(PortfolioSummaryResDTO::from)
        );
    }

    @Transactional(readOnly = true)
    public PortfolioDetailResDTO getDetail(Long portfolioId, Long memberId) {
        Portfolio portfolio = findPortfolio(portfolioId);

        if (portfolio.getVisibility() != PortfolioVisibility.PUBLIC) {
            validateOwnership(portfolio, memberId);
        }

        return toDetailResDTO(portfolio);
    }

    @Transactional(readOnly = true)
    public PortfolioDetailResDTO getBySlug(String publicSlug) {
        Portfolio portfolio = portfolioRepository.findByPublicSlug(publicSlug)
                .orElseThrow(() -> new PortfolioException(PortfolioErrorCode.PORTFOLIO_NOT_FOUND));

        if (portfolio.getVisibility() == PortfolioVisibility.PRIVATE) {
            throw new PortfolioException(PortfolioErrorCode.PORTFOLIO_ACCESS_DENIED);
        }

        return toDetailResDTO(portfolio);
    }

    @Transactional
    public PortfolioResDTO update(Long portfolioId, Long memberId, PortfolioUpdateReqDTO request) {
        Portfolio portfolio = findPortfolio(portfolioId);
        validateOwnership(portfolio, memberId);

        portfolio.updateInfo(
                request.title(),
                request.jobRole(),
                request.careerSummary(),
                request.contactEmail(),
                request.githubUrl(),
                request.personalWebsite(),
                request.oneLiner(),
                request.description(),
                request.visibility() != null ? request.visibility() : portfolio.getVisibility()
        );

        return PortfolioResDTO.from(portfolio);
    }

    @Transactional
    public PortfolioResDTO changeVisibility(Long portfolioId, Long memberId, PortfolioVisibilityReqDTO request) {
        Portfolio portfolio = findPortfolio(portfolioId);
        validateOwnership(portfolio, memberId);
        portfolio.changeVisibility(request.visibility());
        return PortfolioResDTO.from(portfolio);
    }

    @Transactional
    public PortfolioResDTO publish(Long portfolioId, Long memberId) {
        Portfolio portfolio = findPortfolio(portfolioId);
        validateOwnership(portfolio, memberId);
        portfolio.publish();
        portfolio.getTemplate().increaseUseCount();
        return PortfolioResDTO.from(portfolio);
    }

    @Transactional
    public void delete(Long portfolioId, Long memberId) {
        Portfolio portfolio = findPortfolio(portfolioId);
        validateOwnership(portfolio, memberId);
        portfolioRepository.delete(portfolio);
    }

    private PortfolioDetailResDTO toDetailResDTO(Portfolio portfolio) {
        return PortfolioDetailResDTO.of(
                portfolio,
                portfolioFieldRepository.findAllByPortfolioOrderByDisplayOrder(portfolio),
                educationRepository.findAllByPortfolioOrderByStartedAtDesc(portfolio),
                careerRepository.findAllByPortfolioOrderByStartedAtDesc(portfolio),
                certificateRepository.findAllByPortfolioOrderByIssuedAtDesc(portfolio),
                projectRepository.findAllByPortfolioOrderByCreatedAtDesc(portfolio)
        );
    }

    private TalentProfile findTalentProfile(Long memberId) {
        return talentProfileRepository.findByMemberId(memberId)
                .orElseThrow(() -> new GeneralException(GeneralErrorCode.NOT_FOUND));
    }

    Portfolio findPortfolio(Long portfolioId) {
        return portfolioRepository.findById(portfolioId)
                .orElseThrow(() -> new PortfolioException(PortfolioErrorCode.PORTFOLIO_NOT_FOUND));
    }

    void validateOwnership(Portfolio portfolio, Long memberId) {
        if (!portfolio.getTalentProfile().getMember().getId().equals(memberId)) {
            throw new PortfolioException(PortfolioErrorCode.PORTFOLIO_ACCESS_DENIED);
        }
    }
}
