package com.billyclub.points.dto;

import com.billyclub.points.model.Event;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlayerDto {
    private Long id;
    private String name;
    private Integer quota;
    private LocalDateTime timeEntered;
    private Event event;
    private Boolean isWaiting;
    private Boolean isWithdrawal;
    private Integer scoreForEvent;
    private List<String> eagles;
    private List<String> birdies;
    private String team;
    private Integer total;
    private Integer adjustment;

}
