package com.example.kursachserver.controller;

import com.example.kursachserver.dto.request.TrackingSettingsRequest;
import com.example.kursachserver.model.TrackingSettings;
import com.example.kursachserver.service.TrackingSettingsService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/settings")
public class TrackingSettingsController {
    @Autowired
    private TrackingSettingsService settingsService;

    @PreAuthorize("hasAnyRole('WORKER','ADMIN')")
    @GetMapping
    public ResponseEntity<TrackingSettings> getSettings() {
        TrackingSettings settings = settingsService.getSettings();
        return ResponseEntity.ok(settings);
    }
    @PreAuthorize("hasAnyRole('ADMIN')")
    @PutMapping
    public ResponseEntity<TrackingSettings> updateSettings(@RequestBody TrackingSettingsRequest request) {
        TrackingSettings updatedSettings = settingsService.updateSettings(request);
        return ResponseEntity.ok(updatedSettings);
    }
}
