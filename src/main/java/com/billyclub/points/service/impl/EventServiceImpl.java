package com.billyclub.points.service.impl;

import com.billyclub.points.exceptions.ResourceNotFoundException;
import com.billyclub.points.model.Event;
import com.billyclub.points.model.Player;
import com.billyclub.points.repository.EventRepository;
import com.billyclub.points.service.IService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class EventServiceImpl implements IService<Event> {
    @Autowired
    private final EventRepository repo;
    @Autowired
    private final PlayerServiceImpl playerService;

    public EventServiceImpl(EventRepository repo, PlayerServiceImpl playerService) {
        this.repo = repo;
        this.playerService = playerService;
    }


    @Override
    public List<Event> findAll() {
        return repo.findAll();
    }

    @Override
    public Event add(Event event) {
        return (Event) repo.save(event);
    }

    @Override
    public Event findById(Long eventId) {
        return repo.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event","id",eventId));
    }

    @Override
    @Transactional
    public Event update(Long eventId, Event event) {
        Event eventToEdit = findById(eventId);
        BeanUtils.copyProperties(event, eventToEdit);
        return repo.save(eventToEdit);
    }

    @Override
    public Event deleteById(Long eventId) {
        Event eventToDelete = findById(eventId);
        repo.deleteById(eventId);
        return eventToDelete;
    }

    @Override
    public Event save(Event entity) {
        return repo.save(entity);
    }

    @Transactional
    public Event addPlayerToEvent(Long eventId, Long playerId) {
        Event event = findById(eventId);
        Player player = playerService.findById(playerId);
        event.getPlayers().add(player);
        event.addPlayer(player);
        return repo.save(event);
    }
    public Event removePlayerFromEvent(Long eventId, Long playerId) {
        Event event = findById(eventId);
        Player player = playerService.findById(playerId);
        event.removePlayer(player);
        playerService.deleteById(playerId);
        return repo.save(event);
    }

}
