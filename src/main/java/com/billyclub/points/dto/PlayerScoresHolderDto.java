package com.billyclub.points.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PlayerScoresHolderDto {
    Long playerId;
    Integer scoreForEvent;
    String[] eagles;
    String[] birdies;
}
