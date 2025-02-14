package com.example.kursachserver.repository;

import com.example.kursachserver.model.ProcessStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProcessStatusRepository extends JpaRepository<ProcessStatus, Long> {

    Optional<ProcessStatus> findByName(String name);

    boolean existsByName(String name);
}
