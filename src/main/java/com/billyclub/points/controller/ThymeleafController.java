package com.billyclub.points.controller;

import com.billyclub.points.dto.*;
import com.billyclub.points.model.*;
import com.billyclub.points.model.Event;
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
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class ThymeleafController {
    private final EventService eventService;
    private final UserService userService;
    private final PlayerService playerService;

    private final EmailService emailService;
    private final CourseService courseService;

    private static final Logger log = LoggerFactory.getLogger(ThymeleafController.class);
//    private Long eventId;
//    private MultiPlayerScoresMapDto multiPlayerScores;

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
        log.info("Event Details: ID= "+id);
        String currentParam = request.getParameter("current");
        if(currentParam == null)    model.addAttribute("current", Boolean.TRUE);
        else                        model.addAttribute("current", Boolean.valueOf(currentParam));

        model.addAttribute("loginUser", getLoggedInUser());
        Event event = eventService.findById(id);
        if(event.isDayOf() && (event.getStatus()==EventStatus.OPEN || event.getStatus()==EventStatus.PREPAID)){
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
        usersNotInEvent.sort(Comparator.comparing(UserDto::getFirstName,Comparator.naturalOrder()));

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
        log.info("Event Details: exit");
        return "event-detail";
    }

    @PostMapping("/events/{eventId}/addMe")
    public String addMeToEvent(@PathVariable("eventId") Long eventId, Model model) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        log.info("Add to Event ("+eventId+"): entering "+username);
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
        log.info("Remove From Event ("+eventId+"): removing "+playerId);
        Event event = eventService.removePlayerFromEvent(eventId, playerId);
        String link = ServletUtility.getSiteURL(request)+"/events/"+event.getId()+"?current=true";

        Map<String,String> params = new HashMap<>();
        params.put("eventStatus", event.getStatus().name());
        params.put("eventDate", event.getEventDate().format(DateTimeFormatter.ofPattern("MM/dd/yyyy")));
        params.put("startTime", event.getStartTime().format(DateTimeFormatter.ofPattern("HH:mm a")));
        params.put("link", link);

//        try {
//            List<User> recipients = new ArrayList<>();
//            if(event.getEmailRecipient()!=null) {
//                User user = userService.findByFullname(event.getEmailRecipient().getName());
//                recipients.add(user);
//                emailService.sendMovedFromWaitlistEmail(recipients, params, request.getLocale()
//                );
//            }
//        } catch (MessagingException e) {
//            log.info("Remove From Event ("+eventId+"): waitlist email failed");
//            System.out.println("Failed to send email. " + e.getMessage());
////            throw new RuntimeException(e);
//        }

        return "redirect:/events/" + event.getId();
    }

    @PostMapping("/events/{eventId}/addPlayersToEvent")
    public String addPlayersToEvent(@PathVariable("eventId") Long eventId,
                                    @ModelAttribute("selectedUsers") MultiUserValuesDto selected) {
        log.info("Add multiple players to Event ("+eventId+"): enter");
        Event event = eventService.findById(eventId);
        if (selected.getMultiUsersCheckboxes().length > 0) {
            for (String s : selected.getMultiUsersCheckboxes()) {
                Long userId = Long.valueOf(s);
                User user = userService.findById(userId);
                Player newPlayer = playerService.create(user);
                event = eventService.addPlayerToEvent(event.getId(), newPlayer.getId());
            }
        }
        log.info("Add multiple players to Event ("+eventId+"): exit");
        return "redirect:/events/" + eventId;
    }

    @PostMapping("/events/{eventId}/postScore/{playerId}")
    @Transactional
    public String postScoreToEvent(@PathVariable("eventId") Long eventId,
                                   @PathVariable("playerId") Long playerId,
                                   @Valid @ModelAttribute("playerScore") PlayerScoresHolderDto playerScore,
                                   BindingResult result, Model model
    ) {
        log.info("Post Players Score ("+eventId+"): enter playerId "+playerId);

        if (result.hasErrors()) {
            return "postScoreModal";
        }

        Event event = changeEventStatus(eventId, EventStatus.POSTING);
        Player player = playerService.findById(playerId);
        playerService.setScoring(player, playerScore );

        return "redirect:/events/" + event.getId();
    }

