package com.ktb.discussionboard.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class PostResponseDto {
    private Long id;
    private Long userId;
    private String title;
    private String content;
    private String postImageUrl;
    private int likeCount;
    private int viewCount;
    private boolean edited;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
