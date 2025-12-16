package com.valorant.tracker.dto;



import lombok.*;

@Data
@Builder
public class RecentMatchDTO {
    private String matchId;
    private String mapName;
    private String agentName;

    private Integer kills;
    private Integer deaths;
    private Integer assists;
    private Integer score;

    private Boolean won;
    private String date;
}
