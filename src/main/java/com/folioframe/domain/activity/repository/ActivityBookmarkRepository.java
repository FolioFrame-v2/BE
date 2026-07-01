package com.folioframe.domain.activity.repository;

import com.folioframe.domain.activity.entity.Activity;
import com.folioframe.domain.activity.entity.ActivityBookmark;
import com.folioframe.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface ActivityBookmarkRepository extends JpaRepository<ActivityBookmark, Long> {

    boolean existsByMemberAndActivity(Member member, Activity activity);

    Optional<ActivityBookmark> findByMemberAndActivity(Member member, Activity activity);

    List<ActivityBookmark> findAllByMemberOrderByCreatedAtDesc(Member member);

    @Query("SELECT ab.activity.id FROM ActivityBookmark ab WHERE ab.member = :member")
    Set<Long> findActivityIdsByMember(@Param("member") Member member);
}
