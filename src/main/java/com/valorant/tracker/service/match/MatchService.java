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
    private final AgentRepository agentRepository;
    private final PlayerRepository playerRepository;
    private final ValorantApiService apiService;

    @Transactional
    public void updateMatches(String puuid, String region) {
        try {
            System.out.println("📡 Fetching matches for: " + puuid);

            JsonNode matchList = apiService.fetchMatchList(region, puuid);

            if (matchList == null || !matchList.isArray() || matchList.size() == 0) {
                System.out.println("ℹ️ No matches found");
                return;
            }

            System.out.println("📊 Found " + matchList.size() + " matches");

            int saved = 0;
            int skipped = 0;

            for (JsonNode matchData : matchList) {
                try {
                    JsonNode metadata = matchData.get("metadata");
                    String matchId = metadata.get("matchid").asText();

                    MatchPlayerKey key = new MatchPlayerKey(matchId, puuid);
                    if (matchPlayerRepository.existsById(key)) {
                        skipped++;
                        continue;
                    }

                    if (saveMatchFromV3(matchData, puuid)) {
                        saved++;
                    }

                } catch (Exception e) {
                    System.err.println("❌ Error: " + e.getMessage());
                }
            }

            System.out.println("✅ Saved " + saved + " matches, skipped " + skipped);

        } catch (Exception e) {
            System.err.println("❌ Failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private boolean saveMatchFromV3(JsonNode matchData, String targetPuuid) {
        try {
            JsonNode metadata = matchData.get("metadata");
            String matchId = metadata.get("matchid").asText();

            if (!matchRepository.existsById(matchId)) {
                Match match = new Match();
                match.setMatchId(matchId);
                match.setMapName(metadata.get("map").asText());

                String rawMode = metadata.get("mode").asText();
                String normalizedMode = normalizeMode(rawMode);
                match.setMode(normalizedMode);

                match.setRegion(metadata.get("region").asText());

                long startMillis = metadata.get("game_start").asLong();
                match.setStartedAt(OffsetDateTime.ofInstant(
                        Instant.ofEpochMilli(startMillis), ZoneOffset.UTC));

                match.setFetchedAt(OffsetDateTime.now());
                matchRepository.save(match);

                System.out.println("💾 Match saved: " + matchId + " | mode: " + rawMode + " → " + normalizedMode);
            }

            System.out.println("🔍 Looking for ALL players in match: " + matchId);

            if (matchData.has("players")) {
                JsonNode players = matchData.get("players");

                if (players.isObject() && players.has("all_players")) {
                    players = players.get("all_players");
                }

                if (players.isArray()) {
                    int savedPlayers = 0;

                    for (JsonNode player : players) {
                        String puuid = player.get("puuid").asText();

                        MatchPlayerKey key = new MatchPlayerKey(matchId, puuid);
                        if (matchPlayerRepository.existsById(key)) {
                            continue;
                        }

                        if (savePlayerStats(player.get("stats"), matchId, puuid, player)) {
                            savedPlayers++;
                        }
                    }

                    System.out.println("✅ Saved " + savedPlayers + " players in match: " + matchId);
                    return savedPlayers > 0;
                }
            }

            System.out.println("⚠️ No players found in match: " + matchId);
            return false;

        } catch (Exception e) {
            System.err.println("❌ Failed to save match: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private boolean savePlayerStats(JsonNode stats, String matchId, String puuid, JsonNode matchData) {
        try {
            if (stats == null) {
                System.out.println("⚠️ Stats node is null");
                return false;
            }

            MatchPlayer mp = new MatchPlayer();
            mp.setMatchId(matchId);
            mp.setPuuid(puuid);

            mp.setScore(stats.get("score").asInt());
            mp.setKills(stats.get("kills").asInt());
            mp.setDeaths(stats.get("deaths").asInt());
            mp.setAssists(stats.get("assists").asInt());

            if (matchData.has("team")) {
                mp.setTeam(matchData.get("team").asText());
            } else if (stats.has("team")) {
                mp.setTeam(stats.get("team").asText());
            }

            mp.setWon(false);

            // СОХРАНЯЕМ АГЕНТА
            if (matchData.has("character")) {
                String characterName = matchData.get("character").asText();

                String normalizedName = characterName.substring(0, 1).toUpperCase() +
                        characterName.substring(1).toLowerCase();

                Agent agent = agentRepository.findByAgentName(normalizedName).orElse(null);
                if (agent != null) {
                    mp.setAgentId(agent.getAgentId());
                    System.out.println("✓ Agent set: " + normalizedName + " (ID: " + agent.getAgentId() + ")");
                } else {
                    System.out.println("⚠️ Agent not found in DB: " + characterName + " (normalized: " + normalizedName + ")");
                }
            }

            // СОЗДАЕМ ИЛИ ОБНОВЛЯЕМ ИГРОКА
            Player player = playerRepository.findById(puuid).orElse(null);

            if (player == null) {
                try {
                    player = new Player();
                    player.setPuuid(puuid);

                    String playerName = "Unknown";
                    String playerTag = "";
                    String cardUrl = "";

                    if (matchData.has("name") && !matchData.get("name").isNull()) {
                        playerName = matchData.get("name").asText();
                    }

                    if (matchData.has("tag") && !matchData.get("tag").isNull()) {
                        playerTag = matchData.get("tag").asText();
                    }

                    // ПОЛУЧАЕМ АВАТАР (card)
                    if (matchData.has("assets") && matchData.get("assets").has("card")) {
                        JsonNode card = matchData.get("assets").get("card");
                        if (card.has("small") && !card.get("small").isNull()) {
                            cardUrl = card.get("small").asText();
                            System.out.println("✓ Found card icon: " + cardUrl);
                        }
                    }

                    player.setNickname(playerName);
                    player.setTag(playerTag);
                    player.setCardIconUrl(cardUrl);

                    Match match = matchRepository.findById(matchId).orElse(null);
                    if (match != null) {
                        player.setRegion(match.getRegion());
                    }

                    player.setAccountLevel(0);
                    player.setCurrentTier(0);
                    player.setCurrentRr(0);
                    player.setCreatedAt(OffsetDateTime.now());
                    player.setLastUpdated(OffsetDateTime.now());

                    playerRepository.save(player);
                    System.out.println("✅ Created player: " + player.getNickname() + "#" + player.getTag() + " with card");

                } catch (Exception e) {
                    System.err.println("⚠️ Failed to create player " + puuid + ": " + e.getMessage());
                }
            } else {
                // Обновляем аватар если его нет
                if (player.getCardIconUrl() == null || player.getCardIconUrl().isEmpty()) {
                    if (matchData.has("assets") && matchData.get("assets").has("card")) {
                        JsonNode card = matchData.get("assets").get("card");
                        if (card.has("small") && !card.get("small").isNull()) {
                            player.setCardIconUrl(card.get("small").asText());
                            playerRepository.save(player);
                            System.out.println("✓ Updated card icon for: " + player.getNickname());
                        }
                    }
                }
            }

            matchPlayerRepository.save(mp);
            return true;

        } catch (Exception e) {
            System.err.println("❌ Failed to save player stats: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private String normalizeMode(String rawMode) {
        if (rawMode == null) return "unknown";

        String mode = rawMode.toLowerCase().trim();

        switch (mode) {
            case "standard":
            case "unrated":
                return "unrated";

            case "competitive":
            case "ranked":
                return "competitive";

            case "deathmatch":
            case "team deathmatch":
                return "deathmatch";

            case "spikerush":
            case "spike rush":
                return "spikerush";

            case "swiftplay":
            case "swift play":
                return "swiftplay";

            default:
                return mode;
        }
    }
}