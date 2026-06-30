package com.folioframe.domain.portfolio.service;

import com.folioframe.domain.portfolio.dto.request.CertificateReqDTO;
import com.folioframe.domain.portfolio.dto.response.CertificateResDTO;
import com.folioframe.domain.portfolio.entity.Portfolio;
import com.folioframe.domain.portfolio.entity.PortfolioCertificate;
import com.folioframe.domain.portfolio.exception.PortfolioException;
import com.folioframe.domain.portfolio.exception.code.PortfolioErrorCode;
import com.folioframe.domain.portfolio.repository.PortfolioCertificateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PortfolioCertificateService {

    private final PortfolioCertificateRepository certificateRepository;
    private final PortfolioService portfolioService;

    @Transactional
    public CertificateResDTO create(Long portfolioId, Long memberId, CertificateReqDTO request) {
        Portfolio portfolio = portfolioService.findPortfolio(portfolioId);
        portfolioService.validateOwnership(portfolio, memberId);

        PortfolioCertificate certificate = PortfolioCertificate.builder()
                .portfolio(portfolio)
                .name(request.name())
                .issuer(request.issuer())
                .issuedAt(request.issuedAt())
                .expiresAt(request.expiresAt())
                .credentialId(request.credentialId())
                .build();

        return CertificateResDTO.from(certificateRepository.save(certificate));
    }

    @Transactional(readOnly = true)
    public List<CertificateResDTO> getList(Long portfolioId) {
        Portfolio portfolio = portfolioService.findPortfolio(portfolioId);
        return certificateRepository.findAllByPortfolioOrderByIssuedAtDesc(portfolio)
                .stream()
                .map(CertificateResDTO::from)
                .toList();
    }

    @Transactional
    public CertificateResDTO update(Long portfolioId, Long certificateId, Long memberId, CertificateReqDTO request) {
        Portfolio portfolio = portfolioService.findPortfolio(portfolioId);
        portfolioService.validateOwnership(portfolio, memberId);

        PortfolioCertificate certificate = findCertificate(certificateId);
        validateBelongsToPortfolio(certificate, portfolioId);

        certificate.update(
                request.name(),
                request.issuer(),
                request.issuedAt(),
                request.expiresAt(),
                request.credentialId()
        );

        return CertificateResDTO.from(certificate);
    }

    @Transactional
    public void delete(Long portfolioId, Long certificateId, Long memberId) {
        Portfolio portfolio = portfolioService.findPortfolio(portfolioId);
        portfolioService.validateOwnership(portfolio, memberId);

        PortfolioCertificate certificate = findCertificate(certificateId);
        validateBelongsToPortfolio(certificate, portfolioId);

        certificateRepository.delete(certificate);
    }

    private PortfolioCertificate findCertificate(Long certificateId) {
        return certificateRepository.findById(certificateId)
                .orElseThrow(() -> new PortfolioException(PortfolioErrorCode.CERTIFICATE_NOT_FOUND));
    }

    private void validateBelongsToPortfolio(PortfolioCertificate certificate, Long portfolioId) {
        if (!certificate.getPortfolio().getId().equals(portfolioId)) {
            throw new PortfolioException(PortfolioErrorCode.CERTIFICATE_NOT_IN_PORTFOLIO);
        }
    }
}
