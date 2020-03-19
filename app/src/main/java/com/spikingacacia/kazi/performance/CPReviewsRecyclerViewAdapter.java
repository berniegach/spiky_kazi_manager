package com.spikingacacia.kazi.performance;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.spikingacacia.kazi.Preferences;
import com.spikingacacia.kazi.R;
import com.spikingacacia.kazi.performance.CPReviewsF.OnListFragmentInteractionListener;
import com.spikingacacia.kazi.performance.CPReviewsContent.ReviewItem;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link ReviewItem} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class CPReviewsRecyclerViewAdapter extends RecyclerView.Adapter<CPReviewsRecyclerViewAdapter.ViewHolder> {

    private final List<ReviewItem> mValues;
    private final OnListFragmentInteractionListener mListener;
    private List<ReviewItem>itemsCopy;
    private Context mContext;
    private  int mWhichReview=0;
    private Preferences preferences;

    public CPReviewsRecyclerViewAdapter(List<ReviewItem> items, OnListFragmentInteractionListener listener, Context context, int whichReview) {
        mValues = items;
        mListener = listener;
        itemsCopy=new ArrayList<>();
        itemsCopy.addAll(items);
        mContext=context;
        mWhichReview=whichReview;
        preferences = new Preferences(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.f_cpreviews, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        if(!preferences.isDark_theme_enabled())
        {
            holder.mView.setBackgroundColor(mContext.getResources().getColor(R.color.secondary_background_light));
        }
        holder.mItem = mValues.get(position);
        holder.mCountView.setText(mValues.get(position).count);
        holder.mNamesView.setText(mValues.get(position).names);
        holder.mPositionView.setText(mValues.get(position).position);
        holder.mDateView.setText(mValues.get(position).dateAdded);
        if(mValues.get(position).classes==0)
            holder.mRatingView.setVisibility(View.GONE);
        else
            holder.mRatingView.setText(String.valueOf(mValues.get(position).rating)+"/5");

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onPendingClicked(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
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
            for(ReviewItem task:itemsCopy)
            {
                if(task.names.toLowerCase().contains(text))
                    mValues.add(task);
            }
        }
        notifyDataSetChanged();
    }
    public void removeItem(int index)
    {
        mValues.remove(index);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mCountView;
        public final TextView mNamesView;
        public final TextView mPositionView;
        public final TextView mDateView;
        public final TextView mRatingView;
        public final LinearLayout lBase;
        public ReviewItem mItem;


        public ViewHolder(View view) {
            super(view);
            mView = view;
            lBase=(LinearLayout)view.findViewById(R.id.base);
            mCountView = (TextView) view.findViewById(R.id.count);
            mNamesView = (TextView) view.findViewById(R.id.names);
            mPositionView=(TextView) view.findViewById(R.id.position);
            mDateView=(TextView) view.findViewById(R.id.date);
            mRatingView=view.findViewById(R.id.rating);
            lBase.setBackgroundColor( ContextCompat.getColor(mContext, R.color.secondary_background));
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mNamesView.getText() + "'";
        }
    }
}
