package com.billyclub.points.service.impl;

import com.billyclub.points.controller.AuthController;
import com.billyclub.points.dto.EventDto;
import com.billyclub.points.dto.TeamDto;
import com.billyclub.points.exceptions.ResourceNotFoundException;
import com.billyclub.points.model.Event;
import com.billyclub.points.model.EventStatus;
import com.billyclub.points.model.Player;
import com.billyclub.points.model.User;
import com.billyclub.points.repository.EventRepository;
import com.billyclub.points.service.EmailService;
import com.billyclub.points.service.EventService;
import com.billyclub.points.service.PlayerService;
import com.billyclub.points.service.UserService;
import jakarta.mail.MessagingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class EventServiceImpl implements EventService {
    @Autowired
    private final EventRepository eventRepository;
    @Autowired
    private final PlayerService playerService;
    @Autowired
    private final UserService userService;
    @Autowired
    private final EmailService emailService;
    private static final Logger log = LoggerFactory.getLogger(EventServiceImpl.class);
    public EventServiceImpl(EventRepository eventRepository, PlayerServiceImpl playerService, UserService userService, EmailService emailService) {
        this.eventRepository = eventRepository;
        this.playerService = playerService;
        this.userService = userService;
        this.emailService = emailService;
    }


    @Override
    public List<Event> findAll() {
        return eventRepository.findAll();
    }

    @Override
    public Event add(Event event) {
        return eventRepository.save(event);
    }

    @Override
    public Event findById(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event","id",eventId));
    }

    @Override
    @Transactional
    public Event update(Long eventId, Event event) {
        Event eventToEdit = findById(eventId);
        BeanUtils.copyProperties(event, eventToEdit);
        return eventRepository.save(eventToEdit);
    }

    @Override
    public Event deleteById(Long eventId) {
        Event eventToDelete = findById(eventId);
        eventRepository.deleteById(eventId);
        return eventToDelete;
    }

    @Override
    public Event save(Event entity) {
        return eventRepository.save(entity);
    }

    @Transactional
    @Override
    public Event addPlayerToEvent(Long eventId, Long playerId) {
        Event event = findById(eventId);
        Player player = playerService.findById(playerId);
        if(!event.isPlayerInEvent(player.getName())) {
            event.addPlayer(player);
        }
        return eventRepository.save(event);
    }
    @Override
    public Event removePlayerFromEvent(Long eventId, Long playerId) {
        Event event = findById(eventId);
        Player player = playerService.findById(playerId);
        boolean removedFromWaitingList = player.getIsWaiting();
        event.removePlayer(player);
        if(!removedFromWaitingList) {
            Player nextPlayer = event.getNextPlayerWaiting();
            if (nextPlayer != null) {
                event.addPlayer(nextPlayer);
                event.setEmailRecipient(nextPlayer);
            }
        }
        playerService.deleteById(playerId);
        return eventRepository.save(event);
    }

    @Override
    public List<EventDto> findOpenEvents() {
        List<Event> events = eventRepository.findEventsByEventDateGreaterThanEqual(LocalDate.now());
        List<EventDto> list = events.stream().map((event)-> toDto(event))
                .collect(Collectors.toList());
        list.sort((e1, e2) -> (e1.getEventDate().compareTo(e2.getEventDate())));
//        Collections.sort(list,
//                Comparator.comparing(EventDto::getEventDate)
//                        .thenComparing(EventDto::getStartTime));
        return list;
    }

    @Override
    public Event calculateEventScoreboard(Long eventId) {
        Event event = findById(eventId);
        //decide winners
        calculateWinners(event);
        //do scat logic
        calculateScats(event);
        //update future events with adjusted scores
        updateAdjustedScores(event);
        //set status to closed
        event.setStatus(EventStatus.COMPLETED);
        return save(event);
    }

    @Override
    public List<EventDto> findPastEvents() {
        List<Event> events = eventRepository.findEventsPast(LocalDate.now());
        List<EventDto> list = events.stream().map((event)-> toDto(event))
                .collect(Collectors.toList());
        list.sort((e2, e1) -> (e1.getEventDate().compareTo(e2.getEventDate())));
        return list;    }

    @Override
    public Event transfer(EventDto source, Event target) {
        if (source.getEventDate() != null)
            target.setEventDate(source.getEventDate());
        target.setStartTime(source.getStartTime());
        target.setNumOfTimes(source.getNumOfTimes());
        target.setStatus(source.getStatus());
        target.setCourse(source.getCourse());

        return target;
    }

    @Override
    public List<Player> recalculateWaitingList(Event event) {
        List<Player> players = event.getPlayers();
//        boolean timesFull = players.size() >= event.getNumOfTimes() * event.getCourse().getMaxPlayersPerGroup();
        int maxPlayers = event.getNumOfTimes() * event.getCourse().getMaxPlayersPerGroup();
        List<Player> movedFromWaitingList = new ArrayList<>();
        for(int i=0;i<players.size();i++){
            Player player = players.get(i);
            Boolean onWaitlist = player.getIsWaiting();
            if(i < maxPlayers)     {
                player.setIsWaiting(Boolean.FALSE);
                if(onWaitlist)  movedFromWaitingList.add(player);
            }
            else  {
                player.setIsWaiting(Boolean.TRUE);
            }
        }
        return movedFromWaitingList;
     }

    private void updateAdjustedScores(Event event) {
        log.info("calculateWinners:  event "+ event.getId());
        List<Player> eventPlayers = event.getPlayers().stream()
                .filter(p -> !p.getIsWaiting() && !p.getIsWithdrawal())
                .collect(Collectors.toList());

        for (Player p : eventPlayers) {
            System.out.println(p.getName());
            int updatedQuota = (p.getQuota() == 0) ? p.getTotal() : p.getQuota() + p.getAdjustment();
            User user = userService.findByFullname(p.getName());
            user.setPoints(updatedQuota);
            userService.save(user);
            List<Event> playerEvents = eventRepository.findFutureEventsWithPlayerName(event.getEventDate() , p.getName());
            playerEvents.remove(event);
            if(!playerEvents.isEmpty()){
                for(Event e : playerEvents) {
                    Player playerWithName = e.getPlayers().stream()
                            .filter(p1 -> p1.getName().equals(p.getName()))
                            .findAny().orElse(null);
                    if(playerWithName != null){
                        playerWithName.setQuota(updatedQuota);
                        log.info("Updating quota for "+playerWithName.getName()+" for eventId: "+event.getId());
                    }
                    this.save(e);
                }
            }
        }

    }

    private List<Player> getsMoneyBack(List<Player> eventPlayers){
        log.info("getsMoneyBack:  event ");
        List<Player> losers = new ArrayList<>();
        if (eventPlayers.size() > 7 && eventPlayers.size() % 2 != 0) {
            Player lowestScore = eventPlayers.stream()
                    .filter(p -> !p.getIsWaiting() && !p.getIsWithdrawal())
                    .min(Comparator.comparing(Player::getTotal))
                    .get();
            losers.add(lowestScore);
            //check for ties
            for(Player player : eventPlayers){
                if(player.getTotal().equals(lowestScore.getTotal()) && !player.getName().equals(lowestScore.getName())){
                    losers.add(player);
                }
            }

        }
        return losers;
    }
    private void calculateScats(Event event) {
        log.info("calculateScats:  event "+ event.getId());
        List<Player> players = event.getPlayers().stream()
                .filter(p -> !p.getIsWaiting())
                .collect(Collectors.toList());
        List<String> eagles = new ArrayList<>();
        List<String> birds = new ArrayList<>();
        for(int holeNum=1; holeNum <=18; holeNum++){
            for (Player player: players){
                if(player.getEagles().contains(String.valueOf(holeNum))) {
                    eagles.add(holeNum + ": "+player.getName()+"  Eagle");
                }
                if(player.getBirdies().contains(String.valueOf(holeNum))) {
                    birds.add(holeNum + ": "+player.getName()+"  Birdie");
                }
            }
        }
        //skip calcs if no birds or eagles
        if(eagles.isEmpty() && birds.isEmpty())  {
            List<String> list = new ArrayList<>();
            list.add("No scats today! You guys suck.");
            event.setScatWinners(list);
            return;
        }

        if(!eagles.isEmpty()){
            //iterate and cut birds
            for(String e : eagles){
                String[] eagleParts = e.split(":");
                for(String t: birds){
                    String[] birdParts = t.split(":");
                    if(birdParts[0].equals(eagleParts[0])){
                        StringBuilder b = new StringBuilder(t);
                        b.append(" CUT by eagle");
                        birds.set(birds.indexOf(t),  b.toString());
                    }
                }
            }
            //determine if eagles are cut
            for(int i = 0; i < eagles.size()-1; i++){
                String[] first = eagles.get(i).split(":");
                String[] second = eagles.get(i+1).split(":");
                if(first[0].equals(second[0])){
                    if(!first[1].contains("CUT")){
                        StringBuilder b = new StringBuilder(eagles.get(i));
                        b.append(" CUT");
                        eagles.set(i,  b.toString());
                    }
                    if(!second[1].contains("CUT")){
                        StringBuilder b = new StringBuilder(eagles.get(i+1));
                        b.append(" CUT");
                        eagles.set(i+1,  b.toString());
                    }
                }
            }
        }
        if(!birds.isEmpty()){
            for(int i = 0; i < birds.size()-1; i ++) {
                String[] first = birds.get(i).split(":");
                String[] second = birds.get(i+1).split(":");

                if(first[0].equals(second[0])){
                    if(!first[1].contains("CUT")){
                        StringBuilder b = new StringBuilder(birds.get(i));
                        b.append(" CUT");
                        birds.set(i,  b.toString());
                    }
                    if(!second[1].contains("CUT")){
                        StringBuilder b = new StringBuilder(birds.get(i+1));
                        b.append(" CUT");
                        birds.set(i+1,  b.toString());
                    }
                }
            }
        }

        List<String> scats = eagles.stream()
                .filter(s -> !s.contains("CUT"))
                        .collect(Collectors.toList());
        scats.addAll(birds.stream()
                .filter(s -> !s.contains("CUT"))
                .collect(Collectors.toList()));

        int scatWorth = (scats.size() == 0) ? 0 : players.size() * 5 / scats.size() ;
        for(int i = 0; i < scats.size();i++) {
            StringBuilder s = new StringBuilder(scats.get(i));
            s.append("  $"+ scatWorth);
            scats.set(i, s.toString());
        }
        List<String> summary = eagles.stream()
                .filter(s -> s.contains("CUT"))
                .collect(Collectors.toList());
        summary.addAll(birds.stream()
                .filter(s -> s.contains("CUT"))
                .collect(Collectors.toList()));
        Collections.sort(scats,(s1,s2)-> Integer.valueOf(s1.substring(0,s1.indexOf(":"))).compareTo(Integer.valueOf(s2.substring(0,s2.indexOf(":")))));
        Collections.sort(summary,(s1,s2)-> Integer.valueOf(s1.substring(0,s1.indexOf(":"))).compareTo(Integer.valueOf(s2.substring(0,s2.indexOf(":")))));
        event.setScatWinners(scats);
        event.setScatSummary(summary);

    }
    private List<Player> getWinnerTakesAll(List<Player> eventPlayers){
        log.info(" getWinnerTakes all: ");
        List<Player> winners = new ArrayList<>();
        if (eventPlayers.size() < 8) {
            //code for more than one have same best score.

           Player highestScore = eventPlayers.stream()
                    .filter(p -> !p.getIsWaiting() || !p.getIsWithdrawal())
                    .max(Comparator.comparing(Player::getTotal))
                    .get();
            winners.add(highestScore);
            //find if more than one has this score

            for(Player player : eventPlayers){
                if(player.getTotal().equals(highestScore.getTotal()) && !player.getName().equals(highestScore.getName())){
                    winners.add(player);
                }
            }
        }
        return winners;
    }
    private int calculateWinnerTakesAllWinnings(List<Player> eventPlayers){

            List<Player> players = eventPlayers.stream()
                    .filter(p -> !p.getIsWaiting())
                    .collect(Collectors.toList());
        return 5 * players.size();
    }
    private void pickTeams(Event event, List<Player> eventPlayers){
        log.info("pickTeams: ");
        int MAX_TEAMS = 12;
        //shuffle 3 times
        for(int i = 0; i <3; i++){
            Collections.shuffle(eventPlayers);
        }
        //determine number of teams
        List<TeamDto> list = new ArrayList<>();

        for(int i=0;i<MAX_TEAMS;i++){
            TeamDto dto = new TeamDto("Team "+(i+1),0);
            list.add(dto);
        }
        for(int i =0; i < eventPlayers.size();i++){
            TeamDto teamDto=null;
            switch (i){
                case 0,1:
                    teamDto = list.get(0);
                    break;
                case 2,3:
                    teamDto = list.get(1);
                    break;
                case 4,5:
                    teamDto = list.get(2);
                    break;
                case 6,7:
                    teamDto = list.get(3);
                    break;
                case 8,9:
                    teamDto = list.get(4);
                    break;
                case 10,11:
                    teamDto = list.get(5);
                    break;
                case 12,13:
                    teamDto = list.get(6);
                    break;
                case 14,15:
                    teamDto = list.get(7);
                    break;
                case 16,17:
                    teamDto = list.get(8);
                    break;
                case 18,19:
                    teamDto = list.get(9);
                    break;
                case 20,21:
                    teamDto = list.get(10);
                    break;
                case 22,23:
                    teamDto = list.get(11);
                    break;
            }
            Player player = eventPlayers.get(i);
            teamDto.getTeam().add(player.getName());
            teamDto.setScore(teamDto.getScore() + player.getTotal());
            player.setTeam(teamDto.getName());

        }
        List<TeamDto> filtered = list.stream()
                        .filter(t -> !t.getTeam().isEmpty())
                                .sorted((t1,t2)-> t2.getScore().compareTo(t1.getScore()))
                                        .collect(Collectors.toList());

        List<TeamDto> winners = new ArrayList<>();
        List<String> results = new ArrayList<>();

        TeamDto highestScore = filtered.stream()
                .max(Comparator.comparing(TeamDto::getScore))
                .get();
        winners.add(highestScore);
        //any ties
        for(TeamDto t: filtered) {
            if(highestScore.getScore() == t.getScore() && !highestScore.getName().equals(t.getName())){
                winners.add(t);
            }
        }
        if(winners.size()>1){
            List<String> names = winners.stream().map(p->p.getName()).collect(Collectors.toList());
            results.add("Tied for best score: " + names.toString());
            for(int i =0;i<2;i++) {
                Collections.shuffle(winners);
            }
        }
        int money = eventPlayers.size() * 5;
        results.add("Team money is $"+money+"    each person: $"+ money/2);

        TeamDto winner = winners.get(0);
        results.add("WINNER!  "+winner.getName() + ":   "+winner.getTeam()+"   "+winner.getScore());
        filtered.remove(winner);
         for(TeamDto team : filtered) {
            results.add(team.getName() + ":   "+team.getTeam()+"   "+team.getScore());
        }
        results.addAll(event.getEventWinners());
        event.setEventWinners(results);
    }
    private void calculateWinners(Event event) {
        log.info("calculateWinners:  event "+ event.getId());
        //remove new players and still on waiting list
        List<Player> eventPlayers = event.getPlayers().stream()
                .filter(p -> !p.getIsWaiting() && !(p.getQuota() == 0) && !p.getIsWithdrawal())
                .collect(Collectors.toList());

        if(!eventPlayers.isEmpty()) {
            List<Player> losers = this.getsMoneyBack(eventPlayers);
            if (!losers.isEmpty()) {
                //if more than one, pick the winner
                List<String> moneyBackList = new ArrayList<>();
                if (losers.size() > 1) {
                    List<String> names = losers.stream().map(p -> p.getName()).collect(Collectors.toList());
                    moneyBackList.add("Tied for worst score to get money back: " + names.toString());
                    for (int i = 0; i < 2; i++) {
                        Collections.shuffle(losers);
                    }
                }
                Player getsMoneyBack = losers.get(0);

                getsMoneyBack.setTeam("Five dollars back");
                moneyBackList.add(getsMoneyBack.getName() + " - Five dollars back. Total: " + getsMoneyBack.getTotal());
                event.getEventWinners().addAll(moneyBackList);
                //remove from list for teams
                eventPlayers.remove(getsMoneyBack);

            }

            if (eventPlayers.size() < 8) {  //winner takes all
                List<String> winnerListing = new ArrayList<>();
                List<Player> winners = this.getWinnerTakesAll(eventPlayers);
                //if more than one, pick the winner
                if (winners.size() > 1) {
                    List<String> names = winners.stream().map(p -> p.getName()).collect(Collectors.toList());
                    winnerListing.add("Tied for best score: " + names.toString());
                    for (int i = 0; i < 2; i++) {
                        Collections.shuffle(winners);
                    }
                }
                Player winner = winners.get(0);


                winnerListing.add("WINNER IS : " + winner.getName()
                        + "!  Needed " + winner.getQuota() + " points, "
                        + "pulled " + winner.getScoreForEvent() + " today, for a " + ((winner.getTotal() > 0) ? "+ " : "- ")
                        + winner.getTotal() + " total.");
                winnerListing.add(" Pays : $" + calculateWinnerTakesAllWinnings(eventPlayers));
                event.setEventWinners(winnerListing);

            } else {

                pickTeams(event, eventPlayers);

            }
        }
        List<String> displayMessages = new ArrayList<>();
        //first timers
        List<Player> firstTimers =  event.getPlayers().stream()
                .filter(p -> !p.getIsWaiting() && (p.getQuota() == 0) )
                .collect(Collectors.toList());
        if(!firstTimers.isEmpty()){
            for(Player p: firstTimers){
                displayMessages.add("FIRST TIMER (not included in team pairings): "+p.getName());
            }
        }

        //first timers
        List<Player> withdrawn =  event.getPlayers().stream()
                .filter(p -> (p.getIsWithdrawal()) )
                .collect(Collectors.toList());
        if(!withdrawn.isEmpty()){
            for(Player p: withdrawn){
                displayMessages.add("WITHDREW FROM EVENT (not included in team pairings): "+p.getName());
            }
        }
        if(!displayMessages.isEmpty())
            event.getEventWinners().addAll(displayMessages);
    }
    @Override
    public List<EventDto> findAllEvents() {
        List<Event> events = eventRepository.findAll();
        List<EventDto> list = events.stream().map((event)-> toDto(event))
                .collect(Collectors.toList());
        list.sort((e1, e2) -> (e1.getEventDate().compareTo(e2.getEventDate())));
//        Collections.sort(list, (e1, e2) -> (e1.getEventDate().compareTo(e2.getEventDate())));
        return list;
    }

    @Override
    public EventDto toDto(Event event) {
        EventDto eventDto = new EventDto();
        BeanUtils.copyProperties(event, eventDto);
        return eventDto;
    }

    @Override
    public Event toEntity(EventDto dto) {
        Event event = new Event();
        BeanUtils.copyProperties(dto, event);
        return event;
    }

}
