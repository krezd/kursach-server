package com.example.kursachserver.dto.response;

import com.example.kursachserver.enumModel.Role;
import com.example.kursachserver.model.User;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {
    private Long id;
    private String name;
    private String username;
    private String position;
    @Enumerated(EnumType.STRING)
    private Role role;
    private LocalDateTime createUserDate;

    public UserResponse(User user){
        this.id = user.getId();
        this.name = user.getName();
        this.username = user.getUsername();
        this.position = user.getPosition();
        this.role = user.getRole();
        this.createUserDate = user.getCreateUserDate();
    }
}
