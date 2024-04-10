package org.example.repository;

import org.example.Cursa;

public interface CursaRepository extends Repository<Long, Cursa>
{
    public Long getMaxId();
}
