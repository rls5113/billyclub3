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
import java.util.List;

@Data
@ToString
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "events")
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "event_id_seq")
    @SequenceGenerator(name="event_id_seq", sequenceName = "event_seq", allocationSize = 1, initialValue = 1000)
    @Column(name="id",updatable = false, nullable = false)
    public Long id;

    private LocalDate eventDate;
    private LocalTime startTime;
    private Integer numOfTimes;
//    @ManyToMany(cascade = {CascadeType.ALL})
//    @JoinTable(name="event_player",
//            joinColumns = @JoinColumn(name = "event_id", referencedColumnName = "id"),
//            inverseJoinColumns = @JoinColumn(name = "player_id", referencedColumnName = "id"))
    @JsonManagedReference
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "event")
    List<Player> players = new ArrayList<>();

    public Event(Long id, LocalDate eventDate, LocalTime startTime, Integer numOfTimes) {
        this.id = id;
        this.eventDate = eventDate;
        this.startTime = startTime;
        this.numOfTimes = numOfTimes;
//        this.players = new ArrayList<>();
    }
    public void addPlayer(Player player) {
        if(player.getTimeEntered()==null)
            player.setTimeEntered(LocalDateTime.now());
        players.add(player);
        player.setEvent(this);
    }
    public void removePlayer(Player player) {
        players.remove(player);
        player.setEvent(null);
    }

}
