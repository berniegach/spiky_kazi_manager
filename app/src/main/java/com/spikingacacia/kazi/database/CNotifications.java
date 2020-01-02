package com.spikingacacia.kazi.database;

/**
 * Created by $USER_NAME on 11/9/2018.
 **/
public class CNotifications
{
    private int id;
    private int userid;
    private int classes;
    private String message;
    private String dateAdded;

    public CNotifications()
    {
    }

    public CNotifications(int id, int userid, int classes, String message, String dateAdded)
    {
        this.id = id;
        this.userid = userid;
        this.classes = classes;
        this.message = message;
        this.dateAdded = dateAdded;
    }

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public int getUserid()
    {
        return userid;
    }

    public void setUserid(int userid)
    {
        this.userid = userid;
    }

    public int getClasses()
    {
        return classes;
    }

    public void setClasses(int classes)
    {
        this.classes = classes;
    }

    public String getMessage()
    {
        return message;
    }

    public void setMessage(String message)
    {
        this.message = message;
    }

    public String getDateAdded()
    {
        return dateAdded;
    }

    public void setDateAdded(String dateAdded)
    {
        this.dateAdded = dateAdded;
    }


}
