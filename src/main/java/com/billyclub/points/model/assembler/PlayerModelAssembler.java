package com.billyclub.points.model.assembler;

import com.billyclub.points.controller.PlayerController;
import com.billyclub.points.model.Player;
import org.springframework.beans.BeanUtils;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class PlayerModelAssembler extends RepresentationModelAssemblerSupport<Player, PlayerModel> {

    public PlayerModelAssembler() {super(PlayerController.class, PlayerModel.class);}
    @Override
    public PlayerModel toModel(Player player) {
        PlayerModel playerModel = instantiateModel(player);
        BeanUtils.copyProperties(player,playerModel);
        playerModel.add(linkTo(methodOn(PlayerController.class).findById(player.getId())).withSelfRel());
        playerModel.add(linkTo(methodOn(PlayerController.class).getAllPlayers()).withRel(IanaLinkRelations.COLLECTION));
        return playerModel;
    }

    @Override
    public CollectionModel<PlayerModel> toCollectionModel(Iterable<? extends Player> players){
        CollectionModel<PlayerModel> playerModels = super.toCollectionModel(players);
        playerModels.add(linkTo(methodOn(PlayerController.class).getAllPlayers()).withSelfRel());
        return playerModels;
    }
}
