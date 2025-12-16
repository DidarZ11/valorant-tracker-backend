package com.valorant.tracker.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.*;

@Entity
@Table(name = "api_matches_raw")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiMatchRaw {

    @Id
    private String matchId;

    private String region;

    @Column(columnDefinition = "jsonb")
    private String payload;

    private OffsetDateTime fetchedAt;
}
