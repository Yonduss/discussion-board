package com.ktb.discussionboard.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PostLikeResponseDto {
    private int likeCount;

    private boolean liked;
}