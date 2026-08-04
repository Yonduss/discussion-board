package com.ktb.discussionboard.dto;

import com.ktb.discussionboard.domain.MlbTeam;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UpdateFavoriteTeamRequestDto {

    @NotNull(message = "Favorite team is required")
    private MlbTeam favoriteTeam;

    @NotNull(message = "Profile image preference is required")
    private Boolean useTeamLogoAsProfileImage;
}
