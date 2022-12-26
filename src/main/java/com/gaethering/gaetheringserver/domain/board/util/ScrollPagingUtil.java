package com.gaethering.gaetheringserver.domain.board.util;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ScrollPagingUtil<T> {

    private final List<T> itemsWithNextCursor;

    private final int countPerScroll;

    public static <T> ScrollPagingUtil<T> of(List<T> itemsWithNextCursor, int size) {
        return new ScrollPagingUtil<>(itemsWithNextCursor, size);
    }

    public boolean isLastScroll() {
        return this.itemsWithNextCursor.size() <= countPerScroll;
    }

    public List<T> getCurrentScrollItems() {
        if (isLastScroll()) {
            return this.itemsWithNextCursor;
        }
        return this.itemsWithNextCursor.subList(0, countPerScroll);
    }

    public T getNextCursor() {
        return itemsWithNextCursor.get(countPerScroll - 1);
    }


}