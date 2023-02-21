package com.billyclub.points.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventDto {

    private Long id;

    @DateTimeFormat(pattern = "MM/dd/yyyy")
    private LocalDate eventDate;
    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime startTime;
    private Integer numOfTimes;
    private List<PlayerDto> players;
}
