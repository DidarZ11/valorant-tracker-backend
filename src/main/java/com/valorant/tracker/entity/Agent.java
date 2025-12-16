package com.valorant.tracker.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "agents")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Agent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long agentId;

    @Column(length = 24, nullable = false, unique = true)
    private String name;

    @Column(length = 24, nullable = false)
    private String role;
}
