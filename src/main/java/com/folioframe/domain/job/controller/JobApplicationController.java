package com.folioframe.domain.job.controller;

import com.folioframe.domain.job.dto.request.JobApplicationCreateReqDTO;
import com.folioframe.domain.job.dto.request.JobApplicationStatusUpdateReqDTO;
import com.folioframe.domain.job.dto.response.JobApplicationCreateResDTO;
import com.folioframe.domain.job.dto.response.JobApplicationDetailResDTO;
import com.folioframe.domain.job.dto.response.JobApplicationResDTO;
import com.folioframe.domain.job.dto.response.JobApplicationStatusUpdateResDTO;
import com.folioframe.domain.job.enums.ApplicationSortType;
import com.folioframe.domain.job.service.JobApplicationService;
import com.folioframe.global.auth.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Jop Application", description = "채용 공고 지원, 내역 조회 및 상태 변경 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/job-applications")
public class JobApplicationController {

    private final JobApplicationService jobApplicationService;

    @Operation(summary = "채용 공고 지원", description = "원하는 채용 공고에 포트폴리오를 첨부하여 지원합니다.")
    @PostMapping
    public ResponseEntity<JobApplicationCreateResDTO> createApplication(
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody JobApplicationCreateReqDTO request) {

        Long memberId = userDetails.member().getId();
        JobApplicationCreateResDTO response = jobApplicationService.createApplication(memberId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "내 지원 내역 목록 조회", description = "로그인한 사용자의 채용 공고 지원 내역을 페이징하여 조회합니다.")
    @GetMapping
    public ResponseEntity<JobApplicationResDTO> getApplications(
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails,
            @Parameter(description = "정렬 (LATEST: 최신순 / OLDEST: 오래된 순, 기본값: LATEST)")
            @RequestParam(defaultValue = "LATEST") ApplicationSortType sort,
            @Parameter(description = "페이지 번호 (1부터 시작, 기본값: 1)")
            @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "페이지 크기 (기본값: 9)")
            @RequestParam(defaultValue = "9") int size) {

        Long memberId = userDetails.member().getId();

        int pageNumber = Math.max(0, page - 1);

        Sort springSort = switch (sort) {
            case LATEST -> Sort.by(Sort.Direction.DESC, "createdAt");
            case OLDEST -> Sort.by(Sort.Direction.ASC, "createdAt");
        };

        Pageable pageable = PageRequest.of(pageNumber, size, springSort);

        JobApplicationResDTO response = jobApplicationService.getApplications(memberId, pageable);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "지원 내역 상세 조회", description = "특정 지원 내역의 상세 정보를 조회합니다.")
    @GetMapping("/{applicationId}")
    public ResponseEntity<JobApplicationDetailResDTO> getApplicationDetail(
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails,
            @Parameter(description = "조회할 지원 내역 ID") @PathVariable("applicationId") Long applicationId) {

        Long memberId = userDetails.member().getId();
        JobApplicationDetailResDTO response = jobApplicationService.getApplicationDetail(memberId, applicationId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "지원 상태 변경", description = "지원 내역의 상태(승인, 거절 등)를 변경합니다. 해당 공고를 작성한 기업 회원만 접근할 수 있습니다.")
    @PatchMapping("/{applicationId}/status")
    public ResponseEntity<JobApplicationStatusUpdateResDTO> updateApplicationStatus(
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails,
            @Parameter(description = "상태를 변경할 지원 내역 ID") @PathVariable("applicationId") Long applicationId,
            @Valid @RequestBody JobApplicationStatusUpdateReqDTO request) {

        Long memberId = userDetails.member().getId();
        JobApplicationStatusUpdateResDTO response = jobApplicationService.updateApplicationStatus(memberId, applicationId, request);
        return ResponseEntity.ok(response);
    }
}