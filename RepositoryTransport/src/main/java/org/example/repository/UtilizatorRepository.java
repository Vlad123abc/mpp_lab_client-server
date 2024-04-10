package org.example.repository;

import org.example.User;

public interface UtilizatorRepository extends Repository<Long, User>
{
    public Long getMaxId();

    public User getByUsername(String username);
}
