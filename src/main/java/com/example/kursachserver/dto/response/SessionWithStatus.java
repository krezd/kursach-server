package com.example.kursachserver.dto.response;

import com.example.kursachserver.model.Process;
import com.example.kursachserver.model.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import jdk.jfr.Timespan;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.convert.DurationFormat;

import java.security.Timestamp;
import java.time.Duration;
import java.time.ZonedDateTime;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SessionWithStatus {
    private UUID id;
    private ZonedDateTime startSession;
    private ZonedDateTime endSession;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private User user;
    private Duration GOOD;
    private Duration BAD;
    private Duration NEUTRAL;

}
