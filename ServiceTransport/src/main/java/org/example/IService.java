package org.example;

import java.sql.Timestamp;
import java.util.List;

public interface IService
{
    boolean login(String username, String password, IObserver client) throws Exception;

    User getUserByUsername(String username) throws Exception;

    List<Cursa> getAllCurse() throws Exception;

    List<LocCursa> genereaza_lista_locuri(Cursa cursa) throws Exception;

    Cursa cauta_cursa(String destinatie, Timestamp data) throws Exception;

    Integer getNrLocuriLibereCursa(Cursa cursa) throws Exception;

    void rezerva(String nume, Integer nr, Long id_cursa) throws Exception;
}
