package com.billyclub.points.service.impl;

import com.billyclub.points.dto.PlayerDto;
import com.billyclub.points.dto.PlayerScoresHolderDto;
import com.billyclub.points.exceptions.ResourceNotFoundException;
import com.billyclub.points.model.Player;
import com.billyclub.points.model.User;
import com.billyclub.points.repository.PlayerRepository;
import com.billyclub.points.service.PlayerService;
import jakarta.transaction.Transactional;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Service
public class PlayerServiceImpl implements PlayerService {
    @Autowired
    private final PlayerRepository repo;

    public PlayerServiceImpl(PlayerRepository repo) {
        this.repo = repo;
    }


    @Override
    public List<Player> findAll() {
        return repo.findAll();
    }

    @Override
//    @Transactional
    public Player add(Player player) {
        if(player.getTimeEntered()==null)
            player.setTimeEntered(LocalDateTime.now());
        return repo.save(player);
//        return player;
    }

    @Override
    public Player findById(Long playerId) {
        return repo.findById(playerId)
                .orElseThrow(() -> new ResourceNotFoundException("Player","id",playerId));
    }

    @Override
    @Transactional
    public Player update(Long playerId, Player player) {
        Player playerEdit = findById(playerId);
        if(playerEdit.getTimeEntered() != null)
            player.setTimeEntered(playerEdit.getTimeEntered());
        BeanUtils.copyProperties(player, playerEdit);
//        return playerEdit;
        return repo.save(playerEdit);
    }

    @Override
    public Player deleteById(Long playerId) {
        Player player = findById(playerId);
        repo.deleteById(playerId);
        return player;
    }

    @Override
    public Player save(Player entity) {
        return repo.save(entity);
    }

    @Override
    public PlayerDto toDto(Player entity) {
        PlayerDto dto = new PlayerDto();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }

    @Override
    public Player toEntity(PlayerDto dto) {
        Player entity = new Player();
        BeanUtils.copyProperties(dto, entity);
        return entity;
    }

    @Override
    public Player create(User user) {
        Player player = new Player(null, user.getName(), user.getPoints(), 0, 0, 0, LocalDateTime.now(), Boolean.FALSE, null,null,null,null);
        return repo.save(player);
    }

    @Override
    public void setScoring(Player player, PlayerScoresHolderDto holder) {
        player.setScoreForEvent(holder.getScoreForEvent());
        player.setEagles(Arrays.asList(holder.getEagles()));
        player.setBirdies(Arrays.asList(holder.getBirdies()));
        //for first timers, set todays score as total
        player.setTotal((player.getQuota()==0)? player.getQuota() : holder.getScoreForEvent() - player.getQuota() );
        player.setAdjustment(calcAdjustment(player.getTotal()));
        this.save(player);
    }
    private int calcAdjustment(int total) {
        int adjustment = 0;
        switch(total){
            case 6,7,8,9,10,11,12,13,14,15,16,17,18,19,20 -> {
                adjustment = 3;
            }
            case 4,5 -> {
                adjustment = 2;
            }
            case 2,3 -> {
                adjustment = 1;
            }
            case -2,-3 -> {
                adjustment = -1;
            }
            case -4,-5 -> {
                adjustment = -2;
            }
            case -6,-7,-8,-9,-10,-11,-12,-13,-14,-15,-16,-17,-18,-19,-20 -> {
                adjustment = -3;
            }

        }
        return adjustment;
    }
}
