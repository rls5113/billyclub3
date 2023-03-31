package com.billyclub.points.dto;

import com.billyclub.points.model.Event;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class CourseDto {
    public Long id;
    private String name;
    private String phone;
    private Integer maxPlayersPerGroup;
    private List<Event> events = new ArrayList<>();
}
