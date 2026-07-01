package com.folioframe.domain.activity.service;

import com.folioframe.domain.activity.entity.Activity;
import com.folioframe.domain.activity.entity.ActivityBookmark;
import com.folioframe.domain.activity.exception.ActivityException;
import com.folioframe.domain.activity.exception.code.ActivityErrorCode;
import com.folioframe.domain.activity.repository.ActivityBookmarkRepository;
import com.folioframe.domain.member.entity.Member;
import com.folioframe.domain.member.exception.MemberException;
import com.folioframe.domain.member.exception.code.MemberErrorCode;
import com.folioframe.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ActivityBookmarkService {

    private final ActivityBookmarkRepository activityBookmarkRepository;
    private final ActivityService activityService;
    private final MemberRepository memberRepository;

    @Transactional
    public void bookmark(Long activityId, Long memberId) {
        Activity activity = activityService.findActivity(activityId);
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));

        if (activityBookmarkRepository.existsByMemberAndActivity(member, activity)) {
            throw new ActivityException(ActivityErrorCode.ACTIVITY_BOOKMARK_ALREADY_EXISTS);
        }

        ActivityBookmark bookmark = ActivityBookmark.builder()
                .member(member)
                .activity(activity)
                .build();

        activityBookmarkRepository.save(bookmark);
        activity.increaseBookmarkCount();
    }

    @Transactional
    public void cancelBookmark(Long activityId, Long memberId) {
        Activity activity = activityService.findActivity(activityId);
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));

        ActivityBookmark bookmark = activityBookmarkRepository.findByMemberAndActivity(member, activity)
                .orElseThrow(() -> new ActivityException(ActivityErrorCode.ACTIVITY_BOOKMARK_NOT_FOUND));

        activityBookmarkRepository.delete(bookmark);
        activity.decreaseBookmarkCount();
    }
}
