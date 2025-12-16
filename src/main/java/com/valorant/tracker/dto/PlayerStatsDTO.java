package com.valorant.tracker.dto;



import lombok.*;
import java.util.List;

@Data
@Builder
public class PlayerStatsDTO {
    private String puuid;
    private String nickname;
    private String tag;

    private Integer totalKills;
    private Integer totalDeaths;
    private Integer totalAssists;
    private Integer totalMatches;

    private Double kdRatio;
    private Double winRate;
    private Double avgCombatScore;

    private CurrentRankDTO currentRank;
    private List<RecentMatchDTO> recentMatches;
}

