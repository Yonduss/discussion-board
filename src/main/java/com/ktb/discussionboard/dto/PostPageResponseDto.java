package com.ktb.discussionboard.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class PostPageResponseDto {
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
    private boolean hasNext;
    private List<PostResponseDto> posts;
}