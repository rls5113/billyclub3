package com.billyclub.points.dto;

import com.billyclub.points.model.Event;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlayerDto {
    private Long id;
    private String name;
    private Integer pointsToPull;
    private Integer pointsThisEvent;
    private LocalDateTime timeEntered;
    private Event event;

}
