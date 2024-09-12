package com.example.kursachserver.repository;

import com.example.kursachserver.model.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SessionRepository extends JpaRepository<Session, UUID> {
    List<Session> findAllByUserId(Long userId);

    boolean existsByUserId(Long id);
    void deleteAllByUserId(Long userId);
}
