package com.folioframe.domain.portfolio.service;

import com.folioframe.domain.portfolio.dto.ai.AiFeedbackApiReqDTO;
import com.folioframe.domain.portfolio.dto.ai.AiFeedbackApiResDTO;
import com.folioframe.domain.portfolio.dto.ai.AiFieldInputDTO;
import com.folioframe.domain.portfolio.dto.ai.AiFieldRevisionDTO;
import com.folioframe.domain.portfolio.dto.response.PortfolioAiFeedbackResDTO;
import com.folioframe.domain.portfolio.entity.Portfolio;
import com.folioframe.domain.portfolio.entity.PortfolioAiFeedback;
import com.folioframe.domain.portfolio.entity.PortfolioAiField;
import com.folioframe.domain.portfolio.entity.PortfolioField;
import com.folioframe.domain.portfolio.entity.PortfolioProject;
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
        aiFieldRepository.saveAll(aiFields);

        return PortfolioAiFeedbackResDTO.of(feedback, aiFields);
    }

    @Transactional(readOnly = true)
    public PortfolioAiFeedbackResDTO getLatest(Long portfolioId, Long memberId) {
        Portfolio portfolio = portfolioService.findPortfolio(portfolioId);
        portfolioService.validateOwnership(portfolio, memberId);

        PortfolioAiFeedback feedback = feedbackRepository.findTopByPortfolioOrderByVersionDesc(portfolio)
                .orElseThrow(() -> new PortfolioException(PortfolioErrorCode.AI_FEEDBACK_NOT_FOUND));
        List<PortfolioAiField> fields = aiFieldRepository.findAllByFeedback(feedback);

        return PortfolioAiFeedbackResDTO.of(feedback, fields);
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
