package com.spikingacacia.kazi.tasks;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.spikingacacia.kazi.R;

public class CTTasksA extends AppCompatActivity
    implements CTOverviewF.OnFragmentInteractionListener,
        CTAddF.OnFragmentInteractionListener,
        CTAllTasksF.OnListFragmentInteractionListener,
        CTTaskOverviewF.OnFragmentInteractionListener
{
    private String fragmentWhich="overview";
    private int mWhichTask=0;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_cttasks);
        //set actionbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Tasks");
        //set the first base fragment
        Fragment fragment=CTOverviewF.newInstance("","");
        FragmentTransaction transaction=getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.base,fragment,"");
        transaction.commit();
        //fragment manager
        getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener()
        {
            @Override
            public void onBackStackChanged()
            {
                int count=getSupportFragmentManager().getBackStackEntryCount();
                if(count==0)
                    setTitle("Tasks");
                else if(count==1)
                    setTitle(fragmentWhich);
            }
        });
    }
    /**implementation of CTOverview.java**/
    @Override
    public void onAddClicked()
    {
        fragmentWhich="add";
        setTitle("Add");
        Fragment fragment=CTAddF.newInstance(0,0);
        FragmentTransaction transaction=getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.base,fragment,"add");
        transaction.addToBackStack("add");
        transaction.commit();

    }
    @Override
    public void onTaskClicked(final int id)
    {
        mWhichTask=id;
        switch(id)
        {
            case 1:
                fragmentWhich="Pending";
                break;
            case 2:
                fragmentWhich="In Progress";
                break;
            case 3:
                fragmentWhich="Completed";
                break;
            case 4:
                fragmentWhich="Overdue";
                break;
            case 5:
                fragmentWhich="Late";
                break;
            case 6:
                fragmentWhich="All";
                break;
        }
        setTitle(fragmentWhich);
        Fragment fragment=CTAllTasksF.newInstance(1,id);
        FragmentTransaction transaction=getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.base,fragment,fragmentWhich);
        transaction.addToBackStack(fragmentWhich);
        transaction.commit();
    }
    /**implementation of CTAddF.java**/
    @Override
    public void onAddComplete()
    {
        setTitle("Tasks");
        onBackPressed();
    }
    /**implementation of UTAllTasksF.java**/
    @Override
    public void onCalenderClicked()
    {
        setTitle("Calender");
        Fragment fragment=CTCalenderF.newInstance(mWhichTask);
        FragmentTransaction transaction=getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.base,fragment,"calender");
        transaction.addToBackStack("calender");
        transaction.commit();
    }
    @Override
    public void onTaskItemClicked(CTAllTasksContent.Task item)
    {
        setTitle("Task Overview");
        Fragment fragment=CTTaskOverviewF.newInstance(item.id);
        FragmentTransaction transaction=getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.base,fragment,"task_overview");
        transaction.addToBackStack("task_overview");
        transaction.commit();
    }
    /**implementation of CTTaskOverviewF.java**/
    public void onUpdate(int id)
    {
        setTitle("Update Task");
        Fragment fragment=CTAddF.newInstance(1,id);
        FragmentTransaction transaction=getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.base,fragment,"add");
        transaction.addToBackStack("add");
        transaction.commit();
    }
    public void onDelete()
    {
        setTitle(fragmentWhich);
        onBackPressed();
    }
}
