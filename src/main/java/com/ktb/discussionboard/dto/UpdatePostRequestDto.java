package com.ktb.discussionboard.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UpdatePostRequestDto {
    private String title;
    private String content;
    private String postImageUrl;
}
