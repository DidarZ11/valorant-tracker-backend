package com.valorant.tracker.repository;

import com.valorant.tracker.entity.MatchPlayer;
import com.valorant.tracker.entity.MatchPlayerKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MatchPlayerRepository extends JpaRepository<MatchPlayer, MatchPlayerKey> {  // ← Изменили Long на MatchPlayerKey

    // Простой метод без сортировки
    List<MatchPlayer> findByPuuid(String puuid);

    @Query("SELECT SUM(mp.kills) FROM MatchPlayer mp WHERE mp.puuid = ?1")
    Integer sumKillsByPuuid(String puuid);

    @Query("SELECT SUM(mp.deaths) FROM MatchPlayer mp WHERE mp.puuid = ?1")
    Integer sumDeathsByPuuid(String puuid);

    @Query("SELECT SUM(mp.assists) FROM MatchPlayer mp WHERE mp.puuid = ?1")
    Integer sumAssistsByPuuid(String puuid);

    @Query("SELECT COUNT(mp) FROM MatchPlayer mp WHERE mp.puuid = ?1")
    Long countWinsByPuuid(String puuid);
}