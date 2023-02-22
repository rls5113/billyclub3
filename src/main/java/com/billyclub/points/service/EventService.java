package com.billyclub.points.service;

import com.billyclub.points.dto.EventDto;
import com.billyclub.points.model.Event;

import java.util.List;

public interface EventService extends IService<Event> {
    List<EventDto> findAllEvents();
    EventDto convertEntityToDto(Event event);
}
