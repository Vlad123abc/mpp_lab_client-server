package org.example;

import java.sql.Timestamp;
import java.util.Objects;

public class Cursa extends Entity<Long>
{
    private String destinatie;
    private Timestamp plecare;
    private Integer nr_locuri;

    public Cursa(String destinatie, Timestamp plecare, Integer nr_locuri)
    {
        this.destinatie = destinatie;
        this.plecare = plecare;
        this.nr_locuri = nr_locuri;
    }

    public String getDestinatie()
    {
        return destinatie;
    }

    public void setDestinatie(String destinatie)
    {
        this.destinatie = destinatie;
    }

    public Timestamp getPlecare()
    {
        return plecare;
    }

    public void setPlecare(Timestamp plecare)
    {
        this.plecare = plecare;
    }

    public Integer getNr_locuri()
    {
        return nr_locuri;
    }

    public void setNr_locuri(Integer nr_locuri)
    {
        this.nr_locuri = nr_locuri;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Cursa cursa = (Cursa) o;
        return Objects.equals(destinatie, cursa.destinatie) && Objects.equals(plecare, cursa.plecare) && Objects.equals(nr_locuri, cursa.nr_locuri);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(super.hashCode(), destinatie, plecare, nr_locuri);
    }

    @Override
    public String toString()
    {
        return "Cursa{" +
                "destinatie='" + destinatie + '\'' +
                ", plecare=" + plecare +
                ", nr_locuri=" + nr_locuri +
                ", id=" + id +
                '}';
    }
}
