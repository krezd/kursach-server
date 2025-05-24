package com.example.kursachserver.repository;

import com.example.kursachserver.model.User;
import com.example.kursachserver.model.Violation;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ViolationRepository extends JpaRepository<Violation, Long>, JpaSpecificationExecutor<Violation> {
    List<Violation> findByUser(User user);
}

