package com.example.kursachserver.repository;

import com.example.kursachserver.model.BlockedSite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BlockedSiteRepository extends JpaRepository<BlockedSite, Long> {
    boolean existsByDomain(String domain);
    Optional<BlockedSite> findByDomain(String domain);
}