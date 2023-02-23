package com.billyclub.points.service.impl;

import com.billyclub.points.dto.EventDto;
import com.billyclub.points.dto.UserDto;
import com.billyclub.points.exceptions.ResourceNotFoundException;
import com.billyclub.points.model.Event;
import com.billyclub.points.model.Player;
import com.billyclub.points.model.User;
import com.billyclub.points.repository.EventRepository;
import com.billyclub.points.service.EventService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class EventServiceImpl implements EventService {
    @Autowired
    private final EventRepository eventRepository;
    @Autowired
    private final PlayerServiceImpl playerService;

    public EventServiceImpl(EventRepository eventRepository, PlayerServiceImpl playerService) {
        this.eventRepository = eventRepository;
        this.playerService = playerService;
    }


    @Override
    public List<Event> findAll() {
        return eventRepository.findAll();
    }

    @Override
    public Event add(Event event) {
        return (Event) eventRepository.save(event);
    }

    @Override
    public Event findById(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event","id",eventId));
    }

    @Override
    @Transactional
    public Event update(Long eventId, Event event) {
        Event eventToEdit = findById(eventId);
        BeanUtils.copyProperties(event, eventToEdit);
        return eventRepository.save(eventToEdit);
    }

    @Override
    public Event deleteById(Long eventId) {
        Event eventToDelete = findById(eventId);
        eventRepository.deleteById(eventId);
        return eventToDelete;
    }

    @Override
    public Event save(Event entity) {
        return eventRepository.save(entity);
    }

    @Transactional
    @Override
    public Event addPlayerToEvent(Long eventId, Long playerId) {
        Event event = findById(eventId);
        Player player = playerService.findById(playerId);
        event.getPlayers().add(player);
        event.addPlayer(player);
        return eventRepository.save(event);
    }
    @Override
    public Event removePlayerFromEvent(Long eventId, Long playerId) {
        Event event = findById(eventId);
        Player player = playerService.findById(playerId);
        event.removePlayer(player);
        playerService.deleteById(playerId);
        return eventRepository.save(event);
    }
    @Override
    public List<EventDto> findAllEvents() {
        List<Event> events = eventRepository.findAll();
        List<EventDto> list = events.stream().map((event)-> toDto(event))
                .collect(Collectors.toList());
        list.sort((e1, e2) -> (e1.getEventDate().compareTo(e2.getEventDate())));
//        Collections.sort(list, (e1, e2) -> (e1.getEventDate().compareTo(e2.getEventDate())));
        return list;
    }

    @Override
    public EventDto toDto(Event event) {
        EventDto eventDto = new EventDto();
        BeanUtils.copyProperties(event, eventDto);
        return eventDto;
    }

    @Override
    public Event toEntity(EventDto dto) {
        Event event = new Event();
        BeanUtils.copyProperties(event, dto);
        return event;
    }

}
