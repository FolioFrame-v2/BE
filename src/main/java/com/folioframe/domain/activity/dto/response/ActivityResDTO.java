package com.folioframe.domain.activity.dto.response;

import com.folioframe.domain.activity.entity.Activity;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class ActivityResDTO {

    private Long activityId;
    private String title;
    private String category;
    private String organizer;
    private String region;
    private String part;
    private String field;
    private String teamSize;
    private String prize;
    private String sourceName;
    private String sourceUrl;
    private LocalDate recruitmentEnd;
    private int bookmarkCount;
    private int viewCount;
    private boolean bookmarked;

    public static ActivityResDTO of(Activity activity, boolean bookmarked) {
        return ActivityResDTO.builder()
                .activityId(activity.getId())
                .title(activity.getTitle())
                .category(activity.getCategory().getLabel())
                .organizer(activity.getOrganizer())
                .region(activity.getRegion())
                .part(activity.getPart() != null ? activity.getPart().getLabel() : null)
                .field(activity.getField() != null ? activity.getField().getLabel() : null)
                .teamSize(activity.getTeamSize() != null ? activity.getTeamSize().getLabel() : null)
                .prize(activity.getPrize())
                .sourceName(activity.getSourceName())
                .sourceUrl(activity.getSourceUrl())
                .recruitmentEnd(activity.getRecruitmentEnd())
                .bookmarkCount(activity.getBookmarkCount())
                .viewCount(activity.getViewCount())
                .bookmarked(bookmarked)
                .build();
    }
}
