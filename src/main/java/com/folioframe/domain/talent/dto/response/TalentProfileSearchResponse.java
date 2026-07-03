package com.folioframe.domain.talent.dto.response;

import lombok.Builder;
import lombok.Getter;
import java.util.List;

@Getter
@Builder
public class TalentProfileSearchResponse {
    private SearchConditionResponse searchCondition;
    private List<TalentProfileSimpleResponse> content;
    private PageableResponse pageable;

    @Getter
    @Builder
    public static class SearchConditionResponse {
        private String sort;
        private String career;
        private String job;
    }

    @Getter
    @Builder
    public static class PageableResponse {
        private int pageNumber;
        private int pageSize;
        private long totalElements;
    }
}