package com.example.kursachserver.controller;

import com.example.kursachserver.dto.request.AllowedIpUpdateRequest;
import com.example.kursachserver.model.AllowedIp;
import com.example.kursachserver.model.User;
import com.example.kursachserver.repository.AllowedIpRepository;
import com.example.kursachserver.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/allowed-ips")
public class AllowedIpController {
    private final AllowedIpRepository repo;
    private final UserRepository userRepo;

    public AllowedIpController(AllowedIpRepository repo, UserRepository userRepo) {
        this.repo = repo;
        this.userRepo = userRepo;
    }

    @PostMapping
    public AllowedIp add(@RequestParam Long userId, @RequestParam String ip) {
        User user = userRepo.findById(userId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        AllowedIp entry = new AllowedIp(null, user, ip);
        return repo.save(entry);
    }

    @GetMapping("/{userId}")
    public List<AllowedIp> list(@PathVariable Long userId) {
        User user = new User();
        user.setId(userId);
        return repo.findByUser(user);
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateAllowedIps(@RequestBody AllowedIpUpdateRequest req) {
        User user = userRepo.findById(req.getUserId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователь не найден"));

        // Удаляем все старые IP пользователя
        repo.deleteAll(repo.findByUser(user));

        // Добавляем новые
        for (String ip : req.getIps()) {
            if (ip == null || ip.isBlank()) continue;
            AllowedIp allowedIp = new AllowedIp(null, user, ip.trim());
            repo.save(allowedIp);
        }

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        repo.deleteById(id);
    }
}
