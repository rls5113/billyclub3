package com.billyclub.points.controller;

import com.billyclub.points.dto.*;
import com.billyclub.points.model.Event;
import com.billyclub.points.model.EventStatus;
import com.billyclub.points.model.Player;
import com.billyclub.points.model.User;
import com.billyclub.points.service.EventService;
import com.billyclub.points.service.PlayerService;
import com.billyclub.points.service.UserService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.*;
import java.util.stream.Collectors;

@Controller
public class ThymeleafController {
    private final EventService eventService;
    private final UserService userService;
    private final PlayerService playerService;

    private static final Logger LOGGER = LoggerFactory.getLogger(ThymeleafController.class);
    private Long eventId;
    private MultiPlayerScoresMapDto multiPlayerScores;

    public ThymeleafController(EventService eventService, UserService userService, PlayerService playerService) {
        this.eventService = eventService;
        this.userService = userService;
        this.playerService = playerService;
    }

    @GetMapping("/events/current")
    public String listEvents(Model model) {
        List<EventDto> events = eventService.findOpenEvents();
        model.addAttribute("events", events);
        return "event-list";
    }

    @GetMapping("/events/{id}")
    public String getEventDetails(@PathVariable("id") Long id, Model model) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User loggedInUser = userService.findByUsername(username);
        model.addAttribute("loginUser", getLoggedInUser());
        Event event = eventService.findById(id);
        model.addAttribute("event", eventService.toDto(event));

        List<Player> players = event.getPlayers();
        //create a list of player names
        List<String> names = players.stream()
                .map(p -> p.getName())
                .collect(Collectors.toList());
        model.addAttribute("names", names);

        List<PlayerDto> eventPlayers = players.stream()
                .map(p -> playerService.toDto(p))
                .filter(p -> !p.getIsWaiting())
                .collect(Collectors.toList());
        model.addAttribute("eventPlayers", eventPlayers);

        MultiPlayerScoresMapDto scores = new MultiPlayerScoresMapDto();
        Map<String, PlayerScoresHolderDto> scoreHolders = new HashMap<>();
        for (PlayerDto player : eventPlayers) {
            PlayerScoresHolderDto v = new PlayerScoresHolderDto();
            v.setPlayerId(player.getId());
            scoreHolders.put(player.getName(), v);
        }
        scores.setScores(scoreHolders);
        model.addAttribute("multiPlayerScores", scores);

        List<PlayerDto> waitList = players.stream()
                .map(p -> playerService.toDto(p))
                .filter(p -> p.getIsWaiting())
                .collect(Collectors.toList());
        model.addAttribute("waitList", waitList);

        //create list of users not in this event
        List<UserDto> eventUsers = userService.findAllUsers();
        List<UserDto> filtered = eventUsers.stream()
                .filter(u -> players.stream()
                        .anyMatch(p -> p.getName().equals(u.getName())))
                .collect(Collectors.toList());

        List<UserDto> usersNotInEvent = new ArrayList<>();
        usersNotInEvent.addAll(eventUsers);
        usersNotInEvent.removeAll(filtered);
        model.addAttribute("userPickList", usersNotInEvent);

        //model object for multi user checkbox values
        model.addAttribute("selectedUsers", new MultiUserValuesDto());
        model.addAttribute("modalValues", new PlayerScoresHolderDto());

        List<Integer> holes = new ArrayList<>();
        for (int i = 1; i <= 18; i++) {
            holes.add(i);
        }
        model.addAttribute("HOLES", holes);


