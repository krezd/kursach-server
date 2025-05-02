package com.example.kursachserver.model;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "violations")
public class Violation {
    @Id
    @GeneratedValue
    private Long id;
    private Long userId;
    private String type;
    private String severity;
    private Instant occurredAt;
    @Column(columnDefinition = "CLOB")
    private String metadata;
}