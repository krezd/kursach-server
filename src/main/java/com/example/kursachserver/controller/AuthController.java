package com.example.kursachserver.controller;

import com.example.kursachserver.dto.AppError;
import com.example.kursachserver.dto.request.AuthRequest;
import com.example.kursachserver.dto.request.RegistrationRequest;
import com.example.kursachserver.enumModel.Severity;
import com.example.kursachserver.enumModel.ViolationType;
import com.example.kursachserver.model.User;
import com.example.kursachserver.repository.AllowedIpRepository;
import com.example.kursachserver.repository.UserRepository;
import com.example.kursachserver.service.JwtService;
import com.example.kursachserver.service.LoginAttemptService;
import com.example.kursachserver.service.UserService;
import com.example.kursachserver.service.ViolationService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class AuthController {

    private final PasswordEncoder passwordEncoder;
    private final UserService userService;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final LoginAttemptService loginAttemptService;
    private final ViolationService violationService;
    private final UserRepository userRepository;

    @PostMapping("/auth")
    public ResponseEntity<?> authenticate(@RequestBody AuthRequest request) {
        // Проверка brute-force до аутентификации!
        if (loginAttemptService.isBlocked(request.getUsername(), request.getIp())) {
            // Можно загрузить пользователя из БД, если хочешь зафиксировать нарушение
            User user = userRepository.findByUsername(request.getUsername()).orElse(null);
            if (user != null) {
                Map<String, Object> meta = Map.of("ip", request.getIp(), "attempts", LoginAttemptService.MAX_ATTEMPTS);
                violationService.record(user, ViolationType.FAILED_LOGINS, Severity.HIGH, meta);
            }
            return new ResponseEntity<>(new AppError(HttpStatus.FORBIDDEN.value(), "Слишком много неудачных попыток входа. Аккаунт временно заблокирован."), HttpStatus.FORBIDDEN);
        }

        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
            // Если успех — сбросить попытки
            loginAttemptService.loginSucceeded(request.getUsername(), request.getIp());
        } catch (BadCredentialsException e) {
            // Фиксируем неудачную попытку
            loginAttemptService.loginFailed(request.getUsername(), request.getIp());
            return new ResponseEntity<>(new AppError(HttpStatus.UNAUTHORIZED.value(), "Неправильный логин или пароль"), HttpStatus.UNAUTHORIZED);
        }
        // Дальше всё как было
        UserDetails userDetails = userService.loadUserByUsername(request.getUsername());

        return userService.authUser(request);
    }


    @PostMapping("/register")
    public ResponseEntity<?> createNewUser(@RequestBody RegistrationRequest registrationRequest) {
        if (passwordEncoder.matches(registrationRequest.getPassword(), registrationRequest.getConfirmPassword())) {
            return new ResponseEntity<>(new AppError(HttpStatus.UNAUTHORIZED.value(), "Пароли не совпадают"), HttpStatus.UNAUTHORIZED);
        }
        if (userService.isUserExist(registrationRequest.getUsername())) {
            return new ResponseEntity<>(new AppError(HttpStatus.UNAUTHORIZED.value(), "Пользователь с указанным именем уже существует"), HttpStatus.UNAUTHORIZED);
        }
        return userService.createUser(registrationRequest);
    }

    @PostMapping("/signout")
    public void signout(HttpServletRequest request) {
        String username = jwtService.getUsername(jwtService.extractToker(request));
        userService.signout(username);
    }


}
