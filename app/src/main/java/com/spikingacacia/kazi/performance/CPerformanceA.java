package com.spikingacacia.kazi.performance;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.util.Log;

import com.google.android.material.appbar.AppBarLayout;
import com.spikingacacia.kazi.Preferences;
import com.spikingacacia.kazi.R;

public class CPerformanceA extends AppCompatActivity
implements CPOverviewF.OnFragmentInteractionListener, CPReviewsF.OnListFragmentInteractionListener,
CPAddReviewF.OnFragmentInteractionListener,CPUserOverviewF.OnFragmentInteractionListener,
CPUserReviewsF.OnListFragmentInteractionListener{


    private String fragmentWhich="overview";
    public static  String reviewer="manager";
    private Preferences preferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_cperformance);
        preferences = new Preferences(getBaseContext());
        //set actionbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Overview");
        if(!preferences.isDark_theme_enabled())
        {
            setTheme(R.style.AppThemeLight_NoActionBarLight);
            toolbar.setTitleTextColor(getResources().getColor(R.color.text_light));
            toolbar.setPopupTheme(R.style.AppThemeLight_PopupOverlayLight);
            AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.appbar_layout);
            appBarLayout.getContext().setTheme(R.style.AppThemeLight_AppBarOverlayLight);
            appBarLayout.setBackgroundColor(getResources().getColor(R.color.main_background_light));
            findViewById(R.id.main).setBackgroundColor(getResources().getColor(R.color.main_background_light));
        }
        //set the first base fragment
        Fragment fragment=CPOverviewF.newInstance("","");
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
                    setTitle("Overview");
                else if(count==1)
                    setTitle(fragmentWhich);
            }
        });

    }
    /**
     * implementation of CPOverview.java*/
    @Override
    public void onLayoutClicked(int id)
    {
        if(id==1)
        {
            fragmentWhich="Pending Reviews";
            setTitle(fragmentWhich);
            Fragment fragment= CPReviewsF.newInstance(1,0);
            FragmentTransaction transaction=getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.base,fragment,fragmentWhich);
            transaction.addToBackStack(fragmentWhich);
            transaction.commit();
        }
        else if(id==2)
        {
            fragmentWhich="All Reviews";
            setTitle(fragmentWhich);
            Fragment fragment= CPReviewsF.newInstance(1,1);
            FragmentTransaction transaction=getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.base,fragment,fragmentWhich);
            transaction.addToBackStack(fragmentWhich);
            transaction.commit();
        }
    }



    /**
    * implementation of CPReviewsF.java*/
    @Override
    public void onPendingClicked(CPReviewsContent.ReviewItem item)
    {


        fragmentWhich=item.names;
        setTitle(fragmentWhich);
        Fragment fragment;
        if(item.classes==0)
            fragment=CPAddReviewF.newInstance(item.id,Integer.parseInt(item.count));
        else
            fragment=CPUserOverviewF.newInstance(item.userid);
        FragmentTransaction transaction=getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.base,fragment,fragmentWhich);
        transaction.addToBackStack(fragmentWhich);
        transaction.commit();

    }
    /**
     * implementation of CPAddReviewF.java*/
    @Override
    public void onReviewUpdated(int id)
    {
        fragmentWhich="Pending Reviews";
        setTitle(fragmentWhich);
        onBackPressed();
    }
    /**
     * implementation of CPUserOverviewF.java*/
    @Override
    public void onUserLayoutClicked( int userId)
    {
        Fragment fragment=CPUserReviewsF.newInstance(userId);
        FragmentTransaction transaction=getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.base,fragment,fragmentWhich);
        transaction.addToBackStack(fragmentWhich);
        transaction.commit();
    }
    /**
     * implementation of CPUserReviewsF.java*/
    @Override
    public void onUserReviewClicked(CPUserReviewsContent.ReviewItem item)
    {
       // fragmentWhich="";
        //setTitle(fragmentWhich);
        Fragment fragment= CPReviewDetailF.newInstance(item.review,item.toImprove,item.reviewer,item.rating,item.dateAdded);
        FragmentTransaction transaction=getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.base,fragment,fragmentWhich);
        transaction.addToBackStack(fragmentWhich);
        transaction.commit();
    }
}
