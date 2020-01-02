package com.spikingacacia.kazi;

import android.util.Log;

import com.spikingacacia.kazi.database.Permissions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class CPermissionsContent
{

    /**
     * An array of sample (dummy) items.
     */
    public  final List<PermissionItem> ITEMS = new ArrayList<PermissionItem>();

    /**
     * A map of sample (dummy) items, by ID.
     */
    public  final Map<String, PermissionItem> ITEM_MAP = new HashMap<String, PermissionItem>();

    public CPermissionsContent()
    {
        Log.d("PERM:",String.format("%d",LoginActivity.permissionsList.size()));
        //add the permissions
        /* the format follows the following
         * 1. 0/1  0 for permissions given 1 for permissions to others
         * 2. C/U  C for contractor , U for general user
         * 3. E/V/A  E for edit tasks, V for view compliance,  A for add data
         * 4  the persons id*/
        for(int c=0,pos=1; c< LoginActivity.permissionsList.size(); c+=1)
        {

            Permissions permissions= LoginActivity.permissionsList.get(c);
            //for edit tasks
            if(CPermissionsActivity.which==1)
            {
                Log.d("TYPE",String.format("%s",permissions.getType()));
                if(permissions.getType()=='E')
                {
                    Log.d("PERM12:",String.format("%d",LoginActivity.permissionsList.size()));
                    addItem(createDummyItem(pos,permissions.getGiven(),permissions.getPersona(),permissions.getType(),permissions.getId(),
                            permissions.getEmail(),permissions.getUsername(),permissions.getCompany(),permissions.getPosition(),c));
                    pos+=1;
                }
            }
            //for view complaince
            else if(CPermissionsActivity.which==2)
            {
                if(permissions.getType()=='V')
                {
                    Log.d("PERM:34",String.format("%d",LoginActivity.permissionsList.size()));
                    addItem(createDummyItem(pos,permissions.getGiven(),permissions.getPersona(),permissions.getType(),permissions.getId(),
                            permissions.getEmail(),permissions.getUsername(),permissions.getCompany(),permissions.getPosition(),c));
                    pos+=1;
                }
            }
            //for add data
            else if(CPermissionsActivity.which==3)
            {
                if(permissions.getType()=='A')
                {
                    Log.d("PERM67:",String.format("%d",LoginActivity.permissionsList.size()));
                    addItem(createDummyItem(pos,permissions.getGiven(),permissions.getPersona(),permissions.getType(),permissions.getId(),
                            permissions.getEmail(),permissions.getUsername(),permissions.getCompany(),permissions.getPosition(),c));
                    pos+=1;
                }
            }
        }
    }

    private  void addItem(PermissionItem item)
    {
        ITEMS.add(item);
        ITEM_MAP.put(item.count, item);
    }

    private PermissionItem createDummyItem(int count, int given, char persona, char type, int id, String email, String username, String company, String position, int listId)
    {
        return new PermissionItem(String.valueOf(count),given,persona,type,id,email,username,company,position,listId);
    }

    /**
     * A dummy item representing a piece of content.
     */
    public  class PermissionItem
    {
        public final String count;
        public final int given;
        public final char persona;
        public final char type;
        public final int id;
        public final String email;
        public final String username;
        public final String company;
        public final String position;
        public final int listId;

        public PermissionItem(String count, int given, char persona, char type, int id, String email, String username, String company, String position, int listId)
        {
            this.count = count;
            this.given = given;
            this.persona = persona;
            this.type = type;
            this.id = id;
            this.email = email;
            this.username = username;
            this.company = company;
            this.position = position;
            this.listId=listId;
        }


        @Override
        public String toString()
        {
            return username;
        }
    }
}
