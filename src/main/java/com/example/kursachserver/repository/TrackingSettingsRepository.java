package com.example.kursachserver.repository;

import com.example.kursachserver.model.TrackingSettings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TrackingSettingsRepository extends JpaRepository<TrackingSettings, Long> {
}
