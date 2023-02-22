package com.billyclub.points.controller;

import com.billyclub.points.dto.EventDto;
import com.billyclub.points.model.Event;
import com.billyclub.points.service.EventService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
public class ThymeleafController {
    private final EventService eventService;

    public ThymeleafController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping("/events/current")
    public String listEvents(Model model){
        List<EventDto> events = eventService.findAllEvents();
        model.addAttribute("events", events);
        return "event-list";
    }
    @GetMapping("/events/{id}")
    public String getEventDetails(@PathVariable("id") Long id,  Model model){
        Event event = eventService.findById(id);
        model.addAttribute("event", eventService.convertEntityToDto(event));
        return "event-detail";
    }
    @GetMapping("/profile")
    public String getProfile(){
        return "profile";
    }
}