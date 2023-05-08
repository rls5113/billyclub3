package com.billyclub.points.dto;

import com.billyclub.points.model.Course;
import com.billyclub.points.model.EventStatus;
import com.billyclub.points.model.Player;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventDto {

    private Long id;

    @DateTimeFormat(pattern = "MM/dd/yyyy")
    private LocalDate eventDate;
    @DateTimeFormat(pattern = "hh:mm a")
    private LocalTime startTime;
    private Integer numOfTimes;
    private List<Player> players;
    private EventStatus status;
    private boolean isDayOf;
    private boolean isAllScoresIn;
    private List<String> eventWinners;
    private List<String> scatWinners;
    private List<String> scatSummary;
    private Course course;
    private int numberWaitlist;

}
