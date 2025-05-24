package com.example.kursachserver.dto.response;

import com.example.kursachserver.enumModel.Severity;
import com.example.kursachserver.enumModel.ViolationType;
import com.example.kursachserver.model.Violation;
import lombok.Data;

import java.time.Instant;
import java.time.OffsetDateTime;

@Data
public class ViolationDto {
    private Long id;
    private Long userId;
    private String name;        // ФИО или отображаемое имя пользователя
    private String position;    // Должность пользователя
    private ViolationType type;
    private Severity severity;
    private OffsetDateTime occurredAt;
    private String metadata;

    public ViolationDto(Violation v) {
        this.id = v.getId();
        this.userId = v.getUser().getId();
        this.name = v.getUser().getName();        // или getFio() — как у тебя в User
        this.position = v.getUser().getPosition();
        this.type = v.getType();
        this.severity = v.getSeverity();
        this.occurredAt = v.getOccurredAt();
        this.metadata = v.getMetadata();
    }

}
