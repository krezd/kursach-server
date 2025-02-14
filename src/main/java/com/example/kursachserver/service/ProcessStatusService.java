package com.example.kursachserver.service;

import com.example.kursachserver.model.ProcessStatus;
import com.example.kursachserver.model.Session;
import com.example.kursachserver.repository.ProcessStatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProcessStatusService {
    @Autowired
    private ProcessStatusRepository processStatusRepository;
    @Autowired
    private ProcessService processService;

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

    public ResponseEntity<?> createProcessStatus(ProcessStatus processStatus) {
        if (processStatusRepository.existsByName(processStatus.getName())) {
            return new ResponseEntity<>("Статус уже существует",HttpStatus.CONFLICT);
        }
        processStatusRepository.save(processStatus);
        return new ResponseEntity<>(processStatus, HttpStatus.CREATED);
    }

    public ResponseEntity<?> updateProcessStatus(ProcessStatus processStatus) {
        if (processStatusRepository.existsById(processStatus.getId())) {
            processStatus.setId(processStatus.getId());
            processStatusRepository.save(processStatus);
            return new ResponseEntity<>(processStatus, HttpStatus.OK);
        }
        return createProcessStatus(processStatus);
    }

    public ResponseEntity<ProcessStatus> deleteProcessStatus(Long processId) {
        if (processStatusRepository.existsById(processId)) {
            processStatusRepository.deleteById(processId);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    public ResponseEntity<?> updateListProcessStatus(List<ProcessStatus> processStatusList) {
        for (ProcessStatus processStatus : processStatusList) {
            updateProcessStatus(processStatus);
        }
        processService.refreshCache();
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
