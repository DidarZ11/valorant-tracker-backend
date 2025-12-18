package com.valorant.tracker.entity;

import lombok.*;
import java.io.Serializable;
import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MatchPlayerKey implements Serializable {

    private String matchId;
    private String puuid;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MatchPlayerKey that)) return false;
        return Objects.equals(matchId, that.matchId)
                && Objects.equals(puuid, that.puuid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(matchId, puuid);
    }
}
