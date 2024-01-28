package com.billyclub.points.dto;

import com.billyclub.points.model.Coverall;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HoleDto {
    private Long id;
    private Integer num;
    private List<String> winners = new ArrayList<>();
    private LocalDate date;
    private Coverall coverall;
    private Boolean isWinner;

}
