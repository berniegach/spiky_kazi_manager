package com.spikingacacia.kazi.reports;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.core.content.res.ResourcesCompat;
import android.util.Log;
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


public class CRPOverviewF extends Fragment
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
    private LinearLayout l_trades;
    private LinearLayout l_mand;
    private LinearLayout l_jobs;
    private TextView pCount;
    private TextView cCount;
    private TextView ncCount;

    public CRPOverviewF()
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
    public static CRPOverviewF newInstance(String param1, String param2)
    {
        CRPOverviewF fragment = new CRPOverviewF();
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
        if (getArguments() != null)
        {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
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
        View view= inflater.inflate(R.layout.f_crpoverview, container, false);
        //layouts
        l_trades=view.findViewById(R.id.l_trades);
        l_mand=view.findViewById(R.id.l_mand);
        l_jobs=view.findViewById(R.id.l_job);
        //textviews
        pCount=view.findViewById(R.id.pCount);
        cCount=view.findViewById(R.id.cCount);
        ncCount=view.findViewById(R.id.ncCount);
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
        final PieChart chart=view.findViewById(R.id.chart);
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
                if(selectorCounter>0)
                {
                    selectorCounter--;
                    Log.d("SELECTOR ",String.valueOf(selectorCounter));
                    if(selectorCounter==0)
                        setCompliacePie(chart);
                    else
                        setTradesPie(chart,choices[selectorCounter]);
                    String position=choices[selectorCounter];
                    position=position.replace("_"," ");
                    selector.setText(position);
                }

               /* if(selectorCounter==1)
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
                }*/
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
                    Log.d("SELECTOR ",String.valueOf(selectorCounter));
                    if(selectorCounter==0)
                        setCompliacePie(chart);
                    else
                        setTradesPie(chart,choices[selectorCounter]);
                    String position=choices[selectorCounter];
                    position=position.replace("_"," ");
                    selector.setText(position);
                }
                /*if(selectorCounter==0)
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
                }*/
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
       // pieChart.setHoleRadius(95f);
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
    private void setCompliacePie(PieChart pieChart)
    {
        //set the counts
        pCount.setText(String.valueOf(LoginActivity.cGlobalInfo.getStaffCount()));
        cCount.setText(String.valueOf(LoginActivity.cGlobalInfo.getCompliant()));
        ncCount.setText(String.valueOf(LoginActivity.cGlobalInfo.getNoncompliant()));
        l_trades.setVisibility(View.VISIBLE);
        l_mand.setVisibility(View.VISIBLE);
        l_jobs.setVisibility(View.VISIBLE);

        List<PieEntry>entries=new ArrayList<>();
        //colors
        List<Integer>colors=new ArrayList<>();
        List<Integer>tempColors=new ArrayList<>();
        if(LoginActivity.cGlobalInfo.getCompliant()==0 && LoginActivity.cGlobalInfo.getNoncompliant()==0)
        {
            entries.add(new PieEntry(1,"Empty"));
            colors=ColorTemplate.createColors(getResources(),new int[]{R.color.graph_1});
        }
        else
        {
            if(LoginActivity.cGlobalInfo.getCompliant()>0)
            {
                int count=LoginActivity.cGlobalInfo.getCompliant();
                entries.add(new PieEntry(count,count>0?"Compliant":""));
                tempColors.add(R.color.graph_14);
                //ColorTemplate.createColors(getResources(),new int[]{R.color.a_mono3});
            }
            if(LoginActivity.cGlobalInfo.getNoncompliant()>0)
            {
                int count=LoginActivity.cGlobalInfo.getNoncompliant();
                entries.add(new PieEntry(count,count>0?"Non Compliant":""));
                tempColors.add(R.color.graph_13);
                //ColorTemplate.createColors(getResources(),new int[]{R.color.a_mono3});
            }
            int[]tempTempColors=new int[tempColors.size()];
            for(int count=0; count<tempColors.size(); count+=1 )
                tempTempColors[count]=tempColors.get(count);
            colors=ColorTemplate.createColors(getResources(),tempTempColors);

        }
        PieDataSet set=new PieDataSet(entries,"Count");
        set.setSliceSpace(0f);
        //colors
        set.setColors(colors);
        PieData data=new PieData(set);
        data.setValueTextColor(Color.WHITE);
        data.setValueTypeface(font);
        data.setValueFormatter(new PercentFormatter());
        pieChart.setData(data);
        pieChart.highlightValues(null);
        pieChart.invalidate();
        /*
        List<PieEntry>entries=new ArrayList<>();
        entries.add(new PieEntry(LoginActivity.cGlobalInfo.getCompliant(),"Compliant"));
        entries.add(new PieEntry(LoginActivity.cGlobalInfo.getNoncompliant(),"Non Compliant"));
        PieDataSet set=new PieDataSet(entries,"Compliance");
        set.setSliceSpace(0f);
        //colors
        List<Integer>colors=ColorTemplate.createColors(getResources(),new int[]{R.color.a_mono2,R.color.a_comp3});
        set.setColors(colors);
        PieData data=new PieData(set);
        data.setValueTextColor(Color.WHITE);
        data.setValueTypeface(font);
        data.setValueFormatter(new PercentFormatter());
        pieChart.setData(data);
        pieChart.highlightValues(null);
        pieChart.invalidate();*/

    }
    private void setTradesPie(PieChart pieChart, String tradeName)
    {
        //getting the compliant and non compliant count
        int c=0,nc=0;
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
            if(trade.contentEquals(tradeName))
                c+=1;
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
            if(trade.contentEquals(tradeName))
                nc+=1;
        }
        //set the counts
        pCount.setText(String.valueOf(c+nc));
        cCount.setText(String.valueOf(c));
        ncCount.setText(String.valueOf(nc));
        l_trades.setVisibility(View.GONE);
        l_mand.setVisibility(View.GONE);
        l_jobs.setVisibility(View.GONE);
        //setting the piechart
        /*
        List<PieEntry>entries=new ArrayList<>();
        entries.add(new PieEntry(c,"Compliant"));
        entries.add(new PieEntry(nc,"Non Compliant"));
        PieDataSet set=new PieDataSet(entries,"Compliance");
        set.setSliceSpace(0f);
        //colors
        List<Integer>colors=ColorTemplate.createColors(getResources(),new int[]{R.color.a_mono2,R.color.a_comp3});
        set.setColors(colors);
        PieData data=new PieData(set);
        data.setValueTextColor(Color.WHITE);
        data.setValueTypeface(font);
        data.setValueFormatter(new PercentFormatter());
        pieChart.setData(data);
        pieChart.highlightValues(null);
        pieChart.invalidate();*/

        List<PieEntry>entries=new ArrayList<>();
        //colors
        List<Integer>colors=new ArrayList<>();
        List<Integer>tempColors=new ArrayList<>();
        if(c==0 && nc==0)
        {
            entries.add(new PieEntry(1,"Empty"));
            colors=ColorTemplate.createColors(getResources(),new int[]{R.color.graph_1});
        }
        else
        {
            if(c>0)
            {
                entries.add(new PieEntry(c,c>0?"Compliant":""));
                tempColors.add(R.color.graph_14);
                //ColorTemplate.createColors(getResources(),new int[]{R.color.a_mono3});
            }
            if(nc>0)
            {
                entries.add(new PieEntry(nc,nc>0?"Non Compliant":""));
                tempColors.add(R.color.graph_13);
                //ColorTemplate.createColors(getResources(),new int[]{R.color.a_mono3});
            }
            int[]tempTempColors=new int[tempColors.size()];
            for(int count=0; count<tempColors.size(); count+=1 )
                tempTempColors[count]=tempColors.get(count);
            colors=ColorTemplate.createColors(getResources(),tempTempColors);

        }
        PieDataSet set=new PieDataSet(entries,"Count");
        set.setSliceSpace(0f);
        //colors
        set.setColors(colors);
        PieData data=new PieData(set);
        data.setValueTextColor(Color.WHITE);
        data.setValueTypeface(font);
        data.setValueFormatter(new PercentFormatter());
        pieChart.setData(data);
        pieChart.highlightValues(null);
        pieChart.invalidate();
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
    /*
    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener)
        {
            mListener = (OnFragmentInteractionListener) context;
        } else
        {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
        mListener = null;
    }


    public interface OnFragmentInteractionListener
    {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    } */
}