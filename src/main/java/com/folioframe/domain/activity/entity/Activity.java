package com.folioframe.domain.activity.entity;

import com.folioframe.domain.activity.enums.ActivityCategory;
import com.folioframe.domain.activity.enums.ActivityField;
import com.folioframe.domain.activity.enums.ActivityTeamSize;
import com.folioframe.domain.common.enums.JobRole;
import com.folioframe.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "activity")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Activity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "activity_id")
    private Long id;

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false)
    private ActivityCategory category;

    @Column(name = "organizer", length = 200)
    private String organizer;

    @Column(name = "region", length = 100)
    private String region;

    @Enumerated(EnumType.STRING)
    @Column(name = "part")
    private JobRole part;

    @Enumerated(EnumType.STRING)
    @Column(name = "field")
    private ActivityField field;

    @Enumerated(EnumType.STRING)
    @Column(name = "team_size")
    private ActivityTeamSize teamSize;

    @Column(name = "prize", length = 100)
    private String prize;

    @Column(name = "source_name", nullable = false, length = 50)
    private String sourceName;

    @Column(name = "source_url", nullable = false, length = 500)
    private String sourceUrl;

    @Column(name = "recruitment_end")
    private LocalDate recruitmentEnd;

    @Builder.Default
    @Column(name = "bookmark_count", nullable = false)
    private int bookmarkCount = 0;
}
