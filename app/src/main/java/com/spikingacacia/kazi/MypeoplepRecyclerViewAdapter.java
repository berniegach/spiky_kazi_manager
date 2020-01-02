package com.spikingacacia.kazi;

import android.content.Context;
import android.content.DialogInterface;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.spikingacacia.kazi.peoplepFragment.OnListFragmentInteractionListener;
import com.spikingacacia.kazi.CPermissionsContent.PermissionItem;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link PermissionItem} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 */
public class MypeoplepRecyclerViewAdapter extends RecyclerView.Adapter<MypeoplepRecyclerViewAdapter.ViewHolder>
{

    private final List<PermissionItem> mValues;
    private final OnListFragmentInteractionListener mListener;
    private final Context mContext;

    public MypeoplepRecyclerViewAdapter(List<PermissionItem> items, OnListFragmentInteractionListener listener, Context context)
    {
        mValues = items;
        mListener = listener;
        mContext=context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.f_peoplep, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position)
    {
        holder.mItem = mValues.get(position);
        holder.mCountView.setText(mValues.get(position).count);
        holder.mUsernameView.setText(mValues.get(position).username);
        holder.mPositionView.setText(mValues.get(position).position);
        holder.mCompanyView.setText(mValues.get(position).company);


        holder.mView.setOnLongClickListener(new View.OnLongClickListener()
        {
            @Override
            public boolean onLongClick(View view)
            {
                if (null != mListener)
                {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                   // mListener.onListFragmentInteraction(holder.mItem);
                    new AlertDialog.Builder(mContext)
                            .setItems(new String[]{"Delete"}, new DialogInterface.OnClickListener()
                            {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i)
                                {
                                    new AlertDialog.Builder(mContext)
                                            .setMessage("Are you sure you want to delete permissions granted to this person?")
                                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener()
                                            {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i)
                                                {
                                                    dialogInterface.dismiss();
                                                }
                                            })
                                            .setPositiveButton("Proceed", new DialogInterface.OnClickListener()
                                            {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i)
                                                {
                                                    //remove data from notifications
                                                    String remove=String.format("%d,%s,%s,%d:",holder.mItem.given,holder.mItem.persona,holder.mItem.type,holder.mItem.id);
                                                    CSettingsActivity.tempContractorAccount.setPermissions(CSettingsActivity.tempContractorAccount.getPermissions().replace(remove,""));
                                                    CSettingsActivity.settingsChanged=true;
                                                    //remove the item fro the class
                                                    LoginActivity.permissionsList.remove(holder.mItem.listId);
                                                    mValues.remove(position);
                                                    CSettingsActivity.permissionsChanged=true;
                                                    //add permissions to changed
                                                    CSettingsActivity.permissions+=String.format("%d,%d,%s,%s,%d:", LoginActivity.contractorAccount.getId(),0,holder.mItem.persona,holder.mItem.type,holder.mItem.id);
                                                    notifyDataSetChanged();
                                                }
                                            }).create().show();
                                }
                            }).create().show();
                }
                return true;
            }
        });
    }

    @Override
    public int getItemCount()
    {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        public final View mView;
        public final TextView mCountView;
        public final TextView mUsernameView;
        public final TextView mPositionView;
        public final TextView mCompanyView;
        public PermissionItem mItem;

        public ViewHolder(View view)
        {
            super(view);
            mView = view;
            mCountView = (TextView) view.findViewById(R.id.item_number);
            mUsernameView = (TextView) view.findViewById(R.id.username);
            mPositionView=view.findViewById(R.id.position);
            mCompanyView=view.findViewById(R.id.company);
        }

        @Override
        public String toString()
        {
            return super.toString() + " '" + mUsernameView.getText() + "'";
        }
    }
}
