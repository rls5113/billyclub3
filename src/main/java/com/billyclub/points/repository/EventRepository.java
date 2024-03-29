package com.billyclub.points.repository;

import com.billyclub.points.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
public interface EventRepository extends JpaRepository<Event, Long> {

    @Query("select e from Event e where e.eventDate >= :CURRENT_DATE")
    List<Event> findEventsByEventDateGreaterThanEqual(@Param("CURRENT_DATE") LocalDate CURRENT_DATE);
    @Query("select e from Event e where e.eventDate < :CURRENT_DATE")
    List<Event> findEventsPast(@Param("CURRENT_DATE") LocalDate CURRENT_DATE);
    @Query("select e from Event e inner join Player p where p.event.id = e.id and p.name = :name and e.eventDate >= :eventDate and e.status in(0,7)")
    List<Event> findFutureEventsWithPlayerName(@Param("eventDate") LocalDate eventDate, @Param("name") String name);
}
