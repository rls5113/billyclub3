package com.billyclub.points.service;


import com.billyclub.points.dto.UserDto;
import com.billyclub.points.model.User;

import java.util.List;

public interface UserService {
    void saveUser(UserDto userDto);

    User findByEmail(String email);

    List<UserDto> findAllUsers();
}
