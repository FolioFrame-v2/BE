package com.folioframe.domain.activity.controller;

import com.folioframe.domain.activity.dto.response.ActivityResDTO;
import com.folioframe.domain.activity.enums.ActivityCategory;
import com.folioframe.domain.activity.enums.ActivitySortType;
import com.folioframe.domain.activity.exception.code.ActivitySuccessCode;
import com.folioframe.domain.activity.service.ActivityService;
import com.folioframe.global.apiPayload.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/activities")
@RequiredArgsConstructor
public class ActivityController implements ActivityControllerDocs {

    private final ActivityService activityService;

    @Override
    @GetMapping
    public ResponseEntity<ApiResponse<List<ActivityResDTO>>> getActivities(
            @RequestParam(required = false) ActivityCategory category,
            @RequestParam(required = false) ActivitySortType sort,
            @RequestHeader(value = "X-Member-Id", required = false) Long memberId) {
        List<ActivityResDTO> result = activityService.getActivities(category, sort, memberId);
        return ResponseEntity.ok(ApiResponse.onSuccess(ActivitySuccessCode.ACTIVITY_LIST_FOUND, result));
    }

    @Override
    @PostMapping("/{activityId}/views")
    public ResponseEntity<ApiResponse<Void>> incrementViewCount(@PathVariable Long activityId) {
        activityService.incrementViewCount(activityId);
        return ResponseEntity.ok(ApiResponse.onSuccess(ActivitySuccessCode.ACTIVITY_VIEW_COUNTED, null));
    }

    @Override
    @GetMapping("/bookmarks")
    public ResponseEntity<ApiResponse<List<ActivityResDTO>>> getBookmarkedActivities(
            @RequestHeader("X-Member-Id") Long memberId) {
        List<ActivityResDTO> result = activityService.getBookmarkedActivities(memberId);
        return ResponseEntity.ok(ApiResponse.onSuccess(ActivitySuccessCode.ACTIVITY_BOOKMARKED_LIST_FOUND, result));
    }
}
