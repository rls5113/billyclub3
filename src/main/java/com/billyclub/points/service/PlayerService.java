package com.billyclub.points.service;

import com.billyclub.points.dto.PlayerDto;
import com.billyclub.points.model.Player;
import com.billyclub.points.model.User;

public interface PlayerService extends IService<Player, PlayerDto> {

    public Player create(User user);
}
