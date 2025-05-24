package com.example.kursachserver.repository;

import com.example.kursachserver.model.AllowedIp;
import com.example.kursachserver.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@RequestMapping
public interface AllowedIpRepository extends JpaRepository<AllowedIp, Long> {
    List<AllowedIp> findByUser(User user);
    boolean existsByUserAndIp(User user, String ip);
}
