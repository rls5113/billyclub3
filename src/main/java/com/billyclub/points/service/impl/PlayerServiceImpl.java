package com.billyclub.points.service.impl;

import com.billyclub.points.exceptions.ResourceNotFoundException;
import com.billyclub.points.model.Player;
import com.billyclub.points.repository.PlayerRepository;
import com.billyclub.points.service.IService;
import jakarta.transaction.Transactional;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PlayerServiceImpl implements IService<Player> {
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

}
