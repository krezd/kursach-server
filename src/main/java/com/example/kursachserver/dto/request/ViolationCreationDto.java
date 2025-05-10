package com.example.kursachserver.dto.request;

import com.example.kursachserver.enumModel.Severity;
import com.example.kursachserver.enumModel.ViolationType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ViolationCreationDto {
    private Long userId;
    private ViolationType type;
    private Severity severity;   // можно не указывать
    private String         domain;     // для BLOCKED_SITE / UNKNOWN_SITE
    private String         ip;         // для UNAUTHORIZED_IP
    private Integer        attempts;   // для FAILED_LOGINS
    private String         time;       // для AFTER_HOURS_ACCESS / WEEKEND_ACCESS
}