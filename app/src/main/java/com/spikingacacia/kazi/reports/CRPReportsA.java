package com.spikingacacia.kazi.reports;


import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.legacy.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.spikingacacia.kazi.R;
public class CRPReportsA extends AppCompatActivity
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

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_crpreports);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        //toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Skills Reports");

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
            if(position==0)
                return CRPOverviewF.newInstance("","");
            else if(position==1)
                return CRPMissingQualsF.newInstance();
            else if(position==2)
                return CRPMissingMQualsF.newInstance();
            else if(position==3)
                return CRPMissingJQualsF.newInstance();
            else if(position==4)
                return CRPNCCasesF.newInstance();
            else if(position==5)
                return CRPNCDistF.newInstance();
            else
                return CRPOverviewF.newInstance("","");
        }

        @Override
        public int getCount()
        {
            // Show 5 total pages.
            return 6;
        }

        @Override
        public CharSequence getPageTitle(int position)
        {
            switch (position)
            {
                case 0:
                    return "Compliance";
                case 1:
                    return "Missing Qualifications";
                case 2:
                    return "Missing Mandatory Q";
                case 3:
                    return "Missing Job Specific Q";
                case 4:
                    return "Non Compliance Cases";
                case 5:
                    return "Total Non Compliance Distribution";
            }
            return null;
        }
    }
}
