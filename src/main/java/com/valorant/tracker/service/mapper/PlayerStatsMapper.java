package com.valorant.tracker.service.mapper;



import com.valorant.tracker.dto.*;
import com.valorant.tracker.entity.*;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PlayerStatsMapper {

    public PlayerStatsDTO toDto(
            Player player,
            List<MatchPlayer> matches,
            CompetitiveHistory rank
    ) {

        int kills = matches.stream().mapToInt(MatchPlayer::getKills).sum();
        int deaths = matches.stream().mapToInt(MatchPlayer::getDeaths).sum();
        int assists = matches.stream().mapToInt(MatchPlayer::getAssists).sum();

        double kd = deaths > 0 ? (double) kills / deaths : kills;

        return PlayerStatsDTO.builder()
                .puuid(player.getPuuid())
                .nickname(player.getNickname())
                .tag(player.getTag())
                .totalKills(kills)
                .totalDeaths(deaths)
                .totalAssists(assists)
                .totalMatches(matches.size())
                .kdRatio(Math.round(kd * 100.0) / 100.0)
                .currentRank(rank != null
                        ? CurrentRankDTO.builder()
                        .tier(rank.getTierAfter())
                        .rr(rank.getRrAfter())
                        .movement(rank.getMovement())
                        .build()
                        : null)
                .build();
    }
}
