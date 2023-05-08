package com.billyclub.points.repository;

import com.billyclub.points.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    User findByUsername(String username);

    User findUserByName(String fullname);

    Optional<User> findByResetPasswordToken(String token);
    List<User> findAllByActiveIsTrue();

    @Query("select u from users u join u.roles r where r.name='ROLE_ADMIN'")
    List<User> findUsersByRoles();


}
