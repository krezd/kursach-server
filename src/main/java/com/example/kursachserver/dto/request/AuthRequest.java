package com.example.kursachserver.dto.request;

import lombok.Data;

@Data

public class AuthRequest {
    private String username;
    private String password;
    private String ip;
}
