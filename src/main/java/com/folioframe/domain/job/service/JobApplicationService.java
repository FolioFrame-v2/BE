package com.folioframe.domain.job.service;

import com.folioframe.domain.job.dto.request.JobApplicationCreateReqDTO;
import com.folioframe.domain.job.dto.request.JobApplicationStatusUpdateReqDTO;
import com.folioframe.domain.job.dto.response.JobApplicationCreateResDTO;
import com.folioframe.domain.job.dto.response.JobApplicationDetailResDTO;
import com.folioframe.domain.job.dto.response.JobApplicationResDTO;
import com.folioframe.domain.job.dto.response.JobApplicationStatusUpdateResDTO;
import org.springframework.data.domain.Pageable;

public interface JobApplicationService {
    JobApplicationCreateResDTO createApplication(Long memberId, JobApplicationCreateReqDTO request);
    JobApplicationStatusUpdateResDTO updateApplicationStatus(Long memberId, Long applicationId, JobApplicationStatusUpdateReqDTO request);
    JobApplicationResDTO getApplications(Long memberId, Pageable pageable);
    JobApplicationDetailResDTO getApplicationDetail(Long memberId, Long applicationId);
}