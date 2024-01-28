package com.billyclub.points.service;

import com.billyclub.points.dto.CoverallPlayerDto;
import com.billyclub.points.model.CoverallPlayer;
import com.billyclub.points.model.User;

public interface CoverallPlayerService extends IService<CoverallPlayer, CoverallPlayerDto> {

    public CoverallPlayer create(User user);
}
