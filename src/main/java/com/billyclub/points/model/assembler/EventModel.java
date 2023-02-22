package com.billyclub.points.model.assembler;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Builder(toBuilder = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventModel extends RepresentationModel<EventModel>  {
    private Long id;
    private LocalDate eventDate;
    private LocalTime startTime;
    private Integer numOfTimes;
    private List<PlayerModel> players;

}
