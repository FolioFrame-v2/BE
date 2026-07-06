package com.folioframe.domain.member.repository;

import com.folioframe.domain.member.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    Page<Notification> findAllByMemberIdOrderByCreatedAtDesc(Long memberId, Pageable pageable);

    Optional<Notification> findByIdAndMemberId(Long id, Long memberId);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE Notification n SET n.read = true, n.readAt = CURRENT_TIMESTAMP WHERE n.member.id = :memberId AND n.read = false")
    int updateAllReadByMemberId(@Param("memberId") Long memberId);
}