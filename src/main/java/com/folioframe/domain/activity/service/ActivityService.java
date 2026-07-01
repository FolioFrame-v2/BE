package com.folioframe.domain.activity.service;

import com.folioframe.domain.activity.dto.response.ActivityResDTO;
import com.folioframe.domain.activity.entity.Activity;
import com.folioframe.domain.activity.enums.ActivityCategory;
import com.folioframe.domain.activity.enums.ActivitySortType;
import com.folioframe.domain.activity.exception.ActivityException;
import com.folioframe.domain.activity.exception.code.ActivityErrorCode;
import com.folioframe.domain.activity.repository.ActivityBookmarkRepository;
import com.folioframe.domain.activity.repository.ActivityRepository;
import com.folioframe.domain.member.entity.Member;
import com.folioframe.domain.member.exception.MemberException;
import com.folioframe.domain.member.exception.code.MemberErrorCode;
import com.folioframe.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ActivityService {

    private final ActivityRepository activityRepository;
    private final ActivityBookmarkRepository activityBookmarkRepository;
    private final MemberRepository memberRepository;

    public List<ActivityResDTO> getActivities(ActivityCategory category, ActivitySortType sortType, Long memberId) {
        ActivitySortType resolvedSort = (sortType != null) ? sortType : ActivitySortType.LATEST;

        List<Activity> activities = (category != null)
                ? activityRepository.findAllByCategory(category, resolvedSort.getSort())
                : activityRepository.findAll(resolvedSort.getSort());

        Set<Long> bookmarkedIds = getBookmarkedActivityIds(memberId);

        return activities.stream()
                .map(a -> ActivityResDTO.of(a, bookmarkedIds.contains(a.getId())))
                .toList();
    }

    public List<ActivityResDTO> getBookmarkedActivities(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));

        return activityBookmarkRepository.findAllByMemberOrderByCreatedAtDesc(member).stream()
                .map(bm -> ActivityResDTO.of(bm.getActivity(), true))
                .toList();
    }

    @Transactional
    public void incrementViewCount(Long activityId) {
        Activity activity = findActivity(activityId);
        activity.increaseViewCount();
    }

    public Activity findActivity(Long activityId) {
        return activityRepository.findById(activityId)
                .orElseThrow(() -> new ActivityException(ActivityErrorCode.ACTIVITY_NOT_FOUND));
    }

    private Set<Long> getBookmarkedActivityIds(Long memberId) {
        if (memberId == null) {
            return Set.of();
        }
        return memberRepository.findById(memberId)
                .map(activityBookmarkRepository::findActivityIdsByMember)
                .orElse(Set.of());
    }
}
