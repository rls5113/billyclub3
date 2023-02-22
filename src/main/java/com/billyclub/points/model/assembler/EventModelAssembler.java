package com.billyclub.points.model.assembler;

import com.billyclub.points.controller.EventController;
import com.billyclub.points.controller.PlayerController;
import com.billyclub.points.model.Event;
import com.billyclub.points.model.Player;
import org.springframework.beans.BeanUtils;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class EventModelAssembler extends RepresentationModelAssemblerSupport<Event, EventModel> {

    public EventModelAssembler() {
        super(EventController.class, EventModel.class);
    }

    @Override
    public EventModel toModel(Event event) {
        EventModel eventModel = instantiateModel(event);
        BeanUtils.copyProperties(event,eventModel);
        eventModel.setPlayers(toPlayerModel(event.getPlayers()));
        eventModel.add(linkTo(methodOn(EventController.class).findById(event.getId())).withSelfRel());
        eventModel.add(linkTo(methodOn(EventController.class).getAllEvents()).withRel(IanaLinkRelations.COLLECTION));
        return eventModel;
    }

    @Override
    public CollectionModel<EventModel> toCollectionModel(Iterable<? extends Event> events){
        CollectionModel<EventModel> eventModels = super.toCollectionModel(events);
        eventModels.add(linkTo(methodOn(EventController.class).getAllEvents()).withSelfRel());
        return eventModels;
    }

    private List<PlayerModel> toPlayerModel(List<Player> players) {
        if(players.isEmpty())
            return Collections.EMPTY_LIST;
        return players.stream()
                .map(player -> PlayerModel.builder()
                        .id(player.getId())
                        .name(player.getName())
                        .pointsToPull(player.getPointsToPull())
                        .timeEntered(player.getTimeEntered())
                        .build()
                        .add(linkTo(methodOn(PlayerController.class).findById(player.getId()))
                                .withSelfRel()))
                .collect(Collectors.toList());
    }
}
