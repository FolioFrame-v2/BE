package com.folioframe.domain.portfolio.service;

import com.folioframe.domain.portfolio.ai.dto.client.AiFieldInputReqDTO;
import com.folioframe.domain.portfolio.ai.entity.PortfolioAiFeedback;
import com.folioframe.domain.portfolio.ai.entity.PortfolioAiField;
import com.folioframe.domain.portfolio.ai.enums.AiFeedbackStatus;
import com.folioframe.domain.portfolio.ai.enums.AiFieldTargetType;
import com.folioframe.domain.portfolio.ai.repository.PortfolioAiFeedbackRepository;
import com.folioframe.domain.portfolio.ai.repository.PortfolioAiFieldRepository;
import com.folioframe.domain.portfolio.dto.request.PortfolioCreateReqDTO;
import com.folioframe.domain.portfolio.dto.request.PortfolioUpdateReqDTO;
import com.folioframe.domain.portfolio.dto.request.PortfolioVisibilityReqDTO;
import com.folioframe.domain.portfolio.dto.response.PortfolioDetailResDTO;
import com.folioframe.domain.portfolio.dto.response.PortfolioMyListResDTO;
import com.folioframe.domain.portfolio.dto.response.PortfolioPublicListResDTO;
import com.folioframe.domain.portfolio.dto.response.PortfolioResDTO;
import com.folioframe.domain.common.dto.response.TechstackResDTO;
import com.folioframe.domain.common.enums.CareerLevel;
import com.folioframe.domain.common.enums.JobRole;
import com.folioframe.domain.common.service.RegionService;
import com.folioframe.domain.portfolio.enums.PortfolioSortType;
import com.folioframe.global.dto.PageRequest;
import com.folioframe.global.dto.PageResponse;
import com.folioframe.domain.portfolio.entity.Portfolio;
import com.folioframe.domain.portfolio.entity.PortfolioCareer;
import com.folioframe.domain.portfolio.entity.PortfolioCertificate;
import com.folioframe.domain.portfolio.entity.PortfolioEducation;
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
import com.folioframe.domain.talent.repository.TalentCareerRepository;
import com.folioframe.domain.talent.repository.TalentCertificateRepository;
import com.folioframe.domain.talent.repository.TalentEducationRepository;
import com.folioframe.domain.talent.repository.TalentProfileRepository;
import com.folioframe.global.apiPayload.code.GeneralErrorCode;
import com.folioframe.global.apiPayload.exception.GeneralException;
import com.folioframe.domain.common.entity.Techstack;
import com.folioframe.domain.common.repository.TechstackRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
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
    private final PortfolioAiFeedbackRepository portfolioAiFeedbackRepository;
    private final PortfolioAiFieldRepository portfolioAiFieldRepository;
    private final TalentCareerRepository talentCareerRepository;
    private final TalentEducationRepository talentEducationRepository;
    private final TalentCertificateRepository talentCertificateRepository;
    private final RegionService regionService;

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

        copyTalentCareerEducationCertificate(talentProfile, portfolio);

        List<Techstack> techstacks = attachTechstacks(portfolio, request.techstackIds());
        portfolio.markSaved();

        return PortfolioResDTO.from(portfolio, techstacks);
    }

    // 프로필에 등록해둔 경력/학력/자격증을 새 포트폴리오로 복사해온다 (참조가 아닌 완전한 복사본이라
    // 이후 특정 포트폴리오에서만 수정/삭제해도 프로필 원본이나 다른 포트폴리오에는 영향 없음).
    private void copyTalentCareerEducationCertificate(TalentProfile talentProfile, Portfolio portfolio) {
        List<PortfolioCareer> careers = talentCareerRepository.findAllByTalentProfileOrderByStartedAtDesc(talentProfile)
                .stream()
                .map(career -> PortfolioCareer.builder()
                        .portfolio(portfolio)
                        .companyName(career.getCompanyName())
                        .position(career.getPosition())
                        .description(career.getDescription())
                        .startedAt(career.getStartedAt())
                        .endedAt(career.getEndedAt())
                        .build())
                .toList();
        careerRepository.saveAll(careers);

        List<PortfolioEducation> educations = talentEducationRepository.findAllByTalentProfile(talentProfile)
                .stream()
                .map(education -> PortfolioEducation.builder()
                        .portfolio(portfolio)
                        .schoolName(education.getSchoolName())
                        .major(education.getMajor())
                        .degree(education.getDegree())
                        .startedAt(education.getStartedAt())
                        .endedAt(education.getEndedAt())
                        .status(education.getStatus())
                        .build())
                .toList();
        educationRepository.saveAll(educations);

        List<PortfolioCertificate> certificates = talentCertificateRepository.findAllByTalentProfile(talentProfile)
                .stream()
                .map(certificate -> PortfolioCertificate.builder()
                        .portfolio(portfolio)
                        .name(certificate.getName())
                        .issuer(certificate.getIssuer())
                        .issuedAt(certificate.getIssuedAt())
                        .expiresAt(certificate.getExpiresAt())
                        .credentialId(certificate.getCredentialId())
                        .build())
                .toList();
        certificateRepository.saveAll(certificates);
    }

    @Transactional(readOnly = true)
    public PageResponse<PortfolioMyListResDTO> getList(Long memberId, PageRequest pageRequest) {
        TalentProfile talentProfile = findTalentProfile(memberId);
        return PageResponse.of(
                portfolioRepository.findAllByTalentProfileAndConfirmedAtIsNotNullOrderByLastSavedAtDesc(talentProfile, pageRequest.toPageable())
                        .map(PortfolioMyListResDTO::from)
        );
    }

    @Transactional(readOnly = true)
    public PageResponse<PortfolioPublicListResDTO> getPublicList(String keyword, Long regionId, PortfolioSortType sortType, PageRequest pageRequest, Long memberId, CareerLevel career, JobRole jobRole) {
        if (sortType == null) sortType = PortfolioSortType.LATEST;
        // 비로그인 시 상위 3개만 반환 (프론트에서 회원가입 유도)
        PageRequest effectiveRequest = (memberId == null) ? PageRequest.of(1, 3) : pageRequest;
        Integer minYears = career != null ? career.getMinYears() : null;
        Integer maxYearsExclusive = (career != null && career.getMaxYearsExclusive() != Integer.MAX_VALUE)
                ? career.getMaxYearsExclusive() : null;
        RegionService.RegionFilter regionFilter = regionService.resolveRegionFilter(regionId);
        Page<Portfolio> portfolioPage = portfolioRepository.findPublicPortfolios(
                keyword,
                regionFilter.exactRegionId(),
                regionFilter.provinceRegionId(),
                minYears,
                maxYearsExclusive,
                jobRole,
                sortType,
                effectiveRequest.toPageable());

        Map<Long, List<Techstack>> techstacksByPortfolioId = portfolioTechstackRepository
                .findAllByPortfolioInWithTechstack(portfolioPage.getContent())
                .stream()
                .collect(Collectors.groupingBy(
                        pt -> pt.getPortfolio().getId(),
                        Collectors.mapping(PortfolioTechstack::getTechstack, Collectors.toList())
                ));

        return PageResponse.of(portfolioPage, portfolioPage.getContent().stream()
                .map(p -> PortfolioPublicListResDTO.from(p, techstacksByPortfolioId.getOrDefault(p.getId(), List.of())))
                .toList());
    }

    // 공개/비공개 "선택"만으로는(게시하기 전) 남에게 보이면 안 되므로, PUBLIC이어도 확정
    // (confirmedAt) 전이면 소유자만 접근 가능하도록 막는다.
    @Transactional(readOnly = true)
    public PortfolioDetailResDTO getDetail(Long portfolioId, Long memberId) {
        Portfolio portfolio = findPortfolio(portfolioId);

        boolean publiclyVisible = portfolio.getVisibility() == PortfolioVisibility.PUBLIC && portfolio.getConfirmedAt() != null;
        if (!publiclyVisible) {
            validateOwnership(portfolio, memberId);
        }

        return toDetailResDTO(portfolio);
    }

    @Transactional(readOnly = true)
    public PortfolioDetailResDTO getBySlug(String publicSlug) {
        Portfolio portfolio = portfolioRepository.findByPublicSlug(publicSlug)
                .orElseThrow(() -> new PortfolioException(PortfolioErrorCode.PORTFOLIO_NOT_FOUND));

        if (portfolio.getVisibility() != PortfolioVisibility.PUBLIC || portfolio.getConfirmedAt() == null) {
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

    // 저장 확정. 편집 화면의 "저장" 버튼이 호출한다. 원본(v0) 스냅샷이 아직 없으면 함께 만들고,
    // 이미 있으면(AI 첨삭을 먼저 요청해 확정된 경우 포함) 상태 변화 없이 그대로 확정 처리한다.
    @Transactional
    public PortfolioResDTO confirmSave(Long portfolioId, Long memberId) {
        Portfolio portfolio = findPortfolio(portfolioId);
        validateOwnership(portfolio, memberId);

        ensureOriginalSnapshot(portfolio);

        return PortfolioResDTO.from(portfolio, getTechstacks(portfolio));
    }

    // 방치 정리 스케줄러가 호출한다. cutoff보다 오래 전에 생성됐지만 한 번도 확정되지 않은
    // (저장/AI첨삭/게시 중 아무것도 하지 않은) 포트폴리오를 정리한다.
    @Transactional
    public int cleanupAbandonedDrafts(LocalDateTime cutoff) {
        return portfolioRepository.deleteAbandonedDrafts(cutoff);
    }

    // AI 첨삭을 처음 요청하는 순간(PortfolioAiFeedbackService.generate) 또는 "저장" 클릭 시,
    // 그 시점의 라이브 콘텐츠를 원본(version=0)으로 스냅샷 떠둔다. 이미 있으면 아무 일도 하지
    // 않지만, 어느 경로든 도달했다는 것 자체가 "저장 확정"이므로 confirmSave()는 항상 호출한다.
    @Transactional
    public void ensureOriginalSnapshot(Portfolio portfolio) {
        if (portfolioAiFeedbackRepository.findByPortfolioAndVersionAndParentFeedbackIsNull(portfolio, 0).isPresent()) {
            portfolio.confirmSave();
            return;
        }

        TalentProfile talentProfile = portfolio.getTalentProfile();
        List<PortfolioField> customFields = portfolioFieldRepository.findAllByPortfolioOrderByDisplayOrder(portfolio);
        List<PortfolioProject> projects = projectRepository.findAllByPortfolioOrderByCreatedAtDesc(portfolio);
        Map<Long, PortfolioField> fieldById = customFields.stream()
                .collect(Collectors.toMap(PortfolioField::getId, Function.identity()));
        Map<Long, PortfolioProject> projectById = projects.stream()
                .collect(Collectors.toMap(PortfolioProject::getId, Function.identity()));

        PortfolioAiFeedback origin = portfolioAiFeedbackRepository.save(PortfolioAiFeedback.builder()
                .portfolio(portfolio)
                .member(talentProfile.getMember())
                .version(0)
                .status(AiFeedbackStatus.SUCCESS)
                .finalizedAt(LocalDateTime.now())
                .build());

        List<AiFieldInputReqDTO> inputs = buildFieldInputs(portfolio, talentProfile,
                new ArrayList<>(fieldById.values()), new ArrayList<>(projectById.values()));

        List<PortfolioAiField> originFields = inputs.stream()
                .map(input -> PortfolioAiField.builder()
                        .feedback(origin)
                        .targetType(input.fieldType())
                        .portfolioField(input.fieldType() == AiFieldTargetType.CUSTOM_FIELD ? fieldById.get(input.fieldId()) : null)
                        .portfolioProject(input.fieldType() == AiFieldTargetType.PROJECT_SUMMARY ? projectById.get(input.fieldId()) : null)
                        .resolvedText(input.content())
                        .build())
                .toList();
        portfolioAiFieldRepository.saveAll(originFields);

        portfolio.confirmSave();
    }

    // 현재 라이브 콘텐츠(포트폴리오 한줄소개/상세설명, 프로필 소개, 커스텀 필드, 프로젝트 요약)를
    // AI 입력/원본 스냅샷 형식으로 변환한다. 빈 값인 필드는 대상에서 제외한다.
    public List<AiFieldInputReqDTO> buildFieldInputs(
            Portfolio portfolio,
            TalentProfile talentProfile,
            List<PortfolioField> customFields,
            List<PortfolioProject> projects
    ) {
        List<AiFieldInputReqDTO> inputs = new ArrayList<>();

        addIfPresent(inputs, portfolio.getId(), AiFieldTargetType.PORTFOLIO_ONE_LINER,
                AiFieldTargetType.PORTFOLIO_ONE_LINER.getLabel(), null, portfolio.getOneLiner());
        addIfPresent(inputs, portfolio.getId(), AiFieldTargetType.PORTFOLIO_DESCRIPTION,
                AiFieldTargetType.PORTFOLIO_DESCRIPTION.getLabel(), null, portfolio.getDescription());
        addIfPresent(inputs, talentProfile.getId(), AiFieldTargetType.PROFILE_ONE_LINER,
                AiFieldTargetType.PROFILE_ONE_LINER.getLabel(), null, talentProfile.getOneLiner());

        for (PortfolioProject project : projects) {
            addIfPresent(inputs, project.getId(), AiFieldTargetType.PROJECT_SUMMARY,
                    project.getTitle(), null, project.getContent());
        }
        for (PortfolioField field : customFields) {
            addIfPresent(inputs, field.getId(), AiFieldTargetType.CUSTOM_FIELD,
                    field.getTitle(), field.getDescription(), field.getContent());
        }

        return inputs;
    }

    private void addIfPresent(List<AiFieldInputReqDTO> inputs, Long fieldId, AiFieldTargetType type,
                              String title, String description, String content) {
        if (content == null || content.isBlank()) {
            return;
        }
        inputs.add(new AiFieldInputReqDTO(fieldId, type, title, description, content));
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

    public Portfolio findPortfolio(Long portfolioId) {
        return portfolioRepository.findById(portfolioId)
                .orElseThrow(() -> new PortfolioException(PortfolioErrorCode.PORTFOLIO_NOT_FOUND));
    }

    public void validateOwnership(Portfolio portfolio, Long memberId) {
        if (!portfolio.getTalentProfile().getMember().getId().equals(memberId)) {
            throw new PortfolioException(PortfolioErrorCode.PORTFOLIO_ACCESS_DENIED);
        }
    }
}
