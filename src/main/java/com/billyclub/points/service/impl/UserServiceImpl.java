package com.billyclub.points.service.impl;


import com.billyclub.points.dto.UserDto;
import com.billyclub.points.exceptions.ResourceNotFoundException;
import com.billyclub.points.model.Role;
import com.billyclub.points.model.User;
import com.billyclub.points.repository.RoleRepository;
import com.billyclub.points.repository.UserRepository;
import com.billyclub.points.service.UserService;
import jakarta.transaction.Transactional;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
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
    @Transactional
    public User saveUser(UserDto userDto) {
        User user = new User();
        user.setName(userDto.getFirstName() + " " + userDto.getLastName());
        BeanUtils.copyProperties(userDto, user);

        //encrypt the password once we integrate spring security
        //user.setPassword(userDto.getPassword());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        if(user.getRoles().isEmpty()){
            Role role = roleRepository.findByName("ROLE_USER");
            if(role == null){
                role = checkRoleExist();
            }
            user.setRoles(Arrays.asList(role));
        }

        return userRepository.save(user);
    }
    @Override
    public UserDto addUser(UserDto userDto) {
        return toDto(saveUser(userDto));
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(()->new ResourceNotFoundException("User","email",email));
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
        return userRepository.findAll();
    }
   @Override
    public List<UserDto> findAllByActive() {
        return userRepository.findAllByActiveIsTrue().stream()
                .map(u -> toDto(u))
                .collect(Collectors.toList());
    }

    @Override
    public User add(User entity) {
        return userRepository.save(entity);
    }

    @Override
    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User","id",id));
    }

    @Override
    public Role findByName(String name) {
        return roleRepository.findByName(name);
    }

    @Override
    public User findByFullname(String fullname) {
        return userRepository.findUserByName(fullname);
    }

    @Override
    public User updateResetPasswordToken(String token, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(()->new ResourceNotFoundException("User","email",email));
        user.setResetPasswordToken(token);
        return userRepository.save(user);
    }

    @Override
    public User findByResetPasswordToken(String token) {
        return userRepository.findByResetPasswordToken(token)
                .orElseThrow(()->new ResourceNotFoundException("User","ResetPasswordToken","Value does not match."));
    }

    @Override
    public void updatePassword(User user, String newPassword) {
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setResetPasswordToken(null);
        userRepository.save(user);
    }

    @Override
    public User transfer(UserDto source, User target) {
        target.setPoints(source.getPoints());
        target.setEmail(source.getEmail());
        target.setActive(source.getActive());
        target.setMobile(source.getMobile());
        target.setName(source.getName());
        return target;
    }

    @Override
    public User update(Long id, User entity) {
        User userEdit = findById(id);
        BeanUtils.copyProperties(entity, userEdit);
        return userRepository.save(userEdit);
    }

    @Override
    public User deleteById(Long eventId) {
        return null;
    }

    @Override
    public User save(User entity) {
        return userRepository.save(entity);
    }

    public UserDto toDto(User user){
        UserDto userDto = new UserDto();
        String[] parts = user.getName().split(" ");
        userDto.setLastName(parts[parts.length-1]);

        StringBuilder b = new StringBuilder();
        //skip last part, put rest in firstname
        for(int i =0;i< parts.length-1; i++){
                b.append(parts[i]+" ");
        }
        userDto.setFirstName(b.toString());

        BeanUtils.copyProperties(user, userDto);
        return userDto;
    }

    @Override
    public User toEntity(UserDto dto) {
        User user = new User();
        BeanUtils.copyProperties(dto, user);
        return user;
    }

    private Role checkRoleExist() {
        Role role = new Role();
        role.setName("ROLE_USER");
        return roleRepository.save(role);
    }

}
