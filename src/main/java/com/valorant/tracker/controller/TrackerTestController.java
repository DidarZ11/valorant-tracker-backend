package com.valorant.tracker.controller;

import com.valorant.tracker.service.match.MatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/tracker")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class TrackerTestController {

    private final MatchService matchService;

    /**
     * Загрузить матчи для игрока
     * GET /api/v1/tracker/matches/{region}/{puuid}
     */
    @GetMapping("/matches/{region}/{puuid}")
    public String loadMatches(@PathVariable String region, @PathVariable String puuid) {
        matchService.updateMatches(puuid, region);
        return "Matches loaded!";
    }
}
