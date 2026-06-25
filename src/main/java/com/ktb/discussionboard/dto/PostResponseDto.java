package com.ktb.discussionboard.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
public class PostResponseDto {
    private Long id;
    private Long userId;
    private String title;
    private String content;
    private List<String> postImageUrls;
    private int likeCount;
    private int viewCount;
    private boolean edited;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
