package com.example.kursachserver.dto.request;

import lombok.Data;

@Data
public class WorkScheduleItemDto {
    private int dayOfWeek;        // 1-7
    private String startTime;     // "09:00"
    private String endTime;       // "18:00"
}
