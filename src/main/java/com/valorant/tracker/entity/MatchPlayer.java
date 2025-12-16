package com.valorant.tracker.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "match_players")
@Data
@NoArgsConstructor
@AllArgsConstructor
@IdClass(MatchPlayerKey.class)
public class MatchPlayer {

    @Id
    private String matchId;

    @Id
    private String puuid;

    private String team;

    @ManyToOne
    @JoinColumn(name = "agent_id")
    private Agent agent;

    private Integer score;
    private Integer kills;
    private Integer deaths;
    private Integer assists;
    private Integer roundsPlayed;
    private Long playtimeMs;

    private Boolean won;  // ← Добавили это поле!
}