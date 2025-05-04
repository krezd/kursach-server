package com.example.kursachserver.service;

import com.example.kursachserver.dto.request.TrackingSettingsRequest;
import com.example.kursachserver.model.TrackingSettings;
import com.example.kursachserver.repository.TrackingSettingsRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class TrackingSettingsService {
    @Autowired
    private TrackingSettingsRepository trackingSettingsRepository;

    public TrackingSettings getSettings() {
        return trackingSettingsRepository.findById(1L)
                .orElseThrow(() -> new RuntimeException("Настройки не найдены"));
    }

    @Transactional
    public TrackingSettings updateSettings(TrackingSettingsRequest request) {
        TrackingSettings settings = trackingSettingsRepository.findById(1L)
                .orElse(new TrackingSettings());

        settings.setSendTimeMin(request.getSendTimeMin());
        settings.setScanTimeSec(request.getScanTimeSec());
        settings.setLastUpdated(LocalDateTime.now());
        return trackingSettingsRepository.save(settings);
    }
}
