package com.billyclub.points.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TeamDto {
    public static String[] cards = {"AS.png","2H.png","3C.png","4D.png","5S.png","6H.png","7C.png","8D.png","9S.png","10H.png","JC.png","QD.png","KS.png"};
    String name;

    public TeamDto(String name, Integer score) {
        this.name = name;
        this.score = score;
        this.team = new ArrayList<>();
    }

    @NotEmpty
    @NotNull
    @Size(min = 2)
    List<String> team;
   Integer score;
}
