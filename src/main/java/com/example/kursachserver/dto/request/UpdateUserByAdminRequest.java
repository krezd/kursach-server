package com.example.kursachserver.dto.request;

import com.example.kursachserver.enumModel.Role;
import com.example.kursachserver.model.Session;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@RequiredArgsConstructor
public class UpdateUserByAdminRequest {
    private Long id;
    private String position;
    @Enumerated(EnumType.STRING)
    private Role role;
}
