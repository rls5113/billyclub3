package com.billyclub.points.dto;

import jakarta.validation.constraints.NotNull;
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
    @NotNull(message = "Players score for this event is required.")
    Integer scoreForEvent;
    Boolean withdrawal;
    String[] eagles;
    String[] birdies;
}
