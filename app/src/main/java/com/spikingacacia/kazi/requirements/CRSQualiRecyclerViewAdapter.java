package com.spikingacacia.kazi.requirements;

import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import com.google.android.material.textfield.TextInputLayout;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.spikingacacia.kazi.JSONParser;
import com.spikingacacia.kazi.LoginActivity;
import com.spikingacacia.kazi.R;
import com.spikingacacia.kazi.requirements.CRSQualiF.OnListFragmentInteractionListener;
import com.spikingacacia.kazi.requirements.CRSQualiFContent.Item;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import static com.spikingacacia.kazi.LoginActivity.base_url;

public class CRSQualiRecyclerViewAdapter extends RecyclerView.Adapter<CRSQualiRecyclerViewAdapter.ViewHolder>
{
    private String url_delete_quali=base_url+"delete_qualification.php";
    private String url_rename_quali=base_url+"rename_qualification.php";
    private String TAG_SUCCESS="success";
    private String TAG_MESSAGE="message";
    private JSONParser jsonParser;
    private final List<Item> mValues;
    private List<Item>itemsCopy;
    private final OnListFragmentInteractionListener mListener;
    private final Context mContext;


    public CRSQualiRecyclerViewAdapter(List<Item> items, OnListFragmentInteractionListener listener, Context context)
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
                .inflate(R.layout.f_crsquali, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position)
    {
        holder.mItem = mValues.get(position);
        holder.mIdView.setText(mValues.get(position).id);
        holder.mContentView.setText(mValues.get(position).content);
       // holder.mCountView.setText(mValues.get(position).count);

        holder.mView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (null != mListener)
                {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });
        holder.mView.setOnLongClickListener(new View.OnLongClickListener()
        {
            @Override
            public boolean onLongClick(View view)
            {
                new AlertDialog.Builder(mContext)
                        .setItems(new String[]{"Rename","Delete"}, new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i)
                            {
                                if(i==0)
                                {
                                    new AlertDialog.Builder(mContext)
                                            .setMessage("Are you sure you want to rename this qualification?")
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
                                                    final android.app.AlertDialog dialog;
                                                    android.app.AlertDialog.Builder builderPass=new android.app.AlertDialog.Builder(mContext);
                                                    builderPass.setTitle("Name?");
                                                    TextInputLayout textInputLayout=new TextInputLayout(mContext);
                                                    textInputLayout.setPadding(10,0,10,0);
                                                    textInputLayout.setGravity(Gravity.CENTER);
                                                    final EditText editText=new EditText(mContext);
                                                    editText.setPadding(20,10,20,10);
                                                    editText.setTextSize(14);
                                                    textInputLayout.addView(editText,0,new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
                                                    editText.setHint("New Name");
                                                    editText.setError(null);
                                                    editText.setSingleLine(true);
                                                    LinearLayout layout=new LinearLayout(mContext);
                                                    layout.setOrientation(LinearLayout.VERTICAL);
                                                    layout.addView(textInputLayout);
                                                    builderPass.setView(layout);
                                                    builderPass.setPositiveButton("Rename", null);
                                                    builderPass.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
                                                    {
                                                        @Override
                                                        public void onClick(DialogInterface dialogInterface, int i)
                                                        {
                                                            dialogInterface.dismiss();
                                                        }
                                                    });
                                                    dialog=builderPass.create();
                                                    dialog.setOnShowListener(new DialogInterface.OnShowListener()
                                                    {
                                                        @Override
                                                        public void onShow(DialogInterface dialogInterface)
                                                        {
                                                            Button button=((android.app.AlertDialog)dialog).getButton(android.app.AlertDialog.BUTTON_POSITIVE);
                                                            button.setOnClickListener(new View.OnClickListener()
                                                            {
                                                                @Override
                                                                public void onClick(View view)
                                                                {
                                                                    String name=editText.getText().toString();
                                                                    if(name.length()<3)
                                                                    {
                                                                        editText.setError("Name too short");
                                                                    }

                                                                    else
                                                                    {
                                                                        new RenameTradeTask(position,mValues.get(position).content,name).execute((Void)null);
                                                                        //new CreatePositionTask(name).execute((Void)null);
                                                                        dialog.dismiss();
                                                                    }
                                                                }
                                                            });
                                                        }
                                                    });
                                                    dialog.show();
                                                }
                                            }).create().show();
                                }
                                else
                                {
                                    new AlertDialog.Builder(mContext)
                                            .setMessage("Are you sure you want to delete this qualification?")
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
                                                    new DeleteQualiTask(position, mValues.get(position).content,String.valueOf(mValues.get(position).which)).execute((Void) null);
                                                }
                                            }).create().show();
                                }
                            }
                        }).create().show();
                return true;
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
    public void notifyChange(int position,String cont, Character which)
    {
        CRSQualiFContent content=new CRSQualiFContent();
        mValues.add(content.createItem(position,cont,which));
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        public final View mView;
        public final TextView mIdView;
        public final TextView mContentView;
       // public final TextView mCountView;
        public Item mItem;

        public ViewHolder(View view)
        {
            super(view);
            mView = view;
            mIdView = (TextView) view.findViewById(R.id.item_number);
            mContentView = (TextView) view.findViewById(R.id.content);
          //  mCountView=view.findViewById(R.id.count);
        }

        @Override
        public String toString()
        {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
    public class DeleteQualiTask extends AsyncTask<Void, Void, Boolean>
    {
        private int success=0;
        private String mQualiName;
        final private int mPosition;
        final private String mWhich;
        private ProgressBar progressBar;

        DeleteQualiTask(final int position, String tradename,String which)
        {
            mPosition=position;
            mQualiName=tradename;
            mQualiName=mQualiName.replace(" ","_");
            mWhich=which;
            progressBar=new ProgressBar(mContext,null,android.R.attr.progressBarStyleHorizontal);
        }
        @Override
        protected void onPreExecute()
        {
            Log.d("DELETING QUALIFIC: ","deleting....");
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            //building parameters
            List<NameValuePair> info=new ArrayList<NameValuePair>();
            info.add(new BasicNameValuePair("id",Integer.toString(LoginActivity.contractorAccount.getId())));
            info.add(new BasicNameValuePair("qualificationname",mQualiName));
            info.add(new BasicNameValuePair("which",mWhich));

            //getting all account details by making HTTP request
            JSONObject jsonObject= jsonParser.makeHttpRequest(url_delete_quali,"POST",info);
            try
            {
                success=jsonObject.getInt(TAG_SUCCESS);
                if(success==1)
                {
                    return true;
                }
                else
                {
                    String message=jsonObject.getString(TAG_MESSAGE);
                    Log.e(TAG_MESSAGE,""+message);
                    return false;
                }
            }
            catch (JSONException e)
            {
                Log.e("JSON",""+e.getMessage());
                return false;
            }
        }

        @Override
        protected void onPostExecute(final Boolean successful) {
            Log.d("DELETING QUALI: ","finished....");
            if (successful)
            {
                Toast.makeText(mContext,"Deleted Successfully",Toast.LENGTH_SHORT).show();
                Iterator iterator= LoginActivity.personnelColumnsList.entrySet().iterator();
                while (iterator.hasNext())
                {
                    LinkedHashMap.Entry<String,Character>set=(LinkedHashMap.Entry<String, Character>) iterator.next();
                    String name=set.getKey();
                    Character which=set.getValue();
                    String old_name=mValues.get(mPosition).content;
                    old_name=old_name.replace(" ","_");
                    if(old_name.contentEquals(name) && mValues.get(mPosition).which==which )
                    {
                        iterator.remove();
                        mValues.remove(mPosition);
                        notifyDataSetChanged();
                        break;
                    }
                }
            }
            else
            {
                Toast.makeText(mContext,"Error, please try again",Toast.LENGTH_SHORT).show();
            }
        }
        @Override
        protected void onCancelled()
        {
            //mAuthTaskU = null;
        }
    }
    public class RenameTradeTask extends AsyncTask<Void, Void, Boolean>
    {
        private int success=0;
        private String mQualiName;
        private String mNewQualiName;
        final private int mPosition;

        RenameTradeTask(final int position, String qualiname,final String newqualiname)
        {
            mPosition=position;
            mQualiName=qualiname;
            mQualiName= mQualiName.replace(" ","_");
            mNewQualiName=newqualiname;
            mNewQualiName=mNewQualiName.toLowerCase().replace(" ","_");
        }
        @Override
        protected void onPreExecute()
        {
            Log.d("RENAMING QUALI: ","renaming....");
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            //logIn=handler.LogInStaff(mEmail,mPassword);
            //building parameters
            List<NameValuePair> info=new ArrayList<NameValuePair>();
            info.add(new BasicNameValuePair("id",Integer.toString(LoginActivity.contractorAccount.getId())));
            info.add(new BasicNameValuePair("qualificationname",mQualiName));
            info.add(new BasicNameValuePair("newqualificationname",mNewQualiName));
            //getting all account details by making HTTP request
            JSONObject jsonObject= jsonParser.makeHttpRequest(url_rename_quali,"POST",info);
            try
            {
                success=jsonObject.getInt(TAG_SUCCESS);
                if(success==1)
                {
                    return true;
                }
                else
                {
                    String message=jsonObject.getString(TAG_MESSAGE);
                    Log.e(TAG_MESSAGE,""+message);
                    return false;
                }
            }
            catch (JSONException e)
            {
                Log.e("JSON",""+e.getMessage());
                return false;
            }
        }

        @Override
        protected void onPostExecute(final Boolean successful) {
            Log.d("RENAMING QUALI: ","finished....");
            if (successful)
            {
                Character which2='m';
                Toast.makeText(mContext,"Renamed Successfully",Toast.LENGTH_SHORT).show();
                Iterator iterator= LoginActivity.personnelColumnsList.entrySet().iterator();
                while (iterator.hasNext())
                {
                    LinkedHashMap.Entry<String,Character>set=(LinkedHashMap.Entry<String, Character>) iterator.next();
                    String name=set.getKey();
                    Character which=set.getValue();
                    String old_name=mValues.get(mPosition).content;
                    old_name=old_name.replace(" ","_");
                    if(old_name.contentEquals(name) && mValues.get(mPosition).which==which)
                    {
                        which2=which;
                        CRSQualiFContent content=new CRSQualiFContent();
                        mValues.set(mPosition,content.createItem(mPosition,mNewQualiName,which));
                        notifyDataSetChanged();
                        iterator.remove();
                        break;
                    }
                }
                LoginActivity.personnelColumnsList.put(mNewQualiName,which2);
            }
            else
            {
                Toast.makeText(mContext,"Error, please try again",Toast.LENGTH_SHORT).show();
            }
        }
        @Override
        protected void onCancelled()
        {
            //mAuthTaskU = null;
        }
    }
}
