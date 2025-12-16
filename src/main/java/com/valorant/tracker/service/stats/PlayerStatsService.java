package com.valorant.tracker.service.stats;

import com.valorant.tracker.dto.PlayerStatsDTO;
import com.valorant.tracker.entity.*;
import com.valorant.tracker.repository.*;
import com.valorant.tracker.service.mapper.PlayerStatsMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PlayerStatsService {

    private final PlayerRepository playerRepo;
    private final MatchPlayerRepository matchPlayerRepo;
    private final CompetitiveHistoryRepository historyRepo;
    private final PlayerStatsMapper mapper;

    public PlayerStatsDTO getStats(String puuid) {
        Player player = playerRepo.findById(puuid)
                .orElseThrow(() -> new RuntimeException("Player not found"));

        List<MatchPlayer> matches = matchPlayerRepo.findByPuuid(puuid);

        CompetitiveHistory rank = historyRepo.findLatestByPuuid(puuid).orElse(null);

        return mapper.toDto(player, matches, rank);
    }
}