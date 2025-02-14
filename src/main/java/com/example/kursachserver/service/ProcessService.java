package com.example.kursachserver.service;

import com.example.kursachserver.enumModel.Status;
import com.example.kursachserver.model.Process;
import com.example.kursachserver.model.ProcessStatus;
import com.example.kursachserver.model.ProcessUsage;
import com.example.kursachserver.model.Session;
import com.example.kursachserver.repository.ProcessRepository;
import com.example.kursachserver.repository.ProcessStatusRepository;
import com.example.kursachserver.repository.SessionRepository;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@EnableScheduling
public class ProcessService {
    @Autowired
    private ProcessRepository processRepository;
    @Autowired
    private SessionRepository sessionRepository;
    @Autowired
    private ProcessStatusRepository processStatusRepository;
    private final List<ProcessStatus> processStatusList = new ArrayList<>();

    @PostConstruct
    public void initProcessStatusList() {
        this.processStatusList.addAll(processStatusRepository.findAll());
    }


    @Scheduled(fixedRate = 60000 * 10)
    public void refreshCache() {
        List<ProcessStatus> updatedStatuses = processStatusRepository.findAll();
        synchronized (processStatusList) {
            processStatusList.clear();
            processStatusList.addAll(updatedStatuses);
        }
    }

    private ProcessStatus getProcessStatus(String name) {
        return processStatusList.stream()
                .filter(processStatus -> processStatus.getName().equals(name))
                .findFirst()
                .orElse(null);
    }



    public ResponseEntity<?> addProcesses(UUID sessionId, List<Process> processes) {
        // Проверка существования сессии
        Session session = sessionRepository.findById(sessionId).orElse(null);
        if (session != null) {
            // Загружаем существующие процессы для текущей сессии
            List<Process> existingProcesses = processRepository.findAllBySessionId(sessionId);

            for (Process incomingProcess : processes) {
                // Ищем процесс с таким же именем в текущей сессии
                Process matchingProcess = existingProcesses.stream()
                        .filter(p -> p.getProcessName().equals(incomingProcess.getProcessName()))
                        .findFirst()
                        .orElse(null);

                // Получаем или создаем ProcessStatus
                ProcessStatus processStatus = getProcessStatus(incomingProcess.getProcessName());
                if (processStatus == null) {
                    processStatus = new ProcessStatus();
                    processStatus.setName(incomingProcess.getProcessName());
                    processStatus.setStatus(Status.NEUTRAL);
                    processStatusRepository.save(processStatus);
                    synchronized (processStatusList) {
                        processStatusList.add(processStatus);
                    }
                }

                List<ProcessUsage> newUsage = incomingProcess.getUsageTimes();
                if (matchingProcess != null) {
                    // Если процесс уже существует, добавляем новое использование времени
                    newUsage.forEach(processUsage -> processUsage.setProcess(matchingProcess));
                    matchingProcess.getUsageTimes().addAll(newUsage);
                } else {
                    // Если процесс не существует, создаем новый процесс и добавляем использование
                    Process newProcess = new Process();
                    newProcess.setProcessName(incomingProcess.getProcessName());
                    newProcess.setProcessStatus(processStatus);
                    newProcess.setSession(session);

                    newUsage.forEach(processUsage -> processUsage.setProcess(newProcess));
                    newProcess.getUsageTimes().addAll(newUsage);

                    existingProcesses.add(newProcess);  // Добавляем новый процесс в список для последующего сохранения
                }
            }

            processRepository.saveAll(existingProcesses);  // Сохраняем все изменения в БД
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

    public ResponseEntity<?> getAllProcess() {

        return new ResponseEntity<>(processRepository.findAll(), HttpStatus.OK);
    }

    public ResponseEntity<?> getProcessById(Long id) {
        if (processRepository.existsById(id)) {
            ;
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

    public ResponseEntity<?> updateProcessById(Long id, Process process) {
        if (processRepository.existsById(id)) {
            Process oldProcess = processRepository.findById(id).orElse(null);
            process.setId(id);
            process.setSession(oldProcess.getSession());
            return new ResponseEntity<>(processRepository.save(process), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    public ResponseEntity<?> deleteAll() {
        processRepository.deleteAll();
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
