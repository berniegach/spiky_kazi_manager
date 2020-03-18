package com.spikingacacia.kazi.tasks;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.BaseExpandableListAdapter;


import com.spikingacacia.kazi.Preferences;
import com.spikingacacia.kazi.R;

import java.util.List;

public class ExpandableListViewAdapter extends BaseExpandableListAdapter
    {
        private Preferences preferences;
        private Context context;
        private static final class ViewHolder
        {
            TextView textLabel;
        }
        private final List<CTAddF.ParentItem> itemList;
        private final LayoutInflater inflater;
        public ExpandableListViewAdapter(Context context, List<CTAddF.ParentItem>itemList)
        {
            this.inflater=LayoutInflater.from(context);
            this.itemList=itemList;
            this.context=context;
            preferences=new Preferences(context);
        }
        @Override
        public int getGroupCount()
        {
            return itemList.size();
        }

        @Override
        public int getChildrenCount(int groupPosition)
        {
            return itemList.get(groupPosition).getChildItemList().size();
        }

        @Override
        public CTAddF.ParentItem getGroup(int groupPosition)
        {
            return itemList.get(groupPosition);
        }

        @Override
        public CTAddF.ChildItem getChild(int groupPosition, int childPosition)
        {
            return itemList.get(groupPosition).getChildItemList().get(childPosition);
        }

        @Override
        public long getGroupId(int groupPosition)
        {
            return groupPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition)
        {
            return childPosition;
        }

        @Override
        public boolean hasStableIds()
        {
            return true;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent)
        {
            View resultView=convertView;
            ViewHolder holder;
            if(resultView==null)
            {
                resultView=inflater.inflate(R.layout.f_ctadd_list_parent,null);//TODO child layout id
                holder=new ViewHolder();
                holder.textLabel=(TextView)resultView.findViewById(R.id.itemsTitle);//TODO change view id
                resultView.setTag(holder);
                if(!preferences.isDark_theme_enabled())
                    resultView.setBackgroundColor(context.getResources().getColor(R.color.tertiary_background_light));
            }
            else
            {
                holder=(ViewHolder)resultView.getTag();
            }
            final CTAddF.ParentItem item=getGroup(groupPosition);
            holder.textLabel.setText(item.toString());
            return resultView;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent)
        {
            View resultView=convertView;
            ViewHolder holder;
            if(resultView==null)
            {
                resultView=inflater.inflate(R.layout.f_ctadd_list_child,null);//TODO child layout id
                holder=new ViewHolder();
                holder.textLabel=(TextView)resultView.findViewById(R.id.item);//TODO change view id
                resultView.setTag(holder);
                if(!preferences.isDark_theme_enabled())
                    resultView.setBackgroundColor(context.getResources().getColor(R.color.tertiary_background_light));
            }
            else
            {
                holder=(ViewHolder)resultView.getTag();
            }
            final CTAddF.ChildItem item=getChild(groupPosition,childPosition);
            holder.textLabel.setText(item.toString());
            return resultView;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition)
        {
            return true;
        }
                
    }