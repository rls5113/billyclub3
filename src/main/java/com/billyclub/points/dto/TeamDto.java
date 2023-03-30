package com.billyclub.points.dto;

import com.billyclub.points.model.Player;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TeamDto {
    String name;

    public TeamDto(String name, Integer score) {
        this.name = name;
        this.score = score;
    }

    List<String> team = new ArrayList<>();
    Integer score;
}
