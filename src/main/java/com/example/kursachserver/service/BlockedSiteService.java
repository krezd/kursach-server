package com.example.kursachserver.service;

import com.example.kursachserver.dto.response.BlockedSiteEvent;
import com.example.kursachserver.model.BlockedSite;
import com.example.kursachserver.repository.BlockedSiteRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class BlockedSiteService {
    private final BlockedSiteRepository repo;
    private final SimpMessagingTemplate ws;

    public BlockedSiteService(BlockedSiteRepository repo,
                                  SimpMessagingTemplate ws) {
        this.repo = repo;
        this.ws   = ws;
    }


    public ResponseEntity<List<BlockedSite>> getAll() {
        return new ResponseEntity<>(repo.findAll(), HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<BlockedSite> create(String domain, String reason) {
        if (repo.findByDomain(domain).isPresent()) {
            throw new IllegalArgumentException("Domain already blocked: " + domain);
        }
        BlockedSite bs = new BlockedSite();
        bs.setDomain(domain);
        bs.setReason(reason);
        bs.setUpdatedAt(Instant.now());
        BlockedSite saved = repo.save(bs);
        ws.convertAndSend("/topic/blocked-sites", new BlockedSiteEvent(BlockedSiteEvent.Operation.ADD, saved));
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    @Transactional
    public ResponseEntity<BlockedSite> update(Long id, String domain, String reason) {
        BlockedSite bs = repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No BlockedSite with id " + id));
        bs.setDomain(domain);
        bs.setReason(reason);
        bs.setUpdatedAt(Instant.now());
        BlockedSite updated = repo.save(bs);
        ws.convertAndSend("/topic/blocked-sites", new BlockedSiteEvent(BlockedSiteEvent.Operation.UPDATE, updated));
        return new ResponseEntity<>(updated, HttpStatus.OK);
    }


    @Transactional
    public ResponseEntity<?> delete(Long id) {
        BlockedSite bs = repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No BlockedSite with id " + id));
        repo.delete(bs);
        ws.convertAndSend("/topic/blocked-sites", new BlockedSiteEvent(BlockedSiteEvent.Operation.DELETE, bs));
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
