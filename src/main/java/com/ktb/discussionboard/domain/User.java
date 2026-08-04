package com.ktb.discussionboard.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String nickname;

    @Column(name = "profile_image_url")
    private String profileImageUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "favorite_team", length = 3)
    private MlbTeam favoriteTeam;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "profile_image_source",
            nullable = false,
            length = 20,
            columnDefinition = "varchar(20) default 'PERSONAL'"
    )
    private ProfileImageSource profileImageSource = ProfileImageSource.PERSONAL;

    @Column(nullable = false)
    private boolean deleted;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "profile_updated_at", nullable = false)
    private LocalDateTime profileUpdatedAt;

    @Column(name = "password_updated_at", nullable = false)
    private LocalDateTime passwordUpdatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    public void updateFavoriteTeam(
            MlbTeam favoriteTeam,
            boolean useTeamLogoAsProfileImage
    ) {
        this.favoriteTeam = favoriteTeam;
        this.profileImageSource = useTeamLogoAsProfileImage
                && favoriteTeam != null
                ? ProfileImageSource.FAVORITE_TEAM
                : ProfileImageSource.PERSONAL;
    }

    public String resolveDisplayProfileImageUrl() {
        if (profileImageSource == ProfileImageSource.FAVORITE_TEAM
                && favoriteTeam != null) {
            return favoriteTeam.getLogoPath();
        }

        return profileImageUrl;
    }
}
