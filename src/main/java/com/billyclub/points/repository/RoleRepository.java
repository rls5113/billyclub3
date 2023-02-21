package com.billyclub.points.repository;

import com.billyclub.points.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long>   {
    Role findByName(String name);
}
