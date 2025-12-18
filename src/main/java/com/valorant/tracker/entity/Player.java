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
    @Column(name = "puuid", length = 50)
    private String puuid;

    @Column(name = "nickname", length = 100)
    private String nickname;

    @Column(name = "tag", length = 20)
    private String tag;

    @Column(name = "region", length = 20)
    private String region;

    @Column(name = "account_level")
    private Integer accountLevel;

    @Column(name = "current_tier")
    private Integer currentTier;

    @Column(name = "current_rr")
    private Integer currentRr;

    @Column(name = "created_at")
    private OffsetDateTime createdAt;

    @Column(name = "last_updated")
    private OffsetDateTime lastUpdated;

    // НОВОЕ ПОЛЕ
    @Column(name = "card_icon_url", length = 500)
    private String cardIconUrl;
}