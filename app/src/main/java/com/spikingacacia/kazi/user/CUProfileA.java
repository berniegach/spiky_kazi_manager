package com.spikingacacia.kazi.user;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;
import com.spikingacacia.kazi.LoginActivity;
import com.spikingacacia.kazi.Preferences;
import com.spikingacacia.kazi.R;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import static com.spikingacacia.kazi.CommonHelper.collapse;
import static com.spikingacacia.kazi.CommonHelper.expand;

public class CUProfileA extends AppCompatActivity
{
    private String url_get_certs= LoginActivity.base_url+"src/contractors/";//+".jpg";
    private String[]userInfo;
    final private String[]userData=new String[9];
    final private List<String[]>userDataList=new ArrayList<>();
    private Preferences preferences;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_cuprofile);
        preferences= new Preferences(getBaseContext());

        Intent intent=getIntent();
        userInfo=intent.getStringArrayExtra("item");
        //set the toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        CollapsingToolbarLayout collapsingToolbarLayout=findViewById(R.id.collapsingToolbar);
        final Typeface tf= ResourcesCompat.getFont(this,R.font.amita);
        collapsingToolbarLayout.setCollapsedTitleTypeface(tf);
        collapsingToolbarLayout.setExpandedTitleTypeface(tf);
        setSupportActionBar(toolbar);
        setTitle("Profile");

        //layouts
        final LinearLayout l_general=findViewById(R.id.general);
        final LinearLayout l_skills=findViewById(R.id.skills);
        final LinearLayout l_equipments=findViewById(R.id.equipments);
        final LinearLayout l_tasks=findViewById(R.id.tasks);
        final LinearLayout l_feedback=findViewById(R.id.feedback);
        final LinearLayout l_general_base=findViewById(R.id.general_base);
        final LinearLayout l_skills_base=findViewById(R.id.skills_base);
        final LinearLayout l_equipments_base=findViewById(R.id.equipments_base);
        final LinearLayout l_tasks_base=findViewById(R.id.tasks_base);
        final LinearLayout l_feedback_base=findViewById(R.id.feedback_base);
        if(!preferences.isDark_theme_enabled())
        {
            setTheme(R.style.AppThemeLight_NoActionBarLight);
            toolbar.setTitleTextColor(getResources().getColor(R.color.text_light));
            toolbar.setPopupTheme(R.style.AppThemeLight_PopupOverlayLight);
            AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.appbar_layout);
            appBarLayout.getContext().setTheme(R.style.AppThemeLight_AppBarOverlayLight);
            appBarLayout.setBackgroundColor(getResources().getColor(R.color.main_background_light));
            findViewById(R.id.main).setBackgroundColor(getResources().getColor(R.color.main_background_light));
            findViewById(R.id.sec_main).setBackgroundColor(getResources().getColor(R.color.secondary_background_light));
            ((TextView)findViewById(R.id.who)).setTextColor(getResources().getColor(R.color.text_light));
            //layouts
            l_general.setBackgroundColor(getResources().getColor(R.color.tertiary_background_light));
            l_skills.setBackgroundColor(getResources().getColor(R.color.tertiary_background_light));
            l_equipments.setBackgroundColor(getResources().getColor(R.color.tertiary_background_light));
            l_tasks.setBackgroundColor(getResources().getColor(R.color.tertiary_background_light));
            l_feedback.setBackgroundColor(getResources().getColor(R.color.tertiary_background_light));
        }
        l_general.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(l_general_base.getVisibility()==View.VISIBLE)
                {
                    collapse(l_general_base);
                    return;
                }
                expand(l_general_base);
                collapse(l_skills_base);
                collapse(l_equipments_base);
                collapse(l_tasks_base);
                collapse(l_feedback_base);
            }
        });
        l_skills.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(l_skills_base.getVisibility()==View.VISIBLE)
                {
                    collapse(l_skills_base);
                    return;
                }
                collapse(l_general_base);
                expand(l_skills_base);
                collapse(l_equipments_base);
                collapse(l_tasks_base);
                collapse(l_feedback_base);
            }
        });
        l_equipments.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(l_equipments_base.getVisibility()==View.VISIBLE)
                {
                    collapse(l_equipments_base);
                    return;
                }
                collapse(l_general_base);
                collapse(l_skills_base);
                expand(l_equipments_base);
                collapse(l_tasks_base);
                collapse(l_feedback_base);
            }
        });
        l_tasks.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(l_tasks_base.getVisibility()==View.VISIBLE)
                {
                    collapse(l_tasks_base);
                    return;
                }
                collapse(l_general_base);
                collapse(l_skills_base);
                collapse(l_equipments_base);
                expand(l_tasks_base);
                collapse(l_feedback_base);
            }
        });
        l_feedback.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(l_feedback_base.getVisibility()==View.VISIBLE)
                {
                    collapse(l_feedback_base);
                    return;
                }
                collapse(l_general_base);
                collapse(l_skills_base);
                collapse(l_equipments_base);
                collapse(l_tasks_base);
                expand(l_feedback_base);
            }
        });
        getProfilePic();
        getInfo();
        getInfoEquips();
        fillGeneralInfo();
        addSkillsLayouts();
        addEquipsLayouts();
    }
    private void getInfo()
    {
        boolean broken=false;
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
            final String email=token[4];
            final String position=token[5];
            final String quals=token[6];
            final String certs=token[7];
            Character complaince=set.getValue();
            if(userid.contentEquals(userInfo[4]))
            {
                userData[0]=id;
                userData[1]=userid;
                userData[2]=trade;
                userData[3]=names;
                userData[4]=email;
                userData[5]=position;
                userData[6]=quals;
                userData[7]=certs;
                userData[8]=String.valueOf(complaince);
                broken=true;
                break;
            }

        }
        if(broken)
            return;
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
            final String email=token[4];
            final String position=token[5];
            final String quals=token[6];
            final String certs=token[7];
            Character complaince=set.getValue();
            if(userid.contentEquals(userInfo[4]))
            {
                userData[0]=id;
                userData[1]=userid;
                userData[2]=trade;
                userData[3]=names;
                userData[4]=email;
                userData[5]=position;
                userData[6]=quals;
                userData[7]=certs;
                userData[8]=String.valueOf(complaince);
                break;
            }
        }
    }
    private void getInfoEquips()
    {
        Iterator iterator= LoginActivity.cGlobalInfoEquips.getComplaintStaff().entrySet().iterator();
        while (iterator.hasNext())
        {
            String[]userDataEquips=new String[9];
            LinkedHashMap.Entry<String,Character>set=(LinkedHashMap.Entry<String, Character>) iterator.next();
            String name=set.getKey();
            String[] token=name.split(":");
            final String id=token[0];
            final String userid=token[1];
            final String trade=token[2];
            final String names=token[3];
            final String email=token[4];
            final String position=token[5];
            final String quals=token[6];
            final String certs=token[7];
            Character complaince=set.getValue();
            if(userid.contentEquals(userInfo[4]))
            {
                userDataEquips[0]=id;
                userDataEquips[1]=userid;
                userDataEquips[2]=trade;
                userDataEquips[3]=names;
                userDataEquips[4]=email;
                userDataEquips[5]=position;
                userDataEquips[6]=quals;
                userDataEquips[7]=certs;
                userDataEquips[8]=String.valueOf(complaince);
                userDataList.add(userDataEquips);
            }

        }
        Iterator iterator2= LoginActivity.cGlobalInfoEquips.getNonComplaintStaff().entrySet().iterator();
        while (iterator2.hasNext())
        {
            String[]userDataEquips=new String[9];
            LinkedHashMap.Entry<String,Character>set=(LinkedHashMap.Entry<String, Character>) iterator2.next();
            String name=set.getKey();
            String[] token=name.split(":");
            final String id=token[0];
            final String userid=token[1];
            final String trade=token[2];
            final String names=token[3];
            final String email=token[4];
            final String position=token[5];
            final String quals=token[6];
            final String certs=token[7];
            Character complaince=set.getValue();
            if(userid.contentEquals(userInfo[4]))
            {
                userDataEquips[0]=id;
                userDataEquips[1]=userid;
                userDataEquips[2]=trade;
                userDataEquips[3]=names;
                userDataEquips[4]=email;
                userDataEquips[5]=position;
                userDataEquips[6]=quals;
                userDataEquips[7]=certs;
                userDataEquips[8]=String.valueOf(complaince);
                userDataList.add(userDataEquips);
            }
        }
    }
    private void fillGeneralInfo()
    {
        if(userData!=null)
        {
            //names
            ((TextView)(findViewById(R.id.who))).setText(userData[3]);
            ((TextView)findViewById(R.id.names)).setText(userData[3]);
            ((TextView)findViewById(R.id.email)).setText(userData[4]);
            ((TextView)findViewById(R.id.position)).setText(userData[5]);
        }
    }
    private void getProfilePic()
    {
        //get the profile pic
        String url= LoginActivity.base_url+"src/users/"+String.format("%s/profilepics/prof_pic",makeName(Integer.parseInt(userInfo[4])))+".jpg";
        ImageRequest request=new ImageRequest(
                url,
                new Response.Listener<Bitmap>()
                {
                    @Override
                    public void onResponse(Bitmap response)
                    {
                        ((ImageView)findViewById(R.id.image)).setImageBitmap(response);
                        //imageView.setImageBitmap(response);
                        Log.d("volley","succesful");
                    }
                }, 0, 0, null,
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError e)
                    {
                        Log.e("voley",""+e.getMessage()+e.toString());
                    }
                });
        RequestQueue request2 = Volley.newRequestQueue(getBaseContext());
        request2.add(request);
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
    private void addSkillsLayouts()
    {
        Typeface font= ResourcesCompat.getFont(getBaseContext(),R.font.arima_madurai);
        TypedArray array=getBaseContext().getTheme().obtainStyledAttributes(new int[]{android.R.attr.listDivider});
        Drawable drawable=array.getDrawable(0);
        array.recycle();
        LinearLayout layout=findViewById(R.id.skills_base);
        final String[]tokens=(userData[6].substring(0,userData[6].length()-1)).split(",");
        LinearLayout.LayoutParams layoutParams=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0,2,0,1);
        for(int count=0; count<tokens.length; count+=1)
        {
            final String[]tokens2=tokens[count].split("\\.");
            //horizonatl layout
            LinearLayout layout1 = new LinearLayout(getBaseContext());
            layout1.setOrientation(LinearLayout.HORIZONTAL);
            layout1.setWeightSum(10);
            layout1.setLayoutParams(layoutParams);
            layout1.setBackgroundColor(tokens2[0].contentEquals("h")?ContextCompat.getColor(getBaseContext(),R.color.secondary_background):ContextCompat.getColor(getBaseContext(),R.color.tertiary_background));
            layout1.setPadding(10,15,10,15);
            layout1.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    if(userData[7].contains("h."+tokens2[1]) || userData[7].contains("e."+tokens2[1]))
                    {

                        showCertificate(tokens2[1],1);
                    }
                    else
                        Toast.makeText(getBaseContext(),"No Certificate",Toast.LENGTH_SHORT).show();
                }
            });
            //textview to show if the user has the requirement
            TextView t_has_qual=new TextView(getBaseContext());
            LinearLayout.LayoutParams params_t_has_qual=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params_t_has_qual.setMargins(2,0,10,0);
            t_has_qual.setLayoutParams(params_t_has_qual);
            t_has_qual.setBackgroundResource(tokens2[0].contentEquals("h")?R.drawable.circle_compliant:R.drawable.circle_non_compliant);
            //textview for the qualifications
            TextView t_qual=new TextView(getBaseContext());
            t_qual.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT,8));
            t_qual.setTypeface(font);
            String cert_name=tokens2[1];
            cert_name=cert_name.replace("_"," ");
            t_qual.setText(cert_name);
            t_qual.setTextColor(ContextCompat.getColor(getBaseContext(),R.color.text));
            //certificate indicator
            LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(5,10,1);
            TextView t_cert=new TextView(getBaseContext());
            t_cert.setLayoutParams(params);
            t_cert.setTypeface(font);
            t_cert.setBackgroundColor(userData[7].contains("h."+tokens2[1])?ContextCompat.getColor(getBaseContext(),R.color.graph_14):ContextCompat.getColor(getBaseContext(),R.color.graph_13));
            layout1.addView(t_has_qual);
            layout1.addView(t_qual);
            layout1.addView(t_cert);
            layout.addView(layout1);

        }
    }
    private void addEquipsLayouts()
    {
        Typeface font= ResourcesCompat.getFont(getBaseContext(),R.font.arima_madurai);
        TypedArray array=getBaseContext().getTheme().obtainStyledAttributes(new int[]{android.R.attr.listDivider});
        Drawable drawable=array.getDrawable(0);
        array.recycle();
        LinearLayout layout=findViewById(R.id.equipments_base);
        LinearLayout.LayoutParams layoutParams=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0,4,0,0);
        Iterator iterator=userDataList.iterator();
        while(iterator.hasNext())
        {
            final String[]data=(String[]) iterator.next();
            //horizonatl layout
            LinearLayout layout1 = new LinearLayout(getBaseContext());
            layout1.setOrientation(LinearLayout.HORIZONTAL);
            layout1.setWeightSum(10);
            layout1.setLayoutParams(layoutParams);
            layout1.setBackgroundColor(ContextCompat.getColor(getBaseContext(),R.color.secondary_background));
            layout1.setPadding(10,15,10,15);
            layout1.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    if(data[7].contains("h."+data[2]) ||data[7].contains("e."+data[2]) )
                    {
                        showCertificate(data[2],2);
                    }
                    else
                        Toast.makeText(getBaseContext(),"No Certificate",Toast.LENGTH_SHORT).show();
                }
            });
            TextView t_equip=new TextView(getBaseContext());
            t_equip.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT,8));
            t_equip.setTypeface(font);
            t_equip.setText(data[2]);
            t_equip.setTextColor(ContextCompat.getColor(getBaseContext(),R.color.text));
            //certificate indicator
            LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(5,10,1);
            TextView t_cert=new TextView(getBaseContext());
            t_cert.setLayoutParams(params);
            t_cert.setTypeface(font);
            if(data[7].contains("h."+data[2]))
                t_cert.setBackgroundColor(ContextCompat.getColor(getBaseContext(),R.color.graph_14));
            else if(data[7].contains("m."+data[2]))
                t_cert.setBackgroundColor(ContextCompat.getColor(getBaseContext(),R.color.graph_13));
            else
                t_cert.setBackgroundColor(ContextCompat.getColor(getBaseContext(),R.color.graph_12));
            layout1.addView(t_equip);
            layout1.addView(t_cert);
            layout.addView(layout1);

        }
    }
    private void showCertificate(final String name,final int who)
    {
        String url="";
        if(who==1)
        {
            url = url_get_certs + makeName(LoginActivity.contractorAccount.getId()) + "/certificates/" + makeName(Integer.parseInt(userInfo[4])) + '/';
            url += name + ".jpg";
        }
        else
        {
            url = url_get_certs + makeName(LoginActivity.contractorAccount.getId()) + "/equipmentscertificates/" + makeName(Integer.parseInt(userInfo[4])) + '/';
            url += name + ".jpg";
        }

        Log.d("Certificate name","  "+url);
        ImageRequest request=new ImageRequest(
                url,
                new Response.Listener<Bitmap>()
                {
                    @Override
                    public void onResponse(Bitmap response)
                    {
                        ImageView imageView=new ImageView(CUProfileA.this);
                        imageView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                        imageView.setImageBitmap(response);

                        Log.d("volley","succesful getting certificate:  "+name);
                        AlertDialog.Builder builder=new AlertDialog.Builder(CUProfileA.this);
                        builder.setTitle(name)
                                .setNegativeButton("Cancel", new DialogInterface.OnClickListener()
                                {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which)
                                    {
                                        dialog.dismiss();
                                    }
                                });
                        AlertDialog dialog=builder.create();
                        dialog.setView(imageView);
                        dialog.show();
                    }
                }, 0, 0, null,
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError e)
                    {
                        Log.e("voley",""+e.getMessage()+e.toString());
                        Toast.makeText(getBaseContext(),"No Certificate",Toast.LENGTH_SHORT).show();
                    }
                });
        RequestQueue request2 = Volley.newRequestQueue(getBaseContext());
        request2.add(request);
    }

}
