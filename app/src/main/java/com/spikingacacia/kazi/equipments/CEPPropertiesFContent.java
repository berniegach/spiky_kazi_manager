package com.spikingacacia.kazi.equipments;

import com.spikingacacia.kazi.LoginActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CEPPropertiesFContent
{

    /**
     * An array of sample (dummy) items.
     */
    public final List<Item> ITEMS = new ArrayList<Item>();

    /**
     * A map of sample (dummy) items, by ID.
     */
    public final Map<String, Item> ITEM_MAP = new HashMap<String, Item>();

    public CEPPropertiesFContent()
    {
        int pos=1;
        Iterator iterator= LoginActivity.equipmentsColumnsList.entrySet().iterator();
        while (iterator.hasNext())
        {
            LinkedHashMap.Entry<String,Character>set=(LinkedHashMap.Entry<String, Character>) iterator.next();
            String name=set.getKey();
            Character which=set.getValue();
            name=name.replace("_"," ");
            if(which=='m' || which=='j')
            {
                addItem(createItem(pos,name,which));
                pos+=1;
            }
        }
    }

    public void addItem(Item item)
    {
        ITEMS.add(item);
        ITEM_MAP.put(item.id, item);
    }

    public Item createItem(int position, String name, Character which)
    {
        return new Item(String.valueOf(position), name, which);
    }

    /**
     * A dummy item representing a piece of content.
     */
    public class Item
    {
        public final String id;
        public final String content;
        public final String details;
        public final Character which;

        public Item(String id, String content, Character which)
        {
            this.id = id;
            this.content = content;
            this.details = "";
            this.which=which;
        }

        @Override
        public String toString()
        {
            return content;
        }
    }
}
