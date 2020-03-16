package com.spikingacacia.kazi.requirements;

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
import android.widget.Button;
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
import com.spikingacacia.kazi.CommonHelper;
import com.spikingacacia.kazi.LoginActivity;
import com.spikingacacia.kazi.Preferences;
import com.spikingacacia.kazi.R;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;


public class CRSOverviewF extends Fragment  implements OnChartValueSelectedListener
{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    //private OnFragmentInteractionListener mListener;
    private Typeface font;
    private int selectorCounter;
    private String[] choices;
    private int countQualsM=0;
    private int countQualsJ=0;
    private  int countT=0;
    private Preferences preferences;
    private PieChart chart;

    public CRSOverviewF()
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
    public static CRSOverviewF newInstance(String param1, String param2)
    {
        CRSOverviewF fragment = new CRSOverviewF();
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
        selectorCounter=1;
        choices=new String[]{"Fields","Compliance","Positions"};
    }
    @Override
    public void onResume()
    {
        super.onResume();
        //when you swipe to the last tab and come back the counters must be re set to 0
        //otherwise they will add extra counts ono themselves
        getFieldsCounts();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.f_crsoverview, container, false);
        preferences = new Preferences(getContext());
        //textviews
        TextView pCount=view.findViewById(R.id.pCount);
        TextView cCount=view.findViewById(R.id.cCount);
        TextView ncCount=view.findViewById(R.id.ncCount);
        TextView tCount=view.findViewById(R.id.tCount);
        TextView mCount=view.findViewById(R.id.mCount);
        TextView jCount=view.findViewById(R.id.jCount);
        //set the counts
        pCount.setText(String.valueOf(LoginActivity.cGlobalInfo.getStaffCount()));
        cCount.setText(String.valueOf(LoginActivity.cGlobalInfo.getCompliant()));
        ncCount.setText(String.valueOf(LoginActivity.cGlobalInfo.getNoncompliant()));
        getFieldsCounts();
        tCount.setText(String.valueOf(countT));
        mCount.setText(String.valueOf(countQualsM));
        jCount.setText(String.valueOf(countQualsJ));

