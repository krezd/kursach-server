package com.example.kursachserver.dto.request;

import com.example.kursachserver.enumModel.Role;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@NoArgsConstructor
@RequiredArgsConstructor
public class UpdateUserRequest {
    @NonNull
    private String name;
    @NonNull
    private String username;
    @NonNull
    private String oldPassword;
    private String newPassword;
    private String confirmNewPassword;
}


