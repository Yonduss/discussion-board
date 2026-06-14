package com.ktb.discussionboard.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UpdateUserProfileRequestDto {
    private String nickname;
    private String profileImageUrl;
}