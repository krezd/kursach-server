package com.example.kursachserver.service;

import com.example.kursachserver.dto.response.ProcessStatusEvent;
import com.example.kursachserver.model.ProcessStatus;
import com.example.kursachserver.model.Session;
import com.example.kursachserver.repository.ProcessStatusRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProcessStatusService {
    private final ProcessStatusRepository processStatusRepository;
    private final ProcessService processService;
    private final SimpMessagingTemplate ws;

    public ProcessStatusService(ProcessStatusRepository processStatusRepository, ProcessService processService, SimpMessagingTemplate ws) {
        this.processStatusRepository = processStatusRepository;
        this.processService = processService;
        this.ws = ws;
    }

    public ResponseEntity<ProcessStatus> getProcessStatus(Long processId) {
        if (processStatusRepository.existsById(processId)) {
            ProcessStatus processStatus = processStatusRepository.findById(processId).orElse(null);
            return new ResponseEntity<>(processStatus, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    public ResponseEntity<List<ProcessStatus>> getAllProcessStatus() {
        return new ResponseEntity<>(processStatusRepository.findAll(), HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<?> createProcessStatus(ProcessStatus processStatus) {
        if (processStatusRepository.existsByName(processStatus.getName())) {
            return new ResponseEntity<>("Статус уже существует", HttpStatus.CONFLICT);
        }
        processStatusRepository.save(processStatus);
        ws.convertAndSend("/topic/process-status", new ProcessStatusEvent(ProcessStatusEvent.Operation.ADD, processStatus));
        return new ResponseEntity<>(processStatus, HttpStatus.CREATED);
    }

    @Transactional
    public ResponseEntity<?> updateProcessStatus(ProcessStatus processStatus) {
        if (processStatusRepository.existsById(processStatus.getId())) {
            processStatus.setId(processStatus.getId());
            processStatusRepository.save(processStatus);
            ws.convertAndSend("/topic/process-status", new ProcessStatusEvent(ProcessStatusEvent.Operation.UPDATE, processStatus));
            return new ResponseEntity<>(processStatus, HttpStatus.OK);
        }

        return createProcessStatus(processStatus);
    }

    @Transactional
    public ResponseEntity<ProcessStatus> deleteProcessStatus(Long processId) {
        if (processStatusRepository.existsById(processId)) {
            processStatusRepository.deleteById(processId);
            ws.convertAndSend("/topic/process-status", new ProcessStatusEvent(ProcessStatusEvent.Operation.DELETE, new ProcessStatus(processId, null, null)));
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @Transactional
    public ResponseEntity<?> updateListProcessStatus(List<ProcessStatus> processStatusList) {
        for (ProcessStatus processStatus : processStatusList) {
            updateProcessStatus(processStatus);
        }
        processService.refreshCache();
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
