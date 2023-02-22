package com.billyclub.points.controller;

import com.billyclub.points.model.Event;
import com.billyclub.points.model.Player;
import com.billyclub.points.model.assembler.EventModel;
import com.billyclub.points.model.assembler.EventModelAssembler;
import com.billyclub.points.model.assembler.PlayerModelAssembler;
import com.billyclub.points.service.impl.EventServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "api/v1/events")
//@CrossOrigin(origins = "http://localhost:8080")
public class EventController {
    private static final Logger LOGGER = LoggerFactory.getLogger(EventController.class);
//    @Autowired
    private final EventServiceImpl eventService;

    private final EventModelAssembler assembler;

    private final PlayerModelAssembler playerAssembler;
        @Autowired
    public EventController(EventServiceImpl eventService, EventModelAssembler assembler, PlayerModelAssembler playerAssembler) {
//    public EventController(EventService eventService) {
        this.eventService = eventService;
        this.assembler = assembler;
        this.playerAssembler = playerAssembler;
    }

    @GetMapping
    public CollectionModel<EventModel> getAllEvents() {
//    public ResponseEntity<List<Event>> getAllEvents() {

//        return new ResponseEntity<>(eventService.findAll(),HttpStatus.OK);
        return assembler.toCollectionModel(eventService.findAll());
    }
    @GetMapping("/{eventId}")
    public ResponseEntity<EventModel> findById(@PathVariable Long eventId) {
//    public ResponseEntity<Event> findById(@PathVariable Long eventId) {
        Event event = eventService.findById(eventId);
        EventModel eventModel = assembler.toModel(event);
        return new ResponseEntity<>(eventModel, HttpStatus.OK);
    }
    @PostMapping
    public ResponseEntity<EventModel> addEvent(@RequestBody Event event) {
//    public ResponseEntity<Event> addEvent(@RequestBody Event event) {
        Event addedEvent = eventService.add(event);
        EventModel eventModel = assembler.toModel(addedEvent);
        return new ResponseEntity<>( eventModel, HttpStatus.CREATED);
    }
    @PutMapping("/{eventId}")
    public ResponseEntity<EventModel> updateEvent(@PathVariable Long eventId, @RequestBody Event event ) {
//    public ResponseEntity<Event> updateEvent(@PathVariable Long eventId, @RequestBody Event event ) {
        Event addedEvent = eventService.update(eventId, event);
        EventModel eventModel = assembler.toModel(addedEvent);
        return new ResponseEntity<>( eventModel, HttpStatus.OK);
    }
    @DeleteMapping("/{eventId}")
    public ResponseEntity<EventModel> deleteEvent(@PathVariable final Long eventId) {
//    public ResponseEntity<Event> deleteEvent(@PathVariable final Long eventId) {
        Event deletedEvent = eventService.deleteById(eventId);
        EventModel eventModel = assembler.toModel(deletedEvent);
        return new ResponseEntity<>(eventModel, HttpStatus.OK);
    }
    @PostMapping("/{eventId}/players/{playerId}/add")
    public ResponseEntity<EventModel> addPlayerToEvent(@PathVariable final Long eventId,
                                                               @PathVariable Long playerId) {
//    public ResponseEntity<Event> addPlayerToEvent(@PathVariable final Long eventId,
//                                                               @PathVariable Long playerId) {
        Event event = eventService.addPlayerToEvent(eventId,playerId);
        EventModel eventModel = assembler.toModel(event);
        return new ResponseEntity<>(eventModel, HttpStatus.OK);
    }
    @PostMapping("/{eventId}/players/{playerId}/remove")
    public ResponseEntity<EventModel> removePlayerFromEvent(@PathVariable final Long eventId,
                                                               @PathVariable Long playerId) {
//    public ResponseEntity<Event> removePlayerFromEvent(@PathVariable final Long eventId,
//                                                               @PathVariable Long playerId) {
        Event event = eventService.removePlayerFromEvent(eventId,playerId);
        EventModel eventModel = assembler.toModel(event);
        return new ResponseEntity<>(eventModel, HttpStatus.OK);
    }
    @PostMapping("/{eventId}/players/add")
    public ResponseEntity<EventModel> addPlayerToEvent(@PathVariable final Long eventId,
                                                               @RequestBody Player player) {
//    public ResponseEntity<Event> addPlayerToEvent(@PathVariable final Long eventId,
//                                                               @RequestBody Player player) {
        Event event = eventService.findById(eventId);
        event.addPlayer(player);
        event = eventService.save(event);
        EventModel eventModel = assembler.toModel(event);
        return new ResponseEntity<>(eventModel, HttpStatus.OK);
    }
//    @GetMapping("/{eventId}/players")
//    public CollectionModel<PlayerModel> findPlayersForEvent(@PathVariable final Long eventId) {
//        Event event = eventService.findById(eventId);
//        List<Player> players = event.getPlayers();
//        CollectionModel<PlayerModel> playersModel = playerAssembler.toCollectionModel(players);
//        playersModel.add(linkTo(methodOn(EventController.class).findPlayersForEvent(eventId)).withRel("players"));
//        return playersModel;
//    }
}
