package com.billyclub.points.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TeamsDto  {
    @NotEmpty
    @Size(min = 2)
    List<TeamDto> teams = new ArrayList<>();

    public void add(TeamDto teamDto) {
        this.teams.add(teamDto);
    }

}
