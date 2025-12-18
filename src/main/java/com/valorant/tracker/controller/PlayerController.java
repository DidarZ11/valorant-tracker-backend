package com.valorant.tracker.controller;

import com.valorant.tracker.dto.PlayerStatsDTO;
import com.valorant.tracker.dto.MatchHistoryDTO;
import com.valorant.tracker.dto.MatchDetailDTO;
import com.valorant.tracker.entity.Player;
import com.valorant.tracker.service.player.PlayerService;
import com.valorant.tracker.service.stats.PlayerStatsService;
import com.valorant.tracker.service.match.MatchHistoryService;
import com.valorant.tracker.service.match.MatchDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/players")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PlayerController {

    private final PlayerService playerService;
    private final PlayerStatsService statsService;
    private final MatchHistoryService matchHistoryService;
    private final MatchDetailService matchDetailService;

    @GetMapping("/{name}/{tag}")
    public Player getPlayer(
            @PathVariable String name,
            @PathVariable String tag,
            @RequestParam String region) {
        return playerService.getOrCreateWithMMR(name, tag, region);
    }

    @GetMapping("/{puuid}/stats")
    public PlayerStatsDTO getStats(
            @PathVariable String puuid,
            @RequestParam(required = false, defaultValue = "all") String mode) {
        return statsService.getStats(puuid, mode);
    }

    @GetMapping("/{puuid}/matches")
    public List<MatchHistoryDTO> getMatchHistory(
            @PathVariable String puuid,
            @RequestParam(required = false, defaultValue = "all") String mode,
            @RequestParam(required = false, defaultValue = "10") int limit) {
        return matchHistoryService.getMatchHistory(puuid, mode, limit);
    }

    // НОВЫЙ ENDPOINT
    @GetMapping("/matches/{matchId}")
    public MatchDetailDTO getMatchDetail(@PathVariable String matchId) {
        return matchDetailService.getMatchDetail(matchId);
    }
}