package com.billyclub.points.controller;

import com.billyclub.points.dto.*;
import com.billyclub.points.model.*;
import com.billyclub.points.service.*;
import com.billyclub.points.util.ServletUtility;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class ThymeleafController {
    private final EventService eventService;
    private final UserService userService;
    private final PlayerService playerService;

    private EmailService emailService;
    private final CourseService courseService;

    private static final Logger LOGGER = LoggerFactory.getLogger(ThymeleafController.class);
    private Long eventId;
    private MultiPlayerScoresMapDto multiPlayerScores;

    @Autowired
    public ThymeleafController(EventService eventService, UserService userService, PlayerService playerService, EmailService emailService, CourseService courseService) {
        this.eventService = eventService;
        this.userService = userService;
        this.playerService = playerService;
        this.emailService = emailService;
        this.courseService = courseService;
    }

    @GetMapping("/events/current")
    public String listEvents(Model model) {
        List<EventDto> events = eventService.findOpenEvents();
        model.addAttribute("events", events);
        model.addAttribute("current", true);
        return "event-list";
    }
    @GetMapping("/events/past")
    public String listPastEvents(Model model) {
        List<EventDto> events = eventService.findPastEvents();
        model.addAttribute("events", events);
        model.addAttribute("current", false);
        return "event-list";
    }

    @GetMapping("/events/{id}")
    public String getEventDetails(@PathVariable("id") Long id, Model model, HttpServletRequest request) {
        String currentParam = request.getParameter("current");
        if(currentParam == null)    model.addAttribute("current", Boolean.TRUE);
        else                        model.addAttribute("current", Boolean.valueOf(currentParam));

        model.addAttribute("loginUser", getLoggedInUser());
        Event event = eventService.findById(id);
        if(event.isDayOf() && event.getStatus()==EventStatus.OPEN){
            event = changeEventStatus(id, EventStatus.STARTED);
        }
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
        if(event.getStatus().equals(EventStatus.OPEN)){
            eventPlayers.sort(Comparator.comparing(PlayerDto::getTimeEntered,Comparator.naturalOrder()));
        }else{
            eventPlayers.sort(Comparator.comparing(PlayerDto::getTotal,Comparator.nullsFirst(Comparator.reverseOrder())));
        }
        model.addAttribute("eventPlayers", eventPlayers);

//        MultiPlayerScoresMapDto scores = new MultiPlayerScoresMapDto();
//        Map<String, PlayerScoresHolderDto> scoreHolders = new HashMap<>();
//        for (PlayerDto player : eventPlayers) {
//            PlayerScoresHolderDto v = new PlayerScoresHolderDto();
//            v.setPlayerId(player.getId());
//            scoreHolders.put(player.getName(), v);
//        }
//        scores.setScores(scoreHolders);
//        model.addAttribute("multiPlayerScores", scores);

        List<PlayerDto> waitList = players.stream()
                .map(p -> playerService.toDto(p))
                .filter(p -> p.getIsWaiting())
                .sorted(Comparator.comparing(PlayerDto::getTimeEntered,Comparator.naturalOrder()))
                .collect(Collectors.toList());
        model.addAttribute("waitList", waitList);

        //create list of users not in this event
        List<UserDto> eventUsers = userService.findAllByActive();
        List<UserDto> filtered = eventUsers.stream()
                .filter(u -> players.stream()
                        .anyMatch(p1 -> p1.getName().equals(u.getName())))
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

        model.addAttribute("courses",courseService.findAll());


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
                .collect(Collectors.toList()));
        return "redirect:/events/" + eventId;
    }

    @GetMapping("/events/{eventId}/removeMe/{playerId}")
    public String removeMeFromEvent(@PathVariable("eventId") Long eventId,
                                    @PathVariable("playerId") Long playerId, Model model, HttpServletRequest request) {
        Event event = eventService.removePlayerFromEvent(eventId, playerId);
        String link = ServletUtility.getSiteURL(request)+"/events/"+event.getId()+"?current=true";

        try {
            List<User> recipients = new ArrayList<>();
            if(event.getEmailRecipient()!=null) {
                User user = userService.findByFullname(event.getEmailRecipient().getName());
                recipients.add(user);
                emailService.sendMovedFromWaitlistEmail(recipients,
                        event.getEventDate().format(DateTimeFormatter.ofPattern("MM/dd/yyyy")),
                        event.getStartTime().format(DateTimeFormatter.ofPattern("HH:mm a")),
                        request.getLocale(),
                        link);
            }
        } catch (MessagingException e) {
            System.out.println("Failed to send email. " + e.getMessage());
//            throw new RuntimeException(e);
        }

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
                                   @Valid @ModelAttribute("playerScore") PlayerScoresHolderDto playerScore,
                                   BindingResult result, Model model
    ) {
        if (result.hasErrors()) {
            return "postScoreModal";
        }

        Event event = changeEventStatus(eventId, EventStatus.POSTING);
        Player player = playerService.findById(playerId);
        playerService.setScoring(player, playerScore );

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

    @GetMapping("/events/{eventId}/calc-scoring")
    @Transactional
    public String processEventScoreboard(@PathVariable("eventId") Long eventId) {
        Event event = eventService.calculateEventScoreboard(eventId);

        return "redirect:/events/" + eventId;
    }
    @PostMapping("/events/{eventId}/edit")
//    @Transactional
    public String saveEventDetail(@PathVariable("eventId") Long eventId,@Valid @ModelAttribute("event") EventDto eventDto,
                                  BindingResult result, Model model, HttpServletRequest request) {
        Event eventToEdit = eventService.findById(eventId);
        eventToEdit = eventService.transfer(eventDto, eventToEdit);
        String link = ServletUtility.getSiteURL(request)+"/events/"+eventToEdit.getId()+"?current=true";

        if (eventToEdit.getStatus() != EventStatus.COMPLETED) {
            //reprocess waiting list if number of tee times changed
            boolean numberOfTeeTimesChanged = eventDto.getNumOfTimes() == eventToEdit.getNumOfTimes();
            if(numberOfTeeTimesChanged){
                List<Player> list = eventService.recalculateWaitingList(eventToEdit);
                List<User> recipients = list.stream().map(p -> userService.findByFullname(p.getName())).collect(Collectors.toList());
                try {
                    emailService.sendMovedFromWaitlistEmail(recipients,
                            eventToEdit.getEventDate().format(DateTimeFormatter.ofPattern("MM/dd/yyyy")),
                            eventToEdit.getStartTime().format(DateTimeFormatter.ofPattern("HH:mm a")),
                            request.getLocale(),
                            link);
                } catch (MessagingException e) {
                    System.out.println("Failed to send email. " + e.getMessage());
                }
            }

            eventToEdit = eventService.save(eventToEdit);

            switch (eventDto.getStatus()) {
                case CANCELLED, FROST_DELAY, RAIN_DELAY -> {
                    try {
                        List<User> statusRecipients =  eventToEdit.getPlayers().stream()
                                .map(p -> userService.findByFullname(p.getName()))
                                .collect(Collectors.toList());
                        emailService.sendEventStatusChangedEmail(statusRecipients,
                                eventToEdit.getStatus().name(),
                                eventToEdit.getEventDate().format(DateTimeFormatter.ofPattern("MM/dd/yyyy")),
                                link,
                                request.getLocale());
                    } catch (MessagingException e) {
                        System.out.println("Failed to send email. " + e.getMessage());
//                        throw new RuntimeException(e);
                    }
                    break;
                }
                default -> {break;}
            }
        }
        return "redirect:/events/" + eventId;
    }

    @GetMapping("/events/add")
    public String addEvent(Model model) {
        model.addAttribute("event", new EventDto());
        model.addAttribute("courses",courseService.findAll());

        return "event-add";
    }
    @PostMapping("/events/add")
    public String postNewEvent(@ModelAttribute("event") @Valid EventDto eventDto, HttpServletRequest request){
        Event event = eventService.add(eventService.toEntity(eventDto));
        //generate pw reset link
        String link = ServletUtility.getSiteURL(request)+"/events/"+event.getId()+"?current=true";
        List<User> recipients = userService.findAllByActive().stream()
                .map(u -> userService.toEntity(u))
                .collect(Collectors.toList());
        //send email
        try {
            emailService.sendNewEventEmail(recipients,
                    event.getEventDate().format(DateTimeFormatter.ofPattern("MM/dd/yyyy")),
                    event.getStartTime().format(DateTimeFormatter.ofPattern("HH:mm a")),
                    event.getCourse().getName(), link, request.getLocale());
        } catch (MessagingException e) {
            System.out.println("Failed to send email. " + e.getMessage());
//            throw new RuntimeException(e);
        }
        return "redirect:/events/current";
    }

    private User getLoggedInUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userService.findByUsername(username);
    }

    private Event changeEventStatus(Long eventId, EventStatus eventStatus) {
        Event event = eventService.findById(eventId);
        if (event.getStatus() != eventStatus && event.getStatus() != EventStatus.COMPLETED) {
            if(event.isAllScoresIn())   eventStatus = EventStatus.COMPLETED;
            event.setStatus(eventStatus);
            eventService.save(event);
        }
        return event;
    }
    @GetMapping("/courses")
    public String listCourses(Model model) {
        List<CourseDto> courses = courseService.findAll().stream()
                        .map(c -> courseService.toDto(c))
                                .collect(Collectors.toList());
        model.addAttribute("courses", courses);
        return "course-list";
    }
    @GetMapping("/courses/add")
    public String addCourse(Model model) {
        model.addAttribute("course", new CourseDto());
        model.addAttribute("mode", "add");
        return "course-add-edit";
    }

    @PostMapping("/courses/add")
    public String postAddCourse(@Valid @ModelAttribute("course") CourseDto course, BindingResult result, Model model) {
        Course newCourse = courseService.save(courseService.toEntity(course));
        return "redirect:/courses";
    }
    @GetMapping("/courses/{courseId}")
    public String showEditCourse(
            @PathVariable("courseId") Long courseId, Model model) {
        Course courseToEdit = courseService.findById(courseId);
        model.addAttribute("course", courseService.toDto(courseToEdit));
        model.addAttribute("mode","edit");
        return "course-add-edit";
    }
    @PostMapping("/courses/{courseId}/edit")
    public String postEditCourse(
            @PathVariable("courseId") Long courseId,
            @Valid @ModelAttribute("course") CourseDto course,
            BindingResult result, Model model) {
        Course courseToEdit = courseService.findById(courseId);
        if (result.hasErrors()) {
            return "course-add-edit";
        }
        BeanUtils.copyProperties(course, courseToEdit);
        courseService.save(courseToEdit);
        return "redirect:/courses?success";
    }
    @GetMapping("/users")
    public String listRegisteredUsers(Model model){
        List<UserDto> users = userService.findAllUsers().stream()
                .filter((t)-> t.getActive())
                        .sorted((t1,t2)-> t1.getLastName().compareTo(t2.getLastName()))
                                .collect(Collectors.toList());

        model.addAttribute("users", users);
        model.addAttribute("loginUser", getLoggedInUser());

        return "users-list";
    }

    @GetMapping("/users/{userid}/edit")
    public String editUserChanges(@PathVariable("userid") Long userId, Model model){
        User userToEdit = userService.findById(userId);
        model.addAttribute("user", userService.toDto(userToEdit));
        return "user-edit";
    }

    @PostMapping("/users/{userid}/edit")
    public String postUserChanges(@PathVariable("userid") Long userId, @ModelAttribute("user") UserDto userDto){
        User userToEdit = userService.findById(userId);
        userToEdit = userService.transfer(userDto, userToEdit);
        User me = userService.save(userToEdit);
        return "redirect:/users";
    }
    @GetMapping("/users/{userid}/addAsAdmin")
    public String addToAdminRole(@PathVariable("userid") Long userId){
        User userToEdit = userService.findById(userId);
        userToEdit.getRoles().add( userService.findByName("ROLE_ADMIN"));
        User me = userService.save(userToEdit);
        return "redirect:/users/"+userId+"/edit";
    }


}