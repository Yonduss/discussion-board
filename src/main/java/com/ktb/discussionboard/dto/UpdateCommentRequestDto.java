package com.ktb.discussionboard.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UpdateCommentRequestDto {

    @NotBlank(message = "Content is required")
    @Size(max = 255, message = "Comment must not exceed 255 characters")
    private String content;
}
