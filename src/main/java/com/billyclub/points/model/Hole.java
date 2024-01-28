package com.billyclub.points.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "holes")
public class Hole {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hole_id_seq")
    @SequenceGenerator(name="hole_id_seq", sequenceName = "hole_seq", allocationSize = 1, initialValue = 1000)
    @Column(name="id",updatable = false, nullable = false)
    private Long id;
    @NotNull(message = "Name is required.")
    private Integer num;

    private List<String> winners = new ArrayList<>();

    @DateTimeFormat(pattern = "MM/dd/yyyy")
    @NotNull
    @Temporal(TemporalType.DATE)
    private LocalDate date;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
            @JoinColumn(name = "coverall_id")
    private Coverall coverall;
    @Value("false")
    private Boolean isWinner;


}
