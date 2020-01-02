package com.spikingacacia.kazi;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import android.preference.PreferenceActivity;
import androidx.preference.SwitchPreference;

import com.google.android.material.textfield.TextInputLayout;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.preference.EditTextPreference;
import androidx.preference.PreferenceFragmentCompat;

import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;
import com.spikingacacia.kazi.database.ContractorAccount;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CSettingsActivity extends AppCompatActivity
        implements    PreferenceFragmentCompat.OnPreferenceStartFragmentCallback
{
    private static final String TITLE_TAG = "Settings";
    private static String url_update_account= LoginActivity.base_url+"update_contractor_account.php";
    private static String url_update_permissions= LoginActivity.base_url+"update_permissions.php";
    private static String url_delete_account= LoginActivity.base_url+"delete_boss.php";
    private static JSONParser jsonParser;
    private static String TAG_SUCCESS="success";
    private static String TAG_MESSAGE="message";
    private UpdateAccount updateTask;
    private UpdatePermissions updatePermissions;
    public static boolean settingsChanged;
    public static boolean permissionsChanged;
    static private Context context;
    public static ContractorAccount tempContractorAccount;
    public static String permissions;
    public static Bitmap profilePic;
    private static  SharedPreferences loginPreferences;
    private static SharedPreferences.Editor loginPreferencesEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        if (savedInstanceState == null)
        {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings, new HeaderFragment())
                    .commit();
        } else
        {
            setTitle(savedInstanceState.getCharSequence(TITLE_TAG));
        }
        getSupportFragmentManager().addOnBackStackChangedListener(
                new FragmentManager.OnBackStackChangedListener()
                {
                    @Override
                    public void onBackStackChanged()
                    {
                        if (getSupportFragmentManager().getBackStackEntryCount() == 0)
                        {
                            setTitle(R.string.title_activity_settings);
                        }
                    }
                });
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
        {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        ///
        tempContractorAccount=new ContractorAccount();
        tempContractorAccount=LoginActivity.contractorAccount;
        permissionsChanged=false;
        permissions="";
        jsonParser=new JSONParser();
        updateTask=new UpdateAccount();
        updatePermissions=new UpdatePermissions();
        settingsChanged=false;
        context=this;
        //get the profile pic
        String url= LoginActivity.base_url+"src/contractors/"+String.format("%s/profilepics/prof_pic",makeName(LoginActivity.contractorAccount.getId()))+".jpg";
        ImageRequest request=new ImageRequest(
                url,
                new Response.Listener<Bitmap>()
                {
                    @Override
                    public void onResponse(Bitmap response)
                    {
                        profilePic=response;
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
        loginPreferences=context.getSharedPreferences("loginPrefs",MODE_PRIVATE);
        loginPreferencesEditor=loginPreferences.edit();

    }
    @Override
    protected void onDestroy()
    {
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                if (settingsChanged)
                {
                    updateTask.execute((Void)null);
                    if(permissionsChanged)
                        updatePermissions.execute((Void)null);
                }
            }
        }).start();
        super.onDestroy();
    }
    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        // Save current activity title so we can set it again after a configuration change
        outState.putCharSequence(TITLE_TAG, getTitle());
    }
    @Override
    public boolean onSupportNavigateUp()
    {
        if (getSupportFragmentManager().popBackStackImmediate())
        {
            return true;
        }
        return super.onSupportNavigateUp();
    }

    @Override
    public boolean onPreferenceStartFragment(PreferenceFragmentCompat caller, androidx.preference.Preference pref)
    {
        // Instantiate the new Fragment
        final Bundle args = pref.getExtras();
        final Fragment fragment = getSupportFragmentManager().getFragmentFactory().instantiate(
                getClassLoader(),
                pref.getFragment());
        fragment.setArguments(args);
        fragment.setTargetFragment(caller, 0);
        // Replace the existing Fragment with the new Fragment
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.settings, fragment)
                .addToBackStack(null)
                .commit();
        setTitle(pref.getTitle());
        return true;
    }
    public static class HeaderFragment extends PreferenceFragmentCompat
    {

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey)
        {
            setPreferencesFromResource(R.xml.pref_cheaders, rootKey);
        }
    }

    /**
     * This fragment shows general preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    public static class GeneralPreferenceFragment extends PreferenceFragmentCompat
    {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey)
        {
            setPreferencesFromResource(R.xml.pref_cgeneral, rootKey);

            //feedback preference click listener
            EditTextPreference preference_est=findPreference("username");
            preference_est.setSummary(LoginActivity.contractorAccount.getUsername());
            preference_est.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener()
            {
                @Override
                public boolean onPreferenceChange(Preference preference, Object o)
                {
                    String name = o.toString();
                    tempContractorAccount.setUsername(name);
                    settingsChanged=true;
                    preference.setSummary(name);
                    return false;
                }
            });
            //you cannot change the email
            Preference preference_est_type=findPreference("email");
            preference_est_type.setSummary(LoginActivity.contractorAccount.getEmail());

            //password change
            final Preference preference_password=findPreference("password");
            preference_password.setSummary(passwordStars(LoginActivity.contractorAccount.getPassword()));
            preference_password.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener()
            {
                @Override
                public boolean onPreferenceClick(Preference preference)
                {
                    final AlertDialog dialog;
                    AlertDialog.Builder builderPass=new AlertDialog.Builder(context);
                    builderPass.setTitle("Change Password");
                    TextInputLayout lOld=new TextInputLayout(context);
                    lOld.setGravity(Gravity.CENTER);
                    final EditText oldPassword=new EditText(context);
                    oldPassword.setPadding(20,10,20,10);
                    oldPassword.setTextSize(14);
                    oldPassword.setPadding(5,0,5,0);
                    lOld.addView(oldPassword,0,new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
                    TextInputLayout lNew=new TextInputLayout(context);
                    lNew.setGravity(Gravity.CENTER);
                    final EditText newPassword=new EditText(context);
                    newPassword.setPadding(20,10,20,10);
                    newPassword.setTextSize(14);
                    newPassword.setPadding(5,0,5,0);
                    lNew.addView(newPassword,0,new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
                    TextInputLayout lConfirm=new TextInputLayout(context);
                    lConfirm.setGravity(Gravity.CENTER);
                    final EditText confirmPassword=new EditText(context);
                    confirmPassword.setPadding(20,10,20,10);
                    confirmPassword.setTextSize(14);
                    confirmPassword.setPadding(5,0,5,0);
                    lConfirm.addView(confirmPassword,0,new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
                    oldPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    newPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    confirmPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    oldPassword.setHint("Old Password");
                    newPassword.setHint("New Password");
                    confirmPassword.setHint("Confirm Password");
                    oldPassword.setError(null);
                    newPassword.setError(null);
                    confirmPassword.setError(null);
                    LinearLayout layoutPassword=new LinearLayout(context);
                    layoutPassword.setOrientation(LinearLayout.VERTICAL);
                    layoutPassword.addView(lOld);
                    layoutPassword.addView(lNew);
                    layoutPassword.addView(lConfirm);
                    builderPass.setView(layoutPassword);
                    builderPass.setPositiveButton("Change", null);
                    builderPass.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i)
                        {
                            dialogInterface.dismiss();
                        }
                    });
                    dialog=builderPass.create();
                    dialog.setOnShowListener(new DialogInterface.OnShowListener()
                    {
                        @Override
                        public void onShow(DialogInterface dialogInterface)
                        {
                            Button button=((AlertDialog)dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                            button.setOnClickListener(new View.OnClickListener()
                            {
                                @Override
                                public void onClick(View view)
                                {
                                    String old=oldPassword.getText().toString();
                                    String newPass=newPassword.getText().toString();
                                    String confirm=confirmPassword.getText().toString();
                                    if(!old.contentEquals(LoginActivity.contractorAccount.getPassword()))
                                    {
                                        oldPassword.setError("Incorrect old password");
                                    }
                                    else if(newPass.length()<4)
                                    {
                                        newPassword.setError("New password too short");
                                    }
                                    else if(!newPass.contentEquals(confirm))
                                    {
                                        confirmPassword.setError("Password not the same");
                                    }
                                    else
                                    {
                                        tempContractorAccount.setPassword(newPass);
                                        settingsChanged=true;
                                        preference_password.setSummary(passwordStars(newPass));
                                        dialog.dismiss();
                                    }
                                }
                            });
                        }
                    });
                    dialog.show();
                    return false;
                }
            });
            //subscription change
            final Preference preference_subscription=findPreference("subscription");
            preference_subscription.setSummary(LoginActivity.currentSubscription);


        }

        private String passwordStars(String pass)
        {
            String name="";
            for(int count=0; count<pass.length(); count+=1)
                name+="*";
            return name;
        }
    }
    /**
     * This fragment shows location preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class LocationPreferenceFragment extends PreferenceFragmentCompat
    {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey)
        {
            setPreferencesFromResource(R.xml.pref_clocation, rootKey);
            //countries
            String countryCode= LoginActivity.contractorAccount.getCountry();
            ListPreference pref_countries=(ListPreference) findPreference("countries");
            pref_countries.setEntries(getCountriesList());
            pref_countries.setEntryValues(getCountriesListValues());
            pref_countries.setSummary(countryCode.contentEquals("null")?"Please set the country":getCountryFromCode(countryCode));
            pref_countries.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener()
            {
                @Override
                public boolean onPreferenceChange(Preference preference, Object o)
                {
                    String s=o.toString();
                    tempContractorAccount.setCountry(s);
                    settingsChanged=true;
                    preference.setSummary(getCountryFromCode(s));
                    return false;
                }
            });
            //location
            String[] pos=LoginActivity.contractorAccount.getLocation().split(",");
            final Preference pref_location=findPreference("location");
            pref_location.setSummary(pos.length==3?pos[2]:"Please set your location");
            /*
            //visible online
            int online=Integer.parseInt(LoginActivity.accountSeller.getOnline());
            final SwitchPreference pref_visible_online= (SwitchPreference) findPreference("online_visibility");
            pref_visible_online.setChecked(online==1);
            pref_visible_online.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener()
            {
                @Override
                public boolean onPreferenceChange(Preference preference, Object o)
                {
                    if(pref_visible_online.isChecked())
                    {
                        pref_visible_online.setChecked(false);
                        LoginActivity.accountSeller.setOnline("0");
                        settingsChanged=true;
                    }
                    else
                    {
                        pref_visible_online.setChecked(true);
                        LoginActivity.accountSeller.setOnline("1");
                        settingsChanged=true;
                    }
                    return false;
                }
            });
            //deliver
            int deliver=Integer.parseInt(LoginActivity.accountSeller.getDeliver());
            final SwitchPreference pref_deliver= (SwitchPreference) findPreference("online_delivery");
            //pref_deliver.setChecked(deliver==1);
            pref_deliver.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener()
            {
                @Override
                public boolean onPreferenceChange(Preference preference, Object o)
                {
                    if(pref_deliver.isChecked())
                    {
                        pref_deliver.setChecked(false);
                        LoginActivity.accountSeller.setDeliver("0");
                        settingsChanged=true;
                    }
                    else
                    {
                        pref_deliver.setChecked(true);
                        LoginActivity.accountSeller.setDeliver("1");
                        settingsChanged=true;
                    }
                    return false;
                }
            });*/
        }

        private CharSequence[] getCountriesList()
        {
            CharSequence[] countriesList;
            String[] isoCountryCodes= Locale.getISOCountries();
            countriesList=new CharSequence[isoCountryCodes.length];
            for(int count=0; count<isoCountryCodes.length; count+=1)
            {
                String countryCode=isoCountryCodes[count];
                Locale locale=new Locale("",countryCode);
                String countryName=locale.getDisplayCountry();
                countriesList[count]=countryName;
            }
            return countriesList;
        }
        private  CharSequence[] getCountriesListValues()
        {
            CharSequence[] countriesListValues;
            String[] isoCountryCodes= Locale.getISOCountries();
            countriesListValues=new CharSequence[isoCountryCodes.length];
            for(int count=0; count<isoCountryCodes.length; count+=1)
            {
                String countryCode=isoCountryCodes[count];
                countriesListValues[count]=countryCode;
            }
            return countriesListValues;
        }
        private String getCountryFromCode(String code)
        {
            String[] isoCountryCodes= Locale.getISOCountries();
            for(int count=0; count<isoCountryCodes.length; count+=1)
            {
                String countryCode=isoCountryCodes[count];
                Locale locale=new Locale("",countryCode);
                String countryName=locale.getDisplayCountry();
                if (countryCode.contentEquals(code))
                    return countryName;
            }
            return "unknown";
        }
        private String getCodeFromCountry(String country)
        {
            String[] isoCountryCodes= Locale.getISOCountries();
            for(int count=0; count<isoCountryCodes.length; count+=1)
            {
                String countryCode=isoCountryCodes[count];
                Locale locale=new Locale("",countryCode);
                String countryName=locale.getDisplayCountry();
                if (countryName.contentEquals(country))
                    return countryCode;
            }
            return "unknown";
        }
    }
    /**
     * This fragment shows feedback preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class FeedBackPreferenceFragment extends PreferenceFragmentCompat
    {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey)
        {
            setPreferencesFromResource(R.xml.pref_cfeedback, rootKey);

            final Preference pref_ask= (Preference) findPreference("feedback_ask");
            final Preference pref_report= (Preference) findPreference("feedback_report");
            pref_ask.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener()
            {
                @Override
                public boolean onPreferenceClick(Preference preference)
                {
                    composeEmail("New feature");
                    return true;
                }
            });
            pref_report.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener()
            {
                @Override
                public boolean onPreferenceClick(Preference preference)
                {
                    composeEmail("Reporting a problem");
                    return true;
                }
            });

        }

        private void composeEmail(String subject)
        {
            Intent intent=new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("mailto:"));//only email apps should handle this
            intent.putExtra(Intent.EXTRA_EMAIL,new String[]{"spikingacacia@gmail.com"});
            intent.putExtra(Intent.EXTRA_SUBJECT,subject);
            /*if(intent.resolveActivity(getPackageManager())!=null)
            {
                startActivity(intent);
            }*/
            startActivity(intent);
        }




    }
    /**
     * This fragment shows account preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class AccountPreferenceFragment extends PreferenceFragmentCompat
    {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey)
        {
            setPreferencesFromResource(R.xml.pref_caccount, rootKey);

            final Preference pref_del= (Preference) findPreference("account_delete");
            pref_del.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener()
            {
                @Override
                public boolean onPreferenceClick(Preference preference)
                {
                    TextInputLayout textInputLayout=new TextInputLayout(context);
                    textInputLayout.setGravity(Gravity.CENTER);
                    final EditText password=new EditText(context);
                    password.setPadding(20,10,20,10);
                    password.setTextSize(14);
                    password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    password.setHint("Password");
                    password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    password.setError(null);
                    textInputLayout.addView(password,0,new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
                    new AlertDialog.Builder(context)
                            .setTitle("Enter the password")
                            .setView(textInputLayout)
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener()
                            {
                                @Override
                                public void onClick(DialogInterface dialog, int which)
                                {
                                    dialog.dismiss();
                                }
                            })
                            .setPositiveButton("Proceed", new DialogInterface.OnClickListener()
                            {
                                @Override
                                public void onClick(DialogInterface dialog, int which)
                                {
                                    String pass=password.getText().toString();
                                    if(!pass.contentEquals(LoginActivity.contractorAccount.getPassword()))
                                    {
                                        password.setError("Incorrect password");
                                        Toast.makeText(context,"Incorrect password.",Toast.LENGTH_SHORT).show();
                                    }
                                    else
                                    {
                                        new AlertDialog.Builder(context)
                                                .setTitle("DELETE ACCOUNT")
                                                .setMessage("Are you sure you want to delete this account?" +
                                                        "\nThis will permanently remove your information")
                                                .setNegativeButton("Cancel", new DialogInterface.OnClickListener()
                                                {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which)
                                                    {
                                                        dialog.dismiss();
                                                    }
                                                })
                                                .setPositiveButton("Proceed", new DialogInterface.OnClickListener()
                                                {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which)
                                                    {
                                                        new DeleteAccount().execute((Void)null);
                                                    }
                                                }).create().show();
                                    }
                                }
                            }).create().show();

                    return true;
                }
            });

        }

        public class DeleteAccount extends AsyncTask<Void, Void, Boolean>
        {
            DeleteAccount()
            {
                // setDialog(true);
                Log.d("DELETINGACCOUNT","delete started started...");
            }
            @Override
            protected Boolean doInBackground(Void... params)
            {
                //building parameters
                List<NameValuePair>info=new ArrayList<NameValuePair>();
                info.add(new BasicNameValuePair("id",String.valueOf(LoginActivity.contractorAccount.getId())));
                JSONObject jsonObject= jsonParser.makeHttpRequest(url_delete_account,"POST",info);
                Log.d("jsonaccountdelete",jsonObject.toString());
                try
                {
                    int success=jsonObject.getInt(TAG_SUCCESS);
                    if(success==1)
                    {
                        return true;
                    }
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
            protected void onPostExecute(final Boolean success)
            {
                Log.d("settings permissions","finished");
                //setDialog(false);
                if(success)
                {
                    Toast.makeText(context,"Account deleted",Toast.LENGTH_SHORT).show();
                    Intent intent=new Intent(context,LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    getActivity().finish();
                    /*Intent intent=new Intent(context,LoginActivity.class);
                    int mPendingIntentId=12356;
                    PendingIntent pendingIntent=PendingIntent.getActivity(context,mPendingIntentId,intent,PendingIntent.FLAG_CANCEL_CURRENT);
                    AlarmManager alarmManager=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
                    alarmManager.set(AlarmManager.RTC,System.currentTimeMillis()+100,pendingIntent);
                    System.exit(0);*/
                }
                else
                {

                }

            }
        }




    }
    /**
     * This fragment shows account preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class FeaturesPreferenceFragment extends PreferenceFragmentCompat
    {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey)
        {
            setPreferencesFromResource(R.xml.pref_cfeatures, rootKey);

            final SwitchPreference pref_features=(SwitchPreference)  findPreference("equipment");
            pref_features.setChecked(loginPreferences.getBoolean("equipments",true));
            pref_features.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener()
            {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue)
                {
                    loginPreferencesEditor.putBoolean("equipments",(boolean)newValue);
                    loginPreferencesEditor.commit();
                    return true;
                }
            });


        }

    }
    /**
     * This fragment shows help preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class HelpPreferenceFragment extends PreferenceFragmentCompat
    {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey)
        {
            setPreferencesFromResource(R.xml.pref_chelp, rootKey);
        }

    }
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class AboutPreferenceFragment extends PreferenceFragmentCompat
    {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey)
        {
            setPreferencesFromResource(R.xml.pref_about, rootKey);
        }
    }
    /**
     * This fragment shows location preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    /*@TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class PrivilegesPreferenceFragment extends PreferenceFragment
    {
        @Override
        public void onCreate(Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_cprivileges);
            setHasOptionsMenu(true);

            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
            //bindPreferenceSummaryToValue(findPreference("online_visibility"));
            //bindPreferenceSummaryToValue(findPreference("online_delivery"));
            

        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item)
        {
            int id = item.getItemId();
            if (id == android.R.id.home)
            {
                startActivity(new Intent(getActivity(), CSettingsActivity.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
        private CharSequence[] getCountriesList()
        {
            CharSequence[] countriesList;
            String[] isoCountryCodes= Locale.getISOCountries();
            countriesList=new CharSequence[isoCountryCodes.length];
            for(int count=0; count<isoCountryCodes.length; count+=1)
            {
                String countryCode=isoCountryCodes[count];
                Locale locale=new Locale("",countryCode);
                String countryName=locale.getDisplayCountry();
                countriesList[count]=countryName;
            }
            return countriesList;
        }
        private  CharSequence[] getCountriesListValues()
        {
            CharSequence[] countriesListValues;
            String[] isoCountryCodes= Locale.getISOCountries();
            countriesListValues=new CharSequence[isoCountryCodes.length];
            for(int count=0; count<isoCountryCodes.length; count+=1)
            {
                String countryCode=isoCountryCodes[count];
                countriesListValues[count]=countryCode;
            }
            return countriesListValues;
        }
        private String getCountryFromCode(String code)
        {
            String[] isoCountryCodes= Locale.getISOCountries();
            for(int count=0; count<isoCountryCodes.length; count+=1)
            {
                String countryCode=isoCountryCodes[count];
                Locale locale=new Locale("",countryCode);
                String countryName=locale.getDisplayCountry();
                if (countryCode.contentEquals(code))
                    return countryName;
            }
            return "unknown";
        }
        private String getCodeFromCountry(String country)
        {
            String[] isoCountryCodes= Locale.getISOCountries();
            for(int count=0; count<isoCountryCodes.length; count+=1)
            {
                String countryCode=isoCountryCodes[count];
                Locale locale=new Locale("",countryCode);
                String countryName=locale.getDisplayCountry();
                if (countryName.contentEquals(country))
                    return countryCode;
            }
            return "unknown";
        }
    }*/

    public class UpdateAccount extends AsyncTask<Void, Void, Boolean>
    {
        UpdateAccount()
        {
            Log.d("settings","update started...");
        }
        @Override
        protected Boolean doInBackground(Void... params)
        {
            //building parameters
            List<NameValuePair>info=new ArrayList<NameValuePair>();
            info.add(new BasicNameValuePair("id",Integer.toString(tempContractorAccount.getId())));
            info.add(new BasicNameValuePair("password",tempContractorAccount.getPassword()));
            info.add(new BasicNameValuePair("username",tempContractorAccount.getUsername()));
            info.add(new BasicNameValuePair("country",tempContractorAccount.getCountry()));
            info.add(new BasicNameValuePair("location",tempContractorAccount.getLocation()));
            info.add(new BasicNameValuePair("permissions",tempContractorAccount.getPermissions()));
            info.add(new BasicNameValuePair("notifications",tempContractorAccount.getNotifications()));
            //getting all account details by making HTTP request
            JSONObject jsonObject= jsonParser.makeHttpRequest(url_update_account,"POST",info);
            try
            {
                int success=jsonObject.getInt(TAG_SUCCESS);
                if(success==1)
                {
                    return true;
                }
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
        protected void onPostExecute(final Boolean success)
        {
            Log.d("settings","finished");
            if(success)
            {
                Log.d("settings", "update done");
                LoginActivity.contractorAccount=tempContractorAccount;
                settingsChanged=false;
            }
            else
            {
                Log.e("settings", "error");
                Toast.makeText(context,"Your Account was not updated",Toast.LENGTH_LONG).show();
            }

        }
    }
    public class UpdatePermissions extends AsyncTask<Void, Void, Boolean>
    {
        UpdatePermissions()
        {
            Log.d("settings permissions","update started...");
        }
        @Override
        protected Boolean doInBackground(Void... params)
        {
            //building parameters
            List<NameValuePair>info=new ArrayList<NameValuePair>();
            info.add(new BasicNameValuePair("permissions",permissions));
            JSONObject jsonObject= jsonParser.makeHttpRequest(url_update_permissions,"POST",info);
            Log.d("PERMISSIONS SEND: ",info.toString());
            Log.d("PERMISSIONS OBJECT: ",jsonObject.toString());
            try
            {
                int success=jsonObject.getInt(TAG_SUCCESS);
                if(success==1)
                {
                    return true;
                }
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
        protected void onPostExecute(final Boolean success)
        {
            Log.d("settings permissions","finished");
            if(success)
            {
                Log.d("settings permissions", "update done");
                permissionsChanged=false;
            }
            else
            {
                Log.e("settings permissions", "error");
                Toast.makeText(context,"Permissions Updating error",Toast.LENGTH_LONG).show();
            }

        }
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
