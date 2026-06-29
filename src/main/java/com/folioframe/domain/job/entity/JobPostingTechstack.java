package com.folioframe.domain.job.entity;

import com.folioframe.domain.common.entity.Techstack;
import com.folioframe.domain.job.enums.StackType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "job_posting_techstack")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class JobPostingTechstack {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "job_posting_techstack_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_posting_id", nullable = false)
    private JobPosting jobPosting;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "techstack_id", nullable = false)
    private Techstack techstack;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "stack_type", nullable = false)
    private StackType stackType = StackType.REQUIRED;
}
