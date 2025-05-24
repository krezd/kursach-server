package com.example.kursachserver.service;

import com.example.kursachserver.dto.response.ViolationDto;
import com.example.kursachserver.enumModel.Severity;
import com.example.kursachserver.enumModel.ViolationType;
import com.example.kursachserver.model.User;
import com.example.kursachserver.model.Violation;
import com.example.kursachserver.repository.ViolationRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@Service
public class ViolationService {
    private final ViolationRepository repo;
    private final SimpMessagingTemplate ws;
    private final ObjectMapper mapper = new ObjectMapper();

    @Autowired
    public ViolationService(ViolationRepository repo,
                            SimpMessagingTemplate ws) {
        this.repo = repo;
        this.ws = ws;
    }

    @Transactional
    public ViolationDto record(
            User user,
            ViolationType type,
            Severity severity,
            Map<String, Object> meta  // передаём любые дополнительные поля
    ) {
        Violation v = new Violation();
        v.setUser(user);
        v.setType(type);
        v.setSeverity(severity);
        v.setOccurredAt(OffsetDateTime.now());
        String jsonMeta;
        try {
            jsonMeta = mapper.writeValueAsString(meta);
        } catch (JsonProcessingException e) {
            jsonMeta = "{}";
        }
        v.setMetadata(jsonMeta);
        ViolationDto saved = new ViolationDto(repo.save(v));

        ws.convertAndSend("/topic/violations", saved);
        return saved;
    }

    public List<Violation> filter(Long userId, String type, String severity, String from, String to) {
        List<Violation> all = repo.findAll();
        Stream<Violation> stream = all.stream();

        if (userId != null)
            stream = stream.filter(v -> v.getUser() != null && v.getUser().getId().equals(userId));
        if (type != null)
            stream = stream.filter(v -> v.getType().name().equalsIgnoreCase(type));
        if (severity != null)
            stream = stream.filter(v -> v.getSeverity().name().equalsIgnoreCase(severity));
        if (from != null)
            stream = stream.filter(v -> v.getOccurredAt().isAfter(OffsetDateTime.from(Instant.parse(from))));
        if (to != null)
            stream = stream.filter(v -> v.getOccurredAt().isBefore(OffsetDateTime.from(Instant.parse(to))));

        return stream.toList();
    }
}
