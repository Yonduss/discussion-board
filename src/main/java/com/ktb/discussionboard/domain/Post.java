package com.ktb.discussionboard.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Post {
    private Long id;
    private Long userId;
    private String title;
    private String content;
    private String postImageUrl;
    private int likeCount;
    private int viewCount;
    private int reportedCount;
    private boolean deleted;
    private boolean edited;
    private boolean hidden;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}