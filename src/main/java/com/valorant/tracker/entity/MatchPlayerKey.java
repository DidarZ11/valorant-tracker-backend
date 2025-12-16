package com.valorant.tracker.entity;

import lombok.*;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MatchPlayerKey implements Serializable {
    private String matchId;
    private String puuid;
}
