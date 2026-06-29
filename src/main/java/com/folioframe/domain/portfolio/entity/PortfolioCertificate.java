package com.folioframe.domain.portfolio.entity;

import com.folioframe.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDate;

@Entity
@Table(name = "portfolio_certificate")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class PortfolioCertificate extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "portfolio_certificate_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "portfolio_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Portfolio portfolio;

    @Column(name = "name", nullable = false, length = 200)
    private String name;

    @Column(name = "issuer", length = 100)
    private String issuer;

    @Column(name = "issued_at")
    private LocalDate issuedAt;

    // null 이면 영구 유효
    @Column(name = "expires_at")
    private LocalDate expiresAt;

    @Column(name = "credential_id", length = 100)
    private String credentialId;

    public void update(String name, String issuer, LocalDate issuedAt,
                       LocalDate expiresAt, String credentialId) {
        this.name = name;
        this.issuer = issuer;
        this.issuedAt = issuedAt;
        this.expiresAt = expiresAt;
        this.credentialId = credentialId;
    }
}
