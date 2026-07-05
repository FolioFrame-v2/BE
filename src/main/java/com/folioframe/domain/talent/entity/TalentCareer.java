package com.folioframe.domain.talent.entity;

import com.folioframe.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDate;

@Entity
@Table(name = "talent_career")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class TalentCareer extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "talent_career_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "talent_profile_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private TalentProfile talentProfile;

    @Column(name = "company_name", nullable = false, length = 100)
    private String companyName;

    @Column(name = "position", length = 100)
    private String position;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "started_at", nullable = false)
    private LocalDate startedAt;

    // null 이면 재직중
    @Column(name = "ended_at")
    private LocalDate endedAt;

    public void update(String companyName, String position, String description,
                       LocalDate startedAt, LocalDate endedAt) {
        this.companyName = companyName;
        this.position = position;
        this.description = description;
        this.startedAt = startedAt;
        this.endedAt = endedAt;
    }
}
