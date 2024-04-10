package org.example.repository;

import org.example.Entity;
import org.example.Rezervare;

public interface RezervareRepository extends Repository<Long, Rezervare>
{
    public long getMaxId();
}
