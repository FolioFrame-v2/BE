package com.folioframe.domain.portfolio.service;

import com.folioframe.domain.portfolio.dto.ai.AiFeedbackApiReqDTO;
import com.folioframe.domain.portfolio.dto.ai.AiFeedbackApiResDTO;
import com.folioframe.domain.portfolio.dto.ai.AiFieldInputDTO;
import com.folioframe.domain.portfolio.dto.ai.AiFieldRevisionDTO;
import com.folioframe.domain.portfolio.dto.response.PortfolioAiFeedbackResDTO;
import com.folioframe.domain.portfolio.dto.response.PortfolioAiFeedbackVersionResDTO;
import com.folioframe.domain.portfolio.dto.response.AiFieldResultDTO;
import com.folioframe.domain.portfolio.entity.Portfolio;
import com.folioframe.domain.portfolio.entity.PortfolioAiFeedback;
import com.folioframe.domain.portfolio.entity.PortfolioAiField;
import com.folioframe.domain.portfolio.entity.PortfolioField;
import com.folioframe.domain.portfolio.entity.PortfolioProject;
import com.folioframe.domain.portfolio.enums.AiChosenType;
import com.folioframe.domain.portfolio.enums.AiFeedbackStatus;
import com.folioframe.domain.portfolio.enums.AiFieldTargetType;
import com.folioframe.domain.portfolio.exception.PortfolioException;
import com.folioframe.domain.portfolio.exception.code.PortfolioErrorCode;
import com.folioframe.domain.portfolio.repository.PortfolioAiFeedbackRepository;
import com.folioframe.domain.portfolio.repository.PortfolioAiFieldRepository;
import com.folioframe.domain.portfolio.repository.PortfolioFieldRepository;
import com.folioframe.domain.portfolio.repository.PortfolioProjectRepository;
import com.folioframe.domain.talent.entity.TalentProfile;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.HttpStatusCodeException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PortfolioAiFeedbackService {

    private static final Logger log = LoggerFactory.getLogger(PortfolioAiFeedbackService.class);

    private final PortfolioService portfolioService;
    private final PortfolioFieldRepository portfolioFieldRepository;
    private final PortfolioProjectRepository portfolioProjectRepository;
    private final PortfolioAiFeedbackRepository feedbackRepository;
    private final PortfolioAiFieldRepository aiFieldRepository;
    private final RestClient aiServiceRestClient;

    @Transactional
    public PortfolioAiFeedbackResDTO generate(Long portfolioId, Long memberId) {
        Portfolio portfolio = portfolioService.findPortfolio(portfolioId);
        portfolioService.validateOwnership(portfolio, memberId);

        if (portfolio.getAiCheckUsedCount() >= portfolio.getAiCheckMaxCount()) {
            throw new PortfolioException(PortfolioErrorCode.AI_FEEDBACK_LIMIT_EXCEEDED);
        }

        TalentProfile talentProfile = portfolio.getTalentProfile();

        // 직전 버전이 아직 열려있다면(저장/업로드로 확정되지 않았다면), 지금 이 순간의 실제 내용을
        // 그 버전의 최종 확정본으로 동결한다. "AI 첨삭을 다시 누르면 자동으로 저장된다"에 해당.
        finalizeOpenVersion(portfolio, talentProfile);

        List<PortfolioField> customFields = portfolioFieldRepository.findAllByPortfolioOrderByDisplayOrder(portfolio);
        List<PortfolioProject> projects = portfolioProjectRepository.findAllByPortfolioOrderByCreatedAtDesc(portfolio);

        List<AiFieldInputDTO> inputs = buildFieldInputs(portfolio, talentProfile, customFields, projects);
        if (inputs.isEmpty()) {
            throw new PortfolioException(PortfolioErrorCode.AI_FEEDBACK_EMPTY_CONTENT);
        }

        AiFeedbackApiReqDTO request = new AiFeedbackApiReqDTO(
                portfolio.getTitle(),
                portfolio.getJobRole() != null ? portfolio.getJobRole().name() : null,
                inputs
        );

        AiFeedbackApiResDTO response = callAiService(request);

        portfolio.increaseAiCheckUsedCount();

        PortfolioAiFeedback feedback = feedbackRepository.save(PortfolioAiFeedback.builder()
                .portfolio(portfolio)
                .member(talentProfile.getMember())
                .version(feedbackRepository.countByPortfolio(portfolio) + 1)
                .comment(response.comment())
                .score(response.score())
                .status(AiFeedbackStatus.SUCCESS)
                .build());

        Map<Long, PortfolioField> fieldById = customFields.stream()
                .collect(Collectors.toMap(PortfolioField::getId, Function.identity()));
        Map<Long, PortfolioProject> projectById = projects.stream()
                .collect(Collectors.toMap(PortfolioProject::getId, Function.identity()));

        List<PortfolioAiField> aiFields = response.fields().stream()
                .map(revision -> toAiField(feedback, revision, fieldById, projectById, portfolio, talentProfile))
                .toList();

        // 기본값은 AI 수정본 채택: 사용자가 아무것도 선택하지 않고 저장해도 AI 수정본이 반영되고,
        // 특정 필드만 원본으로 되돌리고 싶으면 이후 chooseField(ORIGINAL)로 되돌린다.
        aiFields.forEach(aiField -> {
            aiField.choose(AiChosenType.AI);
            aiField.updateResolvedText(aiField.getAiRevisedText());
            applyToSource(aiField, portfolio, aiField.getAiRevisedText());
        });
        aiFieldRepository.saveAll(aiFields);

        List<AiFieldResultDTO> fieldResults = aiFields.stream().map(AiFieldResultDTO::from).toList();
        return PortfolioAiFeedbackResDTO.of(feedback, fieldResults);
    }

    @Transactional(readOnly = true)
    public PortfolioAiFeedbackResDTO getLatest(Long portfolioId, Long memberId) {
        Portfolio portfolio = portfolioService.findPortfolio(portfolioId);
        portfolioService.validateOwnership(portfolio, memberId);

        PortfolioAiFeedback feedback = feedbackRepository.findTopByPortfolioOrderByVersionDesc(portfolio)
                .orElseThrow(() -> new PortfolioException(PortfolioErrorCode.AI_FEEDBACK_NOT_FOUND));

        return toResDTO(feedback);
    }

    @Transactional(readOnly = true)
    public PortfolioAiFeedbackResDTO getByVersion(Long portfolioId, Integer version, Long memberId) {
        Portfolio portfolio = portfolioService.findPortfolio(portfolioId);
        portfolioService.validateOwnership(portfolio, memberId);

        PortfolioAiFeedback feedback = feedbackRepository.findByPortfolioAndVersion(portfolio, version)
                .orElseThrow(() -> new PortfolioException(PortfolioErrorCode.AI_FEEDBACK_NOT_FOUND));

        return toResDTO(feedback);
    }

    @Transactional(readOnly = true)
    public List<PortfolioAiFeedbackVersionResDTO> getVersions(Long portfolioId, Long memberId) {
        Portfolio portfolio = portfolioService.findPortfolio(portfolioId);
        portfolioService.validateOwnership(portfolio, memberId);

        List<PortfolioAiFeedback> feedbacks = feedbackRepository.findAllByPortfolioOrderByVersionAsc(portfolio);
        if (feedbacks.isEmpty()) {
            return List.of();
        }

        List<PortfolioAiFeedbackVersionResDTO> results = new ArrayList<>();
        results.add(PortfolioAiFeedbackVersionResDTO.original());
        feedbacks.forEach(feedback -> results.add(PortfolioAiFeedbackVersionResDTO.from(feedback)));
        return results;
    }

    // "원본"(첫 AI 첨삭 요청 직전 상태) 조회. version 1의 AiField에 남아있는 originalText를 그대로 보여준다.
    @Transactional(readOnly = true)
    public PortfolioAiFeedbackResDTO getOriginal(Long portfolioId, Long memberId) {
        Portfolio portfolio = portfolioService.findPortfolio(portfolioId);
        portfolioService.validateOwnership(portfolio, memberId);

        PortfolioAiFeedback firstFeedback = feedbackRepository.findByPortfolioAndVersion(portfolio, 1)
                .orElseThrow(() -> new PortfolioException(PortfolioErrorCode.AI_FEEDBACK_NOT_FOUND));

        List<AiFieldResultDTO> fieldResults = aiFieldRepository.findAllByFeedback(firstFeedback).stream()
                .map(AiFieldResultDTO::originalOnly)
                .toList();

        return PortfolioAiFeedbackResDTO.original(fieldResults);
    }

    @Transactional
    public AiFieldResultDTO chooseField(Long portfolioId, Long aiFieldId, Long memberId, AiChosenType chosen) {
        if (chosen == AiChosenType.PENDING) {
            throw new PortfolioException(PortfolioErrorCode.AI_FEEDBACK_INVALID_CHOICE);
        }

        Portfolio portfolio = portfolioService.findPortfolio(portfolioId);
        portfolioService.validateOwnership(portfolio, memberId);

        PortfolioAiField aiField = aiFieldRepository.findById(aiFieldId)
                .orElseThrow(() -> new PortfolioException(PortfolioErrorCode.AI_FEEDBACK_FIELD_NOT_FOUND));

        if (!aiField.getFeedback().getPortfolio().getId().equals(portfolioId)) {
            throw new PortfolioException(PortfolioErrorCode.AI_FEEDBACK_FIELD_NOT_IN_PORTFOLIO);
        }
        if (aiField.getFeedback().isFinalized()) {
            throw new PortfolioException(PortfolioErrorCode.AI_FEEDBACK_VERSION_CLOSED);
        }

        aiField.choose(chosen);
        String text = (chosen == AiChosenType.AI) ? aiField.getAiRevisedText() : aiField.getOriginalText();
        aiField.updateResolvedText(text);
        applyToSource(aiField, portfolio, text);

        return AiFieldResultDTO.from(aiField);
    }

    // 직전 버전을 확정(동결)한다. 오직 generate()가 새 버전을 시작할 때만 호출된다 — 저장/업로드는
    // 확정 트리거가 아니다. 가장 최근 버전은 다음 AI 첨삭을 누르기 전까지 계속 선택/수정 가능한 상태로 남는다.
    private void finalizeOpenVersion(Portfolio portfolio, TalentProfile talentProfile) {
        feedbackRepository.findTopByPortfolioOrderByVersionDesc(portfolio)
                .filter(feedback -> !feedback.isFinalized())
                .ifPresent(feedback -> {
                    List<PortfolioAiField> fields = aiFieldRepository.findAllByFeedback(feedback);
                    for (PortfolioAiField field : fields) {
                        field.updateResolvedText(resolveCurrentText(field, portfolio, talentProfile));
                    }
                    feedback.markFinalized(LocalDateTime.now());
                });
    }

    private PortfolioAiFeedbackResDTO toResDTO(PortfolioAiFeedback feedback) {
        Portfolio portfolio = feedback.getPortfolio();
        TalentProfile talentProfile = portfolio.getTalentProfile();
        boolean open = !feedback.isFinalized();

        List<AiFieldResultDTO> fieldResults = aiFieldRepository.findAllByFeedback(feedback).stream()
                .map(field -> open
                        ? AiFieldResultDTO.from(field, resolveCurrentText(field, portfolio, talentProfile))
                        : AiFieldResultDTO.finalOnly(field))
                .toList();

        return PortfolioAiFeedbackResDTO.of(feedback, fieldResults);
    }

    private String resolveCurrentText(PortfolioAiField field, Portfolio portfolio, TalentProfile talentProfile) {
        return switch (field.getTargetType()) {
            case CUSTOM_FIELD -> field.getPortfolioField() != null ? field.getPortfolioField().getContent() : null;
            case PROJECT_SUMMARY ->
                    field.getPortfolioProject() != null ? field.getPortfolioProject().getContent() : null;
            case PORTFOLIO_ONE_LINER -> portfolio.getOneLiner();
            case PORTFOLIO_DESCRIPTION -> portfolio.getDescription();
            case PROFILE_ONE_LINER -> talentProfile.getOneLiner();
        };
    }

    private void applyToSource(PortfolioAiField aiField, Portfolio portfolio, String text) {
        switch (aiField.getTargetType()) {
            case CUSTOM_FIELD -> {
                PortfolioField field = aiField.getPortfolioField();
                if (field == null) {
                    throw new PortfolioException(PortfolioErrorCode.AI_FEEDBACK_FIELD_NOT_FOUND);
                }
                PortfolioAiFeedback appliedFeedback = aiField.getChosen() == AiChosenType.AI ? aiField.getFeedback() : null;
                field.applyContent(text, appliedFeedback);
            }
            case PROJECT_SUMMARY -> {
                PortfolioProject project = aiField.getPortfolioProject();
                if (project == null) {
                    throw new PortfolioException(PortfolioErrorCode.AI_FEEDBACK_FIELD_NOT_FOUND);
                }
                project.updateContent(text);
            }
            case PORTFOLIO_ONE_LINER -> portfolio.updateOneLiner(text);
            case PORTFOLIO_DESCRIPTION -> portfolio.updateDescription(text);
            case PROFILE_ONE_LINER -> portfolio.getTalentProfile().updateOneLiner(text);
        }
    }

    private List<AiFieldInputDTO> buildFieldInputs(
            Portfolio portfolio,
            TalentProfile talentProfile,
            List<PortfolioField> customFields,
            List<PortfolioProject> projects
    ) {
        List<AiFieldInputDTO> inputs = new ArrayList<>();

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

    private void addIfPresent(List<AiFieldInputDTO> inputs, Long fieldId, AiFieldTargetType type,
                              String title, String description, String content) {
        if (content == null || content.isBlank()) {
            return;
        }
        inputs.add(new AiFieldInputDTO(fieldId, type, title, description, content));
    }

    private PortfolioAiField toAiField(
            PortfolioAiFeedback feedback,
            AiFieldRevisionDTO revision,
            Map<Long, PortfolioField> fieldById,
            Map<Long, PortfolioProject> projectById,
            Portfolio portfolio,
            TalentProfile talentProfile
    ) {
        PortfolioAiField.PortfolioAiFieldBuilder builder = PortfolioAiField.builder()
                .feedback(feedback)
                .targetType(revision.fieldType())
                .aiRevisedText(revision.aiRevisedText());

        switch (revision.fieldType()) {
            case CUSTOM_FIELD -> {
                PortfolioField field = fieldById.get(revision.fieldId());
                builder.portfolioField(field).originalText(field != null ? field.getContent() : null);
            }
            case PROJECT_SUMMARY -> {
                PortfolioProject project = projectById.get(revision.fieldId());
                builder.portfolioProject(project).originalText(project != null ? project.getContent() : null);
            }
            case PORTFOLIO_ONE_LINER -> builder.originalText(portfolio.getOneLiner());
            case PORTFOLIO_DESCRIPTION -> builder.originalText(portfolio.getDescription());
            case PROFILE_ONE_LINER -> builder.originalText(talentProfile.getOneLiner());
        }

        return builder.build();
    }

    private AiFeedbackApiResDTO callAiService(AiFeedbackApiReqDTO request) {
        try {
            AiFeedbackApiResDTO response = aiServiceRestClient.post()
                    .uri("/api/v1/feedback")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(request)
                    .retrieve()
                    .body(AiFeedbackApiResDTO.class);

            if (response == null) {
                throw new PortfolioException(PortfolioErrorCode.AI_SERVICE_UNAVAILABLE);
            }
            return response;
        } catch (HttpStatusCodeException exc) {
            if (exc.getStatusCode().value() == 429) {
                log.warn("AI 서비스 할당량 초과: {}", exc.getMessage());
                throw new PortfolioException(PortfolioErrorCode.AI_SERVICE_QUOTA_EXCEEDED);
            }
            log.error("AI 서비스 호출 실패 (status={})", exc.getStatusCode(), exc);
            throw new PortfolioException(PortfolioErrorCode.AI_SERVICE_UNAVAILABLE);
        } catch (RestClientException exc) {
            log.error("AI 서비스 호출 실패", exc);
            throw new PortfolioException(PortfolioErrorCode.AI_SERVICE_UNAVAILABLE);
        }
    }
}
