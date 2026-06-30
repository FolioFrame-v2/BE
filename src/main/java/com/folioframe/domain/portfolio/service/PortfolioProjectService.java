package com.folioframe.domain.portfolio.service;

import com.folioframe.domain.portfolio.dto.request.ProjectReqDTO;
import com.folioframe.domain.portfolio.dto.response.ProjectResDTO;
import com.folioframe.domain.portfolio.entity.Portfolio;
import com.folioframe.domain.portfolio.entity.PortfolioProject;
import com.folioframe.domain.portfolio.exception.PortfolioException;
import com.folioframe.domain.portfolio.exception.code.PortfolioErrorCode;
import com.folioframe.domain.portfolio.repository.PortfolioProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PortfolioProjectService {

    private final PortfolioProjectRepository projectRepository;
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
                .thumbnailUrl(request.thumbnailUrl())
                .projectUrl(request.projectUrl())
                .durationRange(request.durationRange())
                .build();

        return ProjectResDTO.from(projectRepository.save(project));
    }

    @Transactional(readOnly = true)
    public List<ProjectResDTO> getList(Long portfolioId) {
        Portfolio portfolio = portfolioService.findPortfolio(portfolioId);
        return projectRepository.findAllByPortfolioOrderByCreatedAtDesc(portfolio)
                .stream()
                .map(ProjectResDTO::from)
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
                request.thumbnailUrl(),
                request.projectUrl(),
                request.durationRange()
        );

        return ProjectResDTO.from(project);
    }

    @Transactional
    public void delete(Long portfolioId, Long projectId, Long memberId) {
        Portfolio portfolio = portfolioService.findPortfolio(portfolioId);
        portfolioService.validateOwnership(portfolio, memberId);

        PortfolioProject project = findProject(projectId);
        validateBelongsToPortfolio(project, portfolioId);

        projectRepository.delete(project);
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
