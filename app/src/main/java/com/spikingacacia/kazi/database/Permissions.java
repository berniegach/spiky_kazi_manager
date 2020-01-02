package com.spikingacacia.kazi.database;

/**
 * Created by $USER_NAME on 9/19/2018.
 **/
public class Permissions
{
    private int given;
    private char persona;
    private char type;
    private int id;
    private String email;
    private String username;
    private String company;
    private String position;

    public Permissions()
    {
    }

    public Permissions(int given, char persona, char type, int id, String email, String username, String company, String position)
    {
        this.given = given;
        this.persona = persona;
        this.type = type;
        this.id = id;
        this.email = email;
        this.username = username;
        this.company = company;
        this.position = position;
    }

    public int getGiven()
    {
        return given;
    }

    public void setGiven(int given)
    {
        this.given = given;
    }

    public char getPersona()
    {
        return persona;
    }

    public void setPersona(char persona)
    {
        this.persona = persona;
    }

    public char getType()
    {
        return type;
    }

    public void setType(char type)
    {
        this.type = type;
    }

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    public String getUsername()
    {
        return username;
    }

    public void setUsername(String username)
    {
        this.username = username;
    }

    public String getCompany()
    {
        return company;
    }

    public void setCompany(String company)
    {
        this.company = company;
    }

    public String getPosition()
    {
        return position;
    }

    public void setPosition(String position)
    {
        this.position = position;
    }

}
