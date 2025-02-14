package com.example.kursachserver.controller;

import com.example.kursachserver.model.Session;
import com.example.kursachserver.service.JwtService;
import com.example.kursachserver.service.SessionService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/session")
public class SessionController {
    @Autowired
    private SessionService sessionService;

    private final JwtService jwtService;

    @GetMapping("/{id}")
    public ResponseEntity<?> getSession(@PathVariable UUID id) {
        return sessionService.getSession(id);

    }

    @GetMapping("/")
    public ResponseEntity<?> getAllUserSession(@RequestParam("userId") Long userId) {
        return sessionService.getSessionByUser(userId);
    }

    @GetMapping("/mySession")
    public ResponseEntity<?> getAllMySession(HttpServletRequest request) {
        return sessionService.getSessionByUser(jwtService.getUserId(jwtService.extractToker(request)));
    }

    @GetMapping("/mySessionByDate")
    public ResponseEntity<?> getAllMySessionByDate(@RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date, HttpServletRequest request) {
        return sessionService.getMySessionByDate(date, jwtService.getUserId(jwtService.extractToker(request)));
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping("/allSessionByDate")
    public ResponseEntity<?> getAllSessionByDate(@RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return sessionService.getSessionByDate(date);
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping()
    public ResponseEntity<?> getAllSession() {
        return sessionService.getAllSession();
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping("/search")
    public ResponseEntity<?> searchSessions(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String position,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        if (name == null && position == null && date == null) {
            return sessionService.getAllSession();
        }

        return sessionService.searchSession(name, position, date);
    }

    @PostMapping()
    public ResponseEntity<?> createSession(HttpServletRequest request, @RequestBody Session session) throws JsonProcessingException {
        return sessionService.createSession(jwtService.getUserId(jwtService.extractToker(request)), session);
    }

    @PutMapping()
    public ResponseEntity<?> updateSession(HttpServletRequest request, @RequestBody Session session) {
        return sessionService.updateSession(jwtService.getUserId(jwtService.extractToker(request)), session);
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @DeleteMapping("/")
    public ResponseEntity<?> deleteAllSessionByUserID(@RequestParam("userId") Long userId) {
        return sessionService.deleteSessionByUserId(userId);
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteSessionById(@PathVariable UUID id) {
        return sessionService.deleteSessionById(id);
    }

}
