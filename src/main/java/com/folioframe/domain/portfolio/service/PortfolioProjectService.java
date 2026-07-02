package com.folioframe.domain.portfolio.service;

import com.folioframe.domain.common.entity.Techstack;
import com.folioframe.domain.common.repository.TechstackRepository;
import com.folioframe.domain.portfolio.dto.request.ProjectReqDTO;
import com.folioframe.domain.portfolio.dto.response.ProjectResDTO;
import com.folioframe.domain.common.dto.response.TechstackResDTO;
import com.folioframe.domain.portfolio.entity.Portfolio;
import com.folioframe.domain.portfolio.entity.PortfolioProject;
import com.folioframe.domain.portfolio.entity.ProjectTechstack;
import com.folioframe.domain.portfolio.exception.PortfolioException;
import com.folioframe.domain.portfolio.exception.code.PortfolioErrorCode;
import com.folioframe.domain.portfolio.repository.PortfolioProjectRepository;
import com.folioframe.domain.portfolio.repository.ProjectTechstackRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PortfolioProjectService {

    private final PortfolioProjectRepository projectRepository;
    private final ProjectTechstackRepository projectTechstackRepository;
    private final TechstackRepository techstackRepository;
    private final PortfolioService portfolioService;

    @Transactional
    public ProjectResDTO create(Long portfolioId, Long memberId, ProjectReqDTO request) {
        Portfolio portfolio = portfolioService.findPortfolio(portfolioId);
        portfolioService.validateOwnership(portfolio, memberId);

        PortfolioProject project = PortfolioProject.builder()
                .portfolio(portfolio)
                .title(request.title())
                .role(request.role())
                .content(request.content())
                .projectUrl(request.projectUrl())
                .startedAt(request.startedAt())
                .endedAt(request.endedAt())
                .build();
        projectRepository.save(project);

        List<Techstack> techstacks = attachTechstacks(project, request.techstackIds());
        portfolio.markSaved();

        return ProjectResDTO.from(project, techstacks);
    }

    @Transactional(readOnly = true)
    public List<ProjectResDTO> getList(Long portfolioId) {
        Portfolio portfolio = portfolioService.findPortfolio(portfolioId);
        List<PortfolioProject> projects = projectRepository.findAllByPortfolioOrderByCreatedAtDesc(portfolio);

        Map<Long, List<Techstack>> techstacksByProjectId = projectTechstackRepository
                .findAllByPortfolioProjectInWithTechstack(projects)
                .stream()
                .collect(Collectors.groupingBy(
                        pt -> pt.getPortfolioProject().getId(),
                        Collectors.mapping(ProjectTechstack::getTechstack, Collectors.toList())
                ));

        return projects.stream()
                .map(project -> ProjectResDTO.from(project, techstacksByProjectId.getOrDefault(project.getId(), List.of())))
                .toList();
    }

    @Transactional
    public ProjectResDTO update(Long portfolioId, Long projectId, Long memberId, ProjectReqDTO request) {
        Portfolio portfolio = portfolioService.findPortfolio(portfolioId);
        portfolioService.validateOwnership(portfolio, memberId);

        PortfolioProject project = findProject(projectId);
        validateBelongsToPortfolio(project, portfolioId);

        project.update(
                request.title(),
                request.role(),
                request.content(),
                request.projectUrl(),
                request.startedAt(),
                request.endedAt()
        );

        List<Techstack> techstacks = replaceTechstacks(project, request.techstackIds());
        portfolio.markSaved();

        return ProjectResDTO.from(project, techstacks);
    }

    @Transactional
    public void delete(Long portfolioId, Long projectId, Long memberId) {
        Portfolio portfolio = portfolioService.findPortfolio(portfolioId);
        portfolioService.validateOwnership(portfolio, memberId);

        PortfolioProject project = findProject(projectId);
        validateBelongsToPortfolio(project, portfolioId);

        projectRepository.delete(project);
        portfolio.markSaved();
    }

    @Transactional
    public List<TechstackResDTO> updateTechstacks(Long portfolioId, Long projectId, Long memberId, List<Long> techstackIds) {
        Portfolio portfolio = portfolioService.findPortfolio(portfolioId);
        portfolioService.validateOwnership(portfolio, memberId);

        PortfolioProject project = findProject(projectId);
        validateBelongsToPortfolio(project, portfolioId);

        List<Techstack> techstacks = replaceTechstacks(project, techstackIds);
        portfolio.markSaved();

        return techstacks.stream().map(TechstackResDTO::from).toList();
    }

    private List<Techstack> replaceTechstacks(PortfolioProject project, List<Long> techstackIds) {
        projectTechstackRepository.deleteAllByPortfolioProject(project);
        return attachTechstacks(project, techstackIds);
    }

    private List<Techstack> attachTechstacks(PortfolioProject project, List<Long> techstackIds) {
        if (techstackIds == null || techstackIds.isEmpty()) {
            return List.of();
        }

        Set<Long> uniqueIds = Set.copyOf(techstackIds);
        List<Techstack> techstacks = techstackRepository.findAllByIdIn(techstackIds);
        if (techstacks.size() != uniqueIds.size()) {
            throw new PortfolioException(PortfolioErrorCode.TECHSTACK_NOT_FOUND);
        }

        List<ProjectTechstack> projectTechstacks = techstacks.stream()
                .map(techstack -> ProjectTechstack.builder()
                        .portfolioProject(project)
                        .techstack(techstack)
                        .build())
                .toList();
        projectTechstackRepository.saveAll(projectTechstacks);

        return techstacks;
    }

    private PortfolioProject findProject(Long projectId) {
        return projectRepository.findById(projectId)
                .orElseThrow(() -> new PortfolioException(PortfolioErrorCode.PROJECT_NOT_FOUND));
    }

    private void validateBelongsToPortfolio(PortfolioProject project, Long portfolioId) {
        if (!project.getPortfolio().getId().equals(portfolioId)) {
            throw new PortfolioException(PortfolioErrorCode.PROJECT_NOT_IN_PORTFOLIO);
        }
    }
}
