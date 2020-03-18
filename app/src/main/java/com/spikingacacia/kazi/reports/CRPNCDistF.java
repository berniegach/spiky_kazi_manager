package com.spikingacacia.kazi.reports;


import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.core.content.res.ResourcesCompat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.interfaces.datasets.IDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.spikingacacia.kazi.CommonHelper;
import com.spikingacacia.kazi.LoginActivity;
import com.spikingacacia.kazi.Preferences;
import com.spikingacacia.kazi.R;
import com.spikingacacia.kazi.pie_chart;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class CRPNCDistF extends Fragment
{
    private LinearLayout layout;
    private Typeface font;
    private int selectorCounter;
    private String[] choices;
    private Preferences preferences;
    private PieChart chart;


    public static CRPNCDistF newInstance()
    {
        CRPNCDistF fragment = new CRPNCDistF();
        return fragment;
    }
    public CRPNCDistF()
    {
        // Required empty public constructor
    }
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        selectorCounter=0;
        choices=new String[LoginActivity.tradesList.size()+1];
        choices[0]="All";
        int c=1;
        Iterator iterator=LoginActivity.tradesList.entrySet().iterator();
        while(iterator.hasNext())
        {
            LinkedHashMap.Entry<String,Integer>set=(LinkedHashMap.Entry<String, Integer>) iterator.next();
            String name=set.getKey();
            Integer count=set.getValue();
            choices[c]=name;
            c+=1;
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.f_crpnc_dist, container, false);
        preferences=new Preferences(getContext());
        //main vertical layout
        layout=view.findViewById(R.id.quals_layout);
        //font
        font= ResourcesCompat.getFont(getContext(),R.font.arima_madurai);
        //chart
        chart=view.findViewById(R.id.chart);
        pie_chart.init(chart,getContext());
        setNonComplianceCasesPie(chart);
        //selector textview
        final TextView selector=view.findViewById(R.id.selector);
        final Button back=view.findViewById(R.id.back);
        final Button next=view.findViewById(R.id.next);
        CommonHelper.setVectorDrawable(getContext(),back,R.drawable.ic_back,0,0,0);
        CommonHelper.setVectorDrawable(getContext(),next,0,0,R.drawable.ic_next,0);
        back.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if(selectorCounter>0)
                {
                    selectorCounter--;
                    if(selectorCounter==0)
                        ;//setCompliacePie(chart);
                    else
                        ;//setTradesPie(chart,choices[selectorCounter]);
                    selector.setText(choices[selectorCounter]);
                }
            }
        });
        next.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if(selectorCounter<choices.length)
                {
                    if(selectorCounter!=choices.length-1)
                        selectorCounter++;
                    if(selectorCounter==0)
                        ;//setCompliacePie(chart);
                    else
                        ;//setTradesPie(chart,choices[selectorCounter]);
                    selector.setText(choices[selectorCounter]);
                }
            }
        });
        if(!preferences.isDark_theme_enabled())
        {
            view.findViewById(R.id.chart_back).setBackgroundColor(getResources().getColor(R.color.secondary_background_light));
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
    private void setNonComplianceCasesPie(PieChart pieChart)
    {
        List<PieEntry> entries=new ArrayList<>();
        //missing single case
        int sc=LoginActivity.cGlobalInfo.getUsersWithCase();
        //missing qualification and missing certificate
        int qc=LoginActivity.cGlobalInfo.getUsersWithQCCase();
        //missing qualification and expired certificates
        int qec=LoginActivity.cGlobalInfo.getUsersWithQECCase();
        //missing certificates and expired certicates
        int cec=LoginActivity.cGlobalInfo.getUsersWithCECCase();
        //All three
        int all=LoginActivity.cGlobalInfo.getUsersWithQCECCase();
        if(sc==0 && qc==0 && qec==0 && cec==0 && all==0)
        {
            entries.add(new PieEntry(1,"Empty"));
        }
        else
        {
            if(sc>0)
            {
                entries.add(new PieEntry(sc, "Single Case"));
            }
            if(qc>0)
            {
                entries.add(new PieEntry(qc, "Missing Quals && Missing Certs"));
            }
            if(qec>0)
            {
                entries.add(new PieEntry(qec, "Missing Quals && Expired Certs"));
            }
            if(cec>0)
            {
                entries.add(new PieEntry(cec, "Missing && Expired Certs"));
            }
            if(all>0)
            {
                entries.add(new PieEntry(all, "All Three"));
            }

        }

        pie_chart.add_data(entries,"Cases",chart);
    }

}
