package com.example.kursachserver.controller;

import com.example.kursachserver.model.BlockedSite;
import com.example.kursachserver.service.BlockedSiteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/blocked-sites")
public class BlockedSiteController {
    private final BlockedSiteService service;

    public BlockedSiteController(BlockedSiteService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<BlockedSite>> list() {
        return service.getAll();
    }

    @PostMapping
    public ResponseEntity<BlockedSite> create(@RequestBody BlockedSite dto) {
        return service.create(dto.getDomain(), dto.getReason());
    }

    @PutMapping("/{id}")
    public ResponseEntity<BlockedSite> update(@PathVariable Long id, @RequestBody BlockedSite dto) {
        return service.update(id, dto.getDomain(), dto.getReason());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        return service.delete(id);
    }
}
