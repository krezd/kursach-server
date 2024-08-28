package com.example.kursachserver.repository;

import com.example.kursachserver.model.Process;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ProcessRepository extends JpaRepository<Process,Long> {
    void deleteAllBySessionId(UUID id);
}
