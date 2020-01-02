package com.spikingacacia.kazi.requirements;

import android.util.Log;

import com.spikingacacia.kazi.LoginActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CRSPositionsFContent
{

    /**
     * An array of sample (dummy) items.
     */
    public final List<Item> ITEMS = new ArrayList<Item>();

    /**
     * A map of sample (dummy) items, by ID.
     */
    public final Map<String, Item> ITEM_MAP = new HashMap<String, Item>();

    public CRSPositionsFContent()
    {
        int pos=1;
        Iterator iterator= LoginActivity.tradesList.entrySet().iterator();
        while (iterator.hasNext())
        {
            Log.d("items",Integer.toString(pos));
            LinkedHashMap.Entry<String,Integer>set=(LinkedHashMap.Entry<String, Integer>) iterator.next();
            String name=set.getKey();
            Integer count=set.getValue();
            //replace the _ in the name with space
            //when renaming the position we need to change back the space with _
            name=name.replace("_"," ");
            addItem(createDummyItem(pos,name,count));
            pos+=1;
        }
    }

    public void addItem(Item item)
    {
        ITEMS.add(item);
        ITEM_MAP.put(item.id, item);
    }

    public Item createDummyItem(int position, String name, Integer count)
    {
        return new Item(String.valueOf(position), name, String.valueOf(count));
    }

    /**
     * A dummy item representing a piece of content.
     */
    public class Item
    {
        public final String id;
        public final String content;
        public final String details;
        public final String count;

        public Item(String id, String content, String count)
        {
            this.id = id;
            this.content = content;
            this.details = "";
            this.count=count;
        }

        @Override
        public String toString()
        {
            return content;
        }
    }
}
