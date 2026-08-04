package com.ktb.discussionboard.dto;

import com.ktb.discussionboard.domain.MlbTeam;
import com.ktb.discussionboard.domain.ProfileImageSource;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserResponseDto {
    private Long id;
    private String email;
    private String nickname;
    private String profileImageUrl;
    private String personalProfileImageUrl;
    private MlbTeam favoriteTeam;
    private ProfileImageSource profileImageSource;
}
