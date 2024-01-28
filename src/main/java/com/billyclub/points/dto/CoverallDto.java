package com.billyclub.points.dto;

import com.billyclub.points.model.CoverallPlayer;
import com.billyclub.points.model.CoverallStatus;
import com.billyclub.points.model.Event;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.NumberFormat;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CoverallDto {

    public static int[] holes = {1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18};
    public static String[] colors_text_white = {"#e6194b", "#3cb44b", "#4363d8", "#f58231", "#911eb4", "#f032e6", "#008080", "#9a6324", "#800000", "#808000", "#000075", "#808080"};//, "#000000"};
    public static String[] colors_text_black = {"#ffe119", "#46f0f0", "#bcf60c", "#fabebe", "#e6beff", "#fffac8"};//, "#ffffff"};

    private Long id;

    @DateTimeFormat(pattern = "MM/dd/yyyy")
    @Temporal(TemporalType.DATE)
    @NotNull(message = "Start Date should not be empty")
    private LocalDate startdate;
    @DateTimeFormat(pattern = "MM/dd/yyyy")
    @Temporal(TemporalType.DATE)
    private LocalDate enddate;
    private List<CoverallPlayer> players;
    private CoverallStatus status;
    private List<String> coverallWinners;
    @NumberFormat(pattern="$ #0.00")
    private Double money;
    private List<Event> events;

    public List<Event> getEvents(){
        this.events.sort(Comparator.comparing(Event::getEventDate,Comparator.naturalOrder()));
        return this.events;
    }

}
