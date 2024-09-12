package com.example.kursachserver.controller;

import com.example.kursachserver.model.Session;
import com.example.kursachserver.service.JwtService;
import com.example.kursachserver.service.SessionService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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
    @GetMapping()
    public ResponseEntity<?> getAllSession() {
        return sessionService.getAllSession();

    }
    @PostMapping()
    public ResponseEntity<?> createSession(HttpServletRequest request,@RequestBody Session session) {
        session.setUserId(jwtService.getUserId(jwtService.extractToker(request)));
        return sessionService.createSession(session);
    }

    @PutMapping()
    public ResponseEntity<?> updateSession(@RequestBody Session session) {
        return sessionService.updateSession(session);
    }
    
    @DeleteMapping("/")
    public ResponseEntity<?> deleteAllSessionByUserID(@RequestParam("userId") Long userId) {
        return sessionService.deleteSessionByUserId(userId);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteSessionById(@PathVariable UUID id){
        return sessionService.deleteSessionById(id);
    }

}
