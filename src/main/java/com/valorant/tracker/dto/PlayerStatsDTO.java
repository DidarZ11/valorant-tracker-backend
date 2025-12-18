package com.valorant.tracker.dto;

import lombok.*;

@Data
@Builder
public class PlayerStatsDTO {
    // Базовая информация
    private String puuid;
    private String nickname;
    private String tag;

    // Общая статистика
    private Integer totalKills;
    private Integer totalDeaths;
    private Integer totalAssists;
    private Integer totalMatches;
    private Integer totalWins;

    // Расчетные метрики
    private Double kdRatio;
    private Double winRate;
    private Double avgCombatScore;

    // Текущий ранг (добавили это поле!)
    private CurrentRankDTO currentRank;
}