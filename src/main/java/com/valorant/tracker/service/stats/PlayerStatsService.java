package com.valorant.tracker.service.stats;

import com.valorant.tracker.dto.*;
import com.valorant.tracker.entity.*;
import com.valorant.tracker.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PlayerStatsService {

    private final PlayerRepository playerRepo;
    private final MatchPlayerRepository matchPlayerRepo;
    private final MatchRepository matchRepo;

    public PlayerStatsDTO getStats(String puuid, String mode) {
        Player player = playerRepo.findById(puuid)
                .orElseThrow(() -> new RuntimeException("Player not found"));

        List<MatchPlayer> matches;

        if (mode == null || mode.equalsIgnoreCase("all")) {
            // Все матчи
            matches = matchPlayerRepo.findByPuuid(puuid);
        } else {
            // Фильтр по режиму
            matches = matchPlayerRepo.findByPuuidAndMode(puuid, mode);
        }

        if (matches.isEmpty()) {
            return createEmptyStats(player);
        }

        // Суммируем статистику
        int totalKills = 0;
        int totalDeaths = 0;
        int totalAssists = 0;
        int totalScore = 0;
        long wins = 0;

        for (MatchPlayer mp : matches) {
            totalKills += mp.getKills() != null ? mp.getKills() : 0;
            totalDeaths += mp.getDeaths() != null ? mp.getDeaths() : 0;
            totalAssists += mp.getAssists() != null ? mp.getAssists() : 0;
            totalScore += mp.getScore() != null ? mp.getScore() : 0;
            if (mp.getWon() != null && mp.getWon()) {
                wins++;
            }
        }

        // Расчеты
        double kdRatio = totalDeaths > 0 ? (double) totalKills / totalDeaths : 0;
        double winRate = matches.size() > 0 ? ((double) wins / matches.size()) * 100 : 0;
        double avgScore = matches.size() > 0 ? (double) totalScore / matches.size() : 0;

        return PlayerStatsDTO.builder()
                .puuid(puuid)
                .nickname(player.getNickname())
                .tag(player.getTag())
                .totalKills(totalKills)
                .totalDeaths(totalDeaths)
                .totalAssists(totalAssists)
                .totalMatches(matches.size())
                .totalWins((int) wins)
                .kdRatio(Math.round(kdRatio * 100.0) / 100.0)
                .winRate(Math.round(winRate * 10.0) / 10.0)
                .avgCombatScore(Math.round(avgScore * 10.0) / 10.0)
                .currentRank(null)
                .build();
    }

    private PlayerStatsDTO createEmptyStats(Player player) {
        return PlayerStatsDTO.builder()
                .puuid(player.getPuuid())
                .nickname(player.getNickname())
                .tag(player.getTag())
                .totalKills(0)
                .totalDeaths(0)
                .totalAssists(0)
                .totalMatches(0)
                .totalWins(0)
                .kdRatio(0.0)
                .winRate(0.0)
                .avgCombatScore(0.0)
                .currentRank(null)
                .build();
    }
}