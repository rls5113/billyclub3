package com.billyclub.points.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

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
@Table(name = "events")
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "event_id_seq")
    @SequenceGenerator(name="event_id_seq", sequenceName = "event_seq", allocationSize = 1, initialValue = 1000)
    @Column(updatable = false, nullable = false)
    public Long id;

    @DateTimeFormat(pattern = "MM/dd/yyyy")
    @NotNull
    @Temporal(TemporalType.DATE)
    private LocalDate eventDate;
    @NotNull
    @DateTimeFormat(pattern = "hh:mm a")
    @Temporal(TemporalType.TIME)
    private LocalTime startTime;
    @NotNull
    private Integer numOfTimes;
    private Double customTeamMoney;
    private Double customScatMoney;

    private EventStatus status;
    @JsonManagedReference
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "event")
    private List<Player> players = new ArrayList<>();

    @ElementCollection
    private List<String> eventWinners = new ArrayList<>();
    @ElementCollection
    private List<String> scatWinners = new ArrayList<>();
    @ElementCollection
    private List<String> scatSummary = new ArrayList<>();

//    @JsonManagedReference
    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id")
    private Course course;

    @ManyToMany(fetch = FetchType.LAZY, cascade=CascadeType.ALL)
    @JoinTable(
            name="events_coveralls",
            joinColumns={@JoinColumn(name="EVENT_ID", referencedColumnName="ID")},
            inverseJoinColumns={@JoinColumn(name="COVERALL_ID", referencedColumnName="ID")})
    private List<Coverall> coveralls = new ArrayList<>();


    @Transient
    public boolean isAllScoresIn() {
        if(players.isEmpty()) return false;
        List<Player> haveNotPosted = players.stream()
                .filter(p -> !p.getIsWaiting() && p.getScoreForEvent() < 1)
                .collect(Collectors.toList());
        return haveNotPosted.isEmpty();
    }
    @Transient
    private Player emailRecipient;
    @Transient
    public boolean isDayOf() {
        if(this.eventDate == null || this.startTime == null) return false;
        boolean isUnderway = LocalDate.now().isEqual(eventDate) && LocalTime.now().isAfter(startTime);
        if(isUnderway && (status != EventStatus.STARTED && status != EventStatus.POSTING && status != EventStatus.COMPLETED)){
            this.setStatus(EventStatus.STARTED);
        }
        return isUnderway;
    }
    @Transient
    public int getNumberWaitlist() {
        List<Player> waitlist = players.stream()
                .filter(p -> p.getIsWaiting())
                .collect(Collectors.toList());
        return waitlist.size();
    }
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
        boolean timesFull = players.size() >= numOfTimes * course.getMaxPlayersPerGroup();
        boolean isIn = players.contains(player);
        if(timesFull && !isIn)
            player.setIsWaiting(Boolean.TRUE);
        else
            player.setIsWaiting(Boolean.FALSE);
        if(!isIn)
            players.add(player);

        player.setEvent(this);
    }
    public void removePlayer(Player player) {
            players.remove(player);
            player.setEvent(null);
    }
    public boolean isPlayerInEvent(String name) {
        for (Player p : players) {
            if(p.getName().equals(name)){
                return true;
            }
        }
        return false;
    }

    public Player getNextPlayerWaiting(){
        Player player = null;
        try {
            player = players.stream()
                    .filter(p -> p.getIsWaiting())
                    .min(Comparator.comparing(Player::getTimeEntered))
                    .get();
        }catch (Exception e) {
            System.out.println("No players waiting");
        }
        return player;
    }


}
