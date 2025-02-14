package com.example.kursachserver.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrackingSettingsRequest {
    @NonNull
    private int sendTimeMin;
    @NonNull
    private int scanTimeSec;
}
