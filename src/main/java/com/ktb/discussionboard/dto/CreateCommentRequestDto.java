package com.ktb.discussionboard.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CreateCommentRequestDto {
    private Long parentCommentId;

    @NotBlank(message = "Content is required")
    private String content;
}