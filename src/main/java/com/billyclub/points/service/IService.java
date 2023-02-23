package com.billyclub.points.service;

import java.util.List;

public interface IService<T, S>{

    public List<T> findAll();
    public T add(T entity);
    public T findById(Long eventId);
    public T update(Long id, T entity);
    public T deleteById(Long eventId);
    public T save(T entity);

    public S toDto(T entity);
    public T toEntity(S dto);
}
