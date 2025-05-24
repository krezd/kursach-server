package com.example.kursachserver.dto.request;

import lombok.Data;

import java.util.List;

@Data
public class UserWorkScheduleUpdateDto {
    private Long userId;
    private List<WorkScheduleItemDto> schedule; // 7 записей — на каждый день недели
}