package com.valorant.tracker.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "agents")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Agent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "agent_id")
    private Long agentId;

    @Column(name = "agent_name", unique = true, nullable = false)
    private String agentName;

    @Column(name = "display_icon_url")
    private String displayIconUrl;

    @Column(name = "role")
    private String role;
  
}