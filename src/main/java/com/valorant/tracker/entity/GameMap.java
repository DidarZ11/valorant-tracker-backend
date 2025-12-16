package com.valorant.tracker.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "maps")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GameMap {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long mapId;

    @Column(length = 32, nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private Boolean isActive = true;
}
