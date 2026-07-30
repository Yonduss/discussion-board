package com.ktb.discussionboard.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReportPostRequestDto {

    @NotBlank(message = "Report reason is required")
    @Size(max = 255, message = "Report reason must not exceed 255 characters")
    private String reason;
}
