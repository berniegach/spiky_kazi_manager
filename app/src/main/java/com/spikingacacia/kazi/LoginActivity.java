package com.spikingacacia.kazi;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.widget.Toast;

import com.android.billingclient.api.BillingClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;
import com.spikingacacia.kazi.billing.BillingManager;
import com.spikingacacia.kazi.billing.BillingProvider;
import com.spikingacacia.kazi.skulist.AcquireFragment;
import com.spikingacacia.kazi.database.CGlobalInfo;
import com.spikingacacia.kazi.database.CGlobalInfoEquips;
import com.spikingacacia.kazi.database.CNotifications;
import com.spikingacacia.kazi.database.CReviews;
import com.spikingacacia.kazi.database.CTasks;
import com.spikingacacia.kazi.database.ContractorAccount;
import com.spikingacacia.kazi.database.Permissions;
import com.spikingacacia.kazi.database.UserAccount;


import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.spikingacacia.kazi.billing.BillingManager.BILLING_MANAGER_NOT_INITIALIZED;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements
CreateAccountF.OnFragmentInteractionListener,
        SignInFragment.OnFragmentInteractionListener,
        BillingProvider
{
    private static final int OVERLAY_PERMISSION_CODE=543;
    //REMEMBER TO CHANGE THIS WHEN CHANGING BETWEEN ONLINE AND LOCALHOST
    //public static final String base_url="https://www.spikingacacia.com/kazi_project/android/"; //online
    public static final String base_url="http://10.0.2.2/kazi_project/android/"; //localhost no connection for testing user accounts coz it doesnt require subscription checking
    //public static final String base_url="http://192.168.0.10/kazi_project/android/"; //localhost
    //public static final String base_url="http://192.168.43.228/kazi_project/android/"; //localhost tablet
    private String TAG_SUCCESS="success";
    private String TAG_MESSAGE="message";
    private String TAG="LoginActivity";
    private JSONParser jsonParser;
    private Intent intentLoginProgress;
    public static int loginProgress;
    public static boolean AppRunningInThisActivity=true;//check if the app is running the in this activity
    //whenever you add a background asynctask make sure to update the finalprogress variables accordingly
    public static int cFinalProgress=12;

    private String url_get_permissions=base_url+"get_permissions.php";
    private String url_get_trades_list=base_url+"get_all_trades_from_table.php";
    private String url_get_equipments_list=base_url+"get_all_equipments_from_table.php";
    private String url_get_staff_count_in_trades=base_url+"get_staff_count_from_tradename.php";
    private String url_get_staff_count_in_equipment=base_url+"get_staff_count_from_equipmentname.php";
    private String url_get_personnel_table_columns=base_url+"get_personnel_table_columns.php";
    private String url_get_equipments_table_columns=base_url+"get_equipments_table_columns.php";
    private String url_get_personnel_matrix=base_url+"get_personnel_matrix.php";
    private String url_get_equipments_matrix=base_url+"get_equipments_matrix.php";
    private String url_get_personnel_compliance=base_url+"get_all_personnel_compliance.php";
    private String url_get_equipments_compliance=base_url+"get_all_equipments_compliance.php";
    private String url_get_notifications=base_url+"get_c_notifications.php";
    private String url_get_tasks=base_url+"get_tasks.php";
    private String url_get_reviews=base_url+"get_performance_review.php";
    //contractor
    public static UserAccount userAccount;
    public static ContractorAccount contractorAccount;
	public static List<Permissions>permissionsList;
    public static LinkedHashMap<String,Integer>tradesList;
    public static LinkedHashMap<String,Character>personnelColumnsList;
    public static int[][]personnelMatrix;
    public static CGlobalInfo cGlobalInfo;
    public static CGlobalInfoEquips cGlobalInfoEquips;
    public static LinkedHashMap<String,Integer>equipmentsList;
    public static LinkedHashMap<String,Character>equipmentsColumnsList;
    public static int[][]equipmentsMatrix;
    public static LinkedHashMap<String,CNotifications>cNotificationsList;
    public static List<CTasks>cTasksList;
    public static String currentSubscription="Non";
    public static List<CReviews>cReviewsList;
    //users
    public static String[]userCertificatesNames;
    public static String[]userCertificatesEquipmentsNames;
    //billing
    //subscription information
    public static Context mContext;
    // Tag for a dialog that allows us to find it when screen was rotated
    private static final String DIALOG_TAG = "dialog";

    // Default sample's package name to check if you changed it
    private static final String DEFAULT_PACKAGE_PREFIX = "com.example";

    private BillingManager mBillingManager;
    private AcquireFragment mAcquireFragment;
    private MainViewController mViewController;

    private SharedPreferences loginPreferences;
    private SharedPreferences.Editor loginPreferencesEditor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //preference
        loginPreferences=getBaseContext().getSharedPreferences("loginPrefs",MODE_PRIVATE);
        loginPreferencesEditor=loginPreferences.edit();
        //background intent
        intentLoginProgress=new Intent(LoginActivity.this,ProgressView.class);
        loginProgress=0;
        //accounts
        contractorAccount=new ContractorAccount();
        userAccount =new UserAccount();
        tradesList=new LinkedHashMap<>();
        personnelColumnsList=new LinkedHashMap<>();
        permissionsList=new ArrayList<>();
        equipmentsList=new LinkedHashMap<>();
        equipmentsColumnsList=new LinkedHashMap<>();
        cNotificationsList=new LinkedHashMap<>();
        cTasksList=new ArrayList<>();
        cReviewsList=new ArrayList<>();
        cGlobalInfo=new CGlobalInfo();
        cGlobalInfoEquips=new CGlobalInfoEquips();
        //global variables
        jsonParser=new JSONParser();
        //firebase links
        if((loginPreferences.getBoolean("verify",false)==true) || (loginPreferences.getBoolean("reset_password",false)==true))
        {
            Toast.makeText(getBaseContext(),"Please wait",Toast.LENGTH_SHORT).show();
            FirebaseDynamicLinks.getInstance()
                    .getDynamicLink(getIntent())
                    .addOnSuccessListener(this, new OnSuccessListener<PendingDynamicLinkData>() {
                        @Override
                        public void onSuccess(PendingDynamicLinkData pendingDynamicLinkData) {
                            // Get deep link from result (may be null if no link is found)
                            Uri deepLink = null;
                            if (pendingDynamicLinkData != null)
                            {
                                deepLink = pendingDynamicLinkData.getLink();
                                if(loginPreferences.getBoolean("verify",false)==true)
                                {
                                    setTitle("Sign Up");
                                    Fragment fragment=CreateAccountF.newInstance(1,loginPreferences.getString("email_verify",""));
                                    FragmentTransaction transaction=getSupportFragmentManager().beginTransaction();
                                    transaction.replace(R.id.loginbase,fragment,"createnewaccount");
                                    transaction.addToBackStack("createaccount");
                                    transaction.commit();
                                }
                                else if(loginPreferences.getBoolean("reset_password",false)==true)
                                {
                                    setTitle("Reset Password");
                                    Fragment fragment=CreateAccountF.newInstance(2,loginPreferences.getString("email_reset_password",""));
                                    FragmentTransaction transaction=getSupportFragmentManager().beginTransaction();
                                    transaction.replace(R.id.loginbase,fragment,"createnewaccount");
                                    transaction.addToBackStack("createaccount");
                                    transaction.commit();
                                }

                            }


                            // Handle the deep link. For example, open the linked
                            // content, or apply promotional credit to the user's
                            // account.
                            // ...

                            // ...
                        }
                    })
                    .addOnFailureListener(this, new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "getDynamicLink:onFailure", e);
                        }
                    });
        }

        //fragment manager
        getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener()
        {
            @Override
            public void onBackStackChanged()
            {
                int count=getSupportFragmentManager().getBackStackEntryCount();
                if(count==0)
                    setTitle("Sign In");
            }
        });
        setTitle("Sign In");
        Fragment fragment=SignInFragment.newInstance("","");
        FragmentTransaction transaction=getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.loginbase,fragment,"signin");
        transaction.commit();
        //billing
        mContext=this;
        mViewController = new MainViewController(this);

        if (getPackageName().startsWith(DEFAULT_PACKAGE_PREFIX)) {
            throw new RuntimeException("Please change the sample's package name!");
        }

        // Try to restore dialog fragment if we were showing it prior to screen rotation
        if (savedInstanceState != null) {
            mAcquireFragment = (AcquireFragment) getSupportFragmentManager()
                    .findFragmentByTag(DIALOG_TAG);
        }

        // Create and initialize BillingManager which talks to BillingLibrary
        mBillingManager = new BillingManager(this, mViewController.getUpdateListener());
    }
    @Override
    protected void onDestroy()
    {
        //super.onDestroy();
        if(intentLoginProgress!=null)
            stopService(intentLoginProgress);
        Log.d(TAG, "Destroying helper.");
        if (mBillingManager != null) {
            mBillingManager.destroy();
        }
        super.onDestroy();
    }
    @Override
    protected void onResume()
    {
        super.onResume();
        //clear the variables . if not done youll find some list contents add up on top of the previous ones
        loginProgress=0;
        if(!tradesList.isEmpty())tradesList.clear();
        if(!personnelColumnsList.isEmpty())personnelColumnsList.clear();
        if(!permissionsList.isEmpty())permissionsList.clear();
        if(!equipmentsList.isEmpty())equipmentsList.clear();
        if(!equipmentsColumnsList.isEmpty())equipmentsColumnsList.clear();
        if(!cNotificationsList.isEmpty())cNotificationsList.clear();
        if(!cTasksList.isEmpty())cTasksList.clear();
        if(!cReviewsList.isEmpty())cReviewsList.clear();
        //global variables
        AppRunningInThisActivity=true;
        //billing
        // Note: We query purchases in onResume() to handle purchases completed while the activity
        // is inactive. For example, this can happen if the activity is destroyed during the
        // purchase flow. This ensures that when the activity is resumed it reflects the user's
        // current purchases.
        if (mBillingManager != null
                && mBillingManager.getBillingClientResponseCode() == BillingClient.BillingResponse.OK) {
            mBillingManager.queryPurchases();
        }
    }
    @Override
    protected void onActivityResult(int requestCode,int resultCode,Intent data)
    {
        super.onActivityResult(requestCode,resultCode,data);
        if(requestCode==OVERLAY_PERMISSION_CODE)
        {
            if(Settings.canDrawOverlays(this))
            {
                startService(intentLoginProgress);
            }
           startBackgroundTasks();
        }
    }
    /**
     * Billing functions*/
    @Override
    public BillingManager getBillingManager() {
        return mBillingManager;
    }
    @Override
    public boolean isMonthlySubscribed() {
        Log.d(TAG,"subscribed monthly " +mViewController.isMonthlySubscribed());
        LoginActivity.currentSubscription="Monthly Subscribed";
       /* if(mViewController.isMonthlySubscribed() && LoginActivity.AppRunningInThisActivity)
        {
            Log.d(TAG," Monthly, proceed to menu ");
            Intent intent=new Intent();
            intent.putExtra("allow",true);
            setResult(BILLING_REQUEST_CODE,intent);
            finish();
        }*/
        /*else if(proceedToSubscriptionPurchase==false)
        {
            proceedToSubscriptionPurchase=true;
        }
        else
            onSubscriptionPurchase();*/
        return mViewController.isMonthlySubscribed();
    }

    @Override
    public boolean isYearlySubscribed() {
        LoginActivity.currentSubscription="Yearly Subscribed";
        Log.d(TAG,"subscribed monthly " +mViewController.isYearlySubscribed());
        /*if(mViewController.isYearlySubscribed() && LoginActivity.AppRunningInThisActivity)
        {
            Log.d(TAG," yearly, proceed to menu ");
            Intent intent=new Intent();
            intent.putExtra("allow",true);
            setResult(BILLING_REQUEST_CODE,intent);
            finish();
        }*/
        /*else if(proceedToSubscriptionPurchase==false)
        {
            proceedToSubscriptionPurchase=true;
        }
        else
            onSubscriptionPurchase();*/
        return mViewController.isYearlySubscribed();
    }
    /**
     * User clicked the "Buy Gas" button - show a purchase dialog with all available SKUs
     */
    public void onSubscriptionPurchase() {
        Log.d(TAG, "Purchase button clicked.");

        if (mAcquireFragment == null) {
            mAcquireFragment = new AcquireFragment();
        }

        if (!isAcquireFragmentShown()) {
            getSupportFragmentManager().beginTransaction().add(mAcquireFragment,DIALOG_TAG).commit();
            //mAcquireFragment.show(getSupportFragmentManager(), DIALOG_TAG);

            if (mBillingManager != null
                    && mBillingManager.getBillingClientResponseCode()
                    > BILLING_MANAGER_NOT_INITIALIZED) {
                mAcquireFragment.onManagerReady(this);
            }
        }
    }

    /**
     * Remove loading spinner and refresh the UI
     */
    public void showRefreshedUi() {
        //setWaitScreen(false);
        //updateUi();
        if (mAcquireFragment != null) {
            mAcquireFragment.refreshUI();
        }
    }
    public void onBillingManagerSetupFinished() {
        if (mAcquireFragment != null) {
            mAcquireFragment.onManagerReady(this);
        }
    }
    public boolean isAcquireFragmentShown() {
        return mAcquireFragment != null && mAcquireFragment.isVisible();
    }



    /** Implementation of SignInFragment.java**/
   @Override
   public void onSuccesfull()
   {
       //start the floating service
       if(Build.VERSION.SDK_INT>=23)
       {
           if(!Settings.canDrawOverlays(this))
           {
               //open permissions page
               Intent intent=new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                       Uri.parse("package:"+getPackageName()));
               startActivityForResult(intent,OVERLAY_PERMISSION_CODE);
               //return;
           }
           else
           {
               //REMEMBER TO CHANGE THIS WHEN CHANGING BETWEEN ONLINE AND LOCALHOST
               //if(true)
               if(isMonthlySubscribed()||isYearlySubscribed())
                   startBackgroundTasks();
               else
               {
                   try
                   {
                       //check if the one month since registration has ended
                       //the date added is in the form "d-m-Y H:i"
                       String date_added=contractorAccount.getDateadded();
                       String date_now=contractorAccount.getDateToday();
                       SimpleDateFormat simpleDateFormat=new SimpleDateFormat("dd-MM-yyyy HH:mm");
                       Date date_add= simpleDateFormat.parse(date_added);
                       Date date_n=simpleDateFormat.parse(date_now);
                       long difference=date_n.getTime()-date_add.getTime();
                       long days= TimeUnit.DAYS.convert(difference,TimeUnit.MILLISECONDS);
                       Log.d(TAG,"days "+days);
                       if(days>30)
                       {
                           String message="Your trial period of 30 days has ended.\nWould you like to proceed to the subscriptions purchase?";
                           new AlertDialog.Builder(this)
                                   .setTitle("Kazi 1 month trial")
                                   .setMessage(message)
                                   .setNegativeButton("Cancel", new DialogInterface.OnClickListener()
                                   {
                                       @Override
                                       public void onClick(DialogInterface dialogInterface, int i)
                                       {
                                           dialogInterface.dismiss();
                                       }
                                   })
                                   .setPositiveButton("Proceed", new DialogInterface.OnClickListener()
                                   {
                                       @Override
                                       public void onClick(DialogInterface dialogInterface, int i)
                                       {
                                           onSubscriptionPurchase();
                                       }
                                   })
                                   .create().show();
                       }
                       else
                       {
                           startBackgroundTasks();
                           currentSubscription="Trial period remaining "+(30-days)+" days";
                       }
                   }
                   catch (ParseException e)
                   {
                       Log.e(TAG,"exception "+e.getMessage());
                   }


                   Log.d(TAG, "NO subscriptions");

               }
           }
       }
       else
       {
           if(isMonthlySubscribed()||isYearlySubscribed())
               startBackgroundTasks();
           else
           {
               try
               {
                   //check if the one month since registration has ended
                   //the date added is in the form "d-m-Y H:i"
                   String date_added=contractorAccount.getDateadded();
                   String date_now=contractorAccount.getDateToday();
                   SimpleDateFormat simpleDateFormat=new SimpleDateFormat("dd-MM-yyyy HH:mm");
                   Date date_add= simpleDateFormat.parse(date_added);
                   Date date_n=simpleDateFormat.parse(date_now);
                   long difference=date_n.getTime()-date_add.getTime();
                   long days= TimeUnit.DAYS.convert(difference,TimeUnit.MILLISECONDS);
                   Log.d(TAG,"days "+days);
                   if(days>30)
                   {
                       String message="Your trial period of 30 days has ended.\nWould you like to proceed to the subscriptions purchase?";
                       new AlertDialog.Builder(this)
                               .setTitle("Kazi 1 month trial")
                               .setMessage(message)
                               .setNegativeButton("Cancel", new DialogInterface.OnClickListener()
                               {
                                   @Override
                                   public void onClick(DialogInterface dialogInterface, int i)
                                   {
                                       dialogInterface.dismiss();
                                   }
                               })
                               .setPositiveButton("Proceed", new DialogInterface.OnClickListener()
                               {
                                   @Override
                                   public void onClick(DialogInterface dialogInterface, int i)
                                   {
                                       onSubscriptionPurchase();
                                   }
                               })
                               .create().show();
                   }
                   else
                   {
                       startBackgroundTasks();
                       currentSubscription="Trial period remaining "+(30-days)+" days";
                   }
               }
               catch (ParseException e)
               {
                   Log.e(TAG,"exception "+e.getMessage());
               }


               Log.d(TAG, "NO subscriptions");
           }
       }
   }
   @Override
   public void purchaseSubscription()
   {
       onSubscriptionPurchase();
   }
   private void startBackgroundTasks()
   {
       startService(intentLoginProgress);
       new CPermissionsTask().execute((Void)null);
       new CTradesListTask().execute((Void)null);
       new CPersonnelColumnsListTask().execute((Void)null);
       new CPersonnelMatrixTask().execute((Void)null);
       new CPersonnelComplianceTask().execute((Void)null);
       new CEquipmentsComplianceTask().execute((Void)null);
       new CEquipmentsListTask().execute((Void)null);
       new CEquipmentsColumnsListTask().execute((Void)null);
       new CEquipmentsMatrixTask().execute((Void)null);
       new CNotificationsTask().execute((Void)null);
       new CTasksTask().execute((Void)null);
       new CReviewsTask().execute((Void)null);
       Intent intent=new Intent(this, CMenuActivity.class);
       // intent.putExtra("NOTHING","nothing");
       startActivity(intent);
   }


    /** Implementation of CreateAccountF.java**/
    @Override
    public  void onRegisterFinished()
    {
        setTitle("Sign In");
        onBackPressed();
    }
    @Override
    public void createAccount()
    {
        setTitle("Sign Up");
        Fragment fragment=CreateAccountF.newInstance(0,"");
        FragmentTransaction transaction=getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.loginbase,fragment,"createnewaccount");
        transaction.addToBackStack("createaccount");
        transaction.commit();
    }

    public class CPermissionsTask extends AsyncTask<Void, Void, Boolean>
    {
        private int success=0;

        CPermissionsTask() {
        }
        @Override
        protected void onPreExecute()
        {
            Log.d("PERMISSIONS: ","getting permissions....");
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            //logIn=handler.LogInStaff(mEmail,mPassword);
            //building parameters
            List<NameValuePair> info=new ArrayList<NameValuePair>();
            info.add(new BasicNameValuePair("permissions",contractorAccount.getPermissions()));
            //getting all account details by making HTTP request
            JSONObject jsonObject= jsonParser.makeHttpRequest(url_get_permissions,"GET",info);
            Log.d("JSON PERMISSIONS",jsonObject.toString());
            try
            {
                success=jsonObject.getInt(TAG_SUCCESS);
                if(success==1)
                {
                    //seccesful
                    JSONArray permissionsArray=jsonObject.getJSONArray("data");
                    permissionsArray=permissionsArray.getJSONArray(0);
                    for(int count=0; count<permissionsArray.length(); count+=1)
                    {
                        JSONObject permissionsObject=permissionsArray.getJSONObject(count);
                        //new class
                        Permissions permissions=new Permissions();
                        permissions.setGiven(permissionsObject.getInt("given"));
                        permissions.setPersona((permissionsObject.getString("persona")).charAt(0));
                        permissions.setType((permissionsObject.getString("type")).charAt(0));
                        permissions.setId(permissionsObject.getInt("id"));
                        permissions.setEmail(permissionsObject.getString("email"));
                        permissions.setUsername(permissionsObject.getString("username"));
                        permissions.setCompany(permissionsObject.getString("company"));
                        permissions.setPosition(permissionsObject.getString("position"));
                        //add to the main list
                        permissionsList.add(permissions);

                    }


                   // userAccount.setId(accountObject.getInt("id"));
                    //userAccount.setEmail(accountObject.getString("email"));
                    //userAccount.setPassword(accountObject.getString("password"));
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
        protected void onPostExecute(final Boolean successful) {
            Log.d("PERMISSIONS: ","finished...."+"progress: "+String.valueOf(loginProgress));
            loginProgress+=1;
            if (loginProgress == cFinalProgress)
                stopService(intentLoginProgress);

            if (successful)
            {

            }
            else
            {

            }
        }
        @Override
        protected void onCancelled()
        {
            //mAuthTaskU = null;
        }
    }
    /**
     * The following code will get a list of positions or trades defined in the bosses personnel matrix table
     * * Arguments are:
     * id==boss id.
     * Returns are:
     * success==1 successful get
     * success==0 for id argument missing
     **/
    private class CTradesListTask extends AsyncTask<Void, Void, Boolean>
    {
        @Override
        protected void onPreExecute()
        {
            Log.d("TRADESLIST: ","getting tradeslist....");
            super.onPreExecute();
        }
        @Override
        protected Boolean doInBackground(Void... params)
        {
            boolean ok=true;
            //getting trades list
            List<NameValuePair>infoTrades=new ArrayList<NameValuePair>(); //info for staff count
            infoTrades.add(new BasicNameValuePair("id",Integer.toString(contractorAccount.getId())));
            // making HTTP request
            JSONObject jsonObjectTrades= jsonParser.makeHttpRequest(url_get_trades_list,"POST",infoTrades);
            Log.d("tradesContent",""+jsonObjectTrades.toString());
            try
            {
                JSONArray tradesArrayList=null;
                int success=jsonObjectTrades.getInt(TAG_SUCCESS);
                if(success==1)
                {
                    tradesArrayList=jsonObjectTrades.getJSONArray("tradeslist");
                    //JSONObject tradesArrayObject=tradesArrayList.getJSONObject(0);
                    tradesArrayList=tradesArrayList.getJSONArray(0);
                    for(int count=0; count<tradesArrayList.length(); count+=1)
                    {
                        List<NameValuePair>infoStaffCount=new ArrayList<NameValuePair>(); //info for staff count
                        infoStaffCount.add(new BasicNameValuePair("id",Integer.toString(contractorAccount.getId())));
                        infoStaffCount.add(new BasicNameValuePair("tradename",tradesArrayList.getString(count)));
                        // making HTTP request
                        JSONObject jsonObjectStaffCount= jsonParser.makeHttpRequest(url_get_staff_count_in_trades,"POST",infoStaffCount);
                        Log.d("compliancy",""+jsonObjectStaffCount.toString());
                        try
                        {
                            int successcount=jsonObjectStaffCount.getInt(TAG_SUCCESS);
                            if(successcount==1)
                            {
                                int staffcount=jsonObjectStaffCount.getInt("staffcount");
                                tradesList.put(tradesArrayList.getString(count),staffcount);
                               // addItem(createTradesItem(count+1,tradesArrayList.getString(count).replace("_"," "),Integer.toString(staffcount)));
                            }
                            else
                            {
                                String message=jsonObjectStaffCount.getString(TAG_MESSAGE);
                                Log.e(TAG_MESSAGE,""+message);
                                ok=false;
                            }
                        }
                        catch (JSONException e)
                        {
                            Log.e("JSON",""+e.getMessage());
                            ok= false;
                        }
                    }
                }
                else
                {
                    String message=jsonObjectTrades.getString(TAG_MESSAGE);
                    Log.e(TAG_MESSAGE,""+message);
                    ok= false;
                }
            }
            catch (JSONException e)
            {
                Log.e("JSON",""+e.getMessage());
                ok=false;
            }
            return ok;
        }
        @Override
        protected void onPostExecute(final Boolean successful) {
            Log.d("TRADESLIST: ","finished...."+"progress: "+String.valueOf(loginProgress));
            loginProgress+=1;
            if (loginProgress == cFinalProgress)
                stopService(intentLoginProgress);

            if (successful)
            {

            }
            else
            {

            }
        }
    }
    /**
     * Following code will return a list of equipments.
     * The columns returned are from the first index of equipments to the last index of equipment
     * Arguments are:
     * id==boss id.
     * Returns are:
     * success==1 successful get
     * success==0 for id argument missing
     **/
    private class CEquipmentsListTask extends AsyncTask<Void, Void, Boolean>
    {
        @Override
        protected void onPreExecute()
        {
            Log.d("EQUIPMENTSLIST: ","getting equipmentslist....");
            super.onPreExecute();
        }
        @Override
        protected Boolean doInBackground(Void... params)
        {
            boolean ok=true;
            //getting trades list
            List<NameValuePair>infoTrades=new ArrayList<NameValuePair>(); //info for staff count
            infoTrades.add(new BasicNameValuePair("id",Integer.toString(contractorAccount.getId())));
            // making HTTP request
            JSONObject jsonObjectEquipments= jsonParser.makeHttpRequest(url_get_equipments_list,"POST",infoTrades);
            Log.d("equipmentsContent",""+jsonObjectEquipments.toString());
            try
            {
                JSONArray equipmentsArrayList=null;
                int success=jsonObjectEquipments.getInt(TAG_SUCCESS);
                if(success==1)
                {
                    equipmentsArrayList=jsonObjectEquipments.getJSONArray("equipmentslist");
                    //JSONObject tradesArrayObject=tradesArrayList.getJSONObject(0);
                    equipmentsArrayList=equipmentsArrayList.getJSONArray(0);
                    for(int count=0; count<equipmentsArrayList.length(); count+=1)
                    {
                        List<NameValuePair>infoStaffCount=new ArrayList<NameValuePair>(); //info for staff count
                        infoStaffCount.add(new BasicNameValuePair("id",Integer.toString(contractorAccount.getId())));
                        infoStaffCount.add(new BasicNameValuePair("equipmentsname",equipmentsArrayList.getString(count)));
                        // making HTTP request
                        JSONObject jsonObjectStaffCount= jsonParser.makeHttpRequest(url_get_staff_count_in_equipment,"POST",infoStaffCount);
                        Log.d("compliancy",""+jsonObjectStaffCount.toString());
                        try
                        {
                            int successcount=jsonObjectStaffCount.getInt(TAG_SUCCESS);
                            if(successcount==1)
                            {
                                int staffcount=jsonObjectStaffCount.getInt("staffcount");
                                equipmentsList.put(equipmentsArrayList.getString(count),staffcount);
                                // addItem(createTradesItem(count+1,tradesArrayList.getString(count).replace("_"," "),Integer.toString(staffcount)));
                            }
                            else
                            {
                                String message=jsonObjectStaffCount.getString(TAG_MESSAGE);
                                Log.e(TAG_MESSAGE,""+message);
                                ok=false;
                            }
                        }
                        catch (JSONException e)
                        {
                            Log.e("JSON",""+e.getMessage());
                            ok= false;
                        }
                    }
                }
                else
                {
                    String message=jsonObjectEquipments.getString(TAG_MESSAGE);
                    Log.e(TAG_MESSAGE,""+message);
                    ok= false;
                }
            }
            catch (JSONException e)
            {
                Log.e("JSON",""+e.getMessage());
                ok=false;
            }
            return ok;
        }
        @Override
        protected void onPostExecute(final Boolean successful) {
            Log.d("EQUIPMENTSLIST: ","finished...."+"progress: "+String.valueOf(loginProgress));
            loginProgress+=1;
            if (loginProgress == cFinalProgress)
                stopService(intentLoginProgress);

            if (successful)
            {

            }
            else
            {

            }
        }
    }
    /**
     * Following code will get the boss personnel matrix table columns
     * the columns got start from index 2 all the way to the last position/trade.
     * Arguments are:
     * id==boss id.
     * Returns are:
     * success==1 successful get
     * success==0 for id argument missing
     **/
    private class CPersonnelColumnsListTask extends AsyncTask<Void, Void, Boolean>
    {
        @Override
        protected void onPreExecute()
        {
            Log.d("CPERSONNELCOLUMNSLIST: ","getting columns list....");
            super.onPreExecute();
        }
        @Override
        protected Boolean doInBackground(Void... params)
        {
            //getting columns list
            List<NameValuePair>info=new ArrayList<NameValuePair>(); //info for staff count
            info.add(new BasicNameValuePair("id",Integer.toString(contractorAccount.getId())));
            // making HTTP request
            JSONObject jsonObject= jsonParser.makeHttpRequest(url_get_personnel_table_columns,"POST",info);
            Log.d("cPColumnsContent",""+jsonObject.toString());
            try
            {
                JSONArray columnssArrayList=null;
                int success=jsonObject.getInt(TAG_SUCCESS);
                if(success==1)
                {
                    columnssArrayList=jsonObject.getJSONArray("columns");
                    columnssArrayList=columnssArrayList.getJSONArray(0);
                    for(int count=0; count<columnssArrayList.length(); count+=1)
                    {
                        String name=columnssArrayList.getString(count);
                        String[] namePieces=name.split(":");
                        personnelColumnsList.put(namePieces[0],namePieces[1].charAt(0));

                    }
                    userCertificatesNames=new String[personnelColumnsList.size()];
                    Arrays.fill(userCertificatesNames,"");
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
        protected void onPostExecute(final Boolean successful) {
            Log.d("CPERSONNELCOLUMNS: ","finished...."+"progress: "+String.valueOf(loginProgress));
            loginProgress+=1;
            if (loginProgress == cFinalProgress)
                stopService(intentLoginProgress);

            if (successful)
            {

            }
            else
            {

            }
        }
    }
    /**
     * Following code will get the boss equipments matrix table columns
     * the columns got start from index 2 all the way to the last equipment.
     * Arguments are:
     * id==boss id.
     * Returns are:
     * success==1 successful get
     * success==0 for id argument missing
     **/
    private class CEquipmentsColumnsListTask extends AsyncTask<Void, Void, Boolean>
    {
        @Override
        protected void onPreExecute()
        {
            Log.d("CEQUIPMENTSCOLUMNS: ","getting columns list....");
            super.onPreExecute();
        }
        @Override
        protected Boolean doInBackground(Void... params)
        {
            //getting columns list
            List<NameValuePair>info=new ArrayList<NameValuePair>(); //info for staff count
            info.add(new BasicNameValuePair("id",Integer.toString(contractorAccount.getId())));
            // making HTTP request
            JSONObject jsonObject= jsonParser.makeHttpRequest(url_get_equipments_table_columns,"POST",info);
            Log.d("cEColumnsContent",""+jsonObject.toString());
            try
            {
                JSONArray columnssArrayList=null;
                int success=jsonObject.getInt(TAG_SUCCESS);
                if(success==1)
                {
                    columnssArrayList=jsonObject.getJSONArray("columns");
                    columnssArrayList=columnssArrayList.getJSONArray(0);
                    for(int count=0; count<columnssArrayList.length(); count+=1)
                    {
                        String name=columnssArrayList.getString(count);
                        String[] namePieces=name.split(":");
                        equipmentsColumnsList.put(namePieces[0],namePieces[1].charAt(0));

                    }
                    userCertificatesEquipmentsNames=new String[equipmentsColumnsList.size()];
                    Arrays.fill(userCertificatesEquipmentsNames,"");
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
        protected void onPostExecute(final Boolean successful) {
            Log.d("CEQUIPMENTSCOLUMNS: ","finished...."+"progress: "+String.valueOf(loginProgress));
            loginProgress+=1;
            if (loginProgress == cFinalProgress)
                stopService(intentLoginProgress);

            if (successful)
            {

            }
            else
            {

            }
        }
    }
    /**
     * Following code will get all the rows in the boss personnel matrix table.
     * The columns returned are from index 2 marking the first requirement to the last, position.
     * The rows returned are from index 1 to the last. From the position definitions to all the personnel requirements
     * Arguments are:
     * id==boss id.
     * Returns are:
     * success==1 successful get
     * success==0 for id argument missing
     **/
    private class CPersonnelMatrixTask extends AsyncTask<Void, Void, Boolean>
    {
        @Override
        protected void onPreExecute()
        {
            Log.d("CPERSONNELMATRIXSLIST: ","getting matrix....");
            super.onPreExecute();
        }
        @Override
        protected Boolean doInBackground(Void... params)
        {
            //getting columns list
            List<NameValuePair>info=new ArrayList<NameValuePair>(); //info for staff count
            info.add(new BasicNameValuePair("id",Integer.toString(contractorAccount.getId())));
            // making HTTP request
            JSONObject jsonObject= jsonParser.makeHttpRequest(url_get_personnel_matrix,"POST",info);
            Log.d("cPMatrix",""+jsonObject.toString());
            try
            {
                int success=jsonObject.getInt(TAG_SUCCESS);
                if(success==1)
                {
                    JSONArray matrixArray=jsonObject.getJSONArray("matrix");
                    matrixArray=matrixArray.getJSONArray(0);
                    personnelMatrix=new int[matrixArray.length()][];
                    for(int count=0; count<matrixArray.length(); count++)
                    {
                        JSONArray personnelArray=matrixArray.getJSONArray(count);
                        personnelMatrix[count]=new int[personnelArray.length()];
                        for(int c=0; c<personnelArray.length(); c++)
                        {
                            if(personnelArray.get(c).equals(null))
                                personnelMatrix[count][c]=0;
                            else
                                personnelMatrix[count][c]=personnelArray.getInt(c);
                        }
                    }
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
        protected void onPostExecute(final Boolean successful) {
            Log.d("CPMATRIX: ","finished...."+"progress: "+String.valueOf(loginProgress));
            loginProgress+=1;
            if (loginProgress == cFinalProgress)
                stopService(intentLoginProgress);

            if (successful)
            {

            }
            else
            {

            }
        }
    }
    /**
     * Following code will get all the rows in the boss equipments matrix table.
     * The columns returned are from index 2 marking the first property to the last, equipment.
     * The rows returned are from index 1 to the last. From the equipments definitions to all the personnel equipments properties
     * Arguments are:
     * id==boss id.
     * Returns are:
     * success==1 successful get
     * success==0 for id argument missing
     **/
    private class CEquipmentsMatrixTask extends AsyncTask<Void, Void, Boolean>
    {
        @Override
        protected void onPreExecute()
        {
            Log.d("CEQUIPMATRIXSLIST: ","getting matrix....");
            super.onPreExecute();
        }
        @Override
        protected Boolean doInBackground(Void... params)
        {
            //getting columns list
            List<NameValuePair>info=new ArrayList<NameValuePair>(); //info for staff count
            info.add(new BasicNameValuePair("id",Integer.toString(contractorAccount.getId())));
            // making HTTP request
            JSONObject jsonObject= jsonParser.makeHttpRequest(url_get_equipments_matrix,"POST",info);
            Log.d("cEMatrix",""+jsonObject.toString());
            try
            {
                int success=jsonObject.getInt(TAG_SUCCESS);
                if(success==1)
                {
                    JSONArray matrixArray=jsonObject.getJSONArray("matrix");
                    matrixArray=matrixArray.getJSONArray(0);
                    equipmentsMatrix=new int[matrixArray.length()][];
                    for(int count=0; count<matrixArray.length(); count++)
                    {
                        JSONArray personnelArray=matrixArray.getJSONArray(count);
                        equipmentsMatrix[count]=new int[personnelArray.length()];
                        for(int c=0; c<personnelArray.length(); c++)
                        {
                            if(personnelArray.get(c).equals(null))
                                equipmentsMatrix[count][c]=0;
                            else
                                equipmentsMatrix[count][c]=personnelArray.getInt(c);
                        }
                    }
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
        protected void onPostExecute(final Boolean successful) {
            Log.d("CEMATRIX: ","finished...."+"progress: "+String.valueOf(loginProgress));
            loginProgress+=1;
            if (loginProgress == cFinalProgress)
                stopService(intentLoginProgress);

            if (successful)
            {

            }
            else
            {

            }
        }
    }
    /**
     * Following code will get the compliance data for all the personnel
     * The returned infos are as contained in the result array
     * Arguments are:
     * id==boss id.
     * Returns are:
     * success==1 successful get
     * success==0 for id argument missing and errors
     **/
    private class CPersonnelComplianceTask extends AsyncTask<Void, Void, Boolean>
    {
        @Override
        protected void onPreExecute()
        {
            Log.d("CPERSONNELCOMPLIANCE:","getting compliance....");
            super.onPreExecute();
        }
        @Override
        protected Boolean doInBackground(Void... params)
        {
            //getting columns list
            List<NameValuePair>info=new ArrayList<NameValuePair>(); //info for staff count
            info.add(new BasicNameValuePair("id",Integer.toString(contractorAccount.getId())));
            // making HTTP request
            JSONObject jsonObject= jsonParser.makeHttpRequest(url_get_personnel_compliance,"POST",info);
            Log.d("allComplaince",""+jsonObject.toString());
            try
            {
                int success=jsonObject.getInt(TAG_SUCCESS);
                if(success==1)
                {
                    cGlobalInfo.setStaffCount(jsonObject.getInt("staffcount"));
                    cGlobalInfo.setCompliant(jsonObject.getInt("compliant"));
                    cGlobalInfo.setNoncompliant(jsonObject.getInt("noncompliant"));
                    JSONArray compArray=jsonObject.getJSONArray("compliantstaff");
                    LinkedHashMap<String,Character>tempCompliantStaff=new LinkedHashMap<>();
                    for(int count=0; count<compArray.length(); count+=1)
                    {
                        //add the staff member to the recylcer
                        String staff=compArray.getString(count);
                        String[] token=staff.split(":");
                        final String id=token[0];
                        final String userid=token[1];
                        final String trade=token[2];
                        final String names=token[3];
                        tempCompliantStaff.put(staff,'C');
                    }
                    cGlobalInfo.setComplaintStaff(tempCompliantStaff);
                    JSONArray noncompArray=jsonObject.getJSONArray("noncompliantstaff");
                    LinkedHashMap<String,Character>tempNonCompliantStaff=new LinkedHashMap<>();
                    for(int count=0; count<noncompArray.length(); count+=1)
                    {
                        //add the staff member to the recylcer
                        String staff=noncompArray.getString(count);
                        String[] token=staff.split(":");
                        final String id=token[0];
                        final String userid=token[1];
                        final String trade=token[2];
                        final String names=token[3];
                        tempNonCompliantStaff.put(staff,'N');
                    }
                    cGlobalInfo.setNonComplaintStaff(tempNonCompliantStaff);
                    cGlobalInfo.setMissingQualifications(jsonObject.getInt("missingqualifications"));
                    cGlobalInfo.setMissingCertificates(jsonObject.getInt("missingcertificates"));
                    cGlobalInfo.setExpiredCertificates(jsonObject.getInt("expiredcertificates"));
                    cGlobalInfo.setUsersWithMissingQualifications(jsonObject.getInt("staffwithmissingqualifications"));
                    cGlobalInfo.setUsersWithMissingCertificates(jsonObject.getInt("staffwithmissingcertificates"));
                    cGlobalInfo.setUserWithExpiredCertificates(jsonObject.getInt("staffwithexpiredcertificates"));
                    cGlobalInfo.setUsersWithCase(jsonObject.getInt("staffwith_case"));
                    cGlobalInfo.setUsersWithQCCase(jsonObject.getInt("staffwith_q_c_case"));
                    cGlobalInfo.setUsersWithQECCase(jsonObject.getInt("staffwith_q_e_case"));
                    cGlobalInfo.setUsersWithCECCase(jsonObject.getInt("staffwith_c_ec_case"));
                    cGlobalInfo.setUsersWithQCECCase(jsonObject.getInt("staffwith_q_c_ec_case"));
                    JSONArray eachQualMissingArray=jsonObject.getJSONArray("each_qual_missing_count");
                    int []each_qual_missing_count=new int[eachQualMissingArray.length()];
                    for(int count=0; count<eachQualMissingArray.length(); count+=1)
                    {
                        each_qual_missing_count[count]=eachQualMissingArray.getInt(count);
                    }
                    cGlobalInfo.setEachQualMissingCount(each_qual_missing_count);
                    JSONArray tableColumnsArray=jsonObject.getJSONArray("columnnames");
                    String[]tableColumns=new String[tableColumnsArray.length()];
                    for(int count=0; count<tableColumnsArray.length(); count+=1)
                    {
                        tableColumns[count]=tableColumnsArray.getString(count);
                    }
                    cGlobalInfo.setTableColumns(tableColumns);
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
        protected void onPostExecute(final Boolean successful) {
            Log.d("CPCOMPLIANCE: ","finished...."+"progress: "+String.valueOf(loginProgress));
            loginProgress+=1;
            if (loginProgress == cFinalProgress)
                stopService(intentLoginProgress);
            if (successful)
            {

            }
            else
            {

            }
        }
    }
    private class CEquipmentsComplianceTask extends AsyncTask<Void, Void, Boolean>
    {
        @Override
        protected void onPreExecute()
        {
            Log.d("CEQUIPSCOMPLIANCE:","getting compliance....");
            super.onPreExecute();
        }
        @Override
        protected Boolean doInBackground(Void... params)
        {
            //getting columns list
            List<NameValuePair>info=new ArrayList<NameValuePair>(); //info for staff count
            info.add(new BasicNameValuePair("id",Integer.toString(contractorAccount.getId())));
            // making HTTP request
            JSONObject jsonObject= jsonParser.makeHttpRequest(url_get_equipments_compliance,"POST",info);
            Log.d("allComplainceequips",""+jsonObject.toString());
            try
            {
                int success=jsonObject.getInt(TAG_SUCCESS);
                if(success==1)
                {
                    cGlobalInfoEquips.setEquipsCount(jsonObject.getInt("equipscount"));
                    cGlobalInfoEquips.setStaffCount(jsonObject.getInt("staffcount"));
                    cGlobalInfoEquips.setCompliant(jsonObject.getInt("compliant"));
                    cGlobalInfoEquips.setNoncompliant(jsonObject.getInt("noncompliant"));
                    JSONArray compArray=jsonObject.getJSONArray("compliantstaff");
                    LinkedHashMap<String,Character>tempCompliantStaff=new LinkedHashMap<>();
                    for(int count=0; count<compArray.length(); count+=1)
                    {
                        //add the staff member to the recylcer
                        String staff=compArray.getString(count);
                        String[] token=staff.split(":");
                        final String id=token[0];
                        final String userid=token[1];
                        final String trade=token[2];
                        final String names=token[3];
                        tempCompliantStaff.put(staff,'C');
                    }
                    cGlobalInfoEquips.setComplaintStaff(tempCompliantStaff);
                    JSONArray noncompArray=jsonObject.getJSONArray("noncompliantstaff");
                    LinkedHashMap<String,Character>tempNonCompliantStaff=new LinkedHashMap<>();
                    for(int count=0; count<noncompArray.length(); count+=1)
                    {
                        //add the staff member to the recylcer
                        String staff=noncompArray.getString(count);
                        String[] token=staff.split(":");
                        final String id=token[0];
                        final String userid=token[1];
                        final String trade=token[2];
                        final String names=token[3];
                        tempNonCompliantStaff.put(staff,'N');
                    }
                    cGlobalInfoEquips.setNonComplaintStaff(tempNonCompliantStaff);
                    cGlobalInfoEquips.setMissingQualifications(jsonObject.getInt("missingqualifications"));
                    cGlobalInfoEquips.setMissingCertificates(jsonObject.getInt("missingcertificates"));
                    cGlobalInfoEquips.setExpiredCertificates(jsonObject.getInt("expiredcertificates"));
                    cGlobalInfoEquips.setUsersWithMissingQualifications(jsonObject.getInt("staffwithmissingqualifications"));
                    cGlobalInfoEquips.setUsersWithMissingCertificates(jsonObject.getInt("staffwithmissingcertificates"));
                    cGlobalInfoEquips.setUserWithExpiredCertificates(jsonObject.getInt("staffwithexpiredcertificates"));
                    cGlobalInfoEquips.setUsersWithCase(jsonObject.getInt("staffwith_case"));
                    cGlobalInfoEquips.setUsersWithQCCase(jsonObject.getInt("staffwith_q_c_case"));
                    cGlobalInfoEquips.setUsersWithQECCase(jsonObject.getInt("staffwith_q_e_case"));
                    cGlobalInfoEquips.setUsersWithCECCase(jsonObject.getInt("staffwith_c_ec_case"));
                    cGlobalInfoEquips.setUsersWithQCECCase(jsonObject.getInt("staffwith_q_c_ec_case"));
                    JSONArray eachQualMissingArray=jsonObject.getJSONArray("each_qual_missing_count");
                    int []each_qual_missing_count=new int[eachQualMissingArray.length()];
                    for(int count=0; count<eachQualMissingArray.length(); count+=1)
                    {
                        each_qual_missing_count[count]=eachQualMissingArray.getInt(count);
                    }
                    cGlobalInfoEquips.setEachQualMissingCount(each_qual_missing_count);
                    JSONArray tableColumnsArray=jsonObject.getJSONArray("columnnames");
                    String[]tableColumns=new String[tableColumnsArray.length()];
                    for(int count=0; count<tableColumnsArray.length(); count+=1)
                    {
                        tableColumns[count]=tableColumnsArray.getString(count);
                    }
                    cGlobalInfoEquips.setTableColumns(tableColumns);
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
        protected void onPostExecute(final Boolean successful) {
            Log.d("CECOMPLIANCE: ","finished...."+"progress: "+String.valueOf(loginProgress));
            loginProgress+=1;
            if (loginProgress == cFinalProgress)
                stopService(intentLoginProgress);

            if (successful)
            {

            }
            else
            {

            }
        }
    }
    /**
     * Following code will get the boss notifications
     * The returned infos are id,  userid, classes, messages, dateadded.
     * Arguments are:
     * id==boss id.
     * Returns are:
     * success==1 successful get
     * success==0 for id argument missing
     **/
    private class CNotificationsTask extends AsyncTask<Void, Void, Boolean>
    {
        @Override
        protected void onPreExecute()
        {
            Log.d("CNOTIFICATIONS: ","starting....");
            super.onPreExecute();
        }
        @Override
        protected Boolean doInBackground(Void... params)
        {
            //getting columns list
            List<NameValuePair>info=new ArrayList<NameValuePair>(); //info for staff count
            info.add(new BasicNameValuePair("id",Integer.toString(contractorAccount.getId())));
            // making HTTP request
            JSONObject jsonObject= jsonParser.makeHttpRequest(url_get_notifications,"POST",info);
            Log.d("cNotis",""+jsonObject.toString());
            try
            {
                JSONArray notisArrayList=null;
                int success=jsonObject.getInt(TAG_SUCCESS);
                if(success==1)
                {
                    notisArrayList=jsonObject.getJSONArray("notis");
                    for(int count=0; count<notisArrayList.length(); count+=1)
                    {
                        JSONObject jsonObjectNotis=notisArrayList.getJSONObject(count);
                        int id=jsonObjectNotis.getInt("id");
                        int userid=jsonObjectNotis.getInt("userid");
                        int classes=jsonObjectNotis.getInt("classes");
                        String message=jsonObjectNotis.getString("messages");
                        String date=jsonObjectNotis.getString("dateadded");
                        CNotifications oneCNotifications=new CNotifications(id,userid,classes,message,date);
                        cNotificationsList.put(String.valueOf(id),oneCNotifications);
                    }
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
        protected void onPostExecute(final Boolean successful) {
            Log.d("CNOTIFICATIONS: ","finished...."+"progress: "+String.valueOf(loginProgress));
            loginProgress+=1;
            if (loginProgress == cFinalProgress)
                stopService(intentLoginProgress);

            if (successful)
            {

            }
            else
            {

            }
        }
    }
    /**
     * Following code will all personnel tasks info from boss tasks table.
     * The returned columns are id, titles, descriptions, startings, endings, repetitions, locations, positions, geofence dateadded, datechanged.
     * Arguments are:
     * id==boss id.
     * Returns are:
     * tasks rows
     * success==1 successful get
     * success==0 for missing certificates info
     * success==0 for id argument missing
     **/
    private class CTasksTask extends AsyncTask<Void, Void, Boolean>
    {
        @Override
        protected void onPreExecute()
        {
            Log.d("CTASKS: ","starting....");
            if(!cTasksList.isEmpty())
                cTasksList.clear();
            super.onPreExecute();
        }
        @Override
        protected Boolean doInBackground(Void... params)
        {
            //getting columns list
            List<NameValuePair>info=new ArrayList<NameValuePair>(); //info for staff count
            info.add(new BasicNameValuePair("id",Integer.toString(contractorAccount.getId())));
            // making HTTP request
            JSONObject jsonObject= jsonParser.makeHttpRequest(url_get_tasks,"POST",info);
            Log.d("cTasks",""+jsonObject.toString());
            try
            {
                JSONArray notisArrayList=null;
                int success=jsonObject.getInt(TAG_SUCCESS);
                if(success==1)
                {
                    notisArrayList=jsonObject.getJSONArray("tasks");
                    for(int count=0; count<notisArrayList.length(); count+=1)
                    {
                        JSONObject jsonObjectNotis=notisArrayList.getJSONObject(count);
                        int id=jsonObjectNotis.getInt("id");
                        String title=jsonObjectNotis.getString("title");
                        String description=jsonObjectNotis.getString("description");
                        String starting=jsonObjectNotis.getString("starting");
                        String ending=jsonObjectNotis.getString("ending");
                        int repetition=jsonObjectNotis.getInt("repetition");
                        String location=jsonObjectNotis.getString("location");
                        String position=jsonObjectNotis.getString("position");
                        String geofence=jsonObjectNotis.getString("geofence");
                        String dateadded=jsonObjectNotis.getString("dateadded");
                        String datechanged=jsonObjectNotis.getString("datechanged");
                        int pending=jsonObjectNotis.getInt("p");
                        int inProgress=jsonObjectNotis.getInt("i");
                        int completed=jsonObjectNotis.getInt("c");
                        int overdue=jsonObjectNotis.getInt("o");
                        int late=jsonObjectNotis.getInt("l");
                        String pendingIds=jsonObjectNotis.getString("pids");
                        String inProgressIds=jsonObjectNotis.getString("inids");
                        String completedIds=jsonObjectNotis.getString("cids");
                        String overdueIds=jsonObjectNotis.getString("oids");
                        String lateIds=jsonObjectNotis.getString("lids");


                        CTasks cTasks=new CTasks(id,title,description,starting,ending,repetition,location,position,geofence,dateadded,datechanged,pending,inProgress,completed,overdue,late,pendingIds,inProgressIds,completedIds,overdueIds,lateIds);
                        cTasksList.add(cTasks);
                    }
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
        protected void onPostExecute(final Boolean successful) {
            Log.d("CTASKS: ","finished...."+"progress: "+String.valueOf(loginProgress));
            loginProgress+=1;
            if (loginProgress == cFinalProgress)
                stopService(intentLoginProgress);

            if (successful)
            {

            }
            else
            {

            }
        }
    }
    private class CReviewsTask extends AsyncTask<Void, Void, Boolean>
    {
        @Override
        protected void onPreExecute()
        {
            Log.d("CREVIEWS: ","starting....");
            if(!cReviewsList.isEmpty())
                cReviewsList.clear();
            super.onPreExecute();
        }
        @Override
        protected Boolean doInBackground(Void... params)
        {
            //getting columns list
            List<NameValuePair>info=new ArrayList<NameValuePair>(); //info for staff count
            info.add(new BasicNameValuePair("id",Integer.toString(contractorAccount.getId())));
            // making HTTP request
            JSONObject jsonObject= jsonParser.makeHttpRequest(url_get_reviews,"POST",info);
            Log.d("cReviews",""+jsonObject.toString());
            try
            {
                JSONArray reviewsArrayList=null;
                int success=jsonObject.getInt(TAG_SUCCESS);
                if(success==1)
                {
                    reviewsArrayList=jsonObject.getJSONArray("reviews");
                    for(int count=0; count<reviewsArrayList.length(); count+=1)
                    {
                        JSONObject jsonObjectReviews=reviewsArrayList.getJSONObject(count);

                        int id=jsonObjectReviews.getInt("id");
                        int userid=jsonObjectReviews.getInt("userid");
                        int classes=jsonObjectReviews.getInt("classes");
                        String reviewer=jsonObjectReviews.getString("reviewer");
                        String review=jsonObjectReviews.getString("review");
                        String toimprove=jsonObjectReviews.getString("toimprove");
                        String s_rating=jsonObjectReviews.getString("rating");
                        int rating=0;
                        if(!(s_rating.contentEquals("NULL") ||s_rating.contentEquals("null") || s_rating.contentEquals("") ))
                            rating=Integer.parseInt(s_rating);
                        int themonth=jsonObjectReviews.getInt("themonth");
                        int theyear=jsonObjectReviews.getInt("theyear");
                        String dateadded=jsonObjectReviews.getString("dateadded");
                        String datechanged=jsonObjectReviews.getString("datechanged");

                        CReviews cReviews=new CReviews(id,userid,classes,reviewer,review,toimprove,rating,themonth,theyear,dateadded,datechanged);
                        cReviewsList.add(cReviews);
                    }
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
        protected void onPostExecute(final Boolean successful) {
            Log.d("CREVIEWS: ","finished...."+"progress: "+String.valueOf(loginProgress));
            loginProgress+=1;
            if (loginProgress == cFinalProgress)
                stopService(intentLoginProgress);

            if (successful)
            {

            }
            else
            {

            }
        }
    }


}

