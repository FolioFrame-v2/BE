package com.folioframe.domain.job.controller;

import com.folioframe.domain.job.dto.request.JobPostingReqDTO;
import com.folioframe.domain.job.dto.response.JobPostingDetailResDTO;
import com.folioframe.domain.job.dto.response.JobPostingListResDTO;
import com.folioframe.domain.job.exception.code.JobSuccessCode;
import com.folioframe.domain.job.service.JobPostingService;
import com.folioframe.global.apiPayload.ApiResponse;
import com.folioframe.global.auth.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@Tag(name = "Job Posting", description = "채용 공고 등록, 조회, 수정, 삭제 및 북마크 관련 API")
@RestController
@RequestMapping("/api/v1/job-postings")
@RequiredArgsConstructor
public class JobPostingController {

    private final JobPostingService jobPostingService;

    @Operation(summary = "채용 공고 등록", description = "기업 사용자가 새로운 채용 공고를 등록합니다.")
    @PostMapping
    public ResponseEntity<ApiResponse<Map<String, Long>>> createJobPosting(
            @Valid @RequestBody JobPostingReqDTO request,
            @AuthenticationPrincipal CustomUserDetails user) {

        Long companyId = user.member().getId();

        Long jobPostingId = jobPostingService.createJobPosting(request, companyId);

        return ResponseEntity.status(JobSuccessCode.JOB_POSTING_CREATED.getStatus())
                .body(ApiResponse.onSuccess(JobSuccessCode.JOB_POSTING_CREATED,
                        Map.of("jobPostingId", jobPostingId)));
    }

    @Operation(summary = "채용 공고 목록 조회", description = "키워드와 지역 필터를 사용하여 채용 공고 목록을 페이징 조회합니다.")
    @GetMapping
    public ResponseEntity<ApiResponse<Page<JobPostingListResDTO>>> getJobPostings(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long regionId,
            @PageableDefault(size = 10) Pageable pageable) {

        Page<JobPostingListResDTO> response =
                jobPostingService.getJobPostings(keyword, regionId, pageable);

        return ResponseEntity.ok(
                ApiResponse.onSuccess(JobSuccessCode.JOB_POSTING_LIST_FETCHED, response)
        );
    }

    @Operation(summary = "채용 공고 상세 조회", description = "특정 채용 공고의 상세 정보를 조회합니다.")
    @GetMapping("/{jobPostingId}")
    public ResponseEntity<ApiResponse<JobPostingDetailResDTO>> getJobPostingDetail(
            @PathVariable Long jobPostingId,
            @AuthenticationPrincipal CustomUserDetails user) {

        Long memberId = (user != null) ? user.member().getId() : null;

        JobPostingDetailResDTO response =
                jobPostingService.getJobPostingDetail(jobPostingId, memberId);

        return ResponseEntity.ok(
                ApiResponse.onSuccess(JobSuccessCode.JOB_POSTING_FETCHED, response)
        );
    }

    @Operation(summary = "채용 공고 수정", description = "등록된 채용 공고 정보를 수정합니다.")
    @PatchMapping("/{jobPostingId}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> updateJobPosting(
            @PathVariable Long jobPostingId,
            @Valid @RequestBody JobPostingReqDTO request,
            @AuthenticationPrincipal CustomUserDetails user) {

        Long companyId = user.member().getId();

        LocalDateTime updatedAt =
                jobPostingService.updateJobPosting(jobPostingId, request, companyId);

        return ResponseEntity.ok(
                ApiResponse.onSuccess(JobSuccessCode.JOB_POSTING_UPDATED,
                        Map.of("jobPostingId", jobPostingId, "updatedAt", updatedAt))
        );
    }

    @Operation(summary = "채용 공고 삭제", description = "특정 채용 공고를 삭제합니다. 지원자가 있을 경우 삭제가 불가능합니다.")
    @DeleteMapping("/{jobPostingId}")
    public ResponseEntity<ApiResponse<Void>> deleteJobPosting(
            @PathVariable Long jobPostingId,
            @AuthenticationPrincipal CustomUserDetails user) {

        Long companyId = user.member().getId();

        jobPostingService.deleteJobPosting(jobPostingId, companyId);

        return ResponseEntity.status(JobSuccessCode.JOB_POSTING_DELETED.getStatus())
                .body(ApiResponse.onSuccess(JobSuccessCode.JOB_POSTING_DELETED, null));
    }

    @Operation(summary = "채용 공고 북마크", description = "특정 채용 공고에 대한 북마크 상태를 토글합니다.")
    @PostMapping("/{jobPostingId}/bookmarks")
    public ResponseEntity<ApiResponse<Map<String, Object>>> toggleBookmark(
            @PathVariable Long jobPostingId,
            @AuthenticationPrincipal CustomUserDetails user) {

        Long memberId = user.member().getId();

        Map<String, Object> response =
                jobPostingService.toggleBookmark(jobPostingId, memberId);

        return ResponseEntity.ok(
                ApiResponse.onSuccess(JobSuccessCode.JOB_POSTING_BOOKMARK_TOGGLED, response)
        );
    }
}