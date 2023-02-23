package com.billyclub.points.controller;

import com.billyclub.points.dto.EventDto;
import com.billyclub.points.model.Event;
import com.billyclub.points.model.Player;
import com.billyclub.points.model.User;
import com.billyclub.points.service.EventService;
import com.billyclub.points.service.PlayerService;
import com.billyclub.points.service.UserService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
public class ThymeleafController {
    private final EventService eventService;
    private final UserService userService;
    private final PlayerService playerService;

    public ThymeleafController(EventService eventService, UserService userService, PlayerService playerService) {
        this.eventService = eventService;
        this.userService = userService;
        this.playerService = playerService;
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
        model.addAttribute("event", eventService.toDto(event));
        return "event-detail";
    }
    @PostMapping("/events/{eventId}/addMe")
    public String addMeToEvent(@PathVariable("eventId") Long eventId, Model model) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.findByUsername(username);
        Player player = playerService.create(user);
        Event event = eventService.addPlayerToEvent(eventId, player.getId() );

        model.addAttribute("event", eventService.toDto(event));
        return "event-detail";
    }
    @GetMapping("/profile")
    public String getProfile(){
        return "profile";
    }
}