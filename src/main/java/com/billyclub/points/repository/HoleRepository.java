package com.billyclub.points.repository;

import com.billyclub.points.model.Hole;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HoleRepository extends JpaRepository<Hole, Long> {
}
