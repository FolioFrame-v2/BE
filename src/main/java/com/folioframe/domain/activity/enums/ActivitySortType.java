package com.folioframe.domain.activity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;

@Getter
@RequiredArgsConstructor
public enum ActivitySortType {

    LATEST(Sort.by(Sort.Direction.DESC, "createdAt")),
    POPULAR(Sort.by(Sort.Direction.DESC, "bookmarkCount")),
    MOST_VIEWED(Sort.by(Sort.Direction.DESC, "viewCount"));

    private final Sort sort;
}
