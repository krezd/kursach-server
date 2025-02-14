package com.example.kursachserver.dto.request;

import com.example.kursachserver.enumModel.Role;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
@NoArgsConstructor
public class RegistrationRequest {
    private String name;
    @NonNull
    private String username;
    @NonNull
    private String password;
    @NonNull
    private String confirmPassword;
    private String position;
    @Enumerated(EnumType.STRING)
    private Role role;
}
