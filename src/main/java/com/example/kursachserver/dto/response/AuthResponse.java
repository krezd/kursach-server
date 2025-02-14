package com.example.kursachserver.dto.response;

import com.example.kursachserver.enumModel.Role;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private Role role;
}
