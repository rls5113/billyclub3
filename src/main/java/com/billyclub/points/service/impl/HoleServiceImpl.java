package com.billyclub.points.service.impl;

import com.billyclub.points.dto.HoleDto;
import com.billyclub.points.exceptions.ResourceNotFoundException;
import com.billyclub.points.model.Hole;
import com.billyclub.points.repository.HoleRepository;
import com.billyclub.points.service.HoleService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class HoleServiceImpl implements HoleService {
    private final HoleRepository holeRepository;

    @Autowired
    public HoleServiceImpl(HoleRepository holeRepository) {
        this.holeRepository = holeRepository;
    }

    @Override
    public List<Hole> findAll() {
        return holeRepository.findAll().stream()
                .sorted((h1,h2) -> h1.getNum().compareTo(h2.getNum()))
                .collect(Collectors.toList());
    }

    @Override
    public Hole add(Hole entity) {
        return holeRepository.save(entity);
    }

    @Override
    public Hole findById(Long id) {
        return holeRepository.findById(id)
                .orElseThrow(()->new ResourceNotFoundException("Hole","id",id));
    }

    @Override
    public Hole update(Long id, Hole entity) {
        Hole holeToEdit = findById(id);
        BeanUtils.copyProperties(entity, holeToEdit);

        return holeRepository.save(holeToEdit);
    }

    @Override
    public Hole deleteById(Long id) {
        Hole holeToDelete = findById(id);
        holeRepository.deleteById(id);
        return holeToDelete;
    }

    @Override
    public Hole save(Hole entity) {
        return holeRepository.save(entity);
    }

    @Override
    public HoleDto toDto(Hole entity) {
        HoleDto dto = new HoleDto();
        BeanUtils.copyProperties(entity,dto);
        return dto;
    }

    @Override
    public Hole toEntity(HoleDto dto) {
        Hole hole = new Hole();
        BeanUtils.copyProperties(dto, hole);
        return hole;
    }
}
