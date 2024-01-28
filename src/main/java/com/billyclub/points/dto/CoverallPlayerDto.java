package com.billyclub.points.dto;

import com.billyclub.points.model.Coverall;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CoverallPlayerDto {
    private Long id;
    private String name;

    private List<String> eagles = new ArrayList<>();
    private List<String> birdies = new ArrayList<>();

    private Coverall coverall;
    private boolean firstPlay;

}
