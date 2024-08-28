package com.example.kursachserver.dto.request;

import com.example.kursachserver.model.Process;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.antlr.v4.runtime.misc.NotNull;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SessionRequest {
    @Id
    private UUID id;
    private LocalDateTime startSession;
    private LocalDateTime endSession;
    private List<Process> processes = new ArrayList<>();
}