        //font
        font= ResourcesCompat.getFont(getContext(),R.font.arima_madurai);
        //chart
        chart=view.findViewById(R.id.chart);
        setPieChart(chart);
        setCompliacePie(chart);
        //selector textview
        final TextView selector=view.findViewById(R.id.selector);
        final Button back=view.findViewById(R.id.back);
        final Button next=view.findViewById(R.id.next);
        //set the drawablelft programmatically coz of devices under api 21
        CommonHelper.setVectorDrawable(getContext(),back,R.drawable.ic_back,0,0,0);
        CommonHelper.setVectorDrawable(getContext(),next,0,0,R.drawable.ic_next,0);
        back.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {

                if(selectorCounter==1)
                {
                    selector.setText(choices[0]);
                    view.setEnabled(false);
                    selectorCounter--;
                    setFieldsPie(chart);
                }
                else if(selectorCounter==2)
                {
                    selector.setText(choices[1]);
                    next.setEnabled(true);
                    selectorCounter--;
                    setCompliacePie(chart);
                }
            }
        });
        next.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if(selectorCounter==0)
                {
                    selector.setText(choices[1]);
                    back.setEnabled(true);
                    selectorCounter++;
                    setCompliacePie(chart);
                }
                else if(selectorCounter==1)
                {
                    selector.setText(choices[2]);
                    view.setEnabled(false);
                    selectorCounter++;
                    setTradesPie(chart);
                }
            }
        });
        if(!preferences.isDark_theme_enabled())
        {
            view.findViewById(R.id.fields).setBackgroundColor(getResources().getColor(R.color.tertiary_background_light));
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
    private void setPieChart(PieChart chart)
    {
        if(preferences.isDark_theme_enabled())
            chart.setBackgroundColor(Color.BLACK);
        else
            chart.setBackgroundColor(Color.WHITE);
        chart.setUsePercentValues(true);
        chart.getDescription().setEnabled(false);
        chart.setExtraOffsets(5, 10, 5, 5);

        chart.setDragDecelerationFrictionCoef(0.95f);

        //chart.setCenterTextTypeface(tfLight);
        // chart.setCenterText(generateCenterSpannableText());

        chart.setDrawHoleEnabled(true);
        chart.setHoleColor(Color.WHITE);

        chart.setTransparentCircleColor(Color.WHITE);
        chart.setTransparentCircleAlpha(110);

        chart.setHoleRadius(58f);
        chart.setTransparentCircleRadius(61f);

        chart.setDrawCenterText(true);

        chart.setRotationAngle(0);
        // enable rotation of the chart by touch
        chart.setRotationEnabled(true);
        chart.setHighlightPerTapEnabled(true);

        // chart.setUnit(" €");
        // chart.setDrawUnitsInChart(true);

        // add a selection listener
        chart.setOnChartValueSelectedListener(this);

        chart.animateY(1400, Easing.EaseInOutQuad);
        // chart.spin(2000, 0, 360);

        Legend l = chart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(false);
        l.setXEntrySpace(7f);
        l.setYEntrySpace(0f);
        l.setYOffset(0f);

        // entry label styling
        if(preferences.isDark_theme_enabled())
            chart.setEntryLabelColor(Color.WHITE);
        else
            chart.setEntryLabelColor(Color.BLACK);
        //chart.setEntryLabelTypeface(tfRegular);
        chart.setEntryLabelTextSize(12f);

    }
    private void setCompliacePie(PieChart chart)
    {
        List<PieEntry>entries=new ArrayList<>();
        //colors
        if(LoginActivity.cGlobalInfo.getCompliant()==0 && LoginActivity.cGlobalInfo.getNoncompliant()==0)
        {
            entries.add(new PieEntry(1,"Empty"));
        }
        else
        {
            if(LoginActivity.cGlobalInfo.getCompliant()>0)
            {
                int count=LoginActivity.cGlobalInfo.getCompliant();
                entries.add(new PieEntry(count,count>0?"Compliant":""));
            }
            if(LoginActivity.cGlobalInfo.getNoncompliant()>0)
            {
                int count=LoginActivity.cGlobalInfo.getNoncompliant();
                entries.add(new PieEntry(count,count>0?"Non Compliant":""));
            }

        }

        ///////////////////////////////////
        // NOTE: The order of the entries when being added to the entries array determines their position around the center of
        // the chart.

        PieDataSet dataSet = new PieDataSet(entries, "Compliance");

        dataSet.setDrawIcons(false);

        dataSet.setSliceSpace(0f);
        //dataSet.setIconsOffset(new MPPointF(0, 40));
        dataSet.setSelectionShift(5f);

        // add a lot of colors

        ArrayList<Integer> colors = new ArrayList<>();
        for (int c : ColorTemplate.PASTEL_COLORS)
            colors.add(c);
        for (int c : ColorTemplate.VORDIPLOM_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.JOYFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.COLORFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.LIBERTY_COLORS)
            colors.add(c);



        colors.add(ColorTemplate.getHoloBlue());

        dataSet.setColors(colors);
        //dataSet.setSelectionShift(0f);

        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter(chart));
        data.setValueTextSize(11f);
        //data.setValueTextColor(Color.BLUE);
        //data.setValueTypeface(tfLight);
        chart.setData(data);

        // undo all highlights
        chart.highlightValues(null);

        chart.invalidate();

    }
    private void setFieldsPie(PieChart chart)
    {
        List<PieEntry>entries=new ArrayList<>();
        //colors
        if(countQualsM==0 &&countQualsJ==0)
        {
            entries.add(new PieEntry(1,"Empty"));
        }
        else
        {
            if(countQualsM>0)
            {
                entries.add(new PieEntry(countQualsM,countQualsM>0?"Mandatory":""));
            }
            if(countQualsJ>0)
            {
                entries.add(new PieEntry(countQualsJ,countQualsJ>0?"Job Specific":""));
            }
        }
        // NOTE: The order of the entries when being added to the entries array determines their position around the center of
        // the chart.

        PieDataSet dataSet = new PieDataSet(entries, "Fields");

        dataSet.setDrawIcons(false);

        dataSet.setSliceSpace(0f);
        //dataSet.setIconsOffset(new MPPointF(0, 40));
        dataSet.setSelectionShift(5f);

        // add a lot of colors

        ArrayList<Integer> colors = new ArrayList<>();
        for (int c : ColorTemplate.PASTEL_COLORS)
            colors.add(c);
        for (int c : ColorTemplate.VORDIPLOM_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.JOYFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.COLORFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.LIBERTY_COLORS)
            colors.add(c);



        colors.add(ColorTemplate.getHoloBlue());

        dataSet.setColors(colors);
        //dataSet.setSelectionShift(0f);

        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter(chart));
        data.setValueTextSize(11f);
        //data.setValueTextColor(Color.BLUE);
        //data.setValueTypeface(tfLight);
        chart.setData(data);

        // undo all highlights
        chart.highlightValues(null);

        chart.invalidate();

    }
    private void setTradesPie(PieChart chart)
    {
        List<PieEntry>entries=new ArrayList<>();
        Iterator iterator= LoginActivity.tradesList.entrySet().iterator();
        List<Integer>tempColorsRes=new ArrayList<>();
        if(LoginActivity.tradesList.size()==0)
        {
            entries.add(new PieEntry(1,"Empty"));
        }
        else
        {
            int index=0;
            while (iterator.hasNext())
            {
                LinkedHashMap.Entry<String,Integer>set=(LinkedHashMap.Entry<String, Integer>) iterator.next();
                String name=set.getKey();
                Integer count=set.getValue();
                name=name.replace("_"," ");
                if (count==0)
                    continue;
                entries.add(new PieEntry(count,name));
                index+=1;
            }
        }

        // NOTE: The order of the entries when being added to the entries array determines their position around the center of
        // the chart.

        PieDataSet dataSet = new PieDataSet(entries, "Trades");

        dataSet.setDrawIcons(false);

        dataSet.setSliceSpace(0f);
        //dataSet.setIconsOffset(new MPPointF(0, 40));
        dataSet.setSelectionShift(5f);

        // add a lot of colors

        ArrayList<Integer> colors = new ArrayList<>();
        for (int c : ColorTemplate.PASTEL_COLORS)
            colors.add(c);
        for (int c : ColorTemplate.VORDIPLOM_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.JOYFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.COLORFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.LIBERTY_COLORS)
            colors.add(c);



        colors.add(ColorTemplate.getHoloBlue());

        dataSet.setColors(colors);
        //dataSet.setSelectionShift(0f);

        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter(chart));
        data.setValueTextSize(11f);
        //data.setValueTextColor(Color.BLUE);
        //data.setValueTypeface(tfLight);
        chart.setData(data);

        // undo all highlights
        chart.highlightValues(null);

        chart.invalidate();
    }

    private void getFieldsCounts()
    {
        //count fields
        countQualsM=0;
        countQualsJ=0;
        countT=0;
        Iterator iteratorCount=LoginActivity.personnelColumnsList.entrySet().iterator();
        while(iteratorCount.hasNext())
        {
            LinkedHashMap.Entry<String,Character>set=(LinkedHashMap.Entry<String, Character>) iteratorCount.next();
            String name=set.getKey();
            Character which=set.getValue();
            if(which=='t')
                countT+=1;
            else if(which=='m')
                countQualsM+=1;
            else if(which=='j')
                countQualsJ+=1;
        }
    }
    @Override
    public void onValueSelected(Entry e, Highlight h) {

        if (e == null)
            return;
        Log.i("VAL SELECTED",
                "Value: " + e.getY() + ", index: " + h.getX()
                        + ", DataSet index: " + h.getDataSetIndex());
    }

    @Override
    public void onNothingSelected() {
        Log.i("PieChart", "nothing selected");
    }

}
