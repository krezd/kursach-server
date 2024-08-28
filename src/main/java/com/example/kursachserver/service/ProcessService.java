package com.example.kursachserver.service;

import com.example.kursachserver.model.Process;
import com.example.kursachserver.model.Session;
import com.example.kursachserver.repository.ProcessRepository;
import com.example.kursachserver.repository.SessionRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ProcessService {
    @Autowired
    private ProcessRepository processRepository;
    @Autowired
    private SessionRepository sessionRepository;

    public ResponseEntity<?> addProcesses(UUID sessionId, List<Process> processes) {
        Session session = sessionRepository.findById(sessionId).orElse(null);
        if (session != null) {
            for (Process process : processes) {
                process.setSession(session);
            }
            processRepository.saveAll(processes);
            return new ResponseEntity<>(processes, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    public ResponseEntity<?> addProcess(UUID sessionId, Process process) {
        Session session = sessionRepository.findById(sessionId).orElse(null);
        if (session != null) {
            process.setSession(session);
            return new ResponseEntity<>(processRepository.save(process), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    public ResponseEntity<?> deleteProcessById(Long id) {
        if (processRepository.existsById(id)) {
            processRepository.deleteById(id);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @Transactional
    public ResponseEntity<?> deleteProcessBySessionId(UUID id) {
        if (sessionRepository.existsById(id)) {
            processRepository.deleteAllBySessionId(id);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    public ResponseEntity<?> getAllProcess(){

        return new ResponseEntity<>(processRepository.findAll(),HttpStatus.OK);
    }
    public ResponseEntity<?> getProcessById(Long id) {
        if (processRepository.existsById(id)) {;
            return new ResponseEntity<>(processRepository.findById(id), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    public ResponseEntity<?> getProcessBySessionId(UUID id) {
        if (sessionRepository.existsById(id)) {
            Session session = sessionRepository.findById(id).orElse(null);
            return new ResponseEntity<>(session.getProcesses(), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    public ResponseEntity<?> updateProcessById(Long id,Process process) {
        if (processRepository.existsById(id)) {
            Process oldProcess = processRepository.findById(id).orElse(null);
        process.setId(id);
        process.setSession(oldProcess.getSession());
        return new ResponseEntity<>(processRepository.save(process),HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    public ResponseEntity<?> deleteAll(){
        processRepository.deleteAll();
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
