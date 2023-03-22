package com.billyclub.points.config;

import com.billyclub.points.dto.UserDto;
import com.billyclub.points.model.*;
import com.billyclub.points.repository.RoleRepository;
import com.billyclub.points.service.EventService;
import com.billyclub.points.service.UserService;
import com.billyclub.points.service.impl.EventServiceImpl;
import com.billyclub.points.service.impl.PlayerServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;

@Configuration
@Profile("dev")
class LoadDatabase {

    private static final Logger log = LoggerFactory.getLogger(LoadDatabase.class);

//    @Bean
//    CommandLineRunner initEventsRecords(EventRepository repository) {
//        System.out.println("loading data: POINTS EVENTS");
//        return args -> {
//            log.info("Preloading " + repository.save(new Event(LocalDate.now().plusDays(7), LocalTime.of(6,30,0) ,2)));
//            log.info("Preloading " + repository.save(new Event(LocalDate.now().plusDays(8), LocalTime.of(6,30,0) ,3)));
//            log.info("Preloading " + repository.save(new Event(LocalDate.now().plusDays(14), LocalTime.of(6,30,0) ,4)));
//            log.info("Preloading " + repository.save(new Event(LocalDate.now().plusDays(15), LocalTime.of(6,30,0) ,3)));
//        };
//    }
//    @Bean
//    CommandLineRunner initPlayerRecords(PlayerRepository repository) {
//        System.out.println("loading data: PLAYER");
//        return args -> {
//            log.info("Preloading " + repository.save(new Player("Herman Munster", 25,27,LocalDateTime.now())));
//            log.info("Preloading " + repository.save(new Player("Lilly Munster", 20,18,LocalDateTime.now())));
//            log.info("Preloading " + repository.save(new Player("Eddie Munster", 15,18,LocalDateTime.now())));
//        };
//    }
    @Bean
//    @Autowired
    CommandLineRunner initAddPlayersToEventRecords(RoleRepository roleRepo, EventService eventService,
                                                   UserService userService) {
        System.out.println("loading data: ROLES");
        return args -> {

            log.debug("Running load DATA");
            Role role1 = roleRepo.save(new Role(null, "ROLE_USER",null));
            Role role2 = roleRepo.save(new Role(null, "ROLE_ADMIN",null));
//            log.info(role1.toString());
//            log.info(role2.toString());
////            Role admin = userService.addRole("ADMIN");
//            Role user = userService.addRole("USER");
//  ADMIN
            User rstuart = userService.saveUser(new UserDto(null,"rstuart","Robert","Stuart","stuartrl@comcast.net","password",0,null));
            rstuart.getRoles().add(userService.findByName("ROLE_ADMIN"));
            userService.save(rstuart);

//  ALL THE REST WILL BE USERS
            User hmunster = userService.saveUser(new UserDto(null,"hmunster","Herman","Munster","hmunster@gmail.com","password",0,null));
            User lmunster = userService.saveUser(new UserDto(null,"lmunster","Lilly","Munster","lmunster@gmail.com","password",0,null));
            User emunster = userService.saveUser(new UserDto(null,"emunster","Eddie","Munster","emunster@gmail.com","password",0,null));
            User gmunster = userService.saveUser(new UserDto(null,"gmunster","Grandpa","Munster","gmunster@gmail.com","password",0,null));
            User hsimpson = userService.saveUser(new UserDto(null,"hsimpson","Homer","Simpson","hsimpson@gmail.com","password",0,null));
            User bsimpson = userService.saveUser(new UserDto(null,"bsimpson","Bart","Simpson","bsimpson@gmail.com","password",0,null));
            User lsimpson = userService.saveUser(new UserDto(null,"lsimpson","Lisa","Simpson","lsimpson@gmail.com","password",0,null));
            User msimpson = userService.saveUser(new UserDto(null,"msimpson","Marge","Simpson","msimpson@gmail.com","password",0,null));
            User gaddams = userService.saveUser(new UserDto(null,"gaddams","Gomez","Addams","gaddams@gmail.com","password",0,null));
            User maddams = userService.saveUser(new UserDto(null,"maddams","Morticia","Addams","maddams@gmail.com","password",0,null));
            User fester = userService.saveUser(new UserDto(null,"faddams","Uncle","Fester","faddams@gmail.com","password",0,null));
            User pugsly = userService.saveUser(new UserDto(null,"paddams","Pugsly","Addams","paddams@gmail.com","password",0,null));
            User wed = userService.saveUser(new UserDto(null,"waddams","Wednesday","Addams","waddams@gmail.com","password",0,null));

//
//            log.info("user1 " + rstuart );
//            log.info("user2 " + hmunster );
//            log.info("ADMIN " + admin );
//            log.info("USER " + user );
//


            List<Player> players = Collections.emptyList();
            Event event1 = eventService.add(new Event(null, LocalDate.now().plusDays(7), LocalTime.of(6,30,0) ,1, EventStatus.OPEN, players));
            Event event2 = eventService.add(new Event(null,LocalDate.now().plusDays(8), LocalTime.of(6,30,0) ,2, EventStatus.OPEN, players));
            Event event3 = eventService.add(new Event(null,LocalDate.now().plusDays(14), LocalTime.of(6,30,0) ,3,  EventStatus.OPEN,players));
            Event event4 = eventService.add(new Event(null,LocalDate.now().plusDays(15), LocalTime.of(6,30,0) ,4,  EventStatus.OPEN,players));
            log.info("Preloading " + event1 );
            log.info("Preloading " + event2 );
            log.info("Preloading " + event3 );
            log.info("Preloading " + event4 );
//            Player player1 = playerService.add(new Player("Herman Munster", 25,0));
//            Player player2 = playerService.add(new Player("Lilly Munster", 20,0));
//            Player player3 = playerService.add(new Player("Eddie Munster", 15,0));
//            log.info("Preloading " + player1);
//            log.info("Preloading " + player2);
//            log.info("Preloading " + player3);

//            event1.getPlayers().add(player1);
//            log.info("Saving event after adding players  "+ eventService.addPlayerToEvent(event1.getId(),player1.getId()));
//            log.info("Saving event after adding players  "+ eventService.addPlayerToEvent(event1.getId(),player2.getId()));
//            player1.setEvent(event1);
//            player2.setEvent(event1);
//            event1.getPlayers().add(player2);
//            player1.getEvents().add(event1);
//            player2.getEvents().add(event1);

//            log.info("Saving event after adding players  "+ playerRepository.save(player1));
//            log.info("Saving event after adding players  "+ playerRepository.save(player2));

        };
    }
}