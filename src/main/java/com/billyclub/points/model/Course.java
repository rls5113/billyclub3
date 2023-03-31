package com.billyclub.points.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
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

    private String name;
    private String phone;
    private Integer maxPlayersPerGroup;
    @JsonManagedReference
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "course")
    private List<Event> events = new ArrayList<>();


}
