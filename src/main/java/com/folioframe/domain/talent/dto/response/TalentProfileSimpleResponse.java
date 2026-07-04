package com.folioframe.domain.talent.dto.response;

import lombok.Builder;
import lombok.Getter;
import java.util.List;

@Getter
@Builder
public class TalentProfileSimpleResponse {
    private Long talentProfileId;
    private String name;
    private String oneLiner;
    private String jobRole;
    private String careerLevel;
    private List<String> techStacks;
    private int viewCount;
    private int bookmarkCount;
}