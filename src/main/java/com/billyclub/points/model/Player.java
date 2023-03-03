package com.billyclub.points.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@Entity
@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "players")
public class Player {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "player_id_seq")
    @SequenceGenerator(name="player_id_seq", sequenceName = "player_seq", allocationSize = 1, initialValue = 1000)
    @Column(name="id",updatable = false, nullable = false)
    public Long id;
    @NotNull(message = "Name is required.")
    private String name;
    @NotNull(message = "Points to pull for this event is required.")
    private Integer pointsToPull;

    private Integer pointsThisEvent;

    @NotNull(message = "Time of entry for this player is required.")
    @Column(name = "timeEntered", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime timeEntered;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
            @JoinColumn(name = "event_id")
    private Event event;

    public Player(String name, Integer pointsToPull, Integer pointsThisEvent) {
        this.name = name;
        this.pointsToPull = pointsToPull;
        this.pointsThisEvent = pointsThisEvent;
        this.timeEntered = LocalDateTime.now();
    }

    public Player(long id, String name, Integer pointsToPull, Integer pointsThisEvent, LocalDateTime now) {
        this.id = id;
        this.name = name;
        this.pointsToPull = pointsToPull;
        this.pointsThisEvent = pointsThisEvent;
        this.timeEntered = now;
    }

}
