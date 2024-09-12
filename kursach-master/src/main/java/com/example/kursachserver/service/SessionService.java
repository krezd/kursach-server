package com.example.kursachserver.service;

import com.example.kursachserver.model.Process;
import com.example.kursachserver.model.Session;
import com.example.kursachserver.repository.SessionRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class SessionService {
    @Autowired
    private SessionRepository sessionRepository;
    @Autowired
    private ProcessService processService;

    public ResponseEntity<?> getSession(UUID id){
        if(sessionRepository.existsById(id)){
            return new ResponseEntity<>(sessionRepository.findById(id), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    public ResponseEntity<?> getSessionByUser(Long userId){
        if(sessionRepository.existsByUserId(userId)){
            return new ResponseEntity<>(sessionRepository.findAllByUserId(userId), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    //TODO добавить HttpServletRequest для вытаскивания из токена userID;
    public ResponseEntity<?> createSession(Session session){
        if(sessionRepository.existsById(session.getId())){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        List<Process> processes = session.getProcesses();
        for(Process process1: processes){
            process1.setSession(session);
        }
        return new ResponseEntity<>(sessionRepository.save(session),HttpStatus.CREATED);
    }

    public ResponseEntity<List<?>> getAllSession(){
        return new ResponseEntity<>(sessionRepository.findAll(),HttpStatus.OK);
    }

    //TODO добавить HttpServletRequest для вытаскивания из токена userID;
     public ResponseEntity<?> updateSession(Session session){
        if(sessionRepository.existsById(session.getId())){
            Session session1 = sessionRepository.findById(session.getId()).orElse(null);
            List<Process> processes = session.getProcesses();
            for(Process process1: processes){
                process1.setSession(session);
            }
            session.setUserId(session1.getUserId());
            return new ResponseEntity<>(sessionRepository.save(session),HttpStatus.CREATED);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    @Transactional
    public ResponseEntity<?> deleteSessionByUserId(Long id){
        if(sessionRepository.existsByUserId(id)){
            sessionRepository.deleteAllByUserId(id);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    public ResponseEntity<?> deleteSessionById(UUID id){
        if(sessionRepository.existsById(id)){
            sessionRepository.deleteById(id);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }



}
