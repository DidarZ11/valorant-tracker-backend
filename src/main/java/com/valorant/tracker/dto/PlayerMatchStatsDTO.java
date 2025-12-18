package com.valorant.tracker.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PlayerMatchStatsDTO {
    private String puuid;
    private String playerName;
    private String playerTag;
    private String agentName;
    private String agentRole;

    private Integer kills;
    private Integer deaths;
    private Integer assists;
    private Integer score;
    private Double kd;

    private String team;
    private Boolean won;

    // НОВОЕ ПОЛЕ
    private String cardIconUrl;
}