package com.valorant.tracker.repository;



import com.valorant.tracker.entity.GameMap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GameMapRepository
        extends JpaRepository<GameMap, Long> {

    Optional<GameMap> findByName(String name);
}
