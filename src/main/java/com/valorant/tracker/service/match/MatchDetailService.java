package com.valorant.tracker.service.match;

import com.valorant.tracker.dto.MatchDetailDTO;
import com.valorant.tracker.dto.PlayerMatchStatsDTO;
import com.valorant.tracker.entity.*;
import com.valorant.tracker.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MatchDetailService {

    private final MatchRepository matchRepository;
    private final MatchPlayerRepository matchPlayerRepository;
    private final PlayerRepository playerRepository;
    private final AgentRepository agentRepository;

    public MatchDetailDTO getMatchDetail(String matchId) {
        // Получаем матч
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new RuntimeException("Match not found"));

        // Получаем всех игроков в матче
        List<MatchPlayer> allPlayers = matchPlayerRepository.findByMatchId(matchId);

        List<PlayerMatchStatsDTO> redTeam = new ArrayList<>();
        List<PlayerMatchStatsDTO> blueTeam = new ArrayList<>();

        for (MatchPlayer mp : allPlayers) {
            Player player = playerRepository.findById(mp.getPuuid()).orElse(null);
            Agent agent = mp.getAgentId() != null
                    ? agentRepository.findById(mp.getAgentId()).orElse(null)
                    : null;

            double kd = mp.getDeaths() > 0
                    ? Math.round((double) mp.getKills() / mp.getDeaths() * 100.0) / 100.0
                    : mp.getKills();

            PlayerMatchStatsDTO dto = PlayerMatchStatsDTO.builder()
                    .puuid(mp.getPuuid())
                    .playerName(player != null ? player.getNickname() : "Unknown")
                    .playerTag(player != null ? player.getTag() : "")
                    .agentName(agent != null ? agent.getAgentName() : "Unknown")
                    .agentRole(agent != null ? agent.getRole() : "")
                    .kills(mp.getKills())
                    .deaths(mp.getDeaths())
                    .assists(mp.getAssists())
                    .score(mp.getScore())
                    .kd(kd)
                    .team(mp.getTeam())
                    .won(mp.getWon())
                    .cardIconUrl(player != null ? player.getCardIconUrl() : null) // ДОБАВИЛИ
                    .build();

            if ("Red".equalsIgnoreCase(mp.getTeam())) {
                redTeam.add(dto);
            } else if ("Blue".equalsIgnoreCase(mp.getTeam())) {
                blueTeam.add(dto);
            }
        }

        // Определяем победителя
        String result = "Unknown";
        if (!redTeam.isEmpty() && !blueTeam.isEmpty()) {
            boolean redWon = redTeam.stream().anyMatch(PlayerMatchStatsDTO::getWon);
            result = redWon ? "Red Won" : "Blue Won";
        }

        return MatchDetailDTO.builder()
                .matchId(match.getMatchId())
                .mapName(match.getMapName())
                .mode(match.getMode())
                .startedAt(match.getStartedAt())
                .redTeam(redTeam)
                .blueTeam(blueTeam)
                .result(result)
                .region(match.getRegion())
                .build();
    }
}