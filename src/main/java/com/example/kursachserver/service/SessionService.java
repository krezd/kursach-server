package com.example.kursachserver.service;

import com.example.kursachserver.dto.AppError;
import com.example.kursachserver.dto.response.SessionDTO;
import com.example.kursachserver.dto.response.SessionWithStatus;
import com.example.kursachserver.model.Process;
import com.example.kursachserver.model.Session;
import com.example.kursachserver.model.User;
import com.example.kursachserver.repository.SessionRepository;
import com.example.kursachserver.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class SessionService {
    @Autowired
    private SessionRepository sessionRepository;
    @Autowired
    private ProcessService processService;
    @Autowired
    private UserRepository userRepository;

    public ResponseEntity<?> getSession(UUID id) {
        if (sessionRepository.existsById(id)) {
            return new ResponseEntity<>(sessionRepository.findById(id), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    public ResponseEntity<List<SessionDTO>> getMySession(Long userId) {
        if (sessionRepository.existsByUserId(userId)) {
            List<Session> sessions = sessionRepository.findAllByUserId(userId);

            List<SessionDTO> sessionDTOs = sessions.stream()
                    .map(SessionDTO::toDto)
                    .toList();

            return new ResponseEntity<>(sessionDTOs, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    public ResponseEntity<?> getSessionByUser(Long userId) {
        if (sessionRepository.existsByUserId(userId)) {
            return new ResponseEntity<>(sessionRepository.findAllByUserId(userId), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    public ResponseEntity<?> getMySessionByDate(LocalDate date, Long userId) {
        List<Session> sessions = sessionRepository.findByDateAndUser(date, userId);
        return new ResponseEntity<>(sessions, HttpStatus.OK);
    }

    public ResponseEntity<?> getSessionByDate(LocalDate date) {
        List<Session> sessions = sessionRepository.findByDate(date);
        return new ResponseEntity<>(sessions, HttpStatus.OK);
    }

    public ResponseEntity<?> searchSession(String name, String position, LocalDate date) {
        List<SessionWithStatus> session = getSessionsWithStatus(sessionRepository.searchSessions(name, position, date));
        return new ResponseEntity<>(session, HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<?> createSession(Long userId, Session session) {
        if (sessionRepository.existsById(session.getId())) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return new ResponseEntity<>(new AppError(HttpStatus.NOT_FOUND.value(), "Пользователя не существует"), HttpStatus.NOT_FOUND);
        }
        session.setUser(user);

        List<Process> processes = session.getProcesses();
        for (Process process1 : processes) {
            process1.setSession(session);
        }
        return new ResponseEntity<>(sessionRepository.save(session), HttpStatus.CREATED);
    }

    private List<SessionWithStatus> getSessionsWithStatus(List<Session> sessions) {

        List<SessionWithStatus> sessionsWithStatus = new ArrayList<>();
        for (Session session : sessions) {

            Duration goodTime = Duration.ZERO;
            Duration badTime = Duration.ZERO;
            Duration neutralTime = Duration.ZERO;

            for (Process process : session.getProcesses()) {
                Duration processTime = process.getUsageTimes().stream()
                        .map(usage -> Duration.between(
                                usage.getStartTime(),
                                usage.getEndTime() != null ? usage.getEndTime() : LocalDateTime.now()
                        ))
                        .reduce(Duration.ZERO, Duration::plus);

                switch (process.getProcessStatus().getStatus().toString()) {
                    case "GOOD":
                        goodTime = goodTime.plus(processTime);
                        break;
                    case "BAD":
                        badTime = badTime.plus(processTime);
                        break;
                    case "NEUTRAL":
                        neutralTime = neutralTime.plus(processTime);
                        break;
                }
            }

            sessionsWithStatus.add(new SessionWithStatus(
                    session.getId(),
                    session.getStartSession(),
                    session.getEndSession(),
                    session.getUser(),
                    goodTime,
                    badTime,
                    neutralTime
            ));
        }
        return sessionsWithStatus;
    }

    public ResponseEntity<List<?>> getAllSession() {
        List<SessionWithStatus> sessionsWithStatus = getSessionsWithStatus(sessionRepository.findAll());
        return new ResponseEntity<>(sessionsWithStatus, HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<?> updateSession(Long userId, Session session) {
        if (sessionRepository.existsById(session.getId())) {
            Session session1 = sessionRepository.findById(session.getId()).orElse(null);
            List<Process> processes = session.getProcesses();
            for (Process process1 : processes) {
                process1.setSession(session);
            }
            User user = userRepository.findById(userId).orElse(null);
            if (user == null) {
                return new ResponseEntity<>(new AppError(HttpStatus.NOT_FOUND.value(), "Пользователя не существует"), HttpStatus.NOT_FOUND);
            }
            session.setUser(user);
            return new ResponseEntity<>(sessionRepository.save(session), HttpStatus.CREATED);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @Transactional
    public ResponseEntity<?> deleteSessionByUserId(Long id) {
        if (sessionRepository.existsByUserId(id)) {
            sessionRepository.deleteAllByUserId(id);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @Transactional
    public ResponseEntity<?> deleteSessionById(UUID id) {
        if (sessionRepository.existsById(id)) {
            sessionRepository.deleteById(id);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }


}
