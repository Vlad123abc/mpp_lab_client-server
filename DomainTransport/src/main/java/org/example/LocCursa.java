package org.example;

public class LocCursa
{
    private Integer nr;
    private String client;

    public LocCursa(Integer nr, String client)
    {
        this.nr = nr;
        this.client = client;
    }

    public Integer getNr()
    {
        return nr;
    }

    public void setNr(Integer nr)
    {
        this.nr = nr;
    }

    public String getClient()
    {
        return client;
    }

    public void setClient(String client)
    {
        this.client = client;
    }
}
