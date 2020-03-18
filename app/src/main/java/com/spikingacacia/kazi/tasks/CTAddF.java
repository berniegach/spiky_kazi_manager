package com.spikingacacia.kazi.tasks;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AlertDialog;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.spikingacacia.kazi.JSONParser;
import com.spikingacacia.kazi.LoginActivity;
import com.spikingacacia.kazi.MapsActivity;
import com.spikingacacia.kazi.Preferences;
import com.spikingacacia.kazi.R;
import com.spikingacacia.kazi.database.CTasks;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

public class CTAddF extends Fragment
{
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_ACTION = "action";
    private static final String ARG_ID = "id";
    //action=0 for add task and 1 for update task
    private int action;
    private int taskId;
    private int taskListIndex=0;
    private String url_add_task= LoginActivity.base_url+"create_task.php";
    private String url_update_task= LoginActivity.base_url+"update_task.php";

    private JSONParser jsonParser;
    private String TAG_SUCCESS="success";
    private String TAG_MESSAGE="message";
    private TextView tTitle;
    private TextView tDescription;
    private TextView position;
    private TextView tLocation;
    private String title;
    private String description;
    private String startDate="10/12/2018";
    private String startTime="1:00 pm";
    private String endDate="10/12/2018";
    private String endTime="4:00 pm";
    private int repetition=0;
    private  String location;
    private String mPosition[];
    private String geofence="g";
    private String dateAdded="";
    private String dateChanged="";
    private Preferences preferences;

    private OnFragmentInteractionListener mListener;

