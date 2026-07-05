package com.folioframe.domain.job.dto.request;

import com.folioframe.domain.job.enums.ApplicationStatus;

public record JobApplicationStatusUpdateReqDTO (
        ApplicationStatus status
) {

}
