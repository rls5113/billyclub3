package com.billyclub.points.service;


import com.billyclub.points.dto.UserDto;
import com.billyclub.points.model.User;

import java.util.List;

public interface UserService {
    User saveUser(UserDto userDto);

    UserDto addUser(UserDto userDto);

    User findByEmail(String email);
    User findByUsername(String username);

    List<UserDto> findAllUsers();
}
