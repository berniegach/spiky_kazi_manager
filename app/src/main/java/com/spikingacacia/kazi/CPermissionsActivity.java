package com.spikingacacia.kazi;

import android.content.Intent;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;

public class CPermissionsActivity extends AppCompatActivity
    implements peoplepFragment.OnListFragmentInteractionListener
{
    public static int which;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_cpermissins);
        //set actionbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //get intent
        Intent intent=getIntent();
        which=intent.getIntExtra("which",1);
        if(which==1)
        {
            setTitle("Edit Tasks");
            Fragment fragment=peoplepFragment.newInstance(1);
            FragmentTransaction transaction=getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.base,fragment,"");
            transaction.commit();
        }
        else if(which==2)
        {
            setTitle("View Compliance");
            Fragment fragment=peoplepFragment.newInstance(2);
            FragmentTransaction transaction=getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.base,fragment,"");
            transaction.commit();
        }
        else
        {
            setTitle("Add Data");
            Fragment fragment=peoplepFragment.newInstance(3);
            FragmentTransaction transaction=getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.base,fragment,"");
            transaction.commit();
        }
    }
    @Override
    public void onPermissionsAdded()
    {
        if(which==1)
        {
            setTitle("Edit Tasks");
            Fragment fragment=peoplepFragment.newInstance(1);
            FragmentTransaction transaction=getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.base,fragment,"");
            transaction.commit();
        }
        else if(which==2)
        {
            setTitle("View Compliance");
            Fragment fragment=peoplepFragment.newInstance(2);
            FragmentTransaction transaction=getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.base,fragment,"");
            transaction.commit();
        }
        else
        {
            setTitle("Add Data");
            Fragment fragment=peoplepFragment.newInstance(3);
            FragmentTransaction transaction=getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.base,fragment,"");
            transaction.commit();
        }
    }
}
