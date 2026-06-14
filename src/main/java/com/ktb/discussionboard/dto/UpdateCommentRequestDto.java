package com.ktb.discussionboard.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UpdateCommentRequestDto {

    @NotBlank(message = "Content is required")
    private String content;
}