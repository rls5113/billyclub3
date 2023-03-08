package com.billyclub.points.controller;

import com.billyclub.points.dto.EventDto;
import com.billyclub.points.dto.FormValuesDto;
import com.billyclub.points.dto.UserDto;
import com.billyclub.points.model.Event;
import com.billyclub.points.model.Player;
import com.billyclub.points.model.User;
import com.billyclub.points.service.EventService;
import com.billyclub.points.service.PlayerService;
import com.billyclub.points.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;
import java.util.stream.Collectors;

@Controller
public class ThymeleafController {
    private final EventService eventService;
    private final UserService userService;
    private final PlayerService playerService;
    private static final Logger LOGGER = LoggerFactory.getLogger(ThymeleafController.class);
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
//        String username = SecurityContextHolder.getContext().getAuthentication().getName();
//        User loggedInUser = userService.findByUsername(username);
        model.addAttribute("loginUser", getLoggedInUser());
        Event event = eventService.findById(id);
        model.addAttribute("event", eventService.toDto(event));

        List<Player> players = event.getPlayers();
        //create a list of player names
        List<String> names = players.stream()
                .map( p -> p.getName())
                .collect(Collectors.toList());
        model.addAttribute("names",names);

        //create list of users not in this event
        List<UserDto> eventUsers = userService.findAllUsers();
        List<UserDto> filtered = eventUsers.stream().filter( u -> players.stream()
                .anyMatch( p ->
                    p.getName().equals(u.getName())))
                        .collect(Collectors.toList());

        List<UserDto> others = userService.findAllUsers();
        others.removeAll(filtered);
        model.addAttribute("userPickList", others);
        model.addAttribute("selectedUsers", new FormValuesDto());

        return "event-detail";
    }
    @PostMapping("/events/{eventId}/addMe")
    public String addMeToEvent(@PathVariable("eventId") Long eventId, Model model) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.findByUsername(username);
        Event event = eventService.findById(eventId);
        if(!event.isPlayerInEvent(user.getName())){
            Player newPlayer = playerService.create(user);
            event = eventService.addPlayerToEvent(eventId, newPlayer.getId() );
        }

        model.addAttribute("event", eventService.toDto(event));
        model.addAttribute("players", event.getPlayers().stream()
                .map((p)-> playerService.toDto(p))
                .collect(Collectors.toList()));
        return "redirect:/events/"+eventId;
    }

    @GetMapping("/events/{eventId}/removeMe/{playerId}")
    public String removeMeFromEvent(@PathVariable("eventId") Long eventId, @PathVariable("playerId") Long playerId, Model model) {
        Event event = eventService.removePlayerFromEvent(eventId, playerId );
        return "redirect:/events/"+event.getId();
    }
    @PostMapping("/events/{eventId}/addPlayersToEvent")
    public String addPlayersToEvent(@PathVariable("eventId") Long eventId,
                                    @ModelAttribute("selectedUsers") FormValuesDto selected) {
        Event event = eventService.findById(eventId);
        if(selected.getMultiUsersCheckboxes().length > 0){
            for(String s : selected.getMultiUsersCheckboxes()){
                Long userId = Long.valueOf(s);
                User user = userService.findById(userId);
                Player newPlayer = playerService.create(user);
                event = eventService.addPlayerToEvent(event.getId(), newPlayer.getId() );
            }
        }
        return "redirect:/events/"+eventId;
    }



    @GetMapping("/profile")
    public String getProfile(Model model){
        model.addAttribute("loginUser", getLoggedInUser());
        return "profile";
    }
    @PostMapping("/profile/edit")
    public String editProfile(Model model){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.findByUsername(username);
        model.addAttribute("loginUser", getLoggedInUser());
        return "redirect:/profile";
    }

    private User getLoggedInUser(){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userService.findByUsername(username);


    }

}