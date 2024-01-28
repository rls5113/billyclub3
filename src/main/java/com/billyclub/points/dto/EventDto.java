package com.billyclub.points.dto;

import com.billyclub.points.model.Course;
import com.billyclub.points.model.Coverall;
import com.billyclub.points.model.EventStatus;
import com.billyclub.points.model.Player;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.*;
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
    @NotNull(message = "Event Date should not be empty")
//    @FutureOrPresent(message="Event Date cannot be in the past")
    private LocalDate eventDate;
    @DateTimeFormat(pattern = "hh:mm a")
    @Temporal(TemporalType.TIME)
    @NotNull(message = "Start Time should not be empty")
    private LocalTime startTime;
    @Min(message = "Must be greater than 0", value = 1)
    @Max(message = "Must be value 1 to 7", value = 7)
    @Digits(message = "Numbers only, please", integer = 1, fraction = 0)
    @NotNull(message = "Number of Tee times should not be empty")
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
    private Double customTeamMoney;
    private Double customScatMoney;
    private List<Coverall> coveralls;

}
