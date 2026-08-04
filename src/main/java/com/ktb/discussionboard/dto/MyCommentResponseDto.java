package com.ktb.discussionboard.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class MyCommentResponseDto {
    private Long id;
    private Long postId;
    private String postTitle;
    private String content;
    private boolean edited;
    private LocalDateTime createdAt;
}
