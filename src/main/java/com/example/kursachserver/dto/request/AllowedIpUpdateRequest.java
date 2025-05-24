package com.example.kursachserver.dto.request;

import lombok.Data;

import java.util.List;

@Data
public class AllowedIpUpdateRequest {
    private Long userId;
    private List<String> ips;
}
