package com.example.kursachserver.controller;

import com.example.kursachserver.dto.AppError;
import com.example.kursachserver.dto.request.AuthRequest;
import com.example.kursachserver.dto.request.RegistrationRequest;
import com.example.kursachserver.model.User;
import com.example.kursachserver.service.JwtService;
import com.example.kursachserver.service.UserService;
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

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class AuthController {

    private final PasswordEncoder passwordEncoder;
    private final UserService userService;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @PostMapping("/auth")
    public ResponseEntity<?> authenticate(@RequestBody AuthRequest request) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        } catch (BadCredentialsException e) {
            return new ResponseEntity<>(new AppError(HttpStatus.UNAUTHORIZED.value(), "Неправильный логин или пароль"), HttpStatus.UNAUTHORIZED);
        }
        UserDetails userDetails = userService.loadUserByUsername(request.getUsername());

        return userService.authUser(request.getUsername());
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
