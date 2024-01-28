package com.billyclub.points.service;

import com.billyclub.points.dto.CoverallDto;
import com.billyclub.points.model.Coverall;
import com.billyclub.points.model.Event;

import java.util.List;

public interface CoverallService extends IService<Coverall, CoverallDto> {
//    List<CoverallDto> findAllCoveralls();
    Coverall addPlayerToCoverall(Long coverallId, Long playerId);
//    Event removePlayerFromEvent(Long eventId, Long playerId);

    List<Coverall> findOpenCoveralls();

    Event processEvent(Event event);

//    Event calculateEventScoreboard(Long eventId);

//    List<EventDto> findPastEvents();

//    Event transfer(EventDto source, Event target) ;

//    List<Player> recalculateWaitingList(Event event, boolean addTeeTimes);

//    List<Player> getsMoneyBack(List<Player> eventPlayers);

//    Event savePickteams(Event event, TeamsDto teamsDto, List<String> moneybackList);

}
