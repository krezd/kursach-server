package com.example.kursachserver.dto.response;

import com.example.kursachserver.model.Session;
import lombok.Data;

import java.text.SimpleDateFormat;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.util.UUID;

@Data
public class SessionDTO {
    private UUID id;
    private ZonedDateTime startSession;
    private ZonedDateTime endSession;

    public SessionDTO(UUID id, ZonedDateTime startSession, ZonedDateTime endSession) {
    this.id = id;
    this.startSession = startSession;
    this.endSession = endSession;
    }

    public static SessionDTO toDto(Session session) {
        return new SessionDTO(
                session.getId(),
                session.getStartSession(),
                session.getEndSession()
        );
    }
}
