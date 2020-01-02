package com.spikingacacia.kazi.compliance;

import android.util.Log;

import com.spikingacacia.kazi.LoginActivity;

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
public class CCPUsersContent
{

    /**
     * An array of sample (dummy) items.
     */
    public final List<Item> ITEMS = new ArrayList<Item>();

    /**
     * A map of sample (dummy) items, by ID.
     */
    public final Map<String, Item> ITEM_MAP = new HashMap<String, Item>();

    public CCPUsersContent(String positionName, String positionId)
    {
        int pos=1;
        Iterator iterator= LoginActivity.cGlobalInfo.getComplaintStaff().entrySet().iterator();
        while (iterator.hasNext())
        {
            Log.d("items",Integer.toString(pos));
            LinkedHashMap.Entry<String,Character>set=(LinkedHashMap.Entry<String, Character>) iterator.next();
            String name=set.getKey();
            String[] token=name.split(":");
            final String id=token[0];
            final String userid=token[1];
            final String trade=token[2];
            final String names=token[3];
            Character which=set.getValue();
            if(!trade.contentEquals(positionName) && !positionName.contentEquals("All"))
                continue;
            addItem(createDummyItem(pos,names,which,id,userid,trade));
            pos+=1;
        }
        Iterator iterator2= LoginActivity.cGlobalInfo.getNonComplaintStaff().entrySet().iterator();
        while (iterator2.hasNext())
        {
            Log.d("items",Integer.toString(pos));
            LinkedHashMap.Entry<String,Character>set=(LinkedHashMap.Entry<String, Character>) iterator2.next();
            String name=set.getKey();
            String[] token=name.split(":");
            final String id=token[0];
            final String userid=token[1];
            final String trade=token[2];
            final String names=token[3];
            Character which=set.getValue();
            if(!trade.contentEquals(positionName) && !positionName.contentEquals("All"))
                continue;
            addItem(createDummyItem(pos,names,which,id,userid,trade));
            pos+=1;
        }
    }

    public void addItem(Item item)
    {
        ITEMS.add(item);
        ITEM_MAP.put(item.id, item);
    }

    public Item createDummyItem(int position, String name, Character which,String id, String userid, String trade)
    {
        return new Item(String.valueOf(position), name, String.valueOf(which),id,userid,trade);
    }

    /**
     * A dummy item representing a piece of content.
     */
    public class Item
    {
        public final String id;
        public final String content;
        public final String details;
        public final String which;
        public final String ID;
        public final String userId;
        public final String trade;

        public Item(String id, String content, String which, String ID, String userId, String trade)
        {
            this.id = id;
            this.content = content;
            this.details = "";
            this.which=which;
            this.ID=ID;
            this.userId=userId;
            this.trade=trade;
        }

        @Override
        public String toString()
        {
            return content;
        }
    }
}