//    @PostMapping("/events/{eventId}/postMultiScores")
//    @Transactional
//    public String postScoresToEvent(@PathVariable("eventId") Long eventId,
//                                    @ModelAttribute("multiPlayerScores") @Valid MultiPlayerScoresMapDto multiPlayerScores)
//    {
//        this.eventId = eventId;
//        this.multiPlayerScores = multiPlayerScores;
//        Event event = eventService.findById(eventId);
//        List<PlayerDto> eventPlayers = event.getPlayers().stream()
//                .map(p -> playerService.toDto(p))
//                .filter(p -> !p.getIsWaiting())
//                .collect(Collectors.toList());
//
//        for (PlayerDto player : eventPlayers) {
//            //get the scoreholder for player
//            Map<String, PlayerScoresHolderDto> map = multiPlayerScores.getScores();
//            PlayerScoresHolderDto holder = map.get(player.getName());
//            player.setScoreForEvent(holder.getScoreForEvent());
//            player.setBirdies(Arrays.asList(holder.getBirdies()));
//            playerService.save(playerService.toEntity(player));
//
//            //update future events for this player and save
//        }
//
//        return "redirect:/events/" + eventId;
//    }


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
    @Transactional
    public String editEventDetail(@PathVariable("eventId") Long eventId, @Valid @ModelAttribute("event") EventDto eventDto,
                                  BindingResult result, Model model, HttpServletRequest request) {
        log.info("Save Event ("+eventId+"): enter");
        if (result.hasErrors()) {
            model.addAttribute("event", eventDto);
            model.addAttribute("errors",result.getAllErrors());
            return "redirect:/events/"+eventId+"?#editEventModal";
        }

        Event eventToEdit = eventService.findById(eventId);

        boolean numberOfTeeTimesLess = eventDto.getNumOfTimes() < eventToEdit.getNumOfTimes();
        boolean numberOfTeeTimesMore = eventDto.getNumOfTimes() > eventToEdit.getNumOfTimes();

        eventToEdit = eventService.transfer(eventDto, eventToEdit);
        String link = ServletUtility.getSiteURL(request)+"/events/"+eventToEdit.getId()+"?current=true";

        Map<String, String> params = new HashMap<>();

        params.put("course", eventToEdit.getCourse().getName());
        params.put("eventStatus", eventToEdit.getStatus().name());
        params.put("eventDate", eventToEdit.getEventDate().format(DateTimeFormatter.ofPattern("MM/dd/yyyy")));
        params.put("startTime", eventToEdit.getStartTime().format(DateTimeFormatter.ofPattern("HH:mm a")));
        params.put("link", link);


        if (eventToEdit.getStatus() != EventStatus.COMPLETED) {
            //reprocess waiting list if number of tee times changed
//            boolean numberOfTeeTimesChanged = eventDto.getNumOfTimes() != eventToEdit.getNumOfTimes();
            if(numberOfTeeTimesMore){
                List<Player> list = eventService.recalculateWaitingList(eventToEdit, numberOfTeeTimesMore );
                List<User> recipients = list.stream().map(p -> userService.findByFullname(p.getName())).collect(Collectors.toList());
//                try {
//
//                    emailService.sendMovedFromWaitlistEmail(recipients, params, request.getLocale());
//
//                } catch (MessagingException e) {
//                    log.info("Save Event ("+eventId+"): waitlist email failed");
//                    System.out.println("Failed to send email. " + e.getMessage());
//                }
            }
            else if(numberOfTeeTimesLess){
                List<Player> list = eventService.recalculateWaitingList(eventToEdit, false);
                List<User> recipients = list.stream().map(p -> userService.findByFullname(p.getName())).collect(Collectors.toList());
//                try {
//
//                    emailService.sendMovedToWaitlistEmail(recipients, params, request.getLocale());
//
//                } catch (MessagingException e) {
//                    log.info("Save Event ("+eventId+"): waitlist email failed");
//                    System.out.println("Failed to send email. " + e.getMessage());
//                }
            }

            eventToEdit = eventService.save(eventToEdit);

            switch (eventDto.getStatus()) {
                case CANCELED, FROST_DELAY, RAIN_DELAY -> {
                    try {
                        System.out.println("Save Event ("+eventId+"): sending status changed email");
                        List<User> statusRecipients =  eventToEdit.getPlayers().stream()
                                .map(p -> userService.findByFullname(p.getName()))
                                .collect(Collectors.toList());

                        emailService.sendEventStatusChangedEmail(statusRecipients, params, request.getLocale());

                    } catch (MessagingException e) {
                        System.out.println("Failed to send email. " + e.getMessage());
                        log.info("Save Event ("+eventId+"): failed to send status changed email");
//                        throw new RuntimeException(e);
                    }
                    break;
                }
                default -> {break;}
            }
        }
        log.info("Save Event ("+eventId+"): exit");
        return "redirect:/events/" + eventId;
    }

    @GetMapping("/events/add")
    public String addEvent(Model model) {
        model.addAttribute("event", new EventDto());
        model.addAttribute("courses",courseService.findAll());

        return "event-add";
    }
    @PostMapping("/events/add")
    public String postNewEvent(@Valid @ModelAttribute("event")  EventDto eventDto, BindingResult result, HttpServletRequest request, Model model){
        log.info("Add Event : enter");
        if (result.hasErrors()) {
            model.addAttribute("event", eventDto);
            model.addAttribute("courses",courseService.findAll());
            return "event-add";
        }

        Event event = eventService.add(eventService.toEntity(eventDto));
        //generate link
        String link = ServletUtility.getSiteURL(request)+"/events/"+event.getId()+"?current=true";
//        List<User> recipients = Arrays.asList(userService.findByUsername("rstuart"));  // use to isolate to me
        List<User> recipients = userService.findAll();
        //send email
        log.info("Add Event : sending new event email");

        Map<String, String> params = new HashMap<>();
        params.put("course", event.getCourse().getName());
        params.put("eventStatus", event.getStatus().name());
        params.put("eventDate", event.getEventDate().format(DateTimeFormatter.ofPattern("MM/dd/yyyy")));
        params.put("startTime", event.getStartTime().format(DateTimeFormatter.ofPattern("HH:mm a")));
        params.put("link", link);

        try {

            emailService.sendNewEventEmail(recipients, params, request.getLocale());

        } catch (Exception e) {
            log.info("Add Event : failed to send new event email");
            System.out.println("Failed to send email. " + e.getMessage());
//            throw new RuntimeException(e);
        }
        log.info("Add Event : exit");
        return "redirect:/events/current";
    }

    @GetMapping(value = "/events/{eventId}/pick-teams")
    @Transactional
    public String pickTeams(@PathVariable("eventId") Long eventId, Model model) {
        Event event = eventService.findById(eventId);
        model.addAttribute("event", event);
        model.addAttribute("courses",courseService.findAll());

        //remove new players and still on waiting list
        List<Player> eventPlayers = event.getPlayers().stream()
//                .map(p -> playerService.toDto(p))
                .filter(p -> !p.getIsWaiting() && !(p.getQuota() == 0) && !p.getIsWithdrawal())
                .sorted(Comparator.comparing(Player::getTotal,Comparator.reverseOrder()))
                .collect(Collectors.toList());
        if(eventPlayers.size() <= 7){
            event = eventService.calculateEventScoreboard(eventId);
            return "redirect:/events/" + eventId;
        }

        if(!eventPlayers.isEmpty()) {
            List<Player> losers = eventService.getsMoneyBack(eventPlayers);
            if (!losers.isEmpty()) {
                //if more than one, pick the winner
                List<String> moneyBackList = new ArrayList<>();
                if (losers.size() > 1) {
                    List<String> names = losers.stream().map(p -> p.getName()).collect(Collectors.toList());
                    moneyBackList.add("Tied for worst score: " + names.toString());
                    for (int i = 0; i < 3; i++) {
                        Collections.shuffle(losers);
                    }
                }
                Player getsMoneyBack = losers.get(0);

                getsMoneyBack.setTeam("Odd man money back winner!");
                moneyBackList.add(getsMoneyBack.getName() + " - ODD man money back winner! Worst score: " + getsMoneyBack.getTotal());
                event.getEventWinners().addAll(moneyBackList);
                //remove from list for teams
                eventPlayers.remove(getsMoneyBack);
                model.addAttribute("moneybackList", moneyBackList);

            }
        }

        List<PlayerDto> playerDtos = eventPlayers.stream()
                .map(p -> playerService.toDto(p))
                .collect(Collectors.toList());
        model.addAttribute("playerList",playerDtos);

        TeamsDto teamsDTO = new TeamsDto();
        for(int i=0; i < eventPlayers.size()/2; i++){
            teamsDTO.add(new TeamDto("Team "+(i+1),0));
        }
        model.addAttribute("wrapper",teamsDTO);

//        String[] cards = {"AS.png","2H.png","3C.png","4D.png","5S.png","6H.png","7C.png","8D.png","9S.png","10H.png","JC.png","QD.png","KS.png"};
        model.addAttribute("cards", TeamDto.cards);

        return "event-pick-teams";
    }
    @PostMapping("/events/{eventId}/pick-teams")
    @Transactional
    public String pickTeams(@PathVariable("eventId") Long eventId, @ModelAttribute("wrapper") @Valid TeamsDto wrap,
                                  BindingResult result, Model model, HttpServletRequest request) {
//        List<TeamDto> teams = wrap.getTeams();
//        int i =0;
//        for(TeamDto team: teams) {
//            if(team.getTeam().size() != 2){
//                result.rejectValue("teams["+i+"]", "error", "This team needs 2 players!");
//            }
//            i++;
//        }
//        if (result.hasErrors()) {
//            model.addAttribute("wrapper", wrap);
//            return "redirect:/events/"+eventId+"/pick-teams";
//        }

        Event event = eventService.findById(eventId);
        event = eventService.savePickteams(event, wrap);
        log.info(wrap.toString());

        return "redirect:/events/" + eventId;
    }

    private User getLoggedInUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userService.findByUsername(username);
    }

    private Event changeEventStatus(Long eventId, EventStatus eventStatus) {
        log.info("Change Event status: enter");
        Event event = eventService.findById(eventId);
        if (event.getStatus() != eventStatus && event.getStatus() != EventStatus.COMPLETED) {
            if(event.isAllScoresIn())   eventStatus = EventStatus.COMPLETED;
            event.setStatus(eventStatus);
            eventService.save(event);
        }
        log.info("Change Event status: enter");
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
        log.info("Add new course: enter");
        if (result.hasErrors()) {
            model.addAttribute("course", course);
            model.addAttribute("mode", "add");
            return "course-add-edit";
        }

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
        log.info("Edit Course: enter");
        Course courseToEdit = courseService.findById(courseId);
        if (result.hasErrors()) {
            return "course-add-edit";
        }
        BeanUtils.copyProperties(course, courseToEdit);
        courseService.save(courseToEdit);
        log.info("Edit Course: exit");
        return "redirect:/courses?success";
    }
    @GetMapping("/users")
    public String listRegisteredUsers(Model model){
        List<UserDto> users = userService.findAllUsers().stream()
//                .filter((t)-> t.getActive())
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
        log.info("Edit User: enter");
        User userToEdit = userService.findById(userId);
        userToEdit = userService.transfer(userDto, userToEdit);
        User me = userService.save(userToEdit);
        return "redirect:/users";
    }
    @GetMapping("/users/{userid}/addAsAdmin")
    public String addToAdminRole(@PathVariable("userid") Long userId){
        log.info("Add as Admin: userId: "+userId);
        User userToEdit = userService.findById(userId);
        userToEdit.getRoles().add( userService.findByName("ROLE_ADMIN"));
        User me = userService.save(userToEdit);
        return "redirect:/users/"+userId+"/edit";
    }


}