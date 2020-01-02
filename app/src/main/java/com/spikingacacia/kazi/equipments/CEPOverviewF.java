package com.spikingacacia.kazi.equipments;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.core.content.res.ResourcesCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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


public class CEPOverviewF extends Fragment
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

    public CEPOverviewF()
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
    public static CEPOverviewF newInstance(String param1, String param2)
    {
        CEPOverviewF fragment = new CEPOverviewF();
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
        selectorCounter=1;
        choices=new String[]{"Fields","Compliance","Equipments"};
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
        View view= inflater.inflate(R.layout.f_cepoverview, container, false);
        //textviews
        //equipments count
        TextView eCount=view.findViewById(R.id.eCount);
        //compliant equipments count
        TextView cCount=view.findViewById(R.id.cCount);
        //non compliant equipments count
        TextView ncCount=view.findViewById(R.id.ncCount);
        //important properties count
        TextView iCount=view.findViewById(R.id.iCount);
        //other properties count
        TextView oCount=view.findViewById(R.id.oCount);
        //personnel with equipments count
        TextView perCount=view.findViewById(R.id.perCount);
        //set the counts
        eCount.setText(String.valueOf(LoginActivity.cGlobalInfoEquips.getEquipsCount()));
        cCount.setText(String.valueOf(LoginActivity.cGlobalInfoEquips.getCompliant()));
        ncCount.setText(String.valueOf(LoginActivity.cGlobalInfoEquips.getNoncompliant()));
        getFieldsCounts();
        iCount.setText(String.valueOf(countQualsM));
        oCount.setText(String.valueOf(countQualsJ));
        perCount.setText(String.valueOf(LoginActivity.cGlobalInfoEquips.getStaffCount()));

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
                    setEquipmentsPie(chart);
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
       // pieChart.setHoleColor(Color.TRANSPARENT);
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
        List<PieEntry>entries=new ArrayList<>();
        //colors
        List<Integer>colors=new ArrayList<>();
        List<Integer>tempColors=new ArrayList<>();
        if(LoginActivity.cGlobalInfoEquips.getCompliant()==0 && LoginActivity.cGlobalInfoEquips.getNoncompliant()==0)
        {
            entries.add(new PieEntry(1,"Empty"));
            colors=ColorTemplate.createColors(getResources(),new int[]{R.color.graph_1});
        }
        else
        {
            if(LoginActivity.cGlobalInfoEquips.getCompliant()>0)
            {
                int count=LoginActivity.cGlobalInfoEquips.getCompliant();
                entries.add(new PieEntry(count,count>0?"Pass":""));
                tempColors.add(R.color.graph_14);
                //ColorTemplate.createColors(getResources(),new int[]{R.color.a_mono3});
            }
            if(LoginActivity.cGlobalInfoEquips.getNoncompliant()>0)
            {
                int count=LoginActivity.cGlobalInfoEquips.getNoncompliant();
                entries.add(new PieEntry(count,count>0?"Fail":""));
                tempColors.add(R.color.graph_13);
                //ColorTemplate.createColors(getResources(),new int[]{R.color.a_mono3});
            }
            int[]tempTempColors=new int[tempColors.size()];
            for(int count=0; count<tempColors.size(); count+=1 )
                tempTempColors[count]=tempColors.get(count);
            colors=ColorTemplate.createColors(getResources(),tempTempColors);

        }
        PieDataSet set=new PieDataSet(entries,"Compliance");
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
    private void setFieldsPie(PieChart pieChart)
    {
        List<PieEntry>entries=new ArrayList<>();
        //colors
        List<Integer>colors=new ArrayList<>();
        List<Integer>tempColors=new ArrayList<>();
        if(countQualsM==0 &&countQualsJ==0)
        {
            entries.add(new PieEntry(1,"Empty"));
            colors=ColorTemplate.createColors(getResources(),new int[]{R.color.graph_1});
        }
        else
        {
            if(countQualsM>0)
            {
                entries.add(new PieEntry(countQualsM,countQualsM>0?"Important":""));
                tempColors.add(R.color.graph_11);
            }
            if(countQualsJ>0)
            {
                entries.add(new PieEntry(countQualsJ,countQualsJ>0?"Others":""));
                tempColors.add(R.color.graph_12);
            }
            int[]tempTempColors=new int[tempColors.size()];
            for(int count=0; count<tempColors.size(); count+=1 )
                tempTempColors[count]=tempColors.get(count);
            colors=ColorTemplate.createColors(getResources(),tempTempColors);

        }
        PieDataSet set=new PieDataSet(entries,"Fields");
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
    private void setEquipmentsPie(PieChart pieChart)
    {
        List<PieEntry>entries=new ArrayList<>();
        //colors
        List<Integer>colors=new ArrayList<>();
        List<Integer>tempColors=new ArrayList<>();

        Iterator iterator= LoginActivity.equipmentsList.entrySet().iterator();
        List<Integer>tempColorsRes=new ArrayList<>();
        if(LoginActivity.equipmentsList.size()==0)
        {
            entries.add(new PieEntry(1,"Empty"));
            colors=ColorTemplate.createColors(getResources(),new int[]{R.color.graph_1});
        }
        else
        {
            int index=0;
            while (iterator.hasNext())
            {
                LinkedHashMap.Entry<String,Integer>set=(LinkedHashMap.Entry<String, Integer>) iterator.next();
                String name=set.getKey();
                Integer count=set.getValue();
                if (count==0)
                    continue;
                entries.add(new PieEntry(count,name));
                tempColors.add(getColor(index%16));
                index+=1;
            }
            int[]tempTempColors=new int[tempColors.size()];
            for(int count=0; count<tempColors.size(); count+=1 )
                tempTempColors[count]=tempColors.get(count);
            colors=ColorTemplate.createColors(getResources(),tempTempColors);
        }

        PieDataSet set=new PieDataSet(entries,"Equipments");
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
    private void getFieldsCounts()
    {
        //count fields
        countQualsM=0;
        countQualsJ=0;
        countT=0;
        Iterator iteratorCount=LoginActivity.equipmentsColumnsList.entrySet().iterator();
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
