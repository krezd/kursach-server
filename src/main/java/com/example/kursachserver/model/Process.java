package com.example.kursachserver.model;

import com.example.kursachserver.enumModel.ProcessStatus;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "process_table")
public class Process {
    @Id
    @GeneratedValue
    private Long id;
    private String processName;
    private LocalDateTime startProcess;
    private LocalDateTime endProcess;
    @Enumerated(EnumType.STRING)
    private ProcessStatus processStatus;
    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id")
    private Session session;
}