    public CTAddF()
    {
        // Required empty public constructor
    }
    public static CTAddF newInstance(int action, int id)
    {
        CTAddF fragment = new CTAddF();
        Bundle args = new Bundle();
        args.putInt(ARG_ACTION, action);
        args.putInt(ARG_ID, id);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (getArguments() != null)
        {
            action = getArguments().getInt(ARG_ACTION);
            taskId = getArguments().getInt(ARG_ID);
        }
        jsonParser=new JSONParser();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.f_ctadd, container, false);
        preferences=new Preferences(getContext());
        Button add=view.findViewById(R.id.add_button);
        tTitle=view.findViewById(R.id.title);
        tDescription=view.findViewById(R.id.description);
        position=view.findViewById(R.id.position);
        final TextView start_date=view.findViewById(R.id.start_date);
        final TextView start_time=view.findViewById(R.id.start_time);
        final TextView end_date=view.findViewById(R.id.end_date);
        final TextView end_time=view.findViewById(R.id.end_time);
        tLocation=view.findViewById(R.id.location);
        mPosition=new String[]{"0","0"};
        Spinner spinner=view.findViewById(R.id.spinner);
        LinearLayout l_start=view.findViewById(R.id.l_start);
        LinearLayout l_end=view.findViewById(R.id.l_end);
        //title
        tTitle.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                title=String.valueOf(s);
            }

            @Override
            public void afterTextChanged(Editable s)
            {

            }
        });
        //description
        tDescription.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                description=String.valueOf(s);
            }

            @Override
            public void afterTextChanged(Editable s)
            {

            }
        });
        //onclick listeners
        add.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                addButtonClicked();
            }
        });
        position.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                initializeListView();
            }
        });
        start_date.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                setDatePicker("Set the starting date",start_date,1);
            }
        });
        start_time.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                setTimePicker("Set the starting time",start_time,1);
            }
        });
        end_date.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                setDatePicker("Set the ending date",end_date,2);
            }
        });
        end_time.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                setTimePicker("Set the ending time",end_time,2);
            }
        });
        tLocation.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent=new Intent(getContext(),MapsActivity.class);
                intent.putExtra("who",3);
                startActivityForResult(intent,3);
            }
        });
        //spinner
        ArrayAdapter<CharSequence> adapter=ArrayAdapter.createFromResource(getContext(),R.array.tasks_repeatition,android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l)
            {
                Toast.makeText(getContext(),adapterView.getItemAtPosition(i)+" selected",Toast.LENGTH_SHORT).show();
                repetition=i;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView)
            {
            }
        });
        //update
        if(action==1)
        {
            add.setText("Update");
            //get the task using the id
            int index=0;
            Iterator<CTasks> iterator=LoginActivity.cTasksList.iterator();
            while(iterator.hasNext())
            {
                CTasks cTasks=iterator.next();
                int id=cTasks.getId();
                if(id==taskId)
                {
                    //set the values
                    String title=cTasks.getTitle();
                    String description=cTasks.getDescription();
                    String startings[]=cTasks.getStartings().split(",");
                    String endings[]=cTasks.getEndings().split(",");
                    int repeat=cTasks.getRepetition();
                    String locations[]=cTasks.getLocation().split(",");
                    String positions[]=cTasks.getPosition().split(":");
                    String geofence=cTasks.getGeofence();
                    String dateadded=cTasks.getDateadded();
                    String datechanged=cTasks.getDatechanged();
                    tTitle.setText(title);
                    tDescription.setText(description);
                    start_date.setText(startings[0].split("s")[0]);
                    start_time.setText(startings[0].split("s")[1]);
                    end_date.setText(endings[0].split("s")[0]);
                    end_time.setText(endings[0].split("s")[1]);
                    spinner.setSelection(repeat);
                    repetition=repeat;
                    tLocation.setText(locations[2]);
                    location=cTasks.getLocation();
                    position.setText(positions[0]);
                    mPosition[0]=positions[0];
                    mPosition[1]=positions[1];

                    dateAdded=dateadded;
                    dateChanged=datechanged;
                    taskListIndex=index;


                }
                index+=1;
            }
        }
        if(!preferences.isDark_theme_enabled())
        {
            view.findViewById(R.id.scroll_view).setBackgroundColor(getResources().getColor(R.color.secondary_background_light));
            start_date.setBackgroundColor(getResources().getColor(R.color.tertiary_background_light));
            start_time.setBackgroundColor(getResources().getColor(R.color.tertiary_background_light));
            end_date.setBackgroundColor(getResources().getColor(R.color.tertiary_background_light));
            end_time.setBackgroundColor(getResources().getColor(R.color.tertiary_background_light));
            spinner.setBackgroundColor(getResources().getColor(R.color.tertiary_background_light));
            tLocation.setBackgroundColor(getResources().getColor(R.color.tertiary_background_light));
            position.setBackgroundColor(getResources().getColor(R.color.tertiary_background_light));
        }
        return view;
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
        void onAddComplete();
    }
    private void addButtonClicked()
    {
        if(TextUtils.isEmpty(title) || title.length()<3)
        {
            tTitle.setError("Please set the title");
            tTitle.requestFocus();
            return;
        }
        if(TextUtils.isEmpty(description) || description.length()<3)
        {
            tDescription.setError("Please set the description");
            tDescription.requestFocus();
            return;
        }
        /*if(TextUtils.isEmpty(location) || location.length()<3)
        {
            Toast.makeText(getContext(),"Please set the location",Toast.LENGTH_SHORT).show();
            return;
        }*/
        if(TextUtils.isEmpty(mPosition[0]) || mPosition[0].length()<3)
        {
            Toast.makeText(getContext(),"Please set the position",Toast.LENGTH_SHORT).show();
            return;
        }
        if(action==0)
            new CreateTaskTask().execute((Void)null);
        else if(action==1)
            new UpdateTaskTask().execute((Void)null);
    }
    private void setDatePicker(String title, final TextView t_date,final int choice)
    {
        final String date;
        final AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
        final DatePicker datePicker=new DatePicker(getContext());
        //set the builder
        builder.setView(datePicker);
        builder.setTitle(title);
        builder.setPositiveButton("Set", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
                final int day, month, year;
                //date
                day=datePicker.getDayOfMonth();
                month=datePicker.getMonth()+1;
                year=datePicker.getYear();
                if(choice==1)
                {
                    startDate=String.format("%d/%d/%d",day,month,year);
                    t_date.setText(startDate);
                }
                else
                {
                    endDate=String.format("%d/%d/%d",day,month,year);
                    t_date.setText(endDate);
                }
            }
        });
        builder.create();
        builder.show();
    }
    private void setTimePicker(String title,  final TextView t_time,final int choice)
    {
        final String date;
        final AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
        final TimePicker timePicker=new TimePicker(getContext());
        //timepicker
        timePicker.setIs24HourView(false);
        //set the builder
        builder.setView(timePicker);
        builder.setTitle(title);
        builder.setPositiveButton("Set", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
                final int  mins;
                int hour;
                final String when;
                //time
                hour=timePicker.getCurrentHour();
                mins=timePicker.getCurrentMinute();
                when=hour<12?"AM":"PM";
                hour=hour<=12?hour:hour-12;
                if(choice==1)
                {
                    startTime=String.format("%d:%02d %s",hour,mins,when);
                    t_time.setText(startTime);
                }
                else
                {
                    endTime=String.format("%d:%02d %s",hour,mins,when);
                    t_time.setText(endTime);
                }
            }
        });
        builder.create();
        builder.show();
    }
    private void initializeListView()
    {
        final List<ParentItem>itemList=new ArrayList<>();
        LinkedHashMap<Integer,String>people=new LinkedHashMap<>();
        int pos=0;
        Iterator iterator= LoginActivity.tradesList.entrySet().iterator();
        while (iterator.hasNext())
        {
            if(pos==0)
            {
                ParentItem parentAll=new ParentItem("All");
                itemList.add(parentAll);
                pos+=1;
                continue;
            }
            LinkedHashMap.Entry<String,Integer>set=(LinkedHashMap.Entry<String, Integer>) iterator.next();
            String name=set.getKey();
            Integer count=set.getValue();
            //get the list of people
            people=getPersonnel(name);
            if(people.size()>0)
            {
                ParentItem parentItem=new ParentItem(name);
                Iterator iteratorPeople=people.entrySet().iterator();
                while(iteratorPeople.hasNext())
                {
                    LinkedHashMap.Entry<Integer,String>person=(LinkedHashMap.Entry<Integer, String>) iteratorPeople.next();
                    int id=person.getKey();
                    String names=person.getValue();
                    parentItem.getChildItemList().add(new ChildItem(names,id));
                }
                itemList.add(parentItem);
            }
            pos+=1;
        }
        //layout
        final AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
        builder.setTitle("Choose Position");
        final ExpandableListView expandableListView=new ExpandableListView(getContext());
        expandableListView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT));
        ExpandableListViewAdapter adapter=new ExpandableListViewAdapter(getContext(),itemList);
        expandableListView.setAdapter(adapter);
        builder.setView(expandableListView);
        final AlertDialog dialog=builder.create();
        expandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener()
        {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id)
            {
                String pos=itemList.get(groupPosition).mParentName;
                if(pos.contentEquals("All"))
                {
                    position.setText("All");
                    mPosition[0]="All";
                    dialog.dismiss();
                    return true;
                }
                return false;
            }
        });
        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener()
        {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id)
            {
                String pos=itemList.get(groupPosition).mParentName;
                String per=itemList.get(groupPosition).getChildItemList().get(childPosition).mPositionName;
                int perId=itemList.get(groupPosition).getChildItemList().get(childPosition).mPositionid;
                if(per.contentEquals("All"))
                {
                    position.setText(pos);
                    mPosition[0]=pos;
                }
                else
                {
                    position.setText(per);
                    mPosition[0]=per;
                    mPosition[1]=String.valueOf(perId);
                }
                dialog.dismiss();
                return true;
            }
        });
        dialog.show();
       // builder.create();
       // builder.show();
    }
    public class ParentItem
    {
        private List<ChildItem>childItemList;
        private String mParentName;
        private String mParentId;
        public ParentItem(String content)
        {
            childItemList=new ArrayList<>();
            mParentName =content;
        }
        public List<ChildItem>getChildItemList()
        {
            return childItemList;
        }
        @Override
        public String toString()
        {
            return mParentName;
        }
    }
    public class ChildItem
    {
        //fill with data
        private String mPositionName;
        private int mPositionid;
        public ChildItem(String content, int id)
        {
            mPositionName=content;
            mPositionid=id;
        }
        @Override
        public String toString()
        {
            return mPositionName;
        }
    }
    private LinkedHashMap<Integer,String> getPersonnel(final String position)
    {
        LinkedHashMap<Integer,String>people=new LinkedHashMap<>();
        if( !(LoginActivity.cGlobalInfo.getComplaintStaff().size()>0 || LoginActivity.cGlobalInfo.getNonComplaintStaff().size()>0))
            return people;
        people.put(0,"All");
        Iterator iterator= LoginActivity.cGlobalInfo.getComplaintStaff().entrySet().iterator();
        while (iterator.hasNext())
        {
            LinkedHashMap.Entry<String,Character>set=(LinkedHashMap.Entry<String, Character>) iterator.next();
            String name=set.getKey();
            String[] token=name.split(":");
            final String id=token[0];
            final String userid=token[1];
            final String trade=token[2];
            final String names=token[3];
            Character which=set.getValue();
            if(position.contentEquals(trade))
                people.put(Integer.valueOf(userid),names);
        }
        Iterator iterator2= LoginActivity.cGlobalInfo.getNonComplaintStaff().entrySet().iterator();
        while (iterator2.hasNext())
        {
            LinkedHashMap.Entry<String,Character>set=(LinkedHashMap.Entry<String, Character>) iterator2.next();
            String name=set.getKey();
            String[] token=name.split(":");
            final String id=token[0];
            final String userid=token[1];
            final String trade=token[2];
            final String names=token[3];
            Character which=set.getValue();
            if(position.contentEquals(trade))
                people.put(Integer.valueOf(userid),names);
        }
        return people;
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {

        if(requestCode==3 && resultCode==Activity.RESULT_OK )
        {
            location = data.getStringExtra("location");
            String[]loc=location.split(",");
            if(loc.length>0)
            {
                tLocation.setText(loc[2]);
            }
            Log.d("LOCATION", "onacty2 data"+location);
        }
        Log.d("LOCATION", "onacty2 fragment"+data.getDataString());
    }
    public class CreateTaskTask extends AsyncTask<Void, Void, Boolean>
    {
        private int success;
        private int id=0;
        CreateTaskTask()
        {
            Toast.makeText(getContext(),"Adding task started. Please wait...",Toast.LENGTH_SHORT).show();
            Log.d("CRATETASK"," started...");
        }
        @Override
        protected Boolean doInBackground(Void... params)
        {
            //building parameters
            List<NameValuePair>info=new ArrayList<NameValuePair>();
            info.add(new BasicNameValuePair("id",Integer.toString(LoginActivity.contractorAccount.getId())));
            info.add(new BasicNameValuePair("title",title));
            info.add(new BasicNameValuePair("description",description));
            info.add(new BasicNameValuePair("starting",startDate+"s"+startTime));
            info.add(new BasicNameValuePair("ending",endDate+"s"+endTime));
            info.add(new BasicNameValuePair("repetition",String.valueOf(repetition)));
            info.add(new BasicNameValuePair("location",location));
            info.add(new BasicNameValuePair("position",mPosition[0]+":"+mPosition[1]));
            info.add(new BasicNameValuePair("geofence",geofence));
            JSONObject jsonObject= jsonParser.makeHttpRequest(url_add_task,"POST",info);
            try
            {
                success=jsonObject.getInt(TAG_SUCCESS);
                if(success==1)
                {
                    id=jsonObject.getInt("id");
                    dateAdded=jsonObject.getString("dateadded");
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
        protected void onPostExecute(final Boolean successful)
        {
            if(successful)
            {

                Log.d("adding new task", "done...");
                String start=startDate+"s"+startTime;
                String end=endDate+"s"+endTime;
                String pos=mPosition[0]+":"+mPosition[1];
                CTasks cTasks=new CTasks(id,title,description,start,end,repetition,location, pos,geofence,dateAdded,dateAdded,
                        1,0,0,0,0,"","","","","");
                LoginActivity.cTasksList.add(cTasks);
                Log.d("updating new task", "done...");
                Toast.makeText(getContext(),"Successful",Toast.LENGTH_SHORT).show();
                if(mListener!=null)
                    mListener.onAddComplete();
            }
            else if(success==-2 || success==-1)
            {
                Log.e("adding task", "error");
                Toast.makeText(getContext(),"There was an error. Please try again",Toast.LENGTH_SHORT).show();
            }

        }
    }
    public class UpdateTaskTask extends AsyncTask<Void, Void, Boolean>
    {
        private int success;
        UpdateTaskTask()
        {
            Toast.makeText(getContext(),"Updating started. Please wait...",Toast.LENGTH_SHORT).show();
            Log.d("UPDATETASK"," started...");
        }
        @Override
        protected Boolean doInBackground(Void... params)
        {
            //building parameters
            List<NameValuePair>info=new ArrayList<NameValuePair>();
            info.add(new BasicNameValuePair("bossid",Integer.toString(LoginActivity.contractorAccount.getId())));
            info.add(new BasicNameValuePair("id",String.valueOf(taskId)));
            info.add(new BasicNameValuePair("title",title));
            info.add(new BasicNameValuePair("description",description));
            info.add(new BasicNameValuePair("starting",startDate+"s"+startTime));
            info.add(new BasicNameValuePair("ending",endDate+"s"+endTime));
            info.add(new BasicNameValuePair("repetition",String.valueOf(repetition)));
            info.add(new BasicNameValuePair("location",location));
            info.add(new BasicNameValuePair("position",mPosition[0]+":"+mPosition[1]));
            info.add(new BasicNameValuePair("geofence",geofence));
            JSONObject jsonObject= jsonParser.makeHttpRequest(url_update_task,"POST",info);
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
        protected void onPostExecute(final Boolean successful)
        {
            if(successful)
            {
                CTasks cTasks=LoginActivity.cTasksList.get(taskListIndex);
                cTasks.setTitle(title);
                cTasks.setDescription(description);
                //start time
                String startings[]=cTasks.getStartings().split(",");
                startings[0]=startDate+"s"+startTime;
                String start="";
                for(int c=0; c<startings.length; c++)
                    start+=startings[c];
                cTasks.setStartings(start);
                //end time
                String endings[]=cTasks.getEndings().split(",");
                endings[0]=endDate+"s"+endTime;
                String end="";
                for(int c=0; c<endings.length; c++)
                    end+=endings[c];
                cTasks.setEndings(end);
                cTasks.setRepetition(repetition);
                cTasks.setLocation(location);
                cTasks.setPosition(mPosition[0]+":"+mPosition[1]);
                //set the task
                LoginActivity.cTasksList.set(taskListIndex,cTasks);
                Log.d("updating new task", "done...");
                Toast.makeText(getContext(),"Update Successful",Toast.LENGTH_SHORT).show();


                if(mListener!=null)
                    mListener.onAddComplete();
            }
            else
            {
                Log.e("updating task", "error");
                Toast.makeText(getContext(),"There was an error. Please try again",Toast.LENGTH_SHORT).show();
            }


        }
    }

}