        return "event-detail";
    }

    @PostMapping("/events/{eventId}/addMe")
    public String addMeToEvent(@PathVariable("eventId") Long eventId, Model model) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.findByUsername(username);
        Event event = eventService.findById(eventId);
        if (!event.isPlayerInEvent(user.getName())) {
            Player newPlayer = playerService.create(user);
            event = eventService.addPlayerToEvent(eventId, newPlayer.getId());
        }

        model.addAttribute("event", eventService.toDto(event));
        model.addAttribute("players", event.getPlayers().stream()
                .map(p -> playerService.toDto(p))
//                        .filter(p -> p.is)
                .collect(Collectors.toList()));
        return "redirect:/events/" + eventId;
    }

    @GetMapping("/events/{eventId}/removeMe/{playerId}")
    public String removeMeFromEvent(@PathVariable("eventId") Long eventId,
                                    @PathVariable("playerId") Long playerId, Model model) {
        Event event = eventService.removePlayerFromEvent(eventId, playerId);
        return "redirect:/events/" + event.getId();
    }

    @PostMapping("/events/{eventId}/addPlayersToEvent")
    public String addPlayersToEvent(@PathVariable("eventId") Long eventId,
                                    @ModelAttribute("selectedUsers") MultiUserValuesDto selected) {
        Event event = eventService.findById(eventId);
        if (selected.getMultiUsersCheckboxes().length > 0) {
            for (String s : selected.getMultiUsersCheckboxes()) {
                Long userId = Long.valueOf(s);
                User user = userService.findById(userId);
                Player newPlayer = playerService.create(user);
                event = eventService.addPlayerToEvent(event.getId(), newPlayer.getId());
            }
        }
        return "redirect:/events/" + eventId;
    }

    @PostMapping("/events/{eventId}/postScore/{playerId}")
    @Transactional
    public String postScoreToEvent(@PathVariable("eventId") Long eventId,
                                   @PathVariable("playerId") Long playerId,
                                   @ModelAttribute("playerScore") PlayerScoresHolderDto playerScore
    ) {
        Event event = changeEventStatus(eventId, EventStatus.POST_SCORES);
        Player player = playerService.findById(playerId);
        player.setScoreForEvent(playerScore.getScoreForEvent());
        player.setBirdies(Arrays.asList(playerScore.getBirdies()));
        playerService.save(player);

        return "redirect:/events/" + event.getId();
    }

    @PostMapping("/events/{eventId}/postMultiScores")
    @Transactional
    public String postScoresToEvent(@PathVariable("eventId") Long eventId,
                                    @ModelAttribute("multiPlayerScores") @Valid MultiPlayerScoresMapDto multiPlayerScores)
    {
        this.eventId = eventId;
        this.multiPlayerScores = multiPlayerScores;
        Event event = eventService.findById(eventId);
        List<PlayerDto> eventPlayers = event.getPlayers().stream()
                .map(p -> playerService.toDto(p))
                .filter(p -> !p.getIsWaiting())
                .collect(Collectors.toList());

        for (PlayerDto player : eventPlayers) {
            //get the scoreholder for player
            Map<String, PlayerScoresHolderDto> map = multiPlayerScores.getScores();
            PlayerScoresHolderDto holder = map.get(player.getName());
            player.setScoreForEvent(holder.getScoreForEvent());
            player.setBirdies(Arrays.asList(holder.getBirdies()));
            playerService.save(playerService.toEntity(player));

            //update future events for this player and save
        }

        return "redirect:/events/" + eventId;
    }


    @GetMapping("/profiles")
    public String getProfile(Model model) {
        model.addAttribute("loginUser", getLoggedInUser());
        return "profile";
    }

    @PostMapping("/profiles/edit")
    public String editProfile(@ModelAttribute("user") UserDto user, BindingResult result, Model model) {
//        String username = SecurityContextHolder.getContext().getAuthentication().getName();
//        User user = userService.findByUsername(username);
//        model.addAttribute("loginUser", getLoggedInUser());
        return "redirect:/users";
    }

    private User getLoggedInUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userService.findByUsername(username);
    }

    private Event changeEventStatus(Long eventId, EventStatus eventStatus) {
        Event event = eventService.findById(eventId);
        if (event.getStatus() != eventStatus) {
            event.setStatus(eventStatus);
            eventService.save(event);
        }
        return event;
    }

}