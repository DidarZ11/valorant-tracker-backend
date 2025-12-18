package com.valorant.tracker.dto;

import lombok.Builder;
import lombok.Data;
import java.time.OffsetDateTime;
import java.util.List;

@Data
@Builder
public class MatchDetailDTO {
    private String matchId;
    private String mapName;
    private String mode;
    private OffsetDateTime startedAt;

    // Команды
    private List<PlayerMatchStatsDTO> redTeam;
    private List<PlayerMatchStatsDTO> blueTeam;

    // Общая информация
    private String result; // "Red Won" или "Blue Won"
    private String region;
}