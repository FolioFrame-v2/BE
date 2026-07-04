package com.folioframe.domain.job.repository;

import com.folioframe.domain.job.entity.JobPostingBookmark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface JobPostingBookmarkRepository extends JpaRepository<JobPostingBookmark, Long> {

    boolean existsByJobPostingIdAndMemberId(Long jobPostingId, Long memberId);

    Optional<JobPostingBookmark> findByJobPostingIdAndMemberId(Long jobPostingId, Long memberId);

    @Modifying
    @Query("DELETE FROM JobPostingBookmark jb WHERE jb.jobPosting.id = :jobPostingId")
    void deleteByJobPostingId(@Param("jobPostingId") Long jobPostingId);
}