package com.example.kursachserver.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.antlr.v4.runtime.misc.NotNull;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "session_table")
public class Session {
    @Id
    private UUID id;
    private LocalDateTime startSession;
    private LocalDateTime endSession;
    private Long userId;
    @OneToMany(mappedBy = "session", cascade = CascadeType.ALL)
    private List<Process> processes = new ArrayList<>(  );
}
