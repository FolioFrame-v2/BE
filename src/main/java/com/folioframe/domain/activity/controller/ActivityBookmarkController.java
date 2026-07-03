package com.folioframe.domain.activity.controller;

import com.folioframe.domain.activity.exception.code.ActivitySuccessCode;
import com.folioframe.domain.activity.service.ActivityBookmarkService;
import com.folioframe.global.apiPayload.ApiResponse;
import com.folioframe.global.auth.CurrentMemberId;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/activities/{activityId}/bookmarks")
@RequiredArgsConstructor
public class ActivityBookmarkController implements ActivityBookmarkControllerDocs {

    private final ActivityBookmarkService activityBookmarkService;

    @Override
    @PostMapping
    public ResponseEntity<ApiResponse<Void>> bookmark(
            @PathVariable Long activityId,
            @CurrentMemberId Long memberId) {
        activityBookmarkService.bookmark(activityId, memberId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.onSuccess(ActivitySuccessCode.ACTIVITY_BOOKMARK_CREATED, null));
    }

    @Override
    @DeleteMapping
    public ResponseEntity<ApiResponse<Void>> cancelBookmark(
            @PathVariable Long activityId,
            @CurrentMemberId Long memberId) {
        activityBookmarkService.cancelBookmark(activityId, memberId);
        return ResponseEntity.ok(ApiResponse.onSuccess(ActivitySuccessCode.ACTIVITY_BOOKMARK_DELETED, null));
    }
}
