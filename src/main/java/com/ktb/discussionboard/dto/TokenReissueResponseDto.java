package com.ktb.discussionboard.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TokenReissueResponseDto {
    private String accessToken;
    private String refreshToken;
}
