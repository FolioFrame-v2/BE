package com.folioframe.domain.company.entity;

import com.folioframe.domain.common.entity.Techstack;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "company_techstack")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CompanyTechstack {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "company_techstack_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_profile_id", nullable = false)
    private CompanyProfile companyProfile;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "techstack_id", nullable = false)
    private Techstack techstack;
}
