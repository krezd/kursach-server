package com.example.kursachserver.controller;

import com.example.kursachserver.model.Process;
import com.example.kursachserver.service.ProcessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/process")
public class ProcessController {
    @Autowired
    private ProcessService processService;

    @GetMapping
    public ResponseEntity<?> getAll() {
        return processService.getAllProcess();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getProcess(@PathVariable Long id) {
        return processService.getProcessById(id);
    }

    @GetMapping("/")
    public ResponseEntity<?> getProcess(@RequestParam("sessionId") UUID id) {
        return processService.getProcessBySessionId(id);
    }

    @PostMapping("/{id}")
    public ResponseEntity<?> addProcess(@PathVariable UUID id, @RequestBody Process process) {
        return processService.addProcess(id, process);
    }

    @PostMapping("/{id}/list")
    public ResponseEntity<?> addProcesses(@PathVariable UUID id, @RequestBody List<Process> process) {
        return processService.addProcesses(id, process);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteById(@PathVariable Long id) {
        return processService.deleteProcessById(id);
    }

    @DeleteMapping("/")
    public ResponseEntity<?> deleteBySessionId(@RequestParam("sessionId") UUID id) {
        return processService.deleteProcessBySessionId(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateById(@PathVariable Long id,@RequestBody Process process) {
        return processService.updateProcessById(id, process);
    }

    @DeleteMapping()
    public ResponseEntity<?> deleteAll(){
        return processService.deleteAll();
    }


}
