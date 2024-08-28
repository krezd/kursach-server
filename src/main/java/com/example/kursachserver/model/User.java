package com.example.kursachserver.model;

import com.example.kursachserver.enumModel.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

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
    private String username;
    private String password;
    private String position;
    @Enumerated(EnumType.STRING)
    private Role role;
    private LocalDateTime createUserDate;
    private Boolean authStatus;


}
