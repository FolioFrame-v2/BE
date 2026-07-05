package com.folioframe.domain.chat.entity;

import com.folioframe.domain.chat.enums.ChatOriginType;
import com.folioframe.domain.company.entity.CompanyProfile;
import com.folioframe.domain.job.entity.JobPosting;
import com.folioframe.domain.portfolio.entity.Portfolio;
import com.folioframe.domain.talent.entity.TalentProfile;
import com.folioframe.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "chat_room")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ChatRoom extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chat_room_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_profile_id", nullable = false)
    private CompanyProfile companyProfile;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "talent_profile_id", nullable = false)
    private TalentProfile talentProfile;

    @Enumerated(EnumType.STRING)
    @Column(name = "origin_type", nullable = false)
    private ChatOriginType originType;

    // originType == PORTFOLIO 일 때만 값이 들어감
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "portfolio_id")
    private Portfolio portfolio;

    // originType == JOB_POSTING 일 때만 값이 들어감
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_posting_id")
    private JobPosting jobPosting;

    @Column(name = "last_message_at")
    private LocalDateTime lastMessageAt;

    // 목록 표시·검색용으로 마지막 메시지 내용을 비정규화해서 들고 있음(Bookmark의 count 비정규화 패턴과 동일한 취지)
    @Column(name = "last_message_preview", length = 500)
    private String lastMessagePreview;

    public void updateLastMessage(String preview, LocalDateTime sentAt) {
        this.lastMessagePreview = preview;
        this.lastMessageAt = sentAt;
    }
}
