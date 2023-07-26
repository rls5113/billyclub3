package com.billyclub.points.service.impl;

import com.billyclub.points.dto.CourseDto;
import com.billyclub.points.exceptions.ResourceNotFoundException;
import com.billyclub.points.model.Course;
import com.billyclub.points.repository.CourseRepository;
import com.billyclub.points.service.CourseService;
import jakarta.transaction.Transactional;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CourseServiceImpl implements CourseService {
    private final CourseRepository courseRepository;

    @Autowired
    public CourseServiceImpl(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    @Override
    public List<Course> findAll() {
        return courseRepository.findAll().stream()
                .sorted((c1,c2)-> c1.getId().compareTo(c2.getId()))
                .collect(Collectors.toList());
    }

    @Override
    public Course add(Course entity) {
        return courseRepository.save(entity);
    }

    @Override
    public Course findById(Long courseId) {
        return courseRepository.findById(courseId)
                .orElseThrow(()->new ResourceNotFoundException("Course","id", courseId));
    }

    @Override
    @Transactional
    public Course update(Long id, Course course) {
        Course courseToEdit = findById(id);
        BeanUtils.copyProperties(course, courseToEdit);
        return courseRepository.save(courseToEdit);
    }

    @Override
    public Course deleteById(Long courseId) {
        Course courseToDelete = findById(courseId);
        courseRepository.deleteById(courseId);
        return courseToDelete;
    }

    @Override
    public Course save(Course entity) {
        return courseRepository.save(entity);
    }

    @Override
    public CourseDto toDto(Course entity) {
        CourseDto courseDto = new CourseDto();
        BeanUtils.copyProperties(entity, courseDto);
        return courseDto;
    }

    @Override
    public Course toEntity(CourseDto dto) {
        Course course = new Course();
        BeanUtils.copyProperties(dto, course);
        return course;
    }
}
