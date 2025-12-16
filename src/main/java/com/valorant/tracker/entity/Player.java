package com.valorant.tracker.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "players")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Player {

    @Id
    private String puuid;

    @Column(length = 50, nullable = false)
    private String nickname;  // ← Используем nickname вместо name

    @Column(length = 20, nullable = false)
    private String tag;

    @Column(length = 20)
    private String region;

    private Integer accountLevel;
    private Integer currentTier;
    private Integer currentRr;

    private OffsetDateTime lastUpdated;
    private OffsetDateTime createdAt;
}