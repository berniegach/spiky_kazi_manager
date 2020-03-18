package com.spikingacacia.kazi.tasks;

import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AlertDialog;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.spikingacacia.kazi.JSONParser;
import com.spikingacacia.kazi.LoginActivity;
import com.spikingacacia.kazi.R;
import com.spikingacacia.kazi.database.CTasks;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.spikingacacia.kazi.LoginActivity.base_url;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CTTaskOverviewF.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CTTaskOverviewF#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CTTaskOverviewF extends Fragment
{
    private static final String ARG_ID = "id";
    private int mId;
    private int taskListIndex=0;
    private String url_delete_task=base_url+"delete_task.php";
    private String TAG_SUCCESS="success";
    private String TAG_MESSAGE="message";
    private JSONParser jsonParser;
    private OnFragmentInteractionListener mListener;

    public CTTaskOverviewF()
    {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     */
    // TODO: Rename and change types and number of parameters
    public static CTTaskOverviewF newInstance(int id)
    {
        CTTaskOverviewF fragment = new CTTaskOverviewF();
        Bundle args = new Bundle();
        args.putInt(ARG_ID, id);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (getArguments() != null)
        {
            mId = getArguments().getInt(ARG_ID);
        }
        jsonParser=new JSONParser();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.f_cttask_overview, container, false);
        //get the task using the id
        int index=0;
        Iterator<CTasks> iterator=LoginActivity.cTasksList.iterator();
        while(iterator.hasNext())
        {
            CTasks cTasks=iterator.next();
            int id=cTasks.getId();
            if(id==mId)
            {
                //set the values
                String title=cTasks.getTitle();
                String description=cTasks.getDescription();
                String startings[]=cTasks.getStartings().split(",");
                String endings[]=cTasks.getEndings().split(",");
                String repetition=getResources().getStringArray(R.array.tasks_repeatition)[cTasks.getRepetition()];
                String location[]=cTasks.getLocation().split(",");
                String position[]=cTasks.getPosition().split(":");
                String geofence=cTasks.getGeofence();
                String dateadded=cTasks.getDateadded();
                String datechanged=cTasks.getDatechanged();
                ((TextView)view.findViewById(R.id.title)).setText(title);
                ((TextView)view.findViewById(R.id.description)).setText(description);
                ((TextView)view.findViewById(R.id.start)).setText(startings[0].replace("s","  "));
                ((TextView)view.findViewById(R.id.end)).setText(endings[0].replace("s","  "));
                ((TextView)view.findViewById(R.id.repetition)).setText(repetition);
                //since we added that one can add a task with no location, we need to check if its missing
                if(location.length==3)
                    ((TextView)view.findViewById(R.id.location)).setText(location[2]);
                ((TextView)view.findViewById(R.id.position)).setText(position[0]);
                ((TextView)view.findViewById(R.id.date_added)).setText(dateadded);
                ((TextView)view.findViewById(R.id.date_changed)).setText(datechanged);
                taskListIndex=index;
            }
            index+=1;
        }
        return view;
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.cttask_overview_menu, menu);
        final MenuItem update=menu.findItem(R.id.action_update);
        update.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener()
        {
            @Override
            public boolean onMenuItemClick(MenuItem item)
            {
                if(mListener!=null)
                    mListener.onUpdate(mId);
                return true;
            }
        });
        final MenuItem delete=menu.findItem(R.id.action_delete);
        delete.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener()
        {
            @Override
            public boolean onMenuItemClick(MenuItem item)
            {
                new AlertDialog.Builder(getContext())
                        .setTitle("Delete Task")
                        .setMessage("Are you sure you want to delete the following task?")
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                dialog.dismiss();
                            }
                        })
                        .setPositiveButton("Proceed", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                new DeleteTradeTask().execute((Void)null);
                            }
                        }).create().show();

                return true;
            }
        });
    }

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener)
        {
            mListener = (OnFragmentInteractionListener) context;
        } else
        {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener
    {
        void onUpdate(int id);
        void onDelete();
    }
    public class DeleteTradeTask extends AsyncTask<Void, Void, Boolean>
    {
        private int success=0;

        DeleteTradeTask()
        {
            Toast.makeText(getContext(),"Deleting task started. Please wait...",Toast.LENGTH_SHORT).show();
            Log.d("DELETING TASK: ","deleting....");
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            //logIn=handler.LogInStaff(mEmail,mPassword);
            //building parameters
            List<NameValuePair> info=new ArrayList<NameValuePair>();
            info.add(new BasicNameValuePair("bossid",Integer.toString(LoginActivity.contractorAccount.getId())));
            info.add(new BasicNameValuePair("taskid",String.valueOf(mId)));
            //getting all account details by making HTTP request
            JSONObject jsonObject= jsonParser.makeHttpRequest(url_delete_task,"POST",info);
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
            Log.d("DELETING TASK: ","finished....");
            if (successful)
            {
                Toast.makeText(getContext(),"Task deleted Successfully",Toast.LENGTH_SHORT).show();
                LoginActivity.cTasksList.remove(taskListIndex);
                if(mListener!=null)
                    mListener.onDelete();
            }
            else
            {
                Toast.makeText(getContext(),"Error Deleting, Please try again.",Toast.LENGTH_SHORT).show();
            }
        }

    }

}
