package com.example.kursachserver.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRatingDto {
    private Long userId;
    private String name;
    private String position;
    private Long score;
}
