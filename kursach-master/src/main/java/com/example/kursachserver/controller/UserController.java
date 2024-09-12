package com.example.kursachserver.controller;

import com.example.kursachserver.dto.AppError;
import com.example.kursachserver.dto.request.RegistrationRequest;
import com.example.kursachserver.dto.request.UpdateUserRequest;
import com.example.kursachserver.service.JwtService;
import com.example.kursachserver.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    private final JwtService jwtService;

    private final PasswordEncoder passwordEncoder;

    @PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping()
    public ResponseEntity<?> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/getMyUserData")
    public ResponseEntity<?> getMyUserData(HttpServletRequest request){
        return userService.getUserById(jwtService.getUserId(jwtService.extractToker(request)));
    }
    @PreAuthorize("hasAnyRole('MANAGER','ADMIN')")
    @GetMapping("/{id}")
    public  ResponseEntity<?> getUserById(@PathVariable Long id){
        return userService.getUserById(id);
    }
    @PreAuthorize("hasAnyRole('MANAGER','ADMIN')")
    @GetMapping("/")
    public ResponseEntity<?> getUsersByPosition(@RequestParam("position") String position){
        return userService.getUsersByPosition(position);
    }
/* TODO дописать методы изменения данных пользователя, продумать процесс безопасного изменения админом
/    @PutMapping("/updateByWorker")
    public ResponseEntity<?> updateUserByWorker(HttpServletRequest request, @RequestBody UpdateUserRequest updateUserRequest){
        if (passwordEncoder.matches(updateUserRequest.getNewPassword(),updateUserRequest.getConfirmNewPassword())) {
            return new ResponseEntity<>(new AppError(HttpStatus.UNAUTHORIZED.value(), "Пароли не совпадают"), HttpStatus.UNAUTHORIZED);
        }
        return userService.updateUserByWorker(jwtService.getUserId(jwtService.extractToker(request)),updateUserRequest);

    }
*/
    @PreAuthorize("hasAnyRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUserById(@PathVariable Long id){
        return userService.deleteUserById(id);
    }

}
