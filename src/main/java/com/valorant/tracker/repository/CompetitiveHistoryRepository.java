package com.valorant.tracker.repository;

import com.valorant.tracker.entity.CompetitiveHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CompetitiveHistoryRepository extends JpaRepository<CompetitiveHistory, String> {

    @Query("SELECT ch FROM CompetitiveHistory ch WHERE ch.puuid = ?1 ORDER BY ch.matchStartTime DESC LIMIT 1")
    Optional<CompetitiveHistory> findLatestByPuuid(String puuid);
}