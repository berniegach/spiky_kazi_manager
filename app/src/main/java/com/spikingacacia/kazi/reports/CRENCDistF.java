package com.spikingacacia.kazi.reports;


import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.core.content.res.ResourcesCompat;
import android.view.LayoutInflater;
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
import com.github.mikephil.charting.utils.ColorTemplate;
import com.spikingacacia.kazi.CommonHelper;
import com.spikingacacia.kazi.LoginActivity;
import com.spikingacacia.kazi.R;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class CRENCDistF extends Fragment
{
    private LinearLayout layout;
    private Typeface font;
    private int selectorCounter;
    private String[] choices;


    public static CRENCDistF newInstance()
    {
        CRENCDistF fragment = new CRENCDistF();
        return fragment;
    }
    public CRENCDistF()
    {
        // Required empty public constructor
    }
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
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
        View view=inflater.inflate(R.layout.f_crencdist, container, false);
        //main vertical layout
        layout=view.findViewById(R.id.quals_layout);
        //font
        font= ResourcesCompat.getFont(getContext(),R.font.arima_madurai);
        //chart
        final PieChart chart=view.findViewById(R.id.chart);
        setPieChart(chart);
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
        return view;
    }
    private void setPieChart(PieChart pieChart)
    {
        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);
        pieChart.setExtraOffsets(5,10,5,5);
        pieChart.setDragDecelerationFrictionCoef(0.95f);
        pieChart.setDrawHoleEnabled(false);
        //pieChart.setHoleColor(Color.TRANSPARENT);
        pieChart.setTransparentCircleColor(Color.WHITE);
        pieChart.setTransparentCircleAlpha(110);
        //pieChart.setHoleRadius(95f);
        pieChart.setRotationAngle(0);
        pieChart.setRotationEnabled(true);
        pieChart.setHighlightPerTapEnabled(true);
        pieChart.setEntryLabelColor(Color.WHITE);
        // pieChart.setEntryLabelTypeface(getResources().getFont(R.font.arima_madurai));

        Legend legend=pieChart.getLegend();
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        legend.setOrientation(Legend.LegendOrientation.VERTICAL);
        legend.setDrawInside(false);
        legend.setXEntrySpace(7f);
        legend.setYEntrySpace(0f);
        legend.setYOffset(0f);
        legend.setTextSize(13);
        legend.setTextColor(Color.WHITE);
        legend.setTypeface(font);
        //entry label
        pieChart.setEntryLabelColor(Color.WHITE);
        pieChart.setEntryLabelTypeface(font);
        pieChart.setEntryLabelTextSize(12);


        pieChart.invalidate();

    }
    private void setNonComplianceCasesPie(PieChart pieChart)
    {
        List<PieEntry> entries=new ArrayList<>();
        //colors
        List<Integer>colors=new ArrayList<>();
        List<Integer>tempColors=new ArrayList<>();
        //missing single case
        int sc=LoginActivity.cGlobalInfoEquips.getUsersWithCase();
        //missing qualification and missing certificate
        int qc=LoginActivity.cGlobalInfoEquips.getUsersWithQCCase();
        //missing qualification and expired certificates
        int qec=LoginActivity.cGlobalInfoEquips.getUsersWithQECCase();
        //missing certificates and expired certicates
        int cec=LoginActivity.cGlobalInfoEquips.getUsersWithCECCase();
        //All three
        int all=LoginActivity.cGlobalInfoEquips.getUsersWithQCECCase();
        if(sc==0 && qc==0 && qec==0 && cec==0 && all==0)
        {
            entries.add(new PieEntry(1,"Empty"));
            colors=ColorTemplate.createColors(getResources(),new int[]{R.color.graph_1});
        }
        else
        {
            if(sc>0)
            {
                entries.add(new PieEntry(sc, "Single Case"));
                tempColors.add(getColor(1 % 16));
            }
            if(qc>0)
            {
                entries.add(new PieEntry(qc, "Missing Quals && Missing Certs"));
                tempColors.add(getColor(4 % 16));
            }
            if(qec>0)
            {
                entries.add(new PieEntry(qec, "Missing Quals && Expired Certs"));
                tempColors.add(getColor(5 % 16));
            }
            if(cec>0)
            {
                entries.add(new PieEntry(cec, "Missing && Expired Certs"));
                tempColors.add(getColor(6 % 16));
            }
            if(all>0)
            {
                entries.add(new PieEntry(all, "All Three"));
                tempColors.add(getColor(7 % 16));
            }
            int[]tempTempColors=new int[tempColors.size()];
            for(int count=0; count<tempColors.size(); count+=1 )
                tempTempColors[count]=tempColors.get(count);
            colors=ColorTemplate.createColors(getResources(),tempTempColors);
        }

        PieDataSet set=new PieDataSet(entries,"Cases");
        set.setSliceSpace(0f);
        //colors
        //List<Integer>colors=ColorTemplate.createColors(getResources(),colorsRes);
        set.setColors(colors);
        PieData data=new PieData(set);
        data.setValueTextColor(Color.WHITE);
        data.setValueTypeface(font);
        data.setValueFormatter(new PercentFormatter());
        pieChart.setData(data);
        pieChart.highlightValues(null);
        pieChart.invalidate();

    }
    private int getColor(final int index)
    {
        switch(index)
        {
            case 0:
                return R.color.graph_1;
            case 1:
                return R.color.graph_2;
            case 2:
                return R.color.graph_3;
            case 3:
                return R.color.graph_4;
            case 4:
                return R.color.graph_5;
            case 5:
                return R.color.graph_6;
            case 6:
                return R.color.graph_7;
            case 7:
                return R.color.graph_8;
            case 8:
                return R.color.graph_9;
            case 9:
                return R.color.graph_10;
            case 10:
                return R.color.graph_11;
            case 11:
                return R.color.graph_12;
            case 12:
                return R.color.graph_13;
            case 13:
                return R.color.graph_14;
            case 14:
                return R.color.graph_2;
            case 15:
                return R.color.graph_3;
            default:
                return R.color.graph_4;
        }

    }

}
