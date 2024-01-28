package com.billyclub.points.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "coverallplayers")
public class CoverallPlayer {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "coverallplayer_id_seq")
    @SequenceGenerator(name="coverallplayer_id_seq", sequenceName = "coverallplayer_seq", allocationSize = 1, initialValue = 1000)
    @Column(name="id",updatable = false, nullable = false)
    private Long id;

    @NotNull(message = "Name is required.")
    private String name;

    @ElementCollection
    private List<String> eagles = new ArrayList<>();
    @ElementCollection
    private List<String> birdies = new ArrayList<>();

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
            @JoinColumn(name = "coverall_id")
    private Coverall coverall;
    @Value("true")
    private Boolean isFirstPlay;


}
