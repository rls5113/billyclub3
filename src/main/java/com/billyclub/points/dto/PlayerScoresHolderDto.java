package com.billyclub.points.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlayerScoresHolderDto {
    Long playerId;
    Integer scoreForEvent;
    String[] scats;
}
