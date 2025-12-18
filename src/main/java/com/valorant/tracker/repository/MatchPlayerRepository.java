package com.valorant.tracker.repository;

import com.valorant.tracker.entity.MatchPlayer;
import com.valorant.tracker.entity.MatchPlayerKey;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MatchPlayerRepository extends JpaRepository<MatchPlayer, MatchPlayerKey> {

    List<MatchPlayer> findByPuuid(String puuid);

    // ДОБАВИЛИ: Найти всех игроков в матче
    List<MatchPlayer> findByMatchId(String matchId);

    @Query("SELECT mp FROM MatchPlayer mp JOIN Match m ON mp.matchId = m.matchId " +
            "WHERE mp.puuid = :puuid AND LOWER(m.mode) = LOWER(:mode)")
    List<MatchPlayer> findByPuuidAndMode(@Param("puuid") String puuid, @Param("mode") String mode);

    @Query("SELECT mp FROM MatchPlayer mp JOIN Match m ON mp.matchId = m.matchId " +
            "WHERE mp.puuid = :puuid ORDER BY m.startedAt DESC")
    List<MatchPlayer> findByPuuidOrderByMatchIdDesc(@Param("puuid") String puuid, Pageable pageable);

    @Query("SELECT mp FROM MatchPlayer mp JOIN Match m ON mp.matchId = m.matchId " +
            "WHERE mp.puuid = :puuid AND LOWER(m.mode) = LOWER(:mode) ORDER BY m.startedAt DESC")
    List<MatchPlayer> findByPuuidAndModeOrderByMatchIdDesc(
            @Param("puuid") String puuid,
            @Param("mode") String mode,
            Pageable pageable);
}