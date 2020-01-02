package com.spikingacacia.kazi.equipments;


import androidx.legacy.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.spikingacacia.kazi.R;
public class CEPropertiesA extends AppCompatActivity
    implements CEPEquipmentsF.OnListFragmentInteractionListener,
        CEPPropertiesF.OnListFragmentInteractionListener
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
        setContentView(R.layout.a_ceproperties);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        //toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Equipments");

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
                return CEPOverviewF.newInstance("","");
            else if(position==1)
                return CEPEquipmentsF.newInstance(1);
            else if(position==2)
                return CEPPropertiesF.newInstance(1);
            else if(position==3)
                return CEPEquipPropertiesF.newInstance("","");
            else
                return CEPNotificationsF.newInstance("","");
        }

        @Override
        public int getCount()
        {
            // Show 5 total pages.
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
                    return "Equipments";
                case 2:
                    return "Properties";
                case 3:
                    return "Equipments Properties";
                case 4:
                    return "Notifications";
            }
            return null;
        }
    }
    /**implementation of CRSTrades.java**/
    public void onListFragmentInteraction(CEPEquipmentsFContent.Item item)
    {

    }
    /**implementation of CEPPropertiesF.java**/
    public void onListFragmentInteraction(CEPPropertiesFContent.Item item)
    {

    }
}
