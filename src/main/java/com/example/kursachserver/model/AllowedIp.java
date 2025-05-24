package com.example.kursachserver.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "allowed_ips")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AllowedIp {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore // <--- Добавь это
    private User user;

    @Column(nullable = false)
    private String ip;
}
