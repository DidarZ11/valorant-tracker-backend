package com.valorant.tracker.service.match;

import com.fasterxml.jackson.databind.JsonNode;
import com.valorant.tracker.dto.MatchHistoryDTO;
import com.valorant.tracker.entity.*;
import com.valorant.tracker.repository.*;
import com.valorant.tracker.service.api.ValorantApiService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MatchHistoryService {

    private final MatchPlayerRepository matchPlayerRepository;
    private final MatchRepository matchRepository;
    private final AgentRepository agentRepository;
    private final ValorantApiService apiService;

    public List<MatchHistoryDTO> getMatchHistory(String puuid, String mode, int limit) {
        List<MatchPlayer> matchPlayers;

        if (mode == null || mode.equalsIgnoreCase("all")) {
            matchPlayers = matchPlayerRepository.findByPuuidOrderByMatchIdDesc(puuid,
                    PageRequest.of(0, limit));
        } else {
            matchPlayers = matchPlayerRepository.findByPuuidAndModeOrderByMatchIdDesc(
                    puuid, mode, PageRequest.of(0, limit));
        }

        return matchPlayers.stream()
                .map(mp -> {
                    Match match = matchRepository.findById(mp.getMatchId())
                            .orElse(null);

                    if (match == null) return null;

                    double kd = mp.getDeaths() > 0
                            ? Math.round((double) mp.getKills() / mp.getDeaths() * 100.0) / 100.0
                            : mp.getKills();

                    String agentName = "Unknown";
                    if (mp.getAgentId() != null) {
                        Agent agent = agentRepository.findById(mp.getAgentId()).orElse(null);
                        if (agent != null) {
                            agentName = agent.getAgentName();
                        }
                    }

                    return MatchHistoryDTO.builder()
                            .matchId(mp.getMatchId())
                            .mapName(match.getMapName())
                            .mode(match.getMode())
                            .startedAt(match.getStartedAt())
                            .kills(mp.getKills())
                            .deaths(mp.getDeaths())
                            .assists(mp.getAssists())
                            .score(mp.getScore())
                            .team(mp.getTeam())
                            .won(mp.getWon())
                            .agentName(agentName)
                            .kd(kd)
                            .result(mp.getWon() != null && mp.getWon() ? "Victory" : "Defeat")
                            .build();
                })
                .filter(dto -> dto != null)
                .collect(Collectors.toList());
    }

    /**
     * Обновить агентов для всех матчей игрока
     */
    @Transactional
    public int updateAgentsForPlayer(String puuid) {
        try {
            System.out.println("🔄 Updating agents for player: " + puuid);

            // Получаем игрока для определения региона
            List<MatchPlayer> matchPlayers = matchPlayerRepository.findByPuuid(puuid);

            if (matchPlayers.isEmpty()) {
                System.out.println("⚠️ No matches found for player");
                return 0;
            }

            // Получаем регион из первого матча
            String region = matchRepository.findById(matchPlayers.get(0).getMatchId())
                    .map(Match::getRegion)
                    .orElse("eu");

            // Получаем свежие данные из API
            JsonNode matchList = apiService.fetchMatchList(region, puuid);

            if (matchList == null || !matchList.isArray()) {
                System.out.println("⚠️ Failed to fetch matches from API");
                return 0;
            }

            int updated = 0;

            for (JsonNode matchData : matchList) {
                try {
                    JsonNode metadata = matchData.get("metadata");
                    String matchId = metadata.get("matchid").asText();

                    // Проверяем есть ли этот матч в БД
                    MatchPlayerKey key = new MatchPlayerKey(matchId, puuid);
                    MatchPlayer mp = matchPlayerRepository.findById(key).orElse(null);

                    if (mp == null || mp.getAgentId() != null) {
                        continue; // Пропускаем если матча нет или агент уже установлен
                    }

                    // Ищем данные игрока в players array
                    if (matchData.has("players")) {
                        JsonNode players = matchData.get("players");

                        if (players.isObject() && players.has("all_players")) {
                            players = players.get("all_players");
                        }

                        if (players.isArray()) {
                            for (JsonNode player : players) {
                                if (player.get("puuid").asText().equals(puuid)) {
                                    // Нашли нужного игрока, обновляем агента
                                    if (player.has("character")) {
                                        String characterName = player.get("character").asText();

                                        // Нормализуем имя
                                        String normalizedName = characterName.substring(0, 1).toUpperCase() +
                                                characterName.substring(1).toLowerCase();

                                        Agent agent = agentRepository.findByAgentName(normalizedName).orElse(null);

                                        if (agent != null) {
                                            mp.setAgentId(agent.getAgentId());
                                            matchPlayerRepository.save(mp);
                                            updated++;
                                            System.out.println("✓ Updated: " + matchId + " -> " + normalizedName);
                                        }
                                    }
                                    break;
                                }
                            }
                        }
                    }

                } catch (Exception e) {
                    System.err.println("❌ Error updating match: " + e.getMessage());
                }
            }

            System.out.println("✅ Updated " + updated + " matches");
            return updated;

        } catch (Exception e) {
            System.err.println("❌ Failed to update agents: " + e.getMessage());
            e.printStackTrace();
            return 0;
        }
    }
}