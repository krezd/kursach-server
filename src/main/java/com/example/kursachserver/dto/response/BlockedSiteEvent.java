package com.example.kursachserver.dto.response;

import com.example.kursachserver.model.BlockedSite;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BlockedSiteEvent {
    public enum Operation { ADD, UPDATE, DELETE }
    private Operation operation;
    private BlockedSite blockedSite;
}
