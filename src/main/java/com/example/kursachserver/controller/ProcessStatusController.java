package com.example.kursachserver.controller;

import com.example.kursachserver.model.ProcessStatus;
import com.example.kursachserver.service.ProcessStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/processStatus")
@Controller
public class ProcessStatusController {
    @Autowired
    private ProcessStatusService processStatusService;

    @GetMapping("/{id}")
    public ResponseEntity<ProcessStatus> getProcessStatus(@PathVariable Long id) {
        return processStatusService.getProcessStatus(id);
    }

    @GetMapping("/all")
    public ResponseEntity<List<ProcessStatus>> getAllProcessStatus() {
        return processStatusService.getAllProcessStatus();
    }

    @PostMapping()
    public ResponseEntity<?> createProcessStatus(@RequestBody ProcessStatus processStatus) {
        return processStatusService.createProcessStatus(processStatus);
    }

    @PutMapping()
    public ResponseEntity<?> updateProcessStatus(@RequestBody ProcessStatus processStatus) {
        return processStatusService.updateProcessStatus(processStatus);
    }

    @PutMapping("/all")
    public ResponseEntity<?> updateAllProcessStatus(@RequestBody List<ProcessStatus> processStatus) {
        return processStatusService.updateListProcessStatus(processStatus);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProcessStatus(@PathVariable Long id) {
        return processStatusService.deleteProcessStatus(id);
    }
}
