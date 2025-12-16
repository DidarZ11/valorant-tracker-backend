package com.valorant.tracker.dto;



import lombok.*;

@Data
@Builder
public class CurrentRankDTO {
    private Integer tier;
    private String tierName;
    private Integer rr;
    private String movement;
}

