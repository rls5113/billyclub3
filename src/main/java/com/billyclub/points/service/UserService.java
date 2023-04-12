package com.billyclub.points.service;


import com.billyclub.points.dto.UserDto;
import com.billyclub.points.model.Role;
import com.billyclub.points.model.User;

import java.util.List;
import java.util.Optional;

public interface UserService extends IService<User, UserDto>{
    User saveUser(UserDto userDto);

    UserDto addUser(UserDto userDto);

    User findByEmail(String email);
    User findByUsername(String username);

    List<UserDto> findAllUsers();
    List<UserDto> findAllByActive();

    User findById(Long id);

    Role findByName(String rolename);

    User findByFullname(String fullname);

    public User updateResetPasswordToken(String token, String email);
    public User findByResetPasswordToken(String token);
    public void updatePassword(User user, String newPassword);
    public User transfer(UserDto source, User target);
}
