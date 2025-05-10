package com.example.kursachserver.service;

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
import java.util.Map;

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
    public Violation record(
            User user,
            ViolationType type,
            Severity severity,
            Map<String, Object> meta  // передаём любые дополнительные поля
    ) {
        Violation v = new Violation();
        v.setUser(user);
        v.setType(type);
        v.setSeverity(severity);
        v.setOccurredAt(Instant.now());
        String jsonMeta;
        try {
            jsonMeta = mapper.writeValueAsString(meta);
        } catch (JsonProcessingException e) {
            jsonMeta = "{}";
        }
        v.setMetadata(jsonMeta);
        Violation saved = repo.save(v);

        ws.convertAndSend("/topic/violations", saved);
        return saved;
    }

}
