package com.folioframe.domain.portfolio.service;

import com.folioframe.domain.portfolio.dto.request.PortfolioCreateReqDTO;
import com.folioframe.domain.portfolio.dto.request.PortfolioUpdateReqDTO;
import com.folioframe.domain.portfolio.dto.request.PortfolioVisibilityReqDTO;
import com.folioframe.domain.portfolio.dto.response.PortfolioDetailResDTO;
import com.folioframe.domain.portfolio.dto.response.PortfolioResDTO;
import com.folioframe.domain.portfolio.dto.response.PortfolioSummaryResDTO;
import com.folioframe.domain.common.dto.response.TechstackResDTO;
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
import com.folioframe.domain.portfolio.entity.PortfolioProject;
import com.folioframe.domain.portfolio.entity.PortfolioTechstack;
import com.folioframe.domain.portfolio.entity.ProjectTechstack;
import com.folioframe.domain.portfolio.repository.PortfolioProjectRepository;
import com.folioframe.domain.portfolio.repository.PortfolioRepository;
import com.folioframe.domain.portfolio.repository.PortfolioTechstackRepository;
import com.folioframe.domain.portfolio.repository.PortfolioTemplateRepository;
import com.folioframe.domain.portfolio.repository.ProjectTechstackRepository;
import com.folioframe.domain.portfolio.repository.TemplateFieldRepository;
import com.folioframe.domain.talent.entity.TalentProfile;
import com.folioframe.domain.talent.repository.TalentProfileRepository;
import com.folioframe.global.apiPayload.code.GeneralErrorCode;
import com.folioframe.global.apiPayload.exception.GeneralException;
import com.folioframe.domain.common.entity.Techstack;
import com.folioframe.domain.common.repository.TechstackRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

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
    private final ProjectTechstackRepository projectTechstackRepository;
    private final PortfolioTechstackRepository portfolioTechstackRepository;
    private final TechstackRepository techstackRepository;

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
                        .description(tf.getDescription())
                        .content("")
                        .displayOrder(tf.getDisplayOrder())
                        .build())
                .toList();

        portfolioFieldRepository.saveAll(portfolioFields);

        List<Techstack> techstacks = attachTechstacks(portfolio, request.techstackIds());
        portfolio.markSaved();

        return PortfolioResDTO.from(portfolio, techstacks);
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
                request.oneLiner(),
                request.description(),
                request.visibility() != null ? request.visibility() : portfolio.getVisibility()
        );

        List<Techstack> techstacks = replaceTechstacks(portfolio, request.techstackIds());
        portfolio.markSaved();

        return PortfolioResDTO.from(portfolio, techstacks);
    }

    @Transactional
    public PortfolioResDTO changeVisibility(Long portfolioId, Long memberId, PortfolioVisibilityReqDTO request) {
        Portfolio portfolio = findPortfolio(portfolioId);
        validateOwnership(portfolio, memberId);
        portfolio.changeVisibility(request.visibility());
        return PortfolioResDTO.from(portfolio, getTechstacks(portfolio));
    }

    @Transactional
    public PortfolioResDTO publish(Long portfolioId, Long memberId) {
        Portfolio portfolio = findPortfolio(portfolioId);
        validateOwnership(portfolio, memberId);
        portfolio.publish();
        portfolio.getTemplate().increaseUseCount();
        return PortfolioResDTO.from(portfolio, getTechstacks(portfolio));
    }

    @Transactional
    public void delete(Long portfolioId, Long memberId) {
        Portfolio portfolio = findPortfolio(portfolioId);
        validateOwnership(portfolio, memberId);
        portfolioRepository.delete(portfolio);
    }

    @Transactional
    public List<TechstackResDTO> updateTechstacks(Long portfolioId, Long memberId, List<Long> techstackIds) {
        Portfolio portfolio = findPortfolio(portfolioId);
        validateOwnership(portfolio, memberId);

        List<Techstack> techstacks = replaceTechstacks(portfolio, techstackIds);
        portfolio.markSaved();

        return techstacks.stream().map(TechstackResDTO::from).toList();
    }

    private PortfolioDetailResDTO toDetailResDTO(Portfolio portfolio) {
        List<PortfolioProject> projects = projectRepository.findAllByPortfolioOrderByCreatedAtDesc(portfolio);

        Map<Long, List<Techstack>> techstacksByProjectId = projectTechstackRepository
                .findAllByPortfolioProjectInWithTechstack(projects)
                .stream()
                .collect(Collectors.groupingBy(
                        pt -> pt.getPortfolioProject().getId(),
                        Collectors.mapping(ProjectTechstack::getTechstack, Collectors.toList())
                ));

        List<Techstack> techstacks = getTechstacks(portfolio);

        return PortfolioDetailResDTO.of(
                portfolio,
                portfolioFieldRepository.findAllByPortfolioOrderByDisplayOrder(portfolio),
                educationRepository.findAllByPortfolioOrderByStartedAtDesc(portfolio),
                careerRepository.findAllByPortfolioOrderByStartedAtDesc(portfolio),
                certificateRepository.findAllByPortfolioOrderByIssuedAtDesc(portfolio),
                projects,
                techstacksByProjectId,
                techstacks
        );
    }

    private List<Techstack> getTechstacks(Portfolio portfolio) {
        return portfolioTechstackRepository.findAllByPortfolioWithTechstack(portfolio)
                .stream()
                .map(PortfolioTechstack::getTechstack)
                .toList();
    }

    private List<Techstack> replaceTechstacks(Portfolio portfolio, List<Long> techstackIds) {
        portfolioTechstackRepository.deleteAllByPortfolio(portfolio);
        return attachTechstacks(portfolio, techstackIds);
    }

    private List<Techstack> attachTechstacks(Portfolio portfolio, List<Long> techstackIds) {
        if (techstackIds == null || techstackIds.isEmpty()) {
            return List.of();
        }

        Set<Long> uniqueIds = Set.copyOf(techstackIds);
        List<Techstack> techstacks = techstackRepository.findAllByIdIn(techstackIds);
        if (techstacks.size() != uniqueIds.size()) {
            throw new PortfolioException(PortfolioErrorCode.TECHSTACK_NOT_FOUND);
        }

        List<PortfolioTechstack> portfolioTechstacks = techstacks.stream()
                .map(techstack -> PortfolioTechstack.builder()
                        .portfolio(portfolio)
                        .techstack(techstack)
                        .build())
                .toList();
        portfolioTechstackRepository.saveAll(portfolioTechstacks);

        return techstacks;
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
