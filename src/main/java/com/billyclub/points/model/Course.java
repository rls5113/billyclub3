package com.billyclub.points.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Data
@ToString
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "courses")
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "course_id_seq")
    @SequenceGenerator(name="course_id_seq", sequenceName = "course_seq", allocationSize = 1, initialValue = 1000)
    @Column(updatable = false, nullable = false)
    public Long id;
    @NotEmpty(message = "Course name should not be empty")
    private String name;
    @NotEmpty(message = "Course phone number should not be empty")
    private String phone;
    @NotEmpty(message = "Maximum Players per group, for this course, should not be empty")
    private Integer maxPlayersPerGroup;
    @JsonManagedReference
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "course")
    @ToString.Exclude
    private List<Event> events = new ArrayList<>();


}
