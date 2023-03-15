package com.billyclub.points.dto;

import com.billyclub.points.model.Event;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlayerDto {
    private Long id;
    private String name;
    private Integer pointsToPull;
    private LocalDateTime timeEntered;
    private Event event;
    private Boolean isWaiting;
    private Integer scoreForEvent;
    private List<String> scats;

}
