package com.billyclub.points.dto;

import lombok.Data;

@Data
public class MultiUserValuesDto {
    String[] multiUsersCheckboxes;
    Integer scoreForEvent;
    String[] scats;
}
