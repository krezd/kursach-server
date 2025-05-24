package com.example.kursachserver.controller;

import com.example.kursachserver.dto.request.UserWorkScheduleUpdateDto;
import com.example.kursachserver.dto.request.WorkScheduleItemDto;
import com.example.kursachserver.model.User;
import com.example.kursachserver.model.UserWorkSchedule;
import com.example.kursachserver.repository.UserRepository;
import com.example.kursachserver.repository.UserWorkScheduleRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/work-schedule")
public class UserWorkScheduleController {
    private final UserWorkScheduleRepository scheduleRepo;
    private final UserRepository userRepo;

    public UserWorkScheduleController(UserWorkScheduleRepository scheduleRepo, UserRepository userRepo) {
        this.scheduleRepo = scheduleRepo;
        this.userRepo = userRepo;
    }

    // Получить расписание пользователя (если нет — дефолт)
    @GetMapping("/{userId}")
    public List<WorkScheduleItemDto> getSchedule(@PathVariable Long userId) {
        List<UserWorkSchedule> list = scheduleRepo.findByUserId(userId);
        if (list.isEmpty()) return getDefaultSchedule();
        return list.stream().map(e -> {
            WorkScheduleItemDto dto = new WorkScheduleItemDto();
            dto.setDayOfWeek(e.getDayOfWeek());
            dto.setStartTime(e.getStartTime().toString());
            dto.setEndTime(e.getEndTime().toString());
            return dto;
        }).toList();
    }

    // Массовое обновление расписания
    @PutMapping("/update")
    public ResponseEntity<?> updateSchedule(@RequestBody UserWorkScheduleUpdateDto dto) {
        User user = userRepo.findById(dto.getUserId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        // Удаляем старое расписание
        scheduleRepo.deleteByUserId(user.getId());

        // Сохраняем новое
        for (WorkScheduleItemDto item : dto.getSchedule()) {
            UserWorkSchedule entity = new UserWorkSchedule(
                    null,
                    user,
                    item.getDayOfWeek(),
                    LocalTime.parse(item.getStartTime()),
                    LocalTime.parse(item.getEndTime())
            );
            scheduleRepo.save(entity);
        }
        return ResponseEntity.ok().build();
    }

    // Дефолт: Пн-Пт 09:00-18:00, Сб-Вс выходной
    private List<WorkScheduleItemDto> getDefaultSchedule() {
        List<WorkScheduleItemDto> def = new ArrayList<>();
        for (int i = 1; i <= 7; i++) {
            WorkScheduleItemDto d = new WorkScheduleItemDto();
            d.setDayOfWeek(i);
            if (i >= 1 && i <= 5) { // Пн-Пт
                d.setStartTime("09:00");
                d.setEndTime("18:00");
            } else { // Сб-Вс
                d.setStartTime("00:00");
                d.setEndTime("00:00");
            }
            def.add(d);
        }
        return def;
    }
}

