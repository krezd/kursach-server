package com.example.kursachserver.service;

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class LoginAttemptService {
    private final Map<String, LoginAttempts> attemptsCache = new ConcurrentHashMap<>();
    public static final int MAX_ATTEMPTS = 5;
    private static final long TIME_FRAME_SECONDS = 60; // 10 минут

    public boolean isBlocked(String username, String ip) {
        String key = username + ":" + ip;
        LoginAttempts info = attemptsCache.get(key);
        if (info == null) return false;
        cleanupOldAttempts(info);
        return info.timestamps.size() >= MAX_ATTEMPTS;
    }

    public void loginFailed(String username, String ip) {
        String key = username + ":" + ip;
        attemptsCache.putIfAbsent(key, new LoginAttempts());
        LoginAttempts info = attemptsCache.get(key);
        info.timestamps.add(Instant.now());
        cleanupOldAttempts(info);
    }

    public void loginSucceeded(String username, String ip) {
        String key = username + ":" + ip;
        attemptsCache.remove(key);
    }

    private void cleanupOldAttempts(LoginAttempts info) {
        Instant cutoff = Instant.now().minusSeconds(TIME_FRAME_SECONDS);
        info.timestamps.removeIf(ts -> ts.isBefore(cutoff));
    }

    static class LoginAttempts {
        List<Instant> timestamps = new ArrayList<>();
    }
}
