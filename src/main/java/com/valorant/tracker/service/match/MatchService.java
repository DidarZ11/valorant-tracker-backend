package com.valorant.tracker.service.match;

import com.fasterxml.jackson.databind.JsonNode;
import com.valorant.tracker.entity.*;
import com.valorant.tracker.repository.*;
import com.valorant.tracker.service.api.ValorantApiService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Service
@RequiredArgsConstructor
public class MatchService {

    private final MatchRepository matchRepository;
    private final MatchPlayerRepository matchPlayerRepository;
    private final ValorantApiService apiService;

    @Transactional
    public void updateMatches(String puuid, String region) {
        try {
            System.out.println("📡 Fetching matches for: " + puuid);

            JsonNode matchesData = apiService.fetchMatches(puuid, region);

            if (matchesData == null) {
                System.out.println("ℹ️ No matches data returned from API");
                return;
            }

            if (!matchesData.isArray()) {
                System.out.println("⚠️ Matches data is not an array");
                return;
            }

            if (matchesData.size() == 0) {
                System.out.println("ℹ️ No matches found (empty array)");
                return;
            }

            int count = 0;
            int errors = 0;

            for (JsonNode matchNode : matchesData) {
                try {
                    saveMatch(matchNode, puuid);
                    count++;
                } catch (Exception e) {
                    errors++;
                    System.err.println("❌ Failed to save match: " + e.getMessage());
                }
            }

            System.out.println("✅ Saved " + count + " matches (errors: " + errors + ")");

        } catch (Exception e) {
            System.err.println("❌ Failed to update matches: " + e.getMessage());
            e.printStackTrace();
            // Не бросаем исключение дальше - это не критично
        }
    }

    private void saveMatch(JsonNode matchNode, String targetPuuid) {
        JsonNode metadata = matchNode.get("metadata");
        String matchId = metadata.get("matchid").asText();

        if (matchRepository.existsById(matchId)) {
            System.out.println("⏭️ Match already exists: " + matchId);
            return;
        }

        Match match = new Match();
        match.setMatchId(matchId);
        match.setMapName(metadata.get("map").asText());
        match.setMode(metadata.get("mode").asText());
        match.setRegion(metadata.get("cluster").asText());

        long startMillis = metadata.get("game_start").asLong();
        match.setStartedAt(OffsetDateTime.ofInstant(
                Instant.ofEpochMilli(startMillis), ZoneOffset.UTC));

        match.setFetchedAt(OffsetDateTime.now());
        matchRepository.save(match);

        JsonNode players = matchNode.get("players").get("all_players");
        for (JsonNode player : players) {
            if (player.get("puuid").asText().equals(targetPuuid)) {
                saveMatchPlayer(player, matchId, matchNode);
                break;
            }
        }
    }

    private void saveMatchPlayer(JsonNode player, String matchId, JsonNode matchNode) {
        MatchPlayer mp = new MatchPlayer();
        mp.setMatchId(matchId);
        mp.setPuuid(player.get("puuid").asText());
        mp.setTeam(player.get("team").asText());

        JsonNode stats = player.get("stats");
        mp.setKills(stats.get("kills").asInt());
        mp.setDeaths(stats.get("deaths").asInt());
        mp.setAssists(stats.get("assists").asInt());
        mp.setScore(stats.get("score").asInt());

        String playerTeam = player.get("team").asText().toLowerCase();
        JsonNode teams = matchNode.get("teams");

        boolean won = false;
        if (teams.has(playerTeam)) {
            won = teams.get(playerTeam).get("has_won").asBoolean();
        }
        mp.setWon(won);

        matchPlayerRepository.save(mp);
        System.out.println("✅ Saved match player: " + matchId);
    }
}