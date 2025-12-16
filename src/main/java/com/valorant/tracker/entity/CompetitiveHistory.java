package com.valorant.tracker.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.*;

@Entity
@Table(name = "competitive_history")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompetitiveHistory {

    @Id
    private String matchId;

    @Column(nullable = false)
    private String puuid;

    private String seasonId;
    private OffsetDateTime matchStartTime;
    private Integer tierBefore;
    private Integer tierAfter;
    private Integer rrBefore;
    private Integer rrAfter;
    private Integer rrEarned;
    private Integer rrPerfBonus;
    private String movement;
    private Integer afkPenalty;
}
