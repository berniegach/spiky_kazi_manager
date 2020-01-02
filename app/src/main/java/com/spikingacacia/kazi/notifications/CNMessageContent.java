package com.spikingacacia.kazi.notifications;

import com.spikingacacia.kazi.LoginActivity;
import com.spikingacacia.kazi.database.CNotifications;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class CNMessageContent
{

    /**
     * An array of items.
     */
    public final List<MessageItem> ITEMS = new ArrayList<MessageItem>();
    public final Map<String, MessageItem> ITEM_MAP = new HashMap<String, MessageItem>();

    public CNMessageContent()
    {
        int pos=1;
        Iterator iterator=LoginActivity.cNotificationsList.entrySet().iterator();
        while(iterator.hasNext())
        {
            LinkedHashMap.Entry<String,CNotifications>set=(LinkedHashMap.Entry<String, CNotifications>) iterator.next();
            String name=set.getKey();
            CNotifications cNotifications=set.getValue();
            int id=cNotifications.getId();
            int userid=cNotifications.getUserid();
            int classes=cNotifications.getClasses();
            String message=cNotifications.getMessage();
            String dateAdded=cNotifications.getDateAdded();
            addItem(createItem(pos,id,userid,classes,message,dateAdded));
            pos+=1;
        }
    }

    private  void addItem(MessageItem item)
    {
        ITEMS.add(item);
        ITEM_MAP.put(item.position, item);
    }

    private  MessageItem createItem(int position,int id, int userid,int classes, String message,String dateAdded)
    {
        return new MessageItem(String.valueOf(position),id,userid,classes,message,dateAdded);
    }

    public  class MessageItem
    {
        public final String position;
        public final int id;
        public final int userid;
        public final int classes;
        public final String message;
        public final String dateAdded;

        public MessageItem(String position, int id, int userid, int classes, String message, String dateAdded)
        {
            this.position = position;
            this.id = id;
            this.userid = userid;
            this.classes = classes;
            this.message = message;
            this.dateAdded = dateAdded;
        }

        @Override
        public String toString()
        {
            return message;
        }
    }
}
