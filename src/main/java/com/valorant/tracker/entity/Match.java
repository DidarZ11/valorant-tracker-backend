package com.valorant.tracker.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "matches")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Match {

    @Id
    private String matchId;

    @Column(length = 50)
    private String mapName;

    @Column(length = 20)
    private String mode;

    private OffsetDateTime startedAt;

    @Column(length = 20)
    private String region;

    private OffsetDateTime fetchedAt;
}