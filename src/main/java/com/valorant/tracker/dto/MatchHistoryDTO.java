package com.valorant.tracker.dto;

import lombok.*;
import java.time.OffsetDateTime;

@Data
@Builder
public class MatchHistoryDTO {
    private String matchId;
    private String mapName;
    private String mode;
    private OffsetDateTime startedAt;

    // Статистика игрока
    private Integer kills;
    private Integer deaths;
    private Integer assists;
    private Integer score;
    private String team;
    private Boolean won;

    // Агент
    private String agentName;

    // Дополнительно
    private Double kd;
    private String result; // "Victory" или "Defeat"
}