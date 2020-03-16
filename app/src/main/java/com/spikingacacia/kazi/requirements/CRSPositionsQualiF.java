package com.spikingacacia.kazi.requirements;

import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.spikingacacia.kazi.CommonHelper;
import com.spikingacacia.kazi.JSONParser;
import com.spikingacacia.kazi.LoginActivity;
import com.spikingacacia.kazi.Preferences;
import com.spikingacacia.kazi.R;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

public class CRSPositionsQualiF extends Fragment
{
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;
   // private OnFragmentInteractionListener mListener;
    private LinearLayout layout;
    private String url_update= LoginActivity.base_url+"update_qualifications_matrix.php";
    private JSONParser jsonParser;
    private String TAG_SUCCESS="success";
    private String TAG_MESSAGE="message";
    private int[][]values;
    private float textViewPadding=16;
    private Preferences preferences;

    public CRSPositionsQualiF()
    {
        // Required empty public constructor
    }
    public static CRSPositionsQualiF newInstance(String param1, String param2)
    {
        CRSPositionsQualiF fragment = new CRSPositionsQualiF();
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
        jsonParser=new JSONParser();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.f_crstrades_quali, container, false);
        preferences=new Preferences(getContext());
        ((Button)view.findViewById(R.id.update_button)).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                new UpdateTask().execute((Void)null);
            }
        });

        values=new int[LoginActivity.tradesList.size()][LoginActivity.personnelColumnsList.size()];
        for(int c=0; c<values.length; c++)
        {
            for(int d=0; d<values[c].length; d++)
                values[c][d]=LoginActivity.personnelMatrix[c][d];
        }
        //main layout
        layout=view.findViewById(R.id.base);
        textViewPadding =CommonHelper.convertDpToPixels(16,getContext());
        addLayouts();
        return view;
    }

   /* @Override
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
    }*/

    public interface OnFragmentInteractionListener
    {
        // TODO: Update argument type and name
        void onUpdate();
    }
    private void addLayouts()
    {
        Typeface font= ResourcesCompat.getFont(getContext(),R.font.arima_madurai);
        TypedArray array=getContext().getTheme().obtainStyledAttributes(new int[]{android.R.attr.listDivider});
        Drawable drawable=array.getDrawable(0);
        array.recycle();
        int pos=1;
        Iterator iterator= LoginActivity.tradesList.entrySet().iterator();
        while (iterator.hasNext())
        {
            final int index=pos;
            LinkedHashMap.Entry<String,Integer>set=(LinkedHashMap.Entry<String, Integer>) iterator.next();
            String name=set.getKey();
            name=name.replace("_"," ");
            final Integer count=set.getValue();

            //add the main quaifiations layout
            LinearLayout.LayoutParams layoutParams2=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams2.setMargins(50,0,50,0);
            final LinearLayout mainLayout = new LinearLayout(getContext());
            mainLayout.setOrientation(LinearLayout.VERTICAL);
            mainLayout.setLayoutParams(layoutParams2);

            //layout params;
            LinearLayout.LayoutParams layoutParams=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            //trade layout
            LinearLayout tradeLayout = new LinearLayout(getContext());
            tradeLayout.setOrientation(LinearLayout.HORIZONTAL);
            tradeLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            if(preferences.isDark_theme_enabled())
                tradeLayout.setBackgroundColor(ContextCompat.getColor(getContext(),R.color.secondary_background));
            else
                tradeLayout.setBackgroundColor(ContextCompat.getColor(getContext(),R.color.secondary_background_light));
           // tradeLayout.setPadding(10,10,10,10);
            tradeLayout.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    if(mainLayout.getVisibility()==View.GONE)
                        CommonHelper.expand(mainLayout);
                    else
                        CommonHelper.collapse(mainLayout);
                }
            });
            //item number textview
            TextView textCount=new TextView(getContext());
            textCount.setLayoutParams(layoutParams);
            textCount.setText(String.valueOf(pos));
            textCount.setPadding((int) textViewPadding,(int) textViewPadding,(int) textViewPadding,(int) textViewPadding);
            textCount.setTypeface(font);
            //content textview
            TextView textContent=new TextView(getContext());
            textContent.setLayoutParams(layoutParams);
            textContent.setPadding((int) textViewPadding,(int) textViewPadding,(int) textViewPadding,(int) textViewPadding);
            textContent.setText(name);
            textContent.setTypeface(font);
            //layout for count
            LinearLayout countLayout = new LinearLayout(getContext());
            countLayout.setOrientation(LinearLayout.HORIZONTAL);
            countLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT,1));
            countLayout.setGravity(Gravity.END);
            //quals count textview
            int countOn=0;
            final TextView textQualiCount=new TextView(getContext());
            LinearLayout.LayoutParams paramsTextQualiCount=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            paramsTextQualiCount.setMargins((int) textViewPadding,(int) textViewPadding,(int) textViewPadding,(int) textViewPadding);
            textQualiCount.setLayoutParams(paramsTextQualiCount);
            textQualiCount.setText(name);
            textQualiCount.setBackgroundResource(R.drawable.circle);
            textQualiCount.setText("0");
            textQualiCount.setGravity(Gravity.CENTER);
            textQualiCount.setTypeface(font);

            //add the qualifications layout
            //qualifications
            Iterator iterator_quals=LoginActivity.personnelColumnsList.entrySet().iterator();
            int pos2=1;
            while(iterator_quals.hasNext())
            {
                final int index2=pos2;
                LinkedHashMap.Entry<String,Character>set2=(LinkedHashMap.Entry<String, Character>) iterator_quals.next();
                String name2=set2.getKey();
                name2=name2.replace("_","_");
                Character which=set2.getValue();
                if(which=='t')
                    continue;
                LinearLayout.LayoutParams layoutParams3=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                layoutParams3.setMargins(0,1,0,1);
                LinearLayout qualiLayout = new LinearLayout(getContext());
                qualiLayout.setOrientation(LinearLayout.HORIZONTAL);
                qualiLayout.setLayoutParams(layoutParams3);
                if(preferences.isDark_theme_enabled())
                    qualiLayout.setBackgroundColor(which=='m'?ContextCompat.getColor(getContext(),R.color.secondary_background):ContextCompat.getColor(getContext(),R.color.tertiary_background));
                else
                    qualiLayout.setBackgroundColor(which=='m'?ContextCompat.getColor(getContext(),R.color.secondary_background_light):ContextCompat.getColor(getContext(),R.color.tertiary_background_light));
               // qualiLayout.setPadding(10,10,10,10);
                //item number textview
                TextView textCount2=new TextView(getContext());
                textCount2.setLayoutParams(layoutParams);
                textCount2.setText(String.valueOf(pos2));
                textCount2.setPadding((int) textViewPadding,(int) textViewPadding,(int) textViewPadding,(int) textViewPadding);
                textCount2.setTypeface(font);
                //content textview
                TextView textContent2=new TextView(getContext());
                textContent2.setLayoutParams(layoutParams);
                textContent2.setPadding((int) textViewPadding,(int) textViewPadding,(int) textViewPadding,(int) textViewPadding);
                textContent2.setText(name2);
                textContent2.setTypeface(font);
                //layout for switch
                LinearLayout switchLayout = new LinearLayout(getContext());
                switchLayout.setOrientation(LinearLayout.HORIZONTAL);
                switchLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT,1));
                switchLayout.setGravity(Gravity.END);
                //switch
                Switch oneSwitch = new Switch(new ContextThemeWrapper(getContext(), R.style.switchTheme));
                oneSwitch.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                oneSwitch.setPadding((int) textViewPadding,(int) textViewPadding,(int) textViewPadding,(int) textViewPadding);
                if(values[index-1][index2-1]==1)
                    countOn+=1;
                oneSwitch.setChecked(values[index-1][index2-1]==1);
                final int newCountCountOnAdd=countOn+=1;
                final int newCountCountOnSub=countOn-=1;
                oneSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
                {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b)
                    {
                        if(b)
                        {
                            values[index - 1][index2 - 1] = 1;
                            textQualiCount.setText(String.valueOf(newCountCountOnAdd));
                        }
                        else
                        {
                            values[index - 1][index2 - 1] = 0;
                            textQualiCount.setText(String.valueOf(newCountCountOnSub));
                        }
                    }
                });
                //add layouts
                switchLayout.addView(oneSwitch);
                qualiLayout.addView(textCount2);
                qualiLayout.addView(textContent2);
                qualiLayout.addView(switchLayout);
                mainLayout.addView(qualiLayout);
                mainLayout.setVisibility(View.GONE);
                pos2+=1;
            }
            textQualiCount.setText(String.valueOf(countOn));


            //add qualicount to its own layout
            countLayout.addView(textQualiCount);
            //add views to tradelayout
            tradeLayout.addView(textCount);
            tradeLayout.addView(textContent);
            tradeLayout.addView(countLayout);
            //add layout to main
            layout.addView(tradeLayout);
            layout.addView(mainLayout);

            pos+=1;
        }
    }

    public class UpdateTask extends AsyncTask<Void, Void, Boolean>
    {

        public UpdateTask(){
        }
        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            Toast.makeText(getContext(),"Updating started. Please wait...",Toast.LENGTH_SHORT).show();
        }
        @Override
        protected Boolean doInBackground(Void... params)
        {
            Boolean ok = true;
            //update the trades matrix one by one
            final int end=values[0].length-1;
            int pos=0;
            Iterator iterator= LoginActivity.tradesList.entrySet().iterator();
            while (iterator.hasNext())
            {
                String sqlcommand="UPDATE "+makeName(LoginActivity.contractorAccount.getId())+"_personnel_matrix"+" SET ";
                LinkedHashMap.Entry<String, Integer> set = (LinkedHashMap.Entry<String, Integer>) iterator.next();
                final String name = set.getKey();
                final Integer count = set.getValue();
                int pos2=0;
                Iterator iterator_quals=LoginActivity.personnelColumnsList.entrySet().iterator();
                while(iterator_quals.hasNext())
                {
                    LinkedHashMap.Entry<String, Character> set2 = (LinkedHashMap.Entry<String, Character>) iterator_quals.next();
                    String name2 = set2.getKey();
                    Character which = set2.getValue();
                    if (which == 't')
                        continue;
                    sqlcommand+=name2+"="+String.valueOf(values[pos][pos2]);
                    if(pos2!=end)
                        sqlcommand+=",";
                    pos2+=1;
                    Log.d("values",String.format("pos2==%d end==%d",pos2,end));
                }
                sqlcommand=sqlcommand.substring(0,sqlcommand.length()-1);
                sqlcommand+=" WHERE id="+String.valueOf(pos+1);
                List<NameValuePair> infoCommand=new ArrayList<NameValuePair>();
                infoCommand.add(new BasicNameValuePair("sqlcommand",sqlcommand));
                // making HTTP request
                JSONObject jsonObject= jsonParser.makeHttpRequest(url_update,"POST",infoCommand);
                Log.d("updating info",infoCommand.toString());
                Log.d("updating trades matrix",""+jsonObject.toString());
                try
                {
                    int success=jsonObject.getInt(TAG_SUCCESS);
                    if(success==1)
                    {
                        for(int c=0; c<values.length; c++)
                        {
                            for(int d=0; d<values[c].length; d++)
                                LoginActivity.personnelMatrix[c][d]=values[c][d];
                        }

                    }
                    else
                    {
                        String message=jsonObject.getString(TAG_MESSAGE);
                        Log.e(TAG_MESSAGE,""+message);
                        ok= false;
                    }
                }
                catch (JSONException e)
                {
                    Log.e("JSON",""+e.getMessage());
                    ok= false;
                }
                pos+=1;
            }

            return ok;
        }
        @Override
        protected void onPostExecute(final Boolean success) {

            if (success)
            {
                Toast.makeText(getContext(),"Succesfully updated",Toast.LENGTH_SHORT).show();
            }
            else
                Toast.makeText(getContext(),"There was an error updating, Please try again",Toast.LENGTH_SHORT).show();
           // setDialog(false);

        }
        private String makeName(int id)
        {
            String letters=String.valueOf(id);
            char[] array=letters.toCharArray();
            String name="";
            for(int count=0; count<array.length; count++)
            {
                switch (array[count])
                {
                    case '0':
                        name+="zero";
                        break;
                    case '1':
                        name+="one";
                        break;
                    case '2':
                        name+="two";
                        break;
                    case '3':
                        name+="three";
                        break;
                    case '4':
                        name+="four";
                        break;
                    case '5':
                        name+="five";
                        break;
                    case '6':
                        name+="six";
                        break;
                    case '7':
                        name+="seven";
                        break;
                    case '8':
                        name+="eight";
                        break;
                    case '9':
                        name+="nine";
                        break;
                    default :
                        name+="NON";
                }
            }
            return name;
        }


    }

}
