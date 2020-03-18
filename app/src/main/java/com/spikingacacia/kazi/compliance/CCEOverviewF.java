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
import java.util.List;


public class CCEOverviewF extends Fragment
{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private Preferences preferences;

    //private OnFragmentInteractionListener mListener;
    private Typeface font;
    private PieChart chart;

    public CCEOverviewF()
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
    public static CCEOverviewF newInstance(String param1, String param2)
    {
        CCEOverviewF fragment = new CCEOverviewF();
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
        View view= inflater.inflate(R.layout.f_cceoverview, container, false);
        preferences=new Preferences(getContext());
        //textviews
        TextView tCount=view.findViewById(R.id.tCount);
        TextView cCount=view.findViewById(R.id.cCount);
        TextView ncCount=view.findViewById(R.id.ncCount);
        //set the counts
        tCount.setText(String.valueOf(LoginActivity.cGlobalInfoEquips.getEquipsCount()));
        cCount.setText(String.valueOf(LoginActivity.cGlobalInfoEquips.getCompliant()));
        ncCount.setText(String.valueOf(LoginActivity.cGlobalInfoEquips.getNoncompliant()));
        //font
        font= ResourcesCompat.getFont(getContext(),R.font.arima_madurai);
        //chart
        chart=view.findViewById(R.id.chart);
        pie_chart.init(chart,getContext());
        setCompliacePie(chart);
        if(!preferences.isDark_theme_enabled())
        {
            view.findViewById(R.id.fields).setBackgroundColor(getResources().getColor(R.color.tertiary_background_light));
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
        if(LoginActivity.cGlobalInfoEquips.getCompliant()==0 && LoginActivity.cGlobalInfoEquips.getNoncompliant()==0)
        {
            entries.add(new PieEntry(1,"Empty"));
        }
        else
        {
            if(LoginActivity.cGlobalInfoEquips.getCompliant()>0)
            {
                int count=LoginActivity.cGlobalInfoEquips.getCompliant();
                entries.add(new PieEntry(count,count>0?"Pass":""));
            }
            if(LoginActivity.cGlobalInfoEquips.getNoncompliant()>0)
            {
                int count=LoginActivity.cGlobalInfoEquips.getNoncompliant();
                entries.add(new PieEntry(count,count>0?"Fail":""));
            }

        }
        pie_chart.add_data(entries,"Compliance",chart);
    }
}
