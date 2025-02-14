package com.example.kursachserver.service;

import com.example.kursachserver.dto.AppError;
import com.example.kursachserver.dto.request.RegistrationRequest;
import com.example.kursachserver.dto.request.UpdateUserByAdminRequest;
import com.example.kursachserver.dto.request.UpdateUserRequest;
import com.example.kursachserver.dto.response.AuthResponse;
import com.example.kursachserver.dto.response.UserResponse;
import com.example.kursachserver.dto.response.UserResponseGenerator;
import com.example.kursachserver.enumModel.Role;
import com.example.kursachserver.model.User;
import com.example.kursachserver.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class UserService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private SessionService sessionService;

    @Transactional
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException(String.format("Пользователь '%s' не найден", username)));
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .roles(String.valueOf(user.getRole()))
                .build();

    }

    //TODO проверить работу метода
    public Boolean isUserExist(String username) {
        return userRepository.existsByUsername(username);
    }

    public Boolean isAuthStatus(String username) {
        User user = userRepository.findByUsername(username).orElse(null);
        if (user != null && user.getAuthStatus()) {
            return true;
        }
        return false;
    }

    public ResponseEntity<?> authUser(String username) {
        User user = userRepository.findByUsername(username).orElse(null);
        user.setAuthStatus(true);
        userRepository.save(user);
        return new ResponseEntity<>(new AuthResponse(jwtService.generateJwt(user.getId(), user.getUsername()), user.getRole()), HttpStatus.OK);
    }

    public void signout(String username) {
        User user = userRepository.findByUsername(username).orElse(null);
        if (user != null) {
            user.setAuthStatus(false);
            userRepository.save(user);
        }
    }

    public ResponseEntity<?> createUser(RegistrationRequest request) {
        User user = User.builder()
                .name(request.getName())
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .position(request.getPosition())
                .role(request.getRole())
                .createUserDate(LocalDateTime.now())
                .authStatus(true)
                .build();
        userRepository.save(user);

        return new ResponseEntity<>(new AuthResponse(jwtService.generateJwt(user.getId(), user.getUsername()), user.getRole()), HttpStatus.CREATED);
    }

    public ResponseEntity<?> getAllUsers() {
        List<User> users = userRepository.findAll();
        List<UserResponse> userResponses = new ArrayList<>();
        for(User user : users) {
            userResponses.add(new UserResponse(user));
        }
        return new ResponseEntity<>(userResponses, HttpStatus.OK);
    }

    public ResponseEntity<?> getUserById(Long id) {
        if (userRepository.existsById(id)) {
            return new ResponseEntity<>(new UserResponse(Objects.requireNonNull(userRepository.findById(id).orElse(null))), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    //Получение пользователей по компании
    public ResponseEntity<?> getUsersByPosition(String position) {
        return new ResponseEntity<>(new UserResponseGenerator(userRepository.findAllByPosition(position)), HttpStatus.OK);
    }


    public ResponseEntity<?> deleteUserById(Long id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            sessionService.deleteSessionByUserId(id); //Удаление связанных с пользователем сессий
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    //TODO написать методы удаления всех пользователей по компании и просто всех пользователей
        /*
        public ResponseEntity<?> deleteUsersByCompanyName(String companyName) {
            if (userRepository.existsById(id)) {
                userRepository.deleteById(id);
                //Удаление связанных с пользователем сессий
                sessionService.deleteSessionByUserId(id);
                return new ResponseEntity<>(HttpStatus.OK);
            }
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    */

    //Обновление пользователя самим пользователем
    //Пользователь может поменять лишь имя и пароль
    //TODO дописать методы обновления данных пользователя после написания клиентской части

    public ResponseEntity<?> updateUserByWorker(Long userId, UpdateUserRequest request) {
        if (userRepository.existsById(userId)) {
            User user = userRepository.findById(userId).orElse(null);
            if (passwordEncoder.matches(request.getOldPassword(), user.getPassword()) && user.getUsername().equals(request.getUsername())) {
                user.setName(request.getName());
                if (request.getNewPassword() != null) {
                    user.setPassword(passwordEncoder.encode(request.getNewPassword()));
                }
                userRepository.save(user);
                return new ResponseEntity<>(new AuthResponse(jwtService.generateJwt(user.getId(), user.getUsername()), user.getRole()), HttpStatus.CREATED);
            } else {
                return new ResponseEntity<>(new AppError(HttpStatus.UNAUTHORIZED.value(), "Логин или пароль не совпадает"), HttpStatus.UNAUTHORIZED);
            }

        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    public ResponseEntity<?> updateUserByAdmin(UpdateUserByAdminRequest request) {
        if (userRepository.existsById(request.getId())) {
            User user = userRepository.findById(request.getId()).orElse(null);
            user.setPosition(!Objects.equals(request.getPosition(), "") ? request.getPosition() : user.getPosition());
            user.setRole(!Objects.equals(request.getRole().toString(), "") ? request.getRole() : user.getRole());
            userRepository.save(user);
            return new ResponseEntity<>(new UserResponse(user), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }


}
