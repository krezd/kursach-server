package com.example.kursachserver.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;

@Data
@Entity
@Table(name = "blocked_sites")
public class BlockedSite {
    @Id
    @GeneratedValue
    private Long id;
    @Column(unique = true, nullable = false)
    private String domain;
    private String reason;
    private Instant updatedAt;
}