package com.billyclub.points.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Table(name = "players")
public class Player {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "player_id_seq")
    @SequenceGenerator(name="player_id_seq", sequenceName = "player_seq", allocationSize = 1, initialValue = 1000)
    @Column(name="id",updatable = false, nullable = false)
    private Long id;
    @NotNull(message = "Name is required.")
    private String name;
    @NotNull(message = "Quota for this event is required.")
    private Integer quota;
    private Integer scoreForEvent;
    private Integer total;
    private Integer adjustment;

    @NotNull(message = "Time of entry for this player is required.")
    @Column(columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP", updatable = false)
    private LocalDateTime timeEntered;

    @Value("false")
    private Boolean isWaiting;
    @Value("false")
    private Boolean isWithdrawal;
    private String team;

    private List<String> eagles;
    private List<String> birdies;

    @JsonBackReference
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
            @JoinColumn(name = "event_id")
    private Event event;


}
