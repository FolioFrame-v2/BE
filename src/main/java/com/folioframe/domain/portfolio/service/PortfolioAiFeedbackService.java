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
import com.folioframe.domain.portfolio.enums.PortfolioVisibility;
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

    // AI 첨삭은 항상 "지금 편집 화면에 불러와 있는(선택된) 버전"의 내용을 대상으로 돌아간다 —
    // 게시 중인 콘텐츠나 라이브 콘텐츠가 아니다. sourceVersion/sourceSubVersion을 생략하면 원본(0)을
    // 기준으로 한다(첫 요청이 여기 해당). 결과는 항상 새로운 최상위 버전으로 생성된다.
    @Transactional
    public PortfolioAiFeedbackResDTO generate(Long portfolioId, Integer sourceVersion, Integer sourceSubVersion, Long memberId) {
        Portfolio portfolio = portfolioService.findPortfolio(portfolioId);
        portfolioService.validateOwnership(portfolio, memberId);

        if (portfolio.getAiCheckUsedCount() >= portfolio.getAiCheckMaxCount()) {
            throw new PortfolioException(PortfolioErrorCode.AI_FEEDBACK_LIMIT_EXCEEDED);
        }

        TalentProfile talentProfile = portfolio.getTalentProfile();

        List<PortfolioField> customFields = portfolioFieldRepository.findAllByPortfolioOrderByDisplayOrder(portfolio);
        List<PortfolioProject> projects = portfolioProjectRepository.findAllByPortfolioOrderByCreatedAtDesc(portfolio);

        Map<Long, PortfolioField> fieldById = customFields.stream()
                .collect(Collectors.toMap(PortfolioField::getId, Function.identity()));
        Map<Long, PortfolioProject> projectById = projects.stream()
                .collect(Collectors.toMap(PortfolioProject::getId, Function.identity()));

        // 포트폴리오 최초 AI 첨삭 요청이면, 지금 이 순간의 라이브 콘텐츠를 "원본"(version=0)으로
        // 스냅샷 떠둔다. 이후로는 원본도 다른 버전처럼 독립적으로 편집·게시할 수 있다. 이 스냅샷
        // 생성은 곧 저장 확정과 같은 의미라 PortfolioService.ensureOriginalSnapshot이 confirmSave까지 처리한다.
        portfolioService.ensureOriginalSnapshot(portfolio);

        int resolvedSourceVersion = sourceVersion != null ? sourceVersion : 0;
        PortfolioAiFeedback source = findFeedback(portfolio, resolvedSourceVersion, sourceSubVersion);
        List<PortfolioAiField> sourceFields = aiFieldRepository.findAllByFeedback(source);

        List<AiFieldInputDTO> inputs = buildFieldInputsFromSource(sourceFields, fieldById, projectById);
        if (inputs.isEmpty()) {
            throw new PortfolioException(PortfolioErrorCode.AI_FEEDBACK_EMPTY_CONTENT);
        }
        Map<String, String> sourceTextByKey = sourceFields.stream()
                .collect(Collectors.toMap(this::sourceFieldKey, PortfolioAiField::getResolvedText));

        AiFeedbackApiReqDTO request = new AiFeedbackApiReqDTO(
                portfolio.getTitle(),
                portfolio.getJobRole() != null ? portfolio.getJobRole().name() : null,
                inputs
        );

        AiFeedbackApiResDTO response = callAiService(request);

        portfolio.increaseAiCheckUsedCount();

        // 최상위 버전 번호는 지금까지 생성된 최상위(부모 없는) 버전 중 가장 큰 번호 + 1로 매긴다.
        // (개수 기반으로 매기면 중간 버전이 삭제됐을 때 번호가 충돌할 수 있어 MAX+1을 사용한다.)
        // 어떤 버전(또는 그 자식)을 기준으로 요청했든, 결과는 항상 새 최상위 버전으로 생성된다.
        int nextVersion = feedbackRepository.findTopByPortfolioAndParentFeedbackIsNullOrderByVersionDesc(portfolio)
                .map(top -> top.getVersion() + 1)
                .orElse(1);
        PortfolioAiFeedback feedback = feedbackRepository.save(PortfolioAiFeedback.builder()
                .portfolio(portfolio)
                .member(talentProfile.getMember())
                .version(nextVersion)
                .comment(response.comment())
                .score(response.score())
                .status(AiFeedbackStatus.SUCCESS)
                .build());

        List<PortfolioAiField> aiFields = response.fields().stream()
                .map(revision -> toAiField(feedback, revision, fieldById, projectById, sourceTextByKey))
                .toList();

        // 기본값은 AI 수정본 채택: 사용자가 아무것도 선택하지 않고 저장해도 AI 수정본이 그 버전의
        // 초안에 반영된다. 실제 라이브 콘텐츠는 "게시" API를 호출하기 전까지 전혀 바뀌지 않는다.
        aiFields.forEach(aiField -> {
            aiField.choose(AiChosenType.AI);
            aiField.updateResolvedText(aiField.getAiRevisedText());
        });
        aiFieldRepository.saveAll(aiFields);

        List<AiFieldResultDTO> fieldResults = aiFields.stream().map(AiFieldResultDTO::from).toList();
        return PortfolioAiFeedbackResDTO.of(feedback, fieldResults, false);
    }

    // 편집 화면 진입 시 기본으로 불러올 버전 조회. "가장 최근에 생성된" 버전이 아니라 "가장 최근에
    // 실제로 수정/저장된" 버전을 찾는다 — AI 첨삭을 새로 받았는지와 무관하게, 필드 선택/직접수정으로
    // 마지막에 손댄 버전(원본이든 자식 수정본이든)을 그대로 이어서 보여주기 위함이다.
    @Transactional(readOnly = true)
    public PortfolioAiFeedbackResDTO getLatest(Long portfolioId, Long memberId) {
        Portfolio portfolio = portfolioService.findPortfolio(portfolioId);
        portfolioService.validateOwnership(portfolio, memberId);

        List<PortfolioAiFeedback> topLevelFeedbacks = feedbackRepository.findAllByPortfolioAndParentFeedbackIsNullOrderByVersionAsc(portfolio);
        if (topLevelFeedbacks.isEmpty()) {
            // AI 첨삭을 한 번도 요청한 적 없음: 라이브 콘텐츠가 곧 유일한 "버전"이다.
            return buildLiveOriginalResponse(portfolio);
        }

        List<PortfolioAiFeedback> allFeedbacks = new ArrayList<>(topLevelFeedbacks);
        for (PortfolioAiFeedback feedback : topLevelFeedbacks) {
            allFeedbacks.addAll(feedbackRepository.findAllByParentFeedbackOrderBySubVersionAsc(feedback));
        }

        Map<Long, List<PortfolioAiField>> fieldsByFeedbackId = aiFieldRepository.findAllByFeedbackIn(allFeedbacks).stream()
                .collect(Collectors.groupingBy(field -> field.getFeedback().getId()));

        PortfolioAiFeedback mostRecentlyModified = allFeedbacks.stream()
                .max(java.util.Comparator.comparing(feedback -> lastModifiedAt(feedback, fieldsByFeedbackId)))
                .orElseThrow(() -> new PortfolioException(PortfolioErrorCode.AI_FEEDBACK_NOT_FOUND));

        return toResDTO(mostRecentlyModified);
    }

    @Transactional(readOnly = true)
    public PortfolioAiFeedbackResDTO getByVersion(Long portfolioId, Integer version, Integer subVersion, Long memberId) {
        Portfolio portfolio = portfolioService.findPortfolio(portfolioId);
        portfolioService.validateOwnership(portfolio, memberId);

        PortfolioAiFeedback feedback = findFeedback(portfolio, version, subVersion);
        return toResDTO(feedback);
    }

    @Transactional(readOnly = true)
    public List<PortfolioAiFeedbackVersionResDTO> getVersions(Long portfolioId, Long memberId) {
        Portfolio portfolio = portfolioService.findPortfolio(portfolioId);
        portfolioService.validateOwnership(portfolio, memberId);

        List<PortfolioAiFeedback> topLevelFeedbacks = feedbackRepository.findAllByPortfolioAndParentFeedbackIsNullOrderByVersionAsc(portfolio);
        if (topLevelFeedbacks.isEmpty()) {
            // AI 첨삭을 한 번도 요청한 적 없음: 원본을 라이브 콘텐츠 기준 가상 항목으로만 보여준다.
            boolean publishedAsIs = isPublishedWithoutVersion(portfolio);
            return List.of(PortfolioAiFeedbackVersionResDTO.original(publishedAsIs, portfolio.getLastSavedAt()));
        }

        Long publishedId = portfolio.getPublishedFeedback() != null ? portfolio.getPublishedFeedback().getId() : null;

        Map<Long, List<PortfolioAiFeedback>> revisionsByParentId = new java.util.HashMap<>();
        List<PortfolioAiFeedback> allFeedbacks = new ArrayList<>(topLevelFeedbacks);
        for (PortfolioAiFeedback feedback : topLevelFeedbacks) {
            List<PortfolioAiFeedback> revisions = feedbackRepository.findAllByParentFeedbackOrderBySubVersionAsc(feedback);
            revisionsByParentId.put(feedback.getId(), revisions);
            allFeedbacks.addAll(revisions);
        }

        Map<Long, List<PortfolioAiField>> fieldsByFeedbackId = aiFieldRepository.findAllByFeedbackIn(allFeedbacks).stream()
                .collect(Collectors.groupingBy(field -> field.getFeedback().getId()));

        List<PortfolioAiFeedbackVersionResDTO> results = new ArrayList<>();
        for (PortfolioAiFeedback feedback : topLevelFeedbacks) {
            List<PortfolioAiFeedbackVersionResDTO> revisions = revisionsByParentId.get(feedback.getId()).stream()
                    .map(revision -> PortfolioAiFeedbackVersionResDTO.from(
                            revision, lastModifiedAt(revision, fieldsByFeedbackId), List.of(), revision.getId().equals(publishedId)))
                    .toList();
            results.add(PortfolioAiFeedbackVersionResDTO.from(
                    feedback, lastModifiedAt(feedback, fieldsByFeedbackId), revisions, feedback.getId().equals(publishedId)));
        }
        return results;
    }

    // AI 첨삭 수신 여부와 무관하게, 필드 선택/직접수정으로 실제 내용이 마지막으로 바뀐 시각(필드들의 updatedAt과
    // feedback 자체의 updatedAt 중 가장 나중 시각)을 계산한다.
    private LocalDateTime lastModifiedAt(PortfolioAiFeedback feedback, Map<Long, List<PortfolioAiField>> fieldsByFeedbackId) {
        LocalDateTime latest = feedback.getUpdatedAt();
        for (PortfolioAiField field : fieldsByFeedbackId.getOrDefault(feedback.getId(), List.of())) {
            if (field.getUpdatedAt().isAfter(latest)) {
                latest = field.getUpdatedAt();
            }
        }
        return latest;
    }

    // "원본" 조회. AI 첨삭을 이미 시작했다면 그 순간 스냅샷 떠둔 독립 버전(version=0)을 그대로 보여주고,
    // 아직 한 번도 요청한 적 없다면 지금 라이브 필드 콘텐츠를 읽기 전용으로 보여준다.
    @Transactional(readOnly = true)
    public PortfolioAiFeedbackResDTO getOriginal(Long portfolioId, Long memberId) {
        Portfolio portfolio = portfolioService.findPortfolio(portfolioId);
        portfolioService.validateOwnership(portfolio, memberId);

        return feedbackRepository.findByPortfolioAndVersionAndParentFeedbackIsNull(portfolio, 0)
                .map(this::toResDTO)
                .orElseGet(() -> buildLiveOriginalResponse(portfolio));
    }

    private PortfolioAiFeedbackResDTO buildLiveOriginalResponse(Portfolio portfolio) {
        TalentProfile talentProfile = portfolio.getTalentProfile();
        List<PortfolioField> customFields = portfolioFieldRepository.findAllByPortfolioOrderByDisplayOrder(portfolio);
        List<PortfolioProject> projects = portfolioProjectRepository.findAllByPortfolioOrderByCreatedAtDesc(portfolio);

        List<AiFieldResultDTO> fieldResults = portfolioService.buildFieldInputs(portfolio, talentProfile, customFields, projects).stream()
                .map(input -> AiFieldResultDTO.liveOriginal(
                        input.fieldType(),
                        input.fieldType() == AiFieldTargetType.CUSTOM_FIELD ? input.fieldId() : null,
                        input.fieldType() == AiFieldTargetType.PROJECT_SUMMARY ? input.fieldId() : null,
                        input.content()))
                .toList();

        return PortfolioAiFeedbackResDTO.original(fieldResults, isPublishedWithoutVersion(portfolio));
    }

    @Transactional
    public AiFieldResultDTO chooseField(Long portfolioId, Long aiFieldId, Long memberId, AiChosenType chosen) {
        if (chosen == AiChosenType.PENDING) {
            throw new PortfolioException(PortfolioErrorCode.AI_FEEDBACK_INVALID_CHOICE);
        }

        Portfolio portfolio = portfolioService.findPortfolio(portfolioId);
        portfolioService.validateOwnership(portfolio, memberId);

        PortfolioAiField aiField = findOwnedAiField(portfolioId, aiFieldId);
        if (aiField.getFeedback().isFinalized()) {
            throw new PortfolioException(PortfolioErrorCode.AI_FEEDBACK_VERSION_CLOSED);
        }

        aiField.choose(chosen);
        String text = (chosen == AiChosenType.AI) ? aiField.getAiRevisedText() : aiField.getOriginalText();
        aiField.updateResolvedText(text);

        return AiFieldResultDTO.from(aiField);
    }

    // "직접 수정". 확정 여부와 무관하게 그 버전 자신의 초안(resolvedText)만 덮어쓴다. 실제 라이브
    // 콘텐츠는 바뀌지 않으며, 게시(publish) API를 호출해야만 라이브에 반영된다.
    @Transactional
    public AiFieldResultDTO editField(Long portfolioId, Long aiFieldId, Long memberId, String content) {
        Portfolio portfolio = portfolioService.findPortfolio(portfolioId);
        portfolioService.validateOwnership(portfolio, memberId);

        PortfolioAiField aiField = findOwnedAiField(portfolioId, aiFieldId);
        aiField.updateResolvedText(content);

        return AiFieldResultDTO.from(aiField);
    }

    // 저장(확정). 오픈 상태 버전이면 지금 초안 상태를 최종본으로 확정(finalizedAt 설정)한다.
    // resolvedText는 chooseField/editField가 이미 계속 최신 상태로 유지하고 있으므로 추가로
    // 동기화할 필요는 없다. 이미 확정된 버전(또는 자식 수정본)에 대한 호출은 상태 변화 없이 그대로 응답한다.
    @Transactional
    public PortfolioAiFeedbackResDTO saveVersion(Long portfolioId, Integer version, Integer subVersion, Long memberId) {
        Portfolio portfolio = portfolioService.findPortfolio(portfolioId);
        portfolioService.validateOwnership(portfolio, memberId);

        PortfolioAiFeedback feedback = findFeedback(portfolio, version, subVersion);
        if (!feedback.isFinalized()) {
            feedback.markFinalized(LocalDateTime.now());
        }

        return toResDTO(feedback);
    }

    // "수정본 만들기". 확정된 최상위 버전 바로 아래 자식 버전을 만든다. AI를 다시 호출하지 않고
    // 부모의 확정 콘텐츠를 그대로 복사해 선택 UX 없이 바로 직접 수정만 가능한 상태로 시작한다.
    @Transactional
    public PortfolioAiFeedbackResDTO createRevision(Long portfolioId, Integer version, Integer subVersion, Long memberId) {
        Portfolio portfolio = portfolioService.findPortfolio(portfolioId);
        portfolioService.validateOwnership(portfolio, memberId);

        PortfolioAiFeedback parent = feedbackRepository.findByPortfolioAndVersionAndParentFeedbackIsNull(portfolio, version)
                .orElseThrow(() -> new PortfolioException(PortfolioErrorCode.AI_FEEDBACK_NOT_FOUND));
        if (!parent.isFinalized()) {
            throw new PortfolioException(PortfolioErrorCode.AI_FEEDBACK_REVISION_ON_OPEN_VERSION);
        }

        // subVersion을 지정하면 그 형제 자식(예: 버전2-1)의 최신 내용을 이어받아 다음 자식(버전2-2)을 만든다.
        // 지정하지 않으면 최상위 부모(버전2)의 확정 콘텐츠에서 시작한다. 자식은 항상 같은 부모 밑에 1단계로만 붙는다.
        PortfolioAiFeedback source = parent;
        if (subVersion != null) {
            source = feedbackRepository.findByParentFeedbackAndSubVersion(parent, subVersion)
                    .orElseThrow(() -> new PortfolioException(PortfolioErrorCode.AI_FEEDBACK_NOT_FOUND));
        }

        // 자식 서브버전 번호는 개수가 아닌 MAX+1로 매겨 중간 자식이 삭제됐을 때의 번호 충돌을 피한다.
        int nextSubVersion = feedbackRepository.findTopByParentFeedbackOrderBySubVersionDesc(parent)
                .map(top -> top.getSubVersion() + 1)
                .orElse(1);
        PortfolioAiFeedback child = feedbackRepository.save(PortfolioAiFeedback.builder()
                .portfolio(portfolio)
                .member(parent.getMember())
                .version(parent.getVersion())
                .subVersion(nextSubVersion)
                .parentFeedback(parent)
                .comment(parent.getComment())
                .score(parent.getScore())
                .status(parent.getStatus())
                .finalizedAt(LocalDateTime.now())
                .build());

        List<PortfolioAiField> sourceFields = aiFieldRepository.findAllByFeedback(source);
        List<PortfolioAiField> childFields = sourceFields.stream()
                .map(sourceField -> PortfolioAiField.builder()
                        .feedback(child)
                        .targetType(sourceField.getTargetType())
                        .portfolioField(sourceField.getPortfolioField())
                        .portfolioProject(sourceField.getPortfolioProject())
                        .resolvedText(sourceField.getResolvedText())
                        .build())
                .toList();
        aiFieldRepository.saveAll(childFields);

        return toResDTO(child);
    }

    // 게시. 대상 버전(원본 포함)의 지금 내용을 실제 라이브 엔티티에 복사하고, 그 버전을
    // "지금 게시 중인 버전"으로 기록한다. 라이브 콘텐츠가 바뀌는 유일한 경로다. 아직 확정(저장) 전인
    // 오픈 버전을 게시하면 게시와 동시에 자동으로 확정도 함께 처리한다(게시=확정).
    @Transactional
    public PortfolioAiFeedbackResDTO publishVersion(Long portfolioId, Integer version, Integer subVersion, Long memberId) {
        Portfolio portfolio = portfolioService.findPortfolio(portfolioId);
        portfolioService.validateOwnership(portfolio, memberId);

        // 템플릿 useCount는 "이 템플릿으로 만든 포트폴리오가 처음 게시되는 순간"에만 1 증가한다.
        // 같은 포트폴리오에서 버전을 바꿔가며 다시 게시하는 건 정상적인 반복 동작이라 매번 세지 않는다.
        boolean firstPublish = portfolio.getPublishedAt() == null;

        // AI 첨삭을 한 번도 요청한 적 없는 포트폴리오는 "원본"(version=0) row 자체가 없다.
        // 이 경우 게시는 지금 라이브 콘텐츠를 그대로 공개 전환하는 것과 같다(복사할 대상이 없음).
        if (version == 0 && subVersion == null
                && feedbackRepository.findByPortfolioAndVersionAndParentFeedbackIsNull(portfolio, 0).isEmpty()) {
            portfolio.publish();
            if (firstPublish) {
                portfolio.getTemplate().increaseUseCount();
            }
            return buildLiveOriginalResponse(portfolio);
        }

        PortfolioAiFeedback feedback = findFeedback(portfolio, version, subVersion);
        List<PortfolioAiField> fields = aiFieldRepository.findAllByFeedback(feedback);
        for (PortfolioAiField field : fields) {
            applyToSource(field, portfolio, field.getResolvedText());
        }
        if (!feedback.isFinalized()) {
            feedback.markFinalized(LocalDateTime.now());
        }
        portfolio.publishFeedback(feedback);
        if (firstPublish) {
            portfolio.getTemplate().increaseUseCount();
        }

        return toResDTO(feedback);
    }

    // 버전 삭제. 최상위 버전을 삭제하면 그 아래 자식(수정본) 버전까지 전부 함께 삭제되고(그룹 삭제),
    // 자식 버전만 지정하면 그 자식만 삭제된다. 원본(version=0)은 삭제할 수 없다. 삭제 대상이 지금
    // 게시 중인 버전(또는 그 부모)이라면 포트폴리오 게시를 함께 내린다.
    @Transactional
    public void deleteVersion(Long portfolioId, Integer version, Integer subVersion, Long memberId) {
        Portfolio portfolio = portfolioService.findPortfolio(portfolioId);
        portfolioService.validateOwnership(portfolio, memberId);

        if (version == 0) {
            throw new PortfolioException(PortfolioErrorCode.AI_FEEDBACK_ORIGINAL_NOT_DELETABLE);
        }

        PortfolioAiFeedback feedback = findFeedback(portfolio, version, subVersion);

        PortfolioAiFeedback published = portfolio.getPublishedFeedback();
        boolean removesPublished = published != null && (
                published.getId().equals(feedback.getId())
                || (feedback.isTopLevel() && published.getParentFeedback() != null
                    && published.getParentFeedback().getId().equals(feedback.getId())));
        if (removesPublished) {
            // 게시 중이던 버전이 사라지므로 그 버전이 라이브에 올려둔 내용을 전부 비우고 비공개로 전환한다.
            for (PortfolioAiField field : aiFieldRepository.findAllByFeedback(published)) {
                clearSource(field, portfolio);
            }
            portfolio.unpublish();
        }

        feedbackRepository.delete(feedback);
    }

    // 버전 목록 패널에 표시되는 이름을 사용자가 직접 지정/수정한다. 원본이 아직 실제 row로
    // 승격되기 전(=AI 첨삭을 한 번도 요청한 적 없음)에는 저장할 곳이 없어 지원하지 않는다.
    @Transactional
    public PortfolioAiFeedbackResDTO renameVersion(Long portfolioId, Integer version, Integer subVersion, Long memberId, String label) {
        Portfolio portfolio = portfolioService.findPortfolio(portfolioId);
        portfolioService.validateOwnership(portfolio, memberId);

        PortfolioAiFeedback feedback = findFeedback(portfolio, version, subVersion);
        feedback.rename(label);

        return toResDTO(feedback);
    }

    private void clearSource(PortfolioAiField aiField, Portfolio portfolio) {
        switch (aiField.getTargetType()) {
            case CUSTOM_FIELD -> {
                PortfolioField field = aiField.getPortfolioField();
                if (field != null) {
                    field.applyContent("", null);
                }
            }
            case PROJECT_SUMMARY -> {
                PortfolioProject project = aiField.getPortfolioProject();
                if (project != null) {
                    project.updateContent("");
                }
            }
            case PORTFOLIO_ONE_LINER -> portfolio.updateOneLiner("");
            case PORTFOLIO_DESCRIPTION -> portfolio.updateDescription("");
            case PROFILE_ONE_LINER -> portfolio.getTalentProfile().updateOneLiner("");
        }
    }

    private boolean isPublishedWithoutVersion(Portfolio portfolio) {
        return portfolio.getVisibility() == PortfolioVisibility.PUBLIC && portfolio.getPublishedFeedback() == null;
    }

    private PortfolioAiField findOwnedAiField(Long portfolioId, Long aiFieldId) {
        PortfolioAiField aiField = aiFieldRepository.findById(aiFieldId)
                .orElseThrow(() -> new PortfolioException(PortfolioErrorCode.AI_FEEDBACK_FIELD_NOT_FOUND));
        if (!aiField.getFeedback().getPortfolio().getId().equals(portfolioId)) {
            throw new PortfolioException(PortfolioErrorCode.AI_FEEDBACK_FIELD_NOT_IN_PORTFOLIO);
        }
        return aiField;
    }

    private PortfolioAiFeedback findFeedback(Portfolio portfolio, Integer version, Integer subVersion) {
        if (subVersion == null) {
            return feedbackRepository.findByPortfolioAndVersionAndParentFeedbackIsNull(portfolio, version)
                    .orElseThrow(() -> new PortfolioException(PortfolioErrorCode.AI_FEEDBACK_NOT_FOUND));
        }
        PortfolioAiFeedback parent = feedbackRepository.findByPortfolioAndVersionAndParentFeedbackIsNull(portfolio, version)
                .orElseThrow(() -> new PortfolioException(PortfolioErrorCode.AI_FEEDBACK_NOT_FOUND));
        return feedbackRepository.findByParentFeedbackAndSubVersion(parent, subVersion)
                .orElseThrow(() -> new PortfolioException(PortfolioErrorCode.AI_FEEDBACK_NOT_FOUND));
    }

    private PortfolioAiFeedbackResDTO toResDTO(PortfolioAiFeedback feedback) {
        boolean open = !feedback.isFinalized();
        Portfolio portfolio = feedback.getPortfolio();
        boolean published = portfolio.getPublishedFeedback() != null
                && portfolio.getPublishedFeedback().getId().equals(feedback.getId());

        List<AiFieldResultDTO> fieldResults = aiFieldRepository.findAllByFeedback(feedback).stream()
                .map(field -> open ? AiFieldResultDTO.from(field) : AiFieldResultDTO.finalOnly(field))
                .toList();

        return PortfolioAiFeedbackResDTO.of(feedback, fieldResults, published);
    }

    // 게시 시에만 호출된다. 대상 버전의 확정된 텍스트를 실제 라이브 엔티티에 복사한다.
    private void applyToSource(PortfolioAiField aiField, Portfolio portfolio, String text) {
        switch (aiField.getTargetType()) {
            case CUSTOM_FIELD -> {
                PortfolioField field = aiField.getPortfolioField();
                if (field == null) {
                    throw new PortfolioException(PortfolioErrorCode.AI_FEEDBACK_FIELD_NOT_FOUND);
                }
                field.applyContent(text, aiField.getFeedback());
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

    // 새로 생성되는 버전의 각 필드 originalText는 "이번 AI 첨삭에 실제로 입력으로 들어간 텍스트"
    // (=선택된 소스 버전의 그 순간 내용)여야 한다. 라이브 콘텐츠가 아니다.
    private PortfolioAiField toAiField(
            PortfolioAiFeedback feedback,
            AiFieldRevisionDTO revision,
            Map<Long, PortfolioField> fieldById,
            Map<Long, PortfolioProject> projectById,
            Map<String, String> sourceTextByKey
    ) {
        PortfolioAiField.PortfolioAiFieldBuilder builder = PortfolioAiField.builder()
                .feedback(feedback)
                .targetType(revision.fieldType())
                .aiRevisedText(revision.aiRevisedText());

        switch (revision.fieldType()) {
            case CUSTOM_FIELD -> builder.portfolioField(fieldById.get(revision.fieldId()))
                    .originalText(sourceTextByKey.get(sourceFieldKey(AiFieldTargetType.CUSTOM_FIELD, revision.fieldId())));
            case PROJECT_SUMMARY -> builder.portfolioProject(projectById.get(revision.fieldId()))
                    .originalText(sourceTextByKey.get(sourceFieldKey(AiFieldTargetType.PROJECT_SUMMARY, revision.fieldId())));
            default -> builder.originalText(sourceTextByKey.get(revision.fieldType().name()));
        }

        return builder.build();
    }

    // 선택된 소스 버전(sourceFeedback)의 각 필드를 AI 입력 형식으로 변환한다. 라이브 콘텐츠가 아니라
    // 그 버전 자신의 resolvedText를 사용하되, 제목/설명 같은 메타데이터는 실제 필드/프로젝트에서 가져온다.
    // 소스 버전이 생성된 뒤 그 필드/프로젝트 자체가 삭제됐을 수 있으므로(참조는 남아있는 지연 로딩 프록시),
    // 그 프록시의 getTitle() 등을 직접 호출하지 않고 — 삭제된 행이면 초기화 시점에 예외가 난다 — 이미
    // 현재 조회해둔 fieldById/projectById(살아있는 행만 포함)에서 조회해 없으면 그 필드는 건너뛴다.
    private List<AiFieldInputDTO> buildFieldInputsFromSource(
            List<PortfolioAiField> sourceFields,
            Map<Long, PortfolioField> fieldById,
            Map<Long, PortfolioProject> projectById
    ) {
        List<AiFieldInputDTO> inputs = new ArrayList<>();
        for (PortfolioAiField sourceField : sourceFields) {
            String content = sourceField.getResolvedText();
            if (content == null || content.isBlank()) {
                continue;
            }
            switch (sourceField.getTargetType()) {
                case CUSTOM_FIELD -> {
                    PortfolioField ref = sourceField.getPortfolioField();
                    PortfolioField field = ref != null ? fieldById.get(ref.getId()) : null;
                    if (field != null) {
                        inputs.add(new AiFieldInputDTO(field.getId(), AiFieldTargetType.CUSTOM_FIELD, field.getTitle(), field.getDescription(), content));
                    }
                }
                case PROJECT_SUMMARY -> {
                    PortfolioProject ref = sourceField.getPortfolioProject();
                    PortfolioProject project = ref != null ? projectById.get(ref.getId()) : null;
                    if (project != null) {
                        inputs.add(new AiFieldInputDTO(project.getId(), AiFieldTargetType.PROJECT_SUMMARY, project.getTitle(), null, content));
                    }
                }
                case PORTFOLIO_ONE_LINER -> inputs.add(new AiFieldInputDTO(
                        sourceField.getFeedback().getPortfolio().getId(), AiFieldTargetType.PORTFOLIO_ONE_LINER,
                        AiFieldTargetType.PORTFOLIO_ONE_LINER.getLabel(), null, content));
                case PORTFOLIO_DESCRIPTION -> inputs.add(new AiFieldInputDTO(
                        sourceField.getFeedback().getPortfolio().getId(), AiFieldTargetType.PORTFOLIO_DESCRIPTION,
                        AiFieldTargetType.PORTFOLIO_DESCRIPTION.getLabel(), null, content));
                case PROFILE_ONE_LINER -> inputs.add(new AiFieldInputDTO(
                        sourceField.getFeedback().getPortfolio().getTalentProfile().getId(), AiFieldTargetType.PROFILE_ONE_LINER,
                        AiFieldTargetType.PROFILE_ONE_LINER.getLabel(), null, content));
            }
        }
        return inputs;
    }

    private String sourceFieldKey(PortfolioAiField field) {
        return switch (field.getTargetType()) {
            case CUSTOM_FIELD -> sourceFieldKey(AiFieldTargetType.CUSTOM_FIELD, field.getPortfolioField() != null ? field.getPortfolioField().getId() : null);
            case PROJECT_SUMMARY -> sourceFieldKey(AiFieldTargetType.PROJECT_SUMMARY, field.getPortfolioProject() != null ? field.getPortfolioProject().getId() : null);
            default -> field.getTargetType().name();
        };
    }

    private String sourceFieldKey(AiFieldTargetType type, Long id) {
        return type.name() + ":" + id;
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
