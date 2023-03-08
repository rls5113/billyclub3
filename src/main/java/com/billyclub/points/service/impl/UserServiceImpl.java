package com.billyclub.points.service.impl;


import com.billyclub.points.dto.UserDto;
import com.billyclub.points.exceptions.ResourceNotFoundException;
import com.billyclub.points.model.Role;
import com.billyclub.points.model.User;
import com.billyclub.points.repository.RoleRepository;
import com.billyclub.points.repository.UserRepository;
import com.billyclub.points.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.thymeleaf.util.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
@Service
public class UserServiceImpl implements UserService {
    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository,
                           RoleRepository roleRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User saveUser(UserDto userDto) {
        User user = new User();
        user.setName(userDto.getFirstName() + " " + userDto.getLastName());
        user.setEmail(userDto.getEmail());
        user.setUsername(userDto.getUsername());
        user.setPoints(0);

        //encrypt the password once we integrate spring security
        //user.setPassword(userDto.getPassword());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        Role role = roleRepository.findByName("ROLE_USER");
//        if(role == null){
//            role = checkRoleExist();
//        }
        user.setRoles(Arrays.asList(role));
        return userRepository.save(user);
    }
    @Override
    public UserDto addUser(UserDto userDto) {
        return toDto(saveUser(userDto));
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public List<UserDto> findAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream().map((user) -> toDto(user))
                .collect(Collectors.toList());
    }

    @Override
    public List<User> findAll() {
        return null;
    }

    @Override
    public User add(User entity) {
        return null;
    }

    @Override
    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User","id",id));
    }

    @Override
    public User update(Long id, User entity) {
        return null;
    }

    @Override
    public User deleteById(Long eventId) {
        return null;
    }

    @Override
    public User save(User entity) {
        return null;
    }

    public UserDto toDto(User user){
        UserDto userDto = new UserDto();
        if(user.getName().contains(" ")){
            String[] name = user.getName().split(" ");
            userDto.setFirstName(name[0]);
            userDto.setLastName(name[1]);
        }else {
            userDto.setFirstName(user.getName());
        }

        BeanUtils.copyProperties(user, userDto);
        return userDto;
    }

    @Override
    public User toEntity(UserDto dto) {
        return null;
    }

//    private Role checkRoleExist() {
//        Role role = new Role();
//        role.setName("ROLE_USER");
//        return roleRepository.save(role);
//    }

}
