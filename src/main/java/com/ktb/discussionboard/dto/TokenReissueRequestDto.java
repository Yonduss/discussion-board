package com.ktb.discussionboard.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TokenReissueRequestDto {
    private String refreshToken;
}
