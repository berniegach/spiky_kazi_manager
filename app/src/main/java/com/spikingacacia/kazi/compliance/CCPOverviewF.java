package com.spikingacacia.kazi.compliance;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.core.content.res.ResourcesCompat;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.spikingacacia.kazi.LoginActivity;
import com.spikingacacia.kazi.Preferences;
import com.spikingacacia.kazi.R;
import com.spikingacacia.kazi.pie_chart;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;


public class CCPOverviewF extends Fragment
{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mPositionName;
    private String mPositionId;
    private int complaint=0;
    private int nonCompliant=0;
    private Preferences preferences;

    //private OnFragmentInteractionListener mListener;
    private Typeface font;
    private PieChart chart;

    public CCPOverviewF()
    {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CRSOverviewF.
     */
    // TODO: Rename and change types and number of parameters
    public static CCPOverviewF newInstance(String param1, String param2)
    {
        CCPOverviewF fragment = new CCPOverviewF();
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
            mPositionName = getArguments().getString(ARG_PARAM1);
            mPositionId = getArguments().getString(ARG_PARAM2);
        }
    }
    @Override
    public void onResume()
    {
        super.onResume();
        //when you swipe to the last tab and come back the counters must be re set to 0
        //otherwise they will add extra counts ono themselves
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.f_ccpoverview, container, false);
        preferences=new Preferences(getContext());
        //textviews
        TextView tCount=view.findViewById(R.id.tCount);
        TextView cCount=view.findViewById(R.id.cCount);
        TextView ncCount=view.findViewById(R.id.ncCount);
        TextView position=view.findViewById(R.id.position);
        countComplaint();
        //set the counts
        tCount.setText(Integer.toString(complaint+nonCompliant));
        cCount.setText(Integer.toString(complaint));
        ncCount.setText(Integer.toString(nonCompliant));
        String position_name=mPositionName;
        position_name=position_name.replace("_"," ");
        position.setText(position_name);
        //font
        font= ResourcesCompat.getFont(getContext(),R.font.arima_madurai);
        //chart
        chart=view.findViewById(R.id.chart);
        pie_chart.init(chart,getContext());
        setCompliacePie(chart);
        if(!preferences.isDark_theme_enabled())
        {
            view.findViewById(R.id.fields).setBackgroundColor(getResources().getColor(R.color.tertiary_background_light));
            view.findViewById(R.id.fields_2).setBackgroundColor(getResources().getColor(R.color.tertiary_background_light));
            view.findViewById(R.id.chart_back).setBackgroundColor(getResources().getColor(R.color.main_background_light));
        }
        return view;
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.pie, menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.actionToggleValues: {
                for (IDataSet<?> set : chart.getData().getDataSets())
                    set.setDrawValues(!set.isDrawValuesEnabled());
                item.setChecked(!item.isChecked());
                chart.invalidate();
                break;
            }
            case R.id.actionToggleHole: {
                if (chart.isDrawHoleEnabled())
                    chart.setDrawHoleEnabled(false);
                else
                    chart.setDrawHoleEnabled(true);
                item.setChecked(!item.isChecked());
                chart.invalidate();
                break;
            }
            case R.id.actionTogglePercent:
                chart.setUsePercentValues(!chart.isUsePercentValuesEnabled());
                item.setChecked(!item.isChecked());
                chart.invalidate();
                break;
        }
        return true;
    }
    private void setCompliacePie(PieChart pieChart)
    {
        List<PieEntry>entries=new ArrayList<>();
        //all

        if(complaint==0 && nonCompliant==0)
        {
            entries.add(new PieEntry(1,"Empty"));
        }
        else
        {
            if(complaint>0)
            {
                int count=LoginActivity.cGlobalInfo.getCompliant();
                entries.add(new PieEntry(count,count>0?"Compliant":""));
            }
            if(nonCompliant>0)
            {
                int count=LoginActivity.cGlobalInfo.getNoncompliant();
                entries.add(new PieEntry(count,count>0?"Non Compliant":""));
            }

        }
        pie_chart.add_data(entries,"Compliance",chart);
    }
    private void countComplaint()
    {
        if(mPositionName.contentEquals("All"))
        {
            complaint=LoginActivity.cGlobalInfo.getCompliant();
            nonCompliant=LoginActivity.cGlobalInfo.getNoncompliant();
        }
        else
        {

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
                if(trade.contentEquals(mPositionName))
                    complaint+=1;
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
                if(trade.contentEquals(mPositionName))
                    nonCompliant+=1;
            }
        }
    }
}
