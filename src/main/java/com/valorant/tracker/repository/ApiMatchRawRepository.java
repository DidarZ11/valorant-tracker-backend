package com.valorant.tracker.repository;



import com.valorant.tracker.entity.ApiMatchRaw;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ApiMatchRawRepository
        extends JpaRepository<ApiMatchRaw, String> {
}
