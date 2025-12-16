package com.valorant.tracker.controller;

import com.valorant.tracker.dto.PlayerStatsDTO;
import com.valorant.tracker.entity.Player;
import com.valorant.tracker.service.match.MatchService;
import com.valorant.tracker.service.player.PlayerService;
import com.valorant.tracker.service.stats.PlayerStatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/players")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PlayerController {

    private final PlayerService playerService;
    private final MatchService matchService;
    private final PlayerStatsService statsService;

    @GetMapping("/{name}/{tag}")
    public ResponseEntity<Player> getPlayer(
            @PathVariable String name,
            @PathVariable String tag,
            @RequestParam(defaultValue = "eu") String region) {

        Player player = playerService.getOrCreateWithMMR(name, tag, region);

        // Автоматически загружаем матчи при первом запросе
        try {
            matchService.updateMatches(player.getPuuid(), region);
        } catch (Exception e) {
            System.err.println("⚠️ Failed to load matches: " + e.getMessage());
        }

        return ResponseEntity.ok(player);
    }

    @GetMapping("/{puuid}/stats")
    public ResponseEntity<PlayerStatsDTO> getPlayerStats(
            @PathVariable String puuid) {

        PlayerStatsDTO stats = statsService.getStats(puuid);
        return ResponseEntity.ok(stats);
    }

    @PostMapping("/{puuid}/update")
    public ResponseEntity<String> updatePlayerMatches(
            @PathVariable String puuid,
            @RequestParam(defaultValue = "eu") String region) {

        matchService.updateMatches(puuid, region);
        return ResponseEntity.ok("Matches updated successfully");
    }
}