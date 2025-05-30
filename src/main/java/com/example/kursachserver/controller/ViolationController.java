package com.example.kursachserver.controller;

import com.example.kursachserver.dto.request.ViolationCreationDto;
import com.example.kursachserver.dto.response.ViolationDto;
import com.example.kursachserver.enumModel.Severity;
import com.example.kursachserver.enumModel.ViolationType;
import com.example.kursachserver.model.User;
import com.example.kursachserver.model.Violation;
import com.example.kursachserver.repository.UserRepository;
import com.example.kursachserver.repository.ViolationRepository;
import com.example.kursachserver.service.ViolationService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/violations")
public class ViolationController {

    private final ViolationService violationService;
    private final ViolationRepository violationRepo;
    private final UserRepository userRepo;

    public ViolationController(ViolationService service,
                               ViolationRepository repo, UserRepository userRepo) {
        this.violationService = service;
        this.violationRepo = repo;
        this.userRepo = userRepo;
    }

    @GetMapping
    public List<ViolationDto> getAll() {
        return violationRepo.findAll().stream()
                .map(ViolationDto::new)
                .toList();
    }

    @GetMapping("/filter")
    public List<ViolationDto> filterViolations(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String type,         // ViolationType
            @RequestParam(required = false) String severity,     // Severity
            @RequestParam(required = false) String from,         // ISO DateTime (например, "2024-05-22T00:00:00Z")
            @RequestParam(required = false) String to            // ISO DateTime
    ) {
        return violationService.filter(userId, type, severity, from, to).stream().map(ViolationDto::new).toList();
    }


    // Фильтрация по пользователю
    @GetMapping("/user/{id}")
    public List<ViolationDto> getForUser(@PathVariable Long id) {
        User u = new User();
        u.setId(id);
        return violationRepo.findByUser(u).stream().map(ViolationDto::new).toList();
    }

    // Допустим, сотрудник может изменить степень серьёзности
    @PutMapping("/{id}/severity")
    public ViolationDto updateSeverity(@PathVariable Long id, @RequestParam Severity sev) {
        Violation v = violationRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        v.setSeverity(sev);
        return new ViolationDto(violationRepo.save(v));
    }

    @PostMapping
    public ResponseEntity<ViolationDto> createViolation(
            @RequestBody ViolationCreationDto dto
    ) {
        User user = userRepo.findById(dto.getUserId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        Map<String, Object> meta = new HashMap<>();
        if (dto.getDomain() != null) meta.put("domain", dto.getDomain());
        if (dto.getIp() != null) meta.put("ip", dto.getIp());
        if (dto.getAttempts() != null) meta.put("count", dto.getAttempts());
        if (dto.getTime() != null) meta.put("time", dto.getTime());

        Severity sev = dto.getSeverity();
        if (sev == null) {
            sev = determineSeverity(dto.getType(), dto);
        }

        ViolationDto v = violationService.record(
                user,
                dto.getType(),
                sev,
                meta
        );
        return new ResponseEntity<>(v, HttpStatus.CREATED);
    }

    @GetMapping("/report")
    public ResponseEntity<byte[]> generatePdfReport(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) ViolationType type,
            @RequestParam(required = false) Severity severity,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime to
    ) throws Exception {
        byte[] pdf = violationService.generateReport(userId, type, severity, from, to);
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=\"violations-report.pdf\"")
                .contentType(org.springframework.http.MediaType.APPLICATION_PDF)
                .body(pdf);
    }
    /**
     * Логика подбора уровня серьёзности по типу (и, при желании, деталям)
     */
    private Severity determineSeverity(ViolationType type, ViolationCreationDto dto) {
        switch (type) {
            case BLOCKED_SITE:
                return Severity.CRITICAL;
            case UNKNOWN_SITE:
                return Severity.MEDIUM;
            case UNAUTHORIZED_IP:
                return Severity.HIGH;
            case FAILED_LOGINS:
                // если больше 5 подряд — HIGH, иначе MEDIUM
                Integer c = dto.getAttempts() != null ? dto.getAttempts() : 0;
                return c > 5 ? Severity.HIGH : Severity.MEDIUM;
            case AFTER_HOURS_ACCESS:
                // до 30 минут после — LOW, дольше — MEDIUM
                LocalTime login = LocalTime.parse(dto.getTime());
                if (login.isBefore(LocalTime.of(18, 30))) return Severity.LOW;
                return Severity.MEDIUM;
            case WEEKEND_ACCESS:
                return Severity.LOW;
            default:
                return Severity.MEDIUM;
        }
    }
}
