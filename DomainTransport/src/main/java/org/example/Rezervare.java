package org.example;

import java.util.Objects;

public class Rezervare extends Entity<Long>
{
    private String nume_client;
    private Integer nr_locuri;
    private Long id_cursa;

    public Rezervare() {}    

    public Rezervare(String nume_client, Integer nr_locuri, Long id_cursa)
    {
        this.nume_client = nume_client;
        this.nr_locuri = nr_locuri;
        this.id_cursa = id_cursa;
    }

    public String getNume_client()
    {
        return nume_client;
    }

    public void setNume_client(String nume_client)
    {
        this.nume_client = nume_client;
    }

    public Integer getNr_locuri()
    {
        return nr_locuri;
    }

    public void setNr_locuri(Integer nr_locuri)
    {
        this.nr_locuri = nr_locuri;
    }

    public Long getId_cursa()
    {
        return id_cursa;
    }

    public void setId_cursa(Long id_cursa)
    {
        this.id_cursa = id_cursa;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Rezervare rezervare = (Rezervare) o;
        return Objects.equals(nume_client, rezervare.nume_client) && Objects.equals(nr_locuri, rezervare.nr_locuri) && Objects.equals(id_cursa, rezervare.id_cursa);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(super.hashCode(), nume_client, nr_locuri, id_cursa);
    }

    @Override
    public String toString()
    {
        return "Rezervare{" +
                "nume_client='" + nume_client + '\'' +
                ", nr_locuri=" + nr_locuri +
                ", id_cursa=" + id_cursa +
                ", id=" + id +
                '}';
    }
}
