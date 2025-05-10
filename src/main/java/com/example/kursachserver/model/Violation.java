package com.example.kursachserver.model;

import com.example.kursachserver.enumModel.Severity;
import com.example.kursachserver.enumModel.ViolationType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "violations")
public class Violation {
    @Id @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ViolationType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Severity severity;

    @Column(nullable = false)
    private Instant occurredAt;

    // Здесь свободный JSON / текст с деталями (IP, count, timestamp и т.д.)
    @Column(columnDefinition = "CLOB")
    private String metadata;
}
