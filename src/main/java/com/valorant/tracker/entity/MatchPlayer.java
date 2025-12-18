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
    @Column(name = "match_id")
    private String matchId;

    @Id
    @Column(name = "puuid")
    private String puuid;

    @Column(name = "team")
    private String team;

    @Column(name = "agent_id")
    private Long agentId;

    @Column(name = "score")
    private Integer score;

    @Column(name = "kills")
    private Integer kills;

    @Column(name = "deaths")
    private Integer deaths;

    @Column(name = "assists")
    private Integer assists;

    @Column(name = "rounds_played")
    private Integer roundsPlayed;

    @Column(name = "playtime_ms")
    private Long playtimeMs;

    @Column(name = "won")
    private Boolean won;
}