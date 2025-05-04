package com.example.kursachserver.dto.response;

import com.example.kursachserver.model.ProcessStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProcessStatusEvent {
    public enum Operation { ADD, UPDATE, DELETE }
    private Operation operation;
    private ProcessStatus processStatus;
}
