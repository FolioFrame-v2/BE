package com.folioframe.domain.portfolio.service;

import com.folioframe.domain.portfolio.dto.response.TemplateDetailResDTO;
import com.folioframe.domain.portfolio.dto.response.TemplateResDTO;
import com.folioframe.domain.portfolio.entity.PortfolioTemplate;
import com.folioframe.domain.portfolio.entity.TemplateField;
import com.folioframe.domain.portfolio.exception.PortfolioException;
import com.folioframe.domain.portfolio.exception.code.PortfolioErrorCode;
import com.folioframe.domain.portfolio.repository.PortfolioTemplateRepository;
import com.folioframe.domain.portfolio.repository.TemplateFieldRepository;
import com.folioframe.global.dto.PageRequest;
import com.folioframe.global.dto.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PortfolioTemplateService {

    private final PortfolioTemplateRepository templateRepository;
    private final TemplateFieldRepository templateFieldRepository;

    @Transactional(readOnly = true)
    public PageResponse<TemplateResDTO> getList(PageRequest pageRequest) {
        return PageResponse.of(
                templateRepository.findAllByActiveTrueOrderByUseCountDesc(pageRequest.toPageable())
                        .map(TemplateResDTO::from)
        );
    }

    @Transactional(readOnly = true)
    public TemplateDetailResDTO getDetail(Long templateId) {
        PortfolioTemplate template = findTemplate(templateId);
        List<TemplateField> fields = templateFieldRepository.findAllByTemplateOrderByDisplayOrder(template);
        return TemplateDetailResDTO.from(template, fields);
    }

    PortfolioTemplate findTemplate(Long templateId) {
        return templateRepository.findById(templateId)
                .orElseThrow(() -> new PortfolioException(PortfolioErrorCode.TEMPLATE_NOT_FOUND));
    }
}
