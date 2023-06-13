package com.billyclub.points.dto;

import com.billyclub.points.model.Course;
import com.billyclub.points.model.EventStatus;
import com.billyclub.points.model.Player;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
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
    @Temporal(TemporalType.DATE)
    @NotNull(message = "Event Date is required")
    @FutureOrPresent(message="Event Date cannot be in the past")
    private LocalDate eventDate;
    @DateTimeFormat(pattern = "hh:mm a")
    @Temporal(TemporalType.TIME)
    @NotNull(message = "Start Time is required")
    private LocalTime startTime;
    @NotNull(message = "Number of Tee times is required")
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
