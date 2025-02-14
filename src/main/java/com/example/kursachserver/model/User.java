package com.example.kursachserver.model;

import com.example.kursachserver.enumModel.Role;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "user_table")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @JsonIgnore
    private String username;
    @JsonIgnore
    private String password;
    private String position;
    @Enumerated(EnumType.STRING)
    @JsonIgnore
    private Role role;
    @JsonIgnore
    private LocalDateTime createUserDate;
    @JsonIgnore
    private Boolean authStatus;
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Session> sessions = new ArrayList<>();
}
