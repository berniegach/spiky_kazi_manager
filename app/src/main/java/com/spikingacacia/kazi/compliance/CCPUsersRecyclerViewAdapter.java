package com.spikingacacia.kazi.compliance;

import android.content.Context;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.spikingacacia.kazi.JSONParser;
import com.spikingacacia.kazi.R;
import com.spikingacacia.kazi.compliance.CCPUsersF.OnListFragmentInteractionListener;
import com.spikingacacia.kazi.compliance.CCPUsersContent.Item;


import java.util.ArrayList;
import java.util.List;


/**
 * {@link RecyclerView.Adapter} that can display a {@link Item} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class CCPUsersRecyclerViewAdapter extends RecyclerView.Adapter<CCPUsersRecyclerViewAdapter.ViewHolder>
{
    private String TAG_SUCCESS="success";
    private String TAG_MESSAGE="message";
    private JSONParser jsonParser;
    private final List<Item> mValues;
    private List<Item>itemsCopy;
    private final OnListFragmentInteractionListener mListener;
    private final Context mContext;


    public CCPUsersRecyclerViewAdapter(List<Item> items, OnListFragmentInteractionListener listener, Context context)
    {
        mValues = items;
        mListener = listener;
        itemsCopy=new ArrayList<>();
        itemsCopy.addAll(items);
        mContext=context;
        jsonParser=new JSONParser();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.f_ccpusers, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position)
    {
        holder.mItem = mValues.get(position);
        holder.mIdView.setText(mValues.get(position).id);
        holder.mContentView.setText(mValues.get(position).content);
        holder.mWhichView.setText(mValues.get(position).which);
        holder.mWhichView.setBackgroundColor(mValues.get(position).which.contentEquals("C")?ContextCompat.getColor(mContext,R.color.graph_14):ContextCompat.getColor(mContext,R.color.graph_13));
        holder.mWhichView.setTextColor(mValues.get(position).which.contentEquals("C")?ContextCompat.getColor(mContext,R.color.graph_14):ContextCompat.getColor(mContext,R.color.graph_13));
        holder.mView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (null != mListener)
                {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onUserClicked(holder.mItem);
                }
            }
        });

    }

    @Override
    public int getItemCount()
    {
        return mValues.size();
    }
    public void filter(String text)
    {
        mValues.clear();
        if(text.isEmpty())
            mValues.addAll(itemsCopy);
        else
        {
            text=text.toLowerCase();
            for(Item item:itemsCopy)
            {
                if(item.content.toLowerCase().contains(text))
                    mValues.add(item);
            }
        }
        notifyDataSetChanged();
    }
    /*public void notifyChange(int position,String cont, int count)
    {
        CCPUsersContent
        CCPUsersContent content=new CCPUsersContent();
        mValues.add(content.createDummyItem(position,cont,count));
        notifyDataSetChanged();
    }*/

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        public final View mView;
        public final TextView mIdView;
        public final TextView mContentView;
        public final TextView mWhichView;
        public Item mItem;

        public ViewHolder(View view)
        {
            super(view);
            mView = view;
            mIdView = (TextView) view.findViewById(R.id.item_number);
            mContentView = (TextView) view.findViewById(R.id.content);
            mWhichView=view.findViewById(R.id.which);
        }

        @Override
        public String toString()
        {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}
