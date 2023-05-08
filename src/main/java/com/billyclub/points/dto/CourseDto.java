package com.billyclub.points.dto;

import com.billyclub.points.model.Event;
import jakarta.annotation.Nonnull;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class CourseDto {
    public Long id;
    @NotEmpty(message = "Course name should not be empty")
    private String name;
    @NotEmpty(message = "Course phone number should not be empty")
    private String phone;
    @NotEmpty(message = "Course address should not be empty")
    private String address;
    @NotNull(message = "Max Players Per group is required for waiting list operations.")
    private Integer maxPlayersPerGroup;
    private List<Event> events = new ArrayList<>();
//    private Integer order;
}
