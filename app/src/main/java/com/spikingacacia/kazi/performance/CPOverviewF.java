package com.spikingacacia.kazi.performance;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.DefaultValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.spikingacacia.kazi.JSONParser;
import com.spikingacacia.kazi.LoginActivity;
import com.spikingacacia.kazi.Preferences;
import com.spikingacacia.kazi.R;
import com.spikingacacia.kazi.database.CReviews;
import com.spikingacacia.kazi.pie_chart;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CPOverviewF.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CPOverviewF#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CPOverviewF extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private static String url_update_period= LoginActivity.base_url+"update_boss_review_period.php";
    private static JSONParser jsonParser;
    private static String TAG_SUCCESS="success";
    private static String TAG_MESSAGE="message";
    private PieChart chart;
    private TextView t_p_count;
    private TextView t_a_count;
    private  RatingBar rating_bar;
    private TextView t_rating;
    private int rating=0;
    private int pending=0;
    private int all=0;

    private Spinner spinner;
    private int spinnerSelection=0;
    private  boolean justStarted;
    private Preferences preferences;

    public CPOverviewF() {
        // Required empty public constructor
    }

    public static CPOverviewF newInstance(String param1, String param2) {
        CPOverviewF fragment = new CPOverviewF();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        justStarted=true;
        final int[] period={1,3,6,12};
        for(int c=0; c<period.length; c++)
            if(LoginActivity.contractorAccount.getReviewPeriod()==period[c])
                spinnerSelection=c;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.f_cpoverview, container, false);
        preferences = new Preferences(getContext());
        jsonParser=new JSONParser();
        chart=view.findViewById(R.id.chart);
        t_p_count=view.findViewById(R.id.p_count);
        t_a_count=view.findViewById(R.id.a_count);
        rating_bar=view.findViewById(R.id.rating_bar);
        t_rating=view.findViewById(R.id.rating);

        //initialize the reviews period
        spinner=view.findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter=ArrayAdapter.createFromResource(getContext(),R.array.review_period,android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                final int index=i;
                String message="";
                final int[] period={1,3,6,12};
                if(spinnerSelection==i)
                    return;
                if(i==0)
                    message="Pending reviews will be generated at the end of every month.\nMake sure you log in the app before the end of each month to generate the review requests.";
                else if(i==1)
                    message="Pending reviews will be generated at the end of MARCH, JUNE, SEPTEMBER and DECEMBER. \nMake sure you log in the app before the end of these months to generate the review requests.";
                else if(i==2)
                    message="Pending reviews will be generated at the end of  JUNE and DECEMBER. \nMake sure you log in the app before the end of these months to generate the review requests.";
                else if(i==3)
                    message="Pending reviews will be generated at the end of DECEMBER. \nMake sure you log in the app before the end of december to generate the review requests.";
                new AlertDialog.Builder(getContext())
                        .setMessage(message)
                        .setPositiveButton("Proceed", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //period changed , update it

                                new UpdatePeriod(Integer.toString(period[index]),index).execute((Void)null);
                            }
                        }).create().show();

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        ((LinearLayout)view.findViewById(R.id.pending)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               if(mListener!=null)
                   mListener.onLayoutClicked(1);
            }
        });
        ((LinearLayout)view.findViewById(R.id.all)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mListener!=null)
                    mListener.onLayoutClicked(2);
            }
        });
        if(!preferences.isDark_theme_enabled())
        {
            view.findViewById(R.id.chart_back).setBackgroundColor(getResources().getColor(R.color.main_background_light));
            view.findViewById(R.id.pending).setBackgroundColor(getResources().getColor(R.color.tertiary_background_light));
            view.findViewById(R.id.all).setBackgroundColor(getResources().getColor(R.color.tertiary_background_light));
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

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
    @Override
    public void onResume() {
        super.onResume();
        pending=0;
        all=0;
        rating=0;
        setReviewsCount();
        pie_chart.init(chart,getContext());
        setRatingPie(chart);
        spinner.setSelection(spinnerSelection);
    }
    
    public interface OnFragmentInteractionListener {
        void onLayoutClicked(int id);
    }

    private void setRatingPie(PieChart pieChart)
    {
        List<PieEntry> entries=new ArrayList<>();
        //all
        entries.add(new PieEntry(rating));
        entries.add(new PieEntry(5-rating));

        pie_chart.add_data(entries,"Average Rating",chart);
    }

    private void setReviewsCount()
    {
        int total_rating=0;
        int average_rating=0;
        Iterator<CReviews> iterator=LoginActivity.cReviewsList.iterator();
        while(iterator.hasNext()) {
            CReviews cReviews = iterator.next();
            int classes=cReviews.getClasses();
            if(classes==0)
                pending+=1;
            else
            {
                all+=1;
                total_rating+=cReviews.getRating();
            }
        }
        if(all!=0)
            average_rating=total_rating/all;
        else
            average_rating=0;
        rating=average_rating;
        t_rating.setText(String.format("%d/5",rating));
        rating_bar.setRating(rating);
        t_p_count.setText(String.format("%d",pending));
        t_a_count.setText(String.format("%d",all));
    }
    /**
     * Represents an asynchronous  task used to update the review period
     */
    public class UpdatePeriod extends AsyncTask<Void, Void, Boolean>
    {
        private final String mPeriod;
        private final int whichSpinnerItem;
        private int success;

        UpdatePeriod(String period, int spinnerItem) {
            mPeriod=period;
            whichSpinnerItem=spinnerItem;
        }
        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            Toast.makeText(getContext(),"Updating review period. Please wait...",Toast.LENGTH_SHORT).show();
        }
        @Override
        protected Boolean doInBackground(Void... params)
        {
            //building parameters
            List<NameValuePair> info=new ArrayList<NameValuePair>();
            info.add(new BasicNameValuePair("id",String.valueOf(LoginActivity.contractorAccount.getId())));
            info.add(new BasicNameValuePair("period",mPeriod));
            //getting the json object using post method
            JSONObject jsonObject=jsonParser.makeHttpRequest(url_update_period,"POST",info);
            Log.d("Create response",""+jsonObject.toString());
            try
            {
                success=jsonObject.getInt(TAG_SUCCESS);
                if(success==1)
                    return true;
                else
                {
                    String message=jsonObject.getString(TAG_MESSAGE);
                    Log.e(TAG_MESSAGE,""+message);
                    return false;
                }
            }
            catch (JSONException e)
            {
                Log.e("JSON",""+e.getMessage());
                return false;
            }
        }

        @Override
        protected void onPostExecute(final Boolean successful) {
            if (successful)
            {
                Toast.makeText(getContext(),"Review period successfully updated",Toast.LENGTH_SHORT).show();
                spinnerSelection=whichSpinnerItem;
                LoginActivity.contractorAccount.setReviewPeriod(Integer.parseInt(mPeriod));
            }
            else
            {
                Toast.makeText(getContext(),"Update failed, Please try again",Toast.LENGTH_SHORT).show();
                spinner.setSelection(spinnerSelection);
            }
        }
    }
}
