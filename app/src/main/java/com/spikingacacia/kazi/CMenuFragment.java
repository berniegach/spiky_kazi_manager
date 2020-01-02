package com.spikingacacia.kazi;

import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.spikingacacia.kazi.database.CNotifications;
import com.spikingacacia.kazi.database.CReviews;
import com.spikingacacia.kazi.database.CTasks;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import static com.spikingacacia.kazi.LoginActivity.base_url;
import static com.spikingacacia.kazi.LoginActivity.cNotificationsList;
import static com.spikingacacia.kazi.LoginActivity.cReviewsList;
import static com.spikingacacia.kazi.LoginActivity.cTasksList;
import static com.spikingacacia.kazi.LoginActivity.contractorAccount;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CMenuFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CMenuFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CMenuFragment extends Fragment
{
    private String url_get_notifications=base_url+"get_c_notifications.php";
    private String url_get_tasks=base_url+"get_tasks.php";
    private String url_get_reviews=base_url+"get_performance_review.php";
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;
    private String TAG_SUCCESS="success";
    private String TAG_MESSAGE="message";
    private String TAG="CMenuFragment";
    private JSONParser jsonParser;
    private TextView tMessagesCount;
    private TextView tTasksCount;
    private TextView tReviewsCount;
    private int messagesCount=0;
    private int tasksCount=0;
    private int reviewsCount=0;

    private OnFragmentInteractionListener mListener;

    public CMenuFragment()
    {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CMenuFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CMenuFragment newInstance(String param1, String param2)
    {
        CMenuFragment fragment = new CMenuFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
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
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        jsonParser=new JSONParser();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.f_cmenu, container, false);
        tMessagesCount=view.findViewById(R.id.messages_count);
        tTasksCount=view.findViewById(R.id.tasks_count);
        tReviewsCount=view.findViewById(R.id.reviews_count);
        //requirements
        ((LinearLayout)view.findViewById(R.id.requirements)).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if(mListener!=null)
                    mListener.onMenuClicked(1);
            }
        });
        //compliance
        ((LinearLayout)view.findViewById(R.id.compliance)).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if(mListener!=null)
                    mListener.onMenuClicked(2);
            }
        });
        //users
        ((LinearLayout)view.findViewById(R.id.users)).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if(mListener!=null)
                    mListener.onMenuClicked(3);
            }
        });
        //reports
        ((LinearLayout)view.findViewById(R.id.reports)).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if(mListener!=null)
                    mListener.onMenuClicked(4);
            }
        });
        //tasks
        ((LinearLayout)view.findViewById(R.id.tasks)).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if(mListener!=null)
                    mListener.onMenuClicked(5);
            }
        });
        //messages
        ((LinearLayout)view.findViewById(R.id.messages)).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if(mListener!=null)
                    mListener.onMenuClicked(6);
            }
        });
        //performance review
        ((LinearLayout)view.findViewById(R.id.performance)).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if(mListener!=null)
                    mListener.onMenuClicked(7);
            }
        });
        //privileges
        ((LinearLayout)view.findViewById(R.id.privileges)).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if(mListener!=null)
                    mListener.onMenuClicked(8);
            }
        });
        //settings
        ((LinearLayout)view.findViewById(R.id.settings)).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
               if(mListener!=null)
                   mListener.onMenuClicked(9);
            }
        });
        final Handler handler=new Handler();
        final Runnable runnable=new Runnable()
        {
            @Override
            public void run()
            {
                if(messagesCount!=cNotificationsList.size())
                {
                    messagesCount=cNotificationsList.size();
                    tMessagesCount.setText(String.valueOf(messagesCount));
                    Log.d(TAG,"messages count changed");
                }
                if(tasksCount!=getTasksCount())
                {
                    tasksCount=getTasksCount();
                    tTasksCount.setText(String.valueOf(tasksCount));
                    Log.d(TAG,"tasks count changed");
                }
                if(reviewsCount!=getReviewsCount())
                {
                    reviewsCount=getReviewsCount();
                    tReviewsCount.setText(String.valueOf(reviewsCount));
                    Log.d(TAG,"reviews count changed");
                }
            }
        };
        Thread thread=new Thread()
        {
            @Override
            public void run()
            {
                try
                {
                    while(true)
                    {
                        sleep(10000);
                        refreshMessages();
                        refreshTasks();
                        refreshReviews();
                        handler.post(runnable);
                    }
                }
                catch (InterruptedException e)
                {
                    Log.e(TAG,"error sleeping "+e.getMessage());
                }
            }
        };
        thread.start();
        return view;
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.menu_cmenu, menu);
        final MenuItem logout=menu.findItem(R.id.action_logout);
        logout.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener()
        {
            @Override
            public boolean onMenuItemClick(MenuItem item)
            {
                if(mListener!=null)
                    mListener.onLogOut();
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
    @Override
    public void onResume()
    {
        super.onResume();
        tMessagesCount.setText(String.valueOf(cNotificationsList.size()));
        tTasksCount.setText(String.valueOf(getTasksCount()));
        tReviewsCount.setText(String.valueOf(getReviewsCount()));
    }

    public interface OnFragmentInteractionListener
    {
        void onMenuClicked(int id);
        void onLogOut();
    }
    private int getTasksCount()
    {
        int count=0;
        Iterator<CTasks> iterator=LoginActivity.cTasksList.iterator();
        while(iterator.hasNext())
        {
            CTasks cTasks = iterator.next();
            int pending = cTasks.getPending();
            int inProgress = cTasks.getInProgress();
            //int completed = cTasks.getCompleted();
            //int overdue = cTasks.getOverdue();
            //int late = cTasks.getLate();
            count+=pending+inProgress;
        }
        return count;
    }
    private int getReviewsCount()
    {
        int count=0;
        Iterator<CReviews> iterator= LoginActivity.cReviewsList.iterator();
        while(iterator.hasNext())
        {
            CReviews cReviews = iterator.next();
            //int id = cReviews.getId();
            //int userid = cReviews.getUserid();
            int classes = cReviews.getClasses(); // 0 is pending
            if(classes==0)
                count+=1;
        }
        return count;
    }
    private void refreshMessages()
    {
        //getting columns list
        List<NameValuePair> info=new ArrayList<NameValuePair>(); //info for staff count
        info.add(new BasicNameValuePair("id",Integer.toString(contractorAccount.getId())));
        // making HTTP request
        JSONObject jsonObject= jsonParser.makeHttpRequest(url_get_notifications,"POST",info);
        //Log.d("cNotis",""+jsonObject.toString());
        try
        {
            JSONArray notisArrayList=null;
            int success=jsonObject.getInt(TAG_SUCCESS);
            if(success==1)
            {
                notisArrayList=jsonObject.getJSONArray("notis");
                for(int count=0; count<notisArrayList.length(); count+=1)
                {
                    JSONObject jsonObjectNotis=notisArrayList.getJSONObject(count);
                    int id=jsonObjectNotis.getInt("id");
                    int userid=jsonObjectNotis.getInt("userid");
                    int classes=jsonObjectNotis.getInt("classes");
                    String message=jsonObjectNotis.getString("messages");
                    String date=jsonObjectNotis.getString("dateadded");
                    CNotifications oneCNotifications=new CNotifications(id,userid,classes,message,date);
                    cNotificationsList.put(String.valueOf(id),oneCNotifications);
                }
            }
            else
            {
                String message=jsonObject.getString(TAG_MESSAGE);
                Log.e(TAG_MESSAGE,""+message);
            }
        }
        catch (JSONException e)
        {
            Log.e("JSON",""+e.getMessage());
        }
    }
    private void refreshTasks()
    {
        List<CTasks>tempTasksList=new ArrayList<>();
        //getting columns list
        List<NameValuePair>info=new ArrayList<NameValuePair>(); //info for staff count
        info.add(new BasicNameValuePair("id",Integer.toString(contractorAccount.getId())));
        // making HTTP request
        JSONObject jsonObject= jsonParser.makeHttpRequest(url_get_tasks,"POST",info);
        //Log.d("cTasks",""+jsonObject.toString());
        try
        {
            JSONArray notisArrayList=null;
            int success=jsonObject.getInt(TAG_SUCCESS);
            if(success==1)
            {
                notisArrayList=jsonObject.getJSONArray("tasks");
                for(int count=0; count<notisArrayList.length(); count+=1)
                {
                    JSONObject jsonObjectNotis=notisArrayList.getJSONObject(count);
                    int id=jsonObjectNotis.getInt("id");
                    String title=jsonObjectNotis.getString("title");
                    String description=jsonObjectNotis.getString("description");
                    String starting=jsonObjectNotis.getString("starting");
                    String ending=jsonObjectNotis.getString("ending");
                    int repetition=jsonObjectNotis.getInt("repetition");
                    String location=jsonObjectNotis.getString("location");
                    String position=jsonObjectNotis.getString("position");
                    String geofence=jsonObjectNotis.getString("geofence");
                    String dateadded=jsonObjectNotis.getString("dateadded");
                    String datechanged=jsonObjectNotis.getString("datechanged");
                    int pending=jsonObjectNotis.getInt("p");
                    int inProgress=jsonObjectNotis.getInt("i");
                    int completed=jsonObjectNotis.getInt("c");
                    int overdue=jsonObjectNotis.getInt("o");
                    int late=jsonObjectNotis.getInt("l");
                    String pendingIds=jsonObjectNotis.getString("pids");
                    String inProgressIds=jsonObjectNotis.getString("inids");
                    String completedIds=jsonObjectNotis.getString("cids");
                    String overdueIds=jsonObjectNotis.getString("oids");
                    String lateIds=jsonObjectNotis.getString("lids");


                    CTasks tasks=new CTasks(id,title,description,starting,ending,repetition,location,position,geofence,dateadded,datechanged,pending,inProgress,completed,overdue,late,pendingIds,inProgressIds,completedIds,overdueIds,lateIds);
                    tempTasksList.add(tasks);
                }
                cTasksList=tempTasksList;
            }
            else
            {
                String message=jsonObject.getString(TAG_MESSAGE);
                Log.e(TAG_MESSAGE,""+message);
            }
        }
        catch (JSONException e)
        {
            Log.e("JSON",""+e.getMessage());
        }
    }
    private void refreshReviews()
    {
        List<CReviews>tempReviewsList=new ArrayList<>();
        //getting columns list
        List<NameValuePair>info=new ArrayList<NameValuePair>(); //info for staff count
        info.add(new BasicNameValuePair("id",Integer.toString(contractorAccount.getId())));
        // making HTTP request
        JSONObject jsonObject= jsonParser.makeHttpRequest(url_get_reviews,"POST",info);
        //Log.d("cReviews",""+jsonObject.toString());
        try
        {
            JSONArray reviewsArrayList=null;
            int success=jsonObject.getInt(TAG_SUCCESS);
            if(success==1)
            {
                reviewsArrayList=jsonObject.getJSONArray("reviews");
                for(int count=0; count<reviewsArrayList.length(); count+=1)
                {
                    JSONObject jsonObjectReviews=reviewsArrayList.getJSONObject(count);

                    int id=jsonObjectReviews.getInt("id");
                    int userid=jsonObjectReviews.getInt("userid");
                    int classes=jsonObjectReviews.getInt("classes");
                    String reviewer=jsonObjectReviews.getString("reviewer");
                    String review=jsonObjectReviews.getString("review");
                    String toimprove=jsonObjectReviews.getString("toimprove");
                    String s_rating=jsonObjectReviews.getString("rating");
                    int rating=0;
                    if(!(s_rating.contentEquals("NULL") ||s_rating.contentEquals("null") || s_rating.contentEquals("") ))
                        rating=Integer.parseInt(s_rating);
                    int themonth=jsonObjectReviews.getInt("themonth");
                    int theyear=jsonObjectReviews.getInt("theyear");
                    String dateadded=jsonObjectReviews.getString("dateadded");
                    String datechanged=jsonObjectReviews.getString("datechanged");

                    CReviews reviews=new CReviews(id,userid,classes,reviewer,review,toimprove,rating,themonth,theyear,dateadded,datechanged);
                    tempReviewsList.add(reviews);
                }
                cReviewsList=tempReviewsList;
            }
            else
            {
                String message=jsonObject.getString(TAG_MESSAGE);
                Log.e(TAG_MESSAGE,""+message);
            }
        }
        catch (JSONException e)
        {
            Log.e("JSON",""+e.getMessage());
        }
    }


}
