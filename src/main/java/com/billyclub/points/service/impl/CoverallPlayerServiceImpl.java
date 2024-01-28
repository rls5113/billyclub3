package com.billyclub.points.service.impl;

import com.billyclub.points.dto.CoverallPlayerDto;
import com.billyclub.points.exceptions.ResourceNotFoundException;
import com.billyclub.points.model.CoverallPlayer;
import com.billyclub.points.model.User;
import com.billyclub.points.repository.CoverallPlayerRepository;
import com.billyclub.points.service.CoverallPlayerService;
import jakarta.transaction.Transactional;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CoverallPlayerServiceImpl implements CoverallPlayerService {
    @Autowired
    private final CoverallPlayerRepository repo;

    public CoverallPlayerServiceImpl(CoverallPlayerRepository repo) {
        this.repo = repo;
    }


    @Override
    public List<CoverallPlayer> findAll() {
        return repo.findAll();
    }

    @Override
//    @Transactional
    public CoverallPlayer add(CoverallPlayer player) {
        return repo.save(player);
//        return player;
    }

    @Override
    public CoverallPlayer findById(Long playerId) {
        return repo.findById(playerId)
                .orElseThrow(() -> new ResourceNotFoundException("CoverallPlayer","id",playerId));
    }

    @Override
    @Transactional
    public CoverallPlayer update(Long playerId, CoverallPlayer player) {
        CoverallPlayer playerEdit = findById(playerId);
        BeanUtils.copyProperties(player, playerEdit);
//        return playerEdit;
        return repo.save(playerEdit);
    }

    @Override
    public CoverallPlayer deleteById(Long playerId) {
        CoverallPlayer player = findById(playerId);
        repo.deleteById(playerId);
        return player;
    }

    @Override
    public CoverallPlayer save(CoverallPlayer entity) {
        return repo.save(entity);
    }

    @Override
    public CoverallPlayerDto toDto(CoverallPlayer entity) {
        CoverallPlayerDto dto = new CoverallPlayerDto();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }

    @Override
    public CoverallPlayer toEntity(CoverallPlayerDto dto) {
        CoverallPlayer entity = new CoverallPlayer();
        BeanUtils.copyProperties(dto, entity);
        return entity;
    }

    @Override
    public CoverallPlayer create(User user) {
        CoverallPlayer player = new CoverallPlayer();
        player.setName(user.getName());
        player.setIsFirstPlay(Boolean.TRUE);
        return repo.save(player);
    }

}
