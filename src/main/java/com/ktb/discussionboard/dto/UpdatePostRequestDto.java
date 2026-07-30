package com.ktb.discussionboard.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class UpdatePostRequestDto {
    @NotBlank(message = "Title is required")
    @Size(max = 255, message = "Title must not exceed 255 characters")
    private String title;

    @NotBlank(message = "Content is required")
    @Size(max = 10000, message = "Content must not exceed 10000 characters")
    private String content;

    @Size(max = 10, message = "A post can have at most 10 images")
    private List<
            @NotBlank(message = "Image URL must not be blank")
            @Size(max = 500, message = "Image URL must not exceed 500 characters")
            String> postImageUrls;
}
