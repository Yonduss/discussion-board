package com.ktb.discussionboard.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class MyPostResponseDto {
    private Long id;
    private String title;
    private int likeCount;
    private int viewCount;
    private int commentCount;
    private boolean edited;
    private LocalDateTime createdAt;
}
