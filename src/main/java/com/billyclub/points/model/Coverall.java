package com.billyclub.points.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Data
@ToString
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "coveralls")
public class Coverall {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "coverall_id_seq")
    @SequenceGenerator(name="coverall_id_seq", sequenceName = "coverall_seq", allocationSize = 1, initialValue = 1000)
    @Column(updatable = false, nullable = false)
    public Long id;

    @DateTimeFormat(pattern = "MM/dd/yyyy")
    @NotNull
    @Temporal(TemporalType.DATE)
    private LocalDate startdate;
    @DateTimeFormat(pattern = "MM/dd/yyyy")
    @Temporal(TemporalType.DATE)
    private LocalDate enddate;
    private double money;
    private CoverallStatus status;
    @JsonManagedReference
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "coverall")
    private List<CoverallPlayer> players = new ArrayList<>();

    @JsonManagedReference
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "coverall")
    private List<Hole> holes = new ArrayList<>();

    @ElementCollection
    private List<String> coverallWinners = new ArrayList<>();

    @ManyToMany(mappedBy="coveralls")
    private List<Event> events;
    public List<Event> getEvents(){
        this.events.sort(Comparator.comparing(Event::getEventDate,Comparator.naturalOrder()));
        return this.events;
    }
    public Coverall(Long id, LocalDate date) {
        this.id = id;
        this.startdate = date;
    }
    public void addPlayer(CoverallPlayer player) {
        boolean isIn = players.contains(player);
        if(!isIn)
            players.add(player);

        player.setCoverall(this);
    }
    public void removePlayer(Player player) {
            players.remove(player);
            player.setEvent(null);
    }
    public boolean isPlayerInCoverall(String name) {
        for (CoverallPlayer p : players) {
            if(p.getName().equals(name)){
                return true;
            }
        }
        return false;
    }



}
