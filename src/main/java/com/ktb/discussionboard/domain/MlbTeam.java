package com.ktb.discussionboard.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MlbTeam {
    ARI("/team-logos/ARI_logo.svg"),
    ATH("/team-logos/ATH_logo.svg"),
    ATL("/team-logos/ATL_logo.svg"),
    BAL("/team-logos/BAL_logo.svg"),
    BOS("/team-logos/BOS_logo.svg"),
    CHC("/team-logos/CHC_logo.svg"),
    CWS("/team-logos/CWS_logo.svg"),
    CIN("/team-logos/CIN_logo.svg"),
    CLE("/team-logos/CLE_logo.svg"),
    COL("/team-logos/COL_logo.svg"),
    DET("/team-logos/DET_logo.svg"),
    HOU("/team-logos/HOU_logo.svg"),
    KC("/team-logos/KC_logo.svg"),
    LAA("/team-logos/LAA_logo.svg"),
    LAD("/team-logos/LAD_logo.svg"),
    MIA("/team-logos/MIA_logo.svg"),
    MIL("/team-logos/MIL_logo.svg"),
    MIN("/team-logos/MIN_logo.svg"),
    NYM("/team-logos/NYM_logo.svg"),
    NYY("/team-logos/NYY_logo.svg"),
    PHI("/team-logos/PHI_logo.svg"),
    PIT("/team-logos/PIT_logo.svg"),
    SD("/team-logos/SD_logo.svg"),
    SEA("/team-logos/SEA_logo.svg"),
    SF("/team-logos/SF_logo.svg"),
    STL("/team-logos/STL_logo.svg"),
    TB("/team-logos/TB_logo.svg"),
    TEX("/team-logos/TEX_logo.svg"),
    TOR("/team-logos/TOR_logo.svg"),
    WSH("/team-logos/WSH_logo.svg");

    private final String logoPath;
}
