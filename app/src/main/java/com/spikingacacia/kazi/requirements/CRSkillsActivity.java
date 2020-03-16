package com.spikingacacia.kazi.requirements;


import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.PagerTabStrip;
import androidx.viewpager.widget.ViewPager;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.appbar.AppBarLayout;
import com.spikingacacia.kazi.Preferences;
import com.spikingacacia.kazi.R;
import com.spikingacacia.kazi.compliance.CCPersonnelA;

public class CRSkillsActivity extends AppCompatActivity
    implements CRSPositionsF.OnListFragmentInteractionListener,
        CRSQualiF.OnListFragmentInteractionListener
{

    /**
     * The {@link PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    Preferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_crskills);
        preferences=new Preferences(getBaseContext());
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        //toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Skills");
        if(!preferences.isDark_theme_enabled())
        {
            setTheme(R.style.AppThemeLight_NoActionBarLight);
            toolbar.setTitleTextColor(getResources().getColor(R.color.text_light));
            toolbar.setPopupTheme(R.style.AppThemeLight_PopupOverlayLight);
            AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.appbar_layout);
            appBarLayout.getContext().setTheme(R.style.AppThemeLight_AppBarOverlayLight);
            appBarLayout.setBackgroundColor(getResources().getColor(R.color.main_background_light));
            findViewById(R.id.main).setBackgroundColor(getResources().getColor(R.color.main_background_light));
            PagerTabStrip pagerTabStrip=findViewById(R.id.pager_tab_strip);
            pagerTabStrip.setTextColor(getResources().getColor(R.color.text_light));
        }


    }
    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter
    {

        public SectionsPagerAdapter(FragmentManager fm)
        {
            super(fm);
        }

        @Override
        public Fragment getItem(int position)
        {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            if(position==0)
            {
                return CRSOverviewF.newInstance("","");
            }
            else if(position==1)
            {
                return CRSPositionsF.newInstance(1);
            }
            else if(position==2)
            {
                return CRSQualiF.newInstance(1);
            }
            else if(position==3)
            {
                return CRSPositionsQualiF.newInstance("","");
            }
            else if(position==4)
            {
                return CRSNotificationsF.newInstance("","");
            }
            return CRSOverviewF.newInstance("","");
        }

        @Override
        public int getCount()
        {
            // Show 3 total pages.
            return 5;
        }

        @Override
        public CharSequence getPageTitle(int position)
        {
            switch (position)
            {
                case 0:
                    return "Overview";
                case 1:
                    return "Positions";
                case 2:
                    return "Qualifications";
                case 3:
                    return "Positions Qualifications";
                case 4:
                    return "Notifications";
            }
            return null;
        }
    }
    /**implementation of CRSTrades.java**/
    public void onListFragmentInteraction(CRSPositionsFContent.Item item)
    {
        String position=item.content;
        position=position.replace(" ","_");
        Intent intent=new Intent(CRSkillsActivity.this, CCPersonnelA.class);
        intent.putExtra("position",position);
        intent.putExtra("position_id","");
        startActivity(intent);
    }
    /**implementation of CRSQuali.java**/
    public void onListFragmentInteraction(CRSQualiFContent.Item item)
    {

    }
}
