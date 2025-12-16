package com.valorant.tracker.repository;

import com.valorant.tracker.entity.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PlayerRepository extends JpaRepository<Player, String> {
    Optional<Player> findByNicknameAndTag(String nickname, String tag);

    // Puuid - это уже ID, поэтому используем стандартный метод:
    // Optional<Player> findById(String puuid) - уже есть из JpaRepository
}