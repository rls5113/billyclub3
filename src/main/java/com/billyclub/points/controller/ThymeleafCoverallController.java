package com.billyclub.points.controller;

import com.billyclub.points.dto.*;
import com.billyclub.points.model.*;
import com.billyclub.points.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping(value = "/coveralls")
public class ThymeleafCoverallController {
    private final EventService eventService;
    private final UserService userService;
    private final PlayerService playerService;
    private final EmailService emailService;
    private final CourseService courseService;
    private final CoverallService coverallService;
    private final CoverallPlayerService coverallPlayerService;
    private final HoleService holeService;

    private static final Logger log = LoggerFactory.getLogger(ThymeleafCoverallController.class);
//    private Long coverallId;

    @Autowired
    public ThymeleafCoverallController(EventService eventService, UserService userService, PlayerService playerService, EmailService emailService, CourseService courseService, CoverallService coverallService, CoverallPlayerService coverallPlayerService, HoleService holeService) {
        this.eventService = eventService;
        this.userService = userService;
        this.playerService = playerService;
        this.emailService = emailService;
        this.courseService = courseService;
        this.coverallService = coverallService;
        this.coverallPlayerService = coverallPlayerService;
        this.holeService = holeService;
    }

    @GetMapping("/list")
    public String listCoveralls(Model model) {
        List<CoverallDto> list = coverallService.findAll().stream().map(c -> coverallService.toDto(c)).collect(Collectors.toList());
        model.addAttribute("coveralls", list);
        return "coverall-list";
    }

    @GetMapping("/add")
    public String addCoverall(Model model) {
        CoverallDto dto = new CoverallDto();
        model.addAttribute("coverall", dto);
        return "coverall-add";
    }

    @GetMapping("/{id}")
    public String getCoverallDetails(@PathVariable("id") Long id, Model model) {
        Coverall coverall = coverallService.findById(id);
        model.addAttribute("coverall", coverallService.toDto(coverall));

        List<CoverallPlayer> players = coverall.getPlayers();
        //create list of names
        List<String> names = players.stream()
                .map(p -> p.getName())
                .collect(Collectors.toList());
        model.addAttribute("names", names);
        List<CoverallPlayerDto> coverallPlayers = players.stream()
                .map(p -> coverallPlayerService.toDto(p))
                .collect(Collectors.toList());
        model.addAttribute("coverallPlayers", coverallPlayers);

        List<UserDto> coverallUsers = userService.findAllByActive();
        List<UserDto> filtered = coverallUsers.stream()
                .filter(u -> players.stream()
                        .anyMatch(p1 -> p1.getName().equals(u.getName())))
                .collect(Collectors.toList());

        List<UserDto> usersNotInEvent = new ArrayList<>();
        usersNotInEvent.addAll(coverallUsers);
        usersNotInEvent.removeAll(filtered);
        usersNotInEvent.sort(Comparator.comparing(UserDto::getFirstName,Comparator.naturalOrder()));

        model.addAttribute("userPickList", usersNotInEvent);

        //model object for multi user checkbox values
        model.addAttribute("selectedUsers", new MultiUserValuesDto());

        //holes
//        List<Integer> holes = IntStream.of(CoverallDto.holes).boxed().collect(Collectors.toCollection(ArrayList::new));
//        model.addAttribute("holes", holes);
        List<HoleDto> holes = coverall.getHoles().stream()
                .map(h -> holeService.toDto(h))
                .collect(Collectors.toList());
        holes.sort((h1, h2) -> (h1.getNum().compareTo(h2.getNum())));
        model.addAttribute("holes", holes);

        List<Integer> missing = new ArrayList<>();
        Collections.addAll(missing, Arrays.stream(CoverallDto.holes).boxed().toArray(Integer[]::new));
        for(Hole hole : coverall.getHoles()){
            missing.remove(hole.getNum());
        }
        model.addAttribute("missing", missing);
        return "coverall-detail";
    }
    @PostMapping("/{id}/addPlayers")
    public String addPlayers(@PathVariable("id") Long id,
                                    @ModelAttribute("selectedUsers") MultiUserValuesDto selected) {
        log.info("Add multiple players to coverall ("+id+"): enter");
        Coverall coverall = coverallService.findById(id);
        if (selected.getMultiUsersCheckboxes().length > 0) {
            for (String s : selected.getMultiUsersCheckboxes()) {
                Long userId = Long.valueOf(s);
                User user = userService.findById(userId);
                CoverallPlayer newPlayer = coverallPlayerService.create(user);
                coverall = coverallService.addPlayerToCoverall(coverall.getId(), newPlayer.getId());
            }
        }
        log.info("Add multiple players to coverall ("+id+"): exit");
        return "redirect:/coveralls/" + id;
    }

}