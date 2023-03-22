package com.billyclub.points.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MultiPlayerScoresMapDto {

    private Map<String,PlayerScoresHolderDto> scores = new HashMap<>();
}
