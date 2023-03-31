package com.billyclub.points.repository;

import com.billyclub.points.model.Course;
import com.billyclub.points.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CourseRepository extends JpaRepository<Course, Long> {


}
