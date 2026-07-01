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
import com.folioframe.global.dto.PageRequest;
import com.folioframe.global.dto.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ActivityService {

    private final ActivityRepository activityRepository;
    private final ActivityBookmarkRepository activityBookmarkRepository;
    private final MemberRepository memberRepository;

    public PageResponse<ActivityResDTO> getActivities(ActivityCategory category, ActivitySortType sortType, PageRequest pageRequest, Long memberId) {
        if (sortType == null) sortType = ActivitySortType.LATEST;
        org.springframework.data.domain.PageRequest pageable = pageRequest.toPageable(sortType.getSort());

        Page<Activity> activityPage;
        if (category != null) {
            activityPage = activityRepository.findAllByCategory(category, pageable);
        } else {
            activityPage = activityRepository.findAll(pageable);
        }

        Set<Long> bookmarkedIds = getBookmarkedActivityIds(memberId);

        return PageResponse.of(activityPage, activityPage.getContent().stream()
                .map(a -> ActivityResDTO.of(a, bookmarkedIds.contains(a.getId())))
                .toList());
    }

    public PageResponse<ActivityResDTO> getBookmarkedActivities(Long memberId, PageRequest pageRequest) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));

        Page<ActivityResDTO> page = activityBookmarkRepository
                .findAllByMember(member, pageRequest.toPageable(ActivitySortType.LATEST.getSort()))
                .map(bm -> ActivityResDTO.of(bm.getActivity(), true));

        return PageResponse.of(page);
    }

    @Transactional
    public void incrementViewCount(Long activityId) {
        findActivity(activityId).increaseViewCount();
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
