package com.folioframe.domain.job.dto.request;

import com.folioframe.domain.common.enums.CareerLevel;
import com.folioframe.domain.job.enums.JobPostingSort;
import com.folioframe.domain.job.enums.JobPostingStatus;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.Data;
import org.springdoc.core.annotations.ParameterObject;

@Data
@ParameterObject
public class JobPostingSearchCond {

    @Parameter(description = "기업명, 직무, 기술 스택 검색")
    private String keyword;

    @Parameter(description = "상태 (ALL, ACTIVE: 채용중, CLOSING_SOON: 마감임박)")
    private JobPostingStatus status;

    @Parameter(description = "지역 ID (시/도, 시/구/군)")
    private Long regionId;

    @Parameter(description = "경력 조건")
    private CareerLevel careerLevel;

    @Parameter(description = "정렬 (LATEST / POPULAR / MOST_VIEWED)", example = "LATEST")
    private JobPostingSort sort = JobPostingSort.LATEST;

    @Parameter(description = "페이지 번호 (1부터 시작)", example = "1")
    private Integer page = 1;

    @Parameter(description = "페이지 크기", example = "9")
    private Integer size = 9;
}