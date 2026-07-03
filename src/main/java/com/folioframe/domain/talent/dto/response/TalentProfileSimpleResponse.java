package com.folioframe.domain.talent.dto.response;

import lombok.Builder;
import lombok.Getter;
import java.util.List;

@Getter
@Builder
public class TalentProfileSimpleResponse {
    private Long talentProfileId;
    private String name;
    private String job;
    private String career;
    private List<String> techStacks;
    private int viewCount;
    private int bookmarkCount;
}