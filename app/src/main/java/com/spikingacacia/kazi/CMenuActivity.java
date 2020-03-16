package com.spikingacacia.kazi;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.core.content.res.ResourcesCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.spikingacacia.kazi.compliance.CCEquipmentsA;
import com.spikingacacia.kazi.compliance.CCPersonnelA;
import com.spikingacacia.kazi.equipments.CEPropertiesA;
import com.spikingacacia.kazi.notifications.CNMessageListActivity;
import com.spikingacacia.kazi.performance.CPerformanceA;
import com.spikingacacia.kazi.reports.CREReportsA;
import com.spikingacacia.kazi.reports.CRPReportsA;
import com.spikingacacia.kazi.requirements.CRSkillsActivity;
import com.spikingacacia.kazi.tasks.CTTasksA;

import static com.spikingacacia.kazi.LoginActivity.cFinalProgress;
import static com.spikingacacia.kazi.LoginActivity.loginProgress;

public class CMenuActivity extends AppCompatActivity
    implements CMenuFragment.OnFragmentInteractionListener
{
    private  Preferences preferences;
    private boolean showEquipments=true;
    private boolean runRate=true;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_cmenu);
        //set the toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        CollapsingToolbarLayout collapsingToolbarLayout=findViewById(R.id.collapsingToolbar);
        final Typeface tf= ResourcesCompat.getFont(this,R.font.amita);
        collapsingToolbarLayout.setCollapsedTitleTypeface(tf);
        collapsingToolbarLayout.setExpandedTitleTypeface(tf);
        setSupportActionBar(toolbar);
        setTitle("Menu");

        Fragment fragment=CMenuFragment.newInstance("","");
        FragmentTransaction transaction=getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.base,fragment,"menu");
        transaction.commit();
        preferences=new Preferences(getBaseContext());
        showEquipments=preferences.isShow_equipments();
        if(!preferences.isDark_theme_enabled())
        {
            setTheme(R.style.AppThemeLight);
            findViewById(R.id.main).setBackgroundColor(getResources().getColor(R.color.main_background_light));
            findViewById(R.id.sec_main).setBackgroundColor(getResources().getColor(R.color.secondary_background_light));
            collapsingToolbarLayout.setExpandedTitleColor(getResources().getColor(R.color.text_light));
            collapsingToolbarLayout.setCollapsedTitleTextColor(getResources().getColor(R.color.text_light));
            collapsingToolbarLayout.setBackgroundColor(getResources().getColor(R.color.main_background_light));
            ((TextView)findViewById(R.id.who)).setTextColor(getResources().getColor(R.color.text_light));
            ((TextView)findViewById(R.id.trial)).setTextColor(getResources().getColor(R.color.text_light));
            ((TextView)findViewById(R.id.welcome)).setTextColor(getResources().getColor(R.color.text_light));
            toolbar.setTitleTextColor(getResources().getColor(R.color.text_light));
            toolbar.setPopupTheme(R.style.AppThemeLight_PopupOverlayLight);
            AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.appbar_layout);
            appBarLayout.getContext().setTheme(R.style.AppThemeLight_AppBarOverlayLight);
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState)
    {
        super.onPostCreate(savedInstanceState);
    }
    @Override
    protected void onResume()
    {
        super.onResume();
        showEquipments=preferences.isShow_equipments();
        //set the welcome text
        //we set it in onResume to factor in the possibility of the username changing in the settings
        if(LoginActivity.contractorAccount.getUsername().length()<2 || LoginActivity.contractorAccount.getUsername().contentEquals("null"))
        {
            ((TextView)findViewById(R.id.who)).setText("Please go to settings and set the company name...");
        }
        else
            ((TextView)findViewById(R.id.who)).setText(LoginActivity.contractorAccount.getUsername());
        if(LoginActivity.currentSubscription.length()>10)
            ((TextView)findViewById(R.id.trial)).setText(LoginActivity.currentSubscription);
        else
            ((TextView)findViewById(R.id.trial)).setVisibility(View.GONE);
        checkFields();
        if(runRate)
        {
            AppRater.app_launched(this);
            runRate=false;
        }

    }
    @Override
    public void onBackPressed()
    {
        //super.onBackPressed();
        new AlertDialog.Builder(CMenuActivity.this)
                .setTitle("Quit")
                .setMessage("Are you sure you want to exit?")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton("Ok", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        finishAffinity();
                        //Intent intent=new Intent(Intent.ACTION_MAIN);
                       // intent.addCategory(Intent.CATEGORY_HOME);
                       // startActivity(intent);
                    }
                }).create().show();
    }
    /**implementation of CMenuFragment.java**/
    @Override
    public void onMenuClicked(int id)
    {
        if(id==1)
        {
            //requiremens
            if(showEquipments)
            {
                new AlertDialog.Builder(CMenuActivity.this)
                        .setItems(new String[]{"Skills", "Equipments"}, new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i)
                            {
                                if(i==0)
                                {
                                    //skills
                                    Intent intent=new Intent(CMenuActivity.this, CRSkillsActivity.class);
                                    startActivity(intent);
                                }
                                else if(i==1)
                                {
                                    //equipments
                                    Intent intent=new Intent(CMenuActivity.this,CEPropertiesA.class);
                                    startActivity(intent);
                                }
                            }
                        }).create().show();
            }
            else
            {
                //skills
                Intent intent=new Intent(CMenuActivity.this, CRSkillsActivity.class);
                startActivity(intent);
            }
        }
        else if(id==2)
        {
            //compliance
            if(showEquipments)
            {
                new AlertDialog.Builder(CMenuActivity.this)
                        .setItems(new String[]{"Skills", "Equipments"}, new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i)
                            {
                                if(i==0)
                                {
                                    //skills
                                    if( LoginActivity.cGlobalInfo.getComplaintStaff()==null)
                                    {
                                        Toast.makeText(getBaseContext(),"No users found",Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                    Intent intent=new Intent(CMenuActivity.this, CCPersonnelA.class);
                                    intent.putExtra("position","All");
                                    intent.putExtra("position_id","");
                                    startActivity(intent);
                                }
                                else if(i==1)
                                {
                                    if(LoginActivity.cGlobalInfoEquips.getComplaintStaff()==null)
                                    {
                                        Toast.makeText(getBaseContext(),"No users with equipments found",Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                    //equipments
                                    Intent intent=new Intent(CMenuActivity.this,CCEquipmentsA.class);
                                    startActivity(intent);
                                }
                            }
                        }).create().show();
            }
            else
            {
                //skills
                if( LoginActivity.cGlobalInfo.getComplaintStaff()==null)
                {
                    Toast.makeText(getBaseContext(),"No users found",Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent=new Intent(CMenuActivity.this, CCPersonnelA.class);
                intent.putExtra("position","All");
                intent.putExtra("position_id","");
                startActivity(intent);
            }

        }
        else if(id==3)
        {
            //users
        }
        else if(id==4)
        {
            //reports
            if(showEquipments)
            {
                new AlertDialog.Builder(CMenuActivity.this)
                        .setItems(new String[]{"Skills", "Equipments"}, new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i)
                            {
                                if(i==0)
                                {
                                    //skills
                                    Intent intent=new Intent(CMenuActivity.this, CRPReportsA.class);
                                    startActivity(intent);
                                }
                                else if(i==1)
                                {
                                    //equipments
                                    Intent intent=new Intent(CMenuActivity.this,CREReportsA.class);
                                    startActivity(intent);
                                }
                            }
                        }).create().show();
            }
            else
            {
                //skills
                Intent intent=new Intent(CMenuActivity.this, CRPReportsA.class);
                startActivity(intent);
            }
        }
        else if(id==5)
        {
            //tasks
            Intent intent=new Intent(CMenuActivity.this,CTTasksA.class);
            startActivity(intent);
        }
        else if(id==6)
        {
            //notifications
            Intent intent=new Intent(CMenuActivity.this,CNMessageListActivity.class);
            startActivity(intent);
        }
        else if(id==7)
        {
            //performance review
            Intent intent=new Intent(CMenuActivity.this, CPerformanceA.class);
            startActivity(intent);
        }
        else if(id==9)
        {
            Intent intent=new Intent(this, CSettingsActivity.class);
            // intent.putExtra("NOTHING","nothing");
            startActivity(intent);
        }
    }
    @Override
    public void onLogOut()
    {
        new AlertDialog.Builder(CMenuActivity.this)
                .setTitle("Logout")
                .setMessage("Are you sure you want to log out?")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton("Ok", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        SharedPreferences  loginPreferences=getBaseContext().getSharedPreferences("loginPrefs",MODE_PRIVATE);
                        SharedPreferences.Editor loginPreferencesEditor =loginPreferences.edit();
                        loginPreferencesEditor.putBoolean("rememberme",false);
                        loginPreferencesEditor.commit();
                        finishAffinity();
                        //Intent intent=new Intent(Intent.ACTION_MAIN);
                        //intent.addCategory(Intent.CATEGORY_HOME);
                       // startActivity(intent);
                    }
                }).create().show();


    }
    @Override
    public void play_notification()
    {
        Uri alarmSound =
                RingtoneManager. getDefaultUri (RingtoneManager. TYPE_NOTIFICATION );
        MediaPlayer mp = MediaPlayer. create (getBaseContext(), alarmSound);
        mp.start();
       /* NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(SMenuA.this, default_notification_channel_id )
                        .setSmallIcon(R.mipmap.ic_launcher )
                        .setContentTitle( "New Order" )
                        .setContentText( "a new order has arrived" ) ;
        NotificationManager mNotificationManager = (NotificationManager)
                getSystemService(Context. NOTIFICATION_SERVICE );
        mNotificationManager.notify(( int ) System. currentTimeMillis () ,
                mBuilder.build());*/
    }
    private void checkFields()
    {
        if (loginProgress >= cFinalProgress)
        {
            String message="";
            if(LoginActivity.tradesList.size()==0)
                message="Please define the positions eg Welder, Driver, Doctor etc.\nGo to "+
                        "\u279e Requirements \u279e Skills \u279e Positions \u279e Options(upper right corner) \u279e add";
            else if(LoginActivity.personnelColumnsList.size()==0)
                message="Please define the qualifications required for the positions eg First aid training, computer skills etc.\nGo to "+
                        "\u279e Requirements \u279e Skills \u279e Qualifications \u279e Options(upper right corner) \u279e add";
            else if((LoginActivity.cGlobalInfo.getComplaintStaff().size()==0) && (LoginActivity.cGlobalInfo.getNonComplaintStaff().size()==0))
                message="Personnel information is empty.\nPlease tell your employees to fill in the required information using 'Spiky Kazi Employee app'";
            if(!message.contentEquals(""))
            {
                new AlertDialog.Builder(this)
                        .setTitle("Missing profile information")
                        .setMessage(message)
                        .setPositiveButton("Ok",null)
                        .create().show();
            }
        }
    }

}
