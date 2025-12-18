package com.valorant.tracker.service.player;

import com.fasterxml.jackson.databind.JsonNode;
import com.valorant.tracker.entity.Player;
import com.valorant.tracker.repository.PlayerRepository;
import com.valorant.tracker.service.api.ValorantApiService;
import com.valorant.tracker.service.match.MatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;

@Service
@RequiredArgsConstructor
public class PlayerService {

    private final PlayerRepository playerRepository;
    private final ValorantApiService apiService;

    // ДОБАВИЛИ @Lazy - это загрузит MatchService только когда понадобится
    @Lazy
    private final MatchService matchService;

    @Transactional
    public Player getOrCreate(String name, String tag, String region) {
        return playerRepository.findByNicknameAndTag(name.trim(), tag.trim())
                .orElseGet(() -> createFromApi(name.trim(), tag.trim(), region));
    }

    @Transactional
    public Player getOrCreateWithMMR(String name, String tag, String region) {
        Player player = getOrCreate(name, tag, region);

        // Обновляем MMR данные
        updateMMR(player.getPuuid(), region);

        // Загружаем матчи
        matchService.updateMatches(player.getPuuid(), region);

        // Перезагружаем игрока с обновленными данными
        return playerRepository.findById(player.getPuuid()).orElse(player);
    }

    private Player createFromApi(String name, String tag, String region) {
        System.out.println("🔍 Player not in DB, fetching from API: " + name + "#" + tag);

        JsonNode accountData = apiService.fetchAccount(name, tag);

        Player player = new Player();
        player.setPuuid(accountData.get("puuid").asText());
        player.setNickname(accountData.get("name").asText());
        player.setTag(accountData.get("tag").asText());
        player.setRegion(accountData.get("region").asText());
        player.setAccountLevel(accountData.get("account_level").asInt());
        player.setCurrentTier(0);
        player.setCurrentRr(0);
        player.setCreatedAt(OffsetDateTime.now());
        player.setLastUpdated(OffsetDateTime.now());

        Player saved = playerRepository.save(player);
        System.out.println("✅ Player saved to DB: " + saved.getNickname() + "#" + saved.getTag());

        return saved;
    }

    private void updateMMR(String puuid, String region) {
        try {
            JsonNode mmrData = apiService.fetchMMR(region, puuid);

            if (mmrData != null) {
                playerRepository.findById(puuid).ifPresent(player -> {
                    player.setCurrentTier(mmrData.get("currenttier").asInt());
                    player.setCurrentRr(mmrData.get("ranking_in_tier").asInt());
                    player.setLastUpdated(OffsetDateTime.now());
                    playerRepository.save(player);
                });

                System.out.println("✅ MMR updated for player: " + puuid);
            }
        } catch (Exception e) {
            System.err.println("⚠️ Failed to update MMR: " + e.getMessage());
        }
    }
}
