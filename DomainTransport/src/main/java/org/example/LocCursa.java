package org.example;

import java.io.Serializable;
import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.annotation.*;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.SerializationFeature;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class LocCursa implements Serializable
{
    private Integer Item1;
    private String Item2;

    public LocCursa()
    {
    }
    
    public LocCursa(Integer nr, String client)
    {
        this.Item1 = nr;
        this.Item2 = client;
    }

    @JsonGetter("Item1")
    public Integer getItem1()
    {
        return Item1;
    }

    @JsonSetter("Item1")    
    public void setItem1(Integer Item1)
    {
        this.Item1 = Item1;
    }

    @JsonGetter("Item2")    
    public String getItem2()
    {
        return Item2;
    }

    @JsonSetter("Item2")        
    public void setItem2(String Item2)
    {
        this.Item2 = Item2;
    }
}
