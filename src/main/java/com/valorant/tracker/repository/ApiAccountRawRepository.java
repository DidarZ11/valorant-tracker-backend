package com.valorant.tracker.repository;



import com.valorant.tracker.entity.ApiAccountRaw;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ApiAccountRawRepository
        extends JpaRepository<ApiAccountRaw, String> {

    Optional<ApiAccountRaw> findByNameAndTag(String name, String tag);
}

