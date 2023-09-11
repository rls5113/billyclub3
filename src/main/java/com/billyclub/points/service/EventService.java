package com.billyclub.points.service;

import com.billyclub.points.dto.EventDto;
import com.billyclub.points.model.Event;
import com.billyclub.points.model.Player;

import java.util.List;

public interface EventService extends IService<Event, EventDto> {
    List<EventDto> findAllEvents();
    Event addPlayerToEvent(Long eventId, Long playerId);
    Event removePlayerFromEvent(Long eventId, Long playerId);

    List<EventDto> findOpenEvents();

    Event calculateEventScoreboard(Long eventId);

    List<EventDto> findPastEvents();

    Event transfer(EventDto source, Event target) ;

    List<Player> recalculateWaitingList(Event event, boolean addTeeTimes);

    List<Player> getsMoneyBack(List<Player> eventPlayers);

}
