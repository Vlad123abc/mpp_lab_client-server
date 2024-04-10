package org.example.repository;

import org.example.Entity;

import java.util.List;

public interface Repository<ID, E extends Entity<ID>>
{
    E getById(ID Id);
    List<E> getAll();
    Boolean save(E entity);
    Boolean delete(ID id);
    Boolean update(ID id, E entity);
}
