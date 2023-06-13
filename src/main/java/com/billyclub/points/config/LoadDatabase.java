package com.billyclub.points.config;

import com.billyclub.points.dto.UserDto;
import com.billyclub.points.model.*;
import com.billyclub.points.repository.RoleRepository;
import com.billyclub.points.service.CourseService;
import com.billyclub.points.service.EventService;
import com.billyclub.points.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

//@Configuration
//@Profile("dev")
class LoadDatabase {

    private static final Logger log = LoggerFactory.getLogger(LoadDatabase.class);

    @Bean
    @Autowired
    CommandLineRunner initAddPlayersToEventRecords(RoleRepository roleRepo, EventService eventService,
                                                   UserService userService, CourseService courseService) {
        System.out.println("loading data: ROLES");
        return args -> {

            log.info("Running load DATA");
//            Role role1 = roleRepo.save(new Role(null, "ROLE_USER",null));
//            Role role2 = roleRepo.save(new Role(null, "ROLE_ADMIN",null));
//  ADMIN
//            User rstuart = userService.saveUser(new UserDto(null,"rstuart","Robert","Stuart","stuartrl@comcast.net","password","",0, Arrays.asList(role1,role2),null,true));

            //production users
//            User brianberry = userService.saveUser(new UserDto(null,"brianb","Brian","Berry","bberry@reliancefirstcapital.com","password","",22,Arrays.asList(role1),null,true));
//  ALL THE REST WILL BE USERS
//            User hmunster = userService.saveUser(new UserDto(null,"hmunster","Herman","Munster","hmunster@gmail.com","password","",0,Arrays.asList(role1),null,true));
//            User lmunster = userService.saveUser(new UserDto(null,"lmunster","Lilly","Munster","lmunster@gmail.com","password","",0,Arrays.asList(role1),null,true));
//            User emunster = userService.saveUser(new UserDto(null,"emunster","Eddie","Munster","emunster@gmail.com","password","",0,Arrays.asList(role1),null,true));
//            User gmunster = userService.saveUser(new UserDto(null,"gmunster","Grandpa","Munster","gmunster@gmail.com","password","",0,Arrays.asList(role1),null,true));
//            User hsimpson = userService.saveUser(new UserDto(null,"hsimpson","Homer","Simpson","hsimpson@gmail.com","password","",0,Arrays.asList(role1),null,true));
//            User bsimpson = userService.saveUser(new UserDto(null,"bsimpson","Bart","Simpson","bsimpson@gmail.com","password","",0,Arrays.asList(role1),null,true));
//            User lsimpson = userService.saveUser(new UserDto(null,"lsimpson","Lisa","Simpson","lsimpson@gmail.com","password","",0,Arrays.asList(role1),null,true));
//            User msimpson = userService.saveUser(new UserDto(null,"msimpson","Marge","Simpson","msimpson@gmail.com","password","",0,Arrays.asList(role1),null,true));
//            User gaddams = userService.saveUser(new UserDto(null,"gaddams","Gomez","Addams","gaddams@gmail.com","password","",0,Arrays.asList(role1),null,true));
//            User maddams = userService.saveUser(new UserDto(null,"maddams","Morticia","Addams","maddams@gmail.com","password","",0,Arrays.asList(role1),null,true));
//            User fester = userService.saveUser(new UserDto(null,"faddams","Uncle","Fester","faddams@gmail.com","password","",0,Arrays.asList(role1),null,true));
//            User pugsly = userService.saveUser(new UserDto(null,"paddams","Pugsly","Addams","paddams@gmail.com","password","",0,Arrays.asList(role1),null,true));
//            User wed = userService.saveUser(new UserDto(null,"waddams","Wednesday","Addams","waddams@gmail.com","password","",0,Arrays.asList(role1),null,true));
//            User fredf = userService.saveUser(new UserDto(null,"fred","Fred","Flintstone","fredf@gmail.com","password","",0,Arrays.asList(role1),null,true));
//            User wilmaf = userService.saveUser(new UserDto(null,"wilma","Wilma","Flintstone","wilmaf@gmail.com","password","",0,Arrays.asList(role1),null,true));
//            User pebblesf = userService.saveUser(new UserDto(null,"pebbles","Pebbles","Flintstone","pebblesf@gmail.com","password","",0,Arrays.asList(role1),null,true));
//            User barney = userService.saveUser(new UserDto(null,"barney","Barney","Rubble","barney@gmail.com","password","",0,Arrays.asList(role1),null,true));
//            User betty = userService.saveUser(new UserDto(null,"betty","Betty","Rubble","betty@gmail.com","password","",0,Arrays.asList(role1),null,true));
//            User bambam = userService.saveUser(new UserDto(null,"bambam","BamBam","Rubble","bambam@gmail.com","password","",0,Arrays.asList(role1),null,true));
//            User george = userService.saveUser(new UserDto(null,"george","George","Jetson","george@gmail.com","password","",0,Arrays.asList(role1),null,true));
//            User jane = userService.saveUser(new UserDto(null,"jane","Jane","Jetson","jane@gmail.com","password","",0,Arrays.asList(role1),null,true));
//            User judy = userService.saveUser(new UserDto(null,"judy","Judy","Jetson","judy@gmail.com","password","",0,Arrays.asList(role1),null,true));
//            User elroy = userService.saveUser(new UserDto(null,"elroy","Elroy","Jetson","elroy@gmail.com","password","",0,Arrays.asList(role1),null,true));
//            User astro = userService.saveUser(new UserDto(null,"astro","Astro","Jetson","astro@gmail.com","password","",0,Arrays.asList(role1),null,true));
//            User rosey = userService.saveUser(new UserDto(null,"rosey","Rosey","Bot","rosey@gmail.com","password","",0,Arrays.asList(role1),null,true));

//  COURSES
//            Course bridge = courseService.save(new Course(null,"Franklin Bridge","(615) 794-9400","750 Riverview Dr, Franklin, TN 37064", 5, Collections.emptyList()));
//            Course towhee = courseService.save(new Course(null,"Towhee","(931) 486-1253","3901 Kedron Rd, Spring Hill, TN 37174", 4, Collections.emptyList()));
//            Course saddlecreek = courseService.save(new Course(null,"Saddle Creek","(931) 422-2014","1480 Fayetteville Hwy, Lewisburg, TN 37091", 4, Collections.emptyList()));
//            Course championsrun = courseService.save(new Course(null,"Champions Run","(615) 274-2301","14262 Mt Pleasant Rd, Rockvale, TN 37153", 4, Collections.emptyList()));
////  CROSSVILLE
//            Course druidhills = courseService.save(new Course(null,"Druid Hills","(931) 484-3711","435 Lakeview Dr, Crossville, TN 38558", 4, Collections.emptyList()));
//            Course heatherhurstcrag = courseService.save(new Course(null,"Heatherhurst Crag","(931) 484-3799","421 Stonehenge Dr, Crossville, TN 38558", 4, Collections.emptyList()));
//            Course heatherhurstbrae = courseService.save(new Course(null,"Heatherhurst Brae","(931) 484-3799","421 Stonehenge Dr, Crossville, TN 38558", 4, Collections.emptyList()));
//            Course stonehenge = courseService.save(new Course(null,"Stonehenge","(931) 484-3731","222 Fairfield Blvd, Crossville, TN 38558", 4, Collections.emptyList()));
//            Course dorchester = courseService.save(new Course(null,"Dorchester","(931) 484-3709","576 Westchester Dr, Crossville, TN 38558", 4, Collections.emptyList()));


//  EVENTS
            List<Player> players = Collections.emptyList();
            Course bridge = new Course();
            bridge.setId(1000L);
            Event event0 = eventService.add(new Event(null, LocalDate.now(), LocalTime.of(6,30,0) ,1, EventStatus.OPEN, players,null,null,null,bridge,null));
            Event event5 = eventService.add(new Event(null, LocalDate.now(), LocalTime.of(12,30,0) ,1, EventStatus.OPEN, players,null,null,null,bridge,null));
            Event event6 = eventService.add(new Event(null, LocalDate.now().plusDays(1), LocalTime.of(6,30,0) ,1, EventStatus.OPEN, players,null,null,null,bridge,null));
            Event event7 = eventService.add(new Event(null, LocalDate.now().plusDays(2), LocalTime.of(6,30,0) ,1, EventStatus.OPEN, players,null,null,null,bridge,null));
            Event event1 = eventService.add(new Event(null, LocalDate.now().plusDays(7), LocalTime.of(6,30,0) ,1, EventStatus.OPEN, players,null,null,null,bridge,null));
            Event event2 = eventService.add(new Event(null,LocalDate.now().plusDays(8), LocalTime.of(6,30,0) ,2, EventStatus.OPEN, players,null,null,null,bridge,null));
            Event event3 = eventService.add(new Event(null,LocalDate.now().plusDays(14), LocalTime.of(6,30,0) ,3,  EventStatus.OPEN,players,null,null,null,bridge,null));
            Event event4 = eventService.add(new Event(null,LocalDate.now().plusDays(15), LocalTime.of(6,30,0) ,4,  EventStatus.OPEN,players,null,null,null,bridge,null));

        };
    }
}