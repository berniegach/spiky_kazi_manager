package com.spikingacacia.kazi;

import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.spikingacacia.kazi.database.Permissions;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class peoplepFragment extends Fragment
{

    // TODO: Customize parameter argument names
    private static final String ARG_WHO = "who";
    // TODO: Customize parameters
    public static int mWho = 1;
    private OnListFragmentInteractionListener mListener;
    private String url_find_account= LoginActivity.base_url+"find_account.php";
    private String TAG_SUCCESS="success";
    private String TAG_MESSAGE="message";
    JSONParser jsonParser;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public peoplepFragment()
    {
    }
    public static peoplepFragment newInstance(int who)
    {
        peoplepFragment fragment = new peoplepFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_WHO, who);
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
            mWho = getArguments().getInt(ARG_WHO);
        }
        jsonParser=new JSONParser();

    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.menu_c_peoplepfragment, menu);
        super.onCreateOptionsMenu(menu,inflater);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch(item.getItemId())
        {
            case R.id.action_add:
                add();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.f_peoplep_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView)
        {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            recyclerView.addItemDecoration(new DividerItemDecoration(getContext(),DividerItemDecoration.VERTICAL));
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            CPermissionsContent content=new CPermissionsContent();
            recyclerView.setAdapter(new MypeoplepRecyclerViewAdapter(content.ITEMS, mListener,getContext()));
        }
        return view;
    }


    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener)
        {
            mListener = (OnListFragmentInteractionListener) context;
        } else
        {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
        mListener = null;
    }
    public interface OnListFragmentInteractionListener
    {
        // TODO: Update argument type and name
       // void onListFragmentInteraction(PermissionItem item);
        void onPermissionsAdded();
    }
    private void add()
    {
        final int person=1;
        int manner=1;
        new AlertDialog.Builder(getContext())
                .setTitle("Choose the persona")
                .setItems(new String[]{"Contractor", "General User"}, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int p)
                    {
                        final int fp=p;
                        new AlertDialog.Builder(getContext())
                                .setItems(new String[]{"Email address", "QR Code"}, new DialogInterface.OnClickListener()
                                {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int m)
                                    {
                                        final int fm=m;
                                        if(m==0)
                                        {
                                            final EditText editText=new EditText(getContext());
                                            editText.setHint("someone@email.com");
                                            editText.setError(null);
                                            //dialog
                                            final AlertDialog dialog;
                                            final AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
                                                    builder.setTitle("Enter the email")
                                                            .setView(editText)
                                                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener()
                                                            {
                                                                @Override
                                                                public void onClick(DialogInterface dialogInterface, int i)
                                                                {
                                                                    dialogInterface.dismiss();
                                                                }
                                                            })
                                                            .setPositiveButton("Ok",null);
                                            dialog=builder.create();
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
                                                                    String email=editText.getText().toString();
                                                                    if(!email.contains("@"))
                                                                        editText.setError("Not a valid email");
                                                                    else if(email.contentEquals(LoginActivity.contractorAccount.getEmail()))
                                                                        editText.setError("This is your email");
                                                                    else
                                                                    {
                                                                        new AddContractor(fp,fm,email,0).execute((Void)null);
                                                                        dialog.dismiss();
                                                                    }
                                                                }
                                                            });
                                                        }
                                                    });
                                            dialog.show();
                                        }
                                    }
                                }).create().show();
                    }
                }).create().show();
    }
    public class AddContractor extends AsyncTask<Void, Void, Boolean>
    {

        private final int mPersona;
        private final int mManner;
        private final String mEmail;
        private final int mId;
        private final String type;
        private int success=0;

        AddContractor(int persona,int manner,String email, int id)
        {

            mPersona=persona;
            mManner=manner;
            mEmail = email;
            mId=id;
            if(mWho==1)
                type="E";
            else if(mWho==2)
                type="V";
            else
                type="A";

        }
        @Override
        protected void onPreExecute()
        {
            Log.d("UPDATE: ","updating permission......");
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            //building parameters
            List<NameValuePair>info=new ArrayList<NameValuePair>();
            info.add(new BasicNameValuePair("persona",Integer.toString(mPersona)));
            info.add(new BasicNameValuePair("manner",Integer.toString(mManner)));
            info.add(new BasicNameValuePair("email",mEmail));
            info.add(new BasicNameValuePair("id",Integer.toString(mId)));
            //getting all account details by making HTTP request
            JSONObject jsonObject= jsonParser.makeHttpRequest(url_find_account,"POST",info);
            Log.d("JSON SEND",info.toString());
            Log.d("JSON",jsonObject.toString());
            try
            {
                success=jsonObject.getInt(TAG_SUCCESS);
                if(success==1)
                {
                    /* the format follows the following
                     * 1. 0/1  0 for permissions given 1 for permissions to others
                     * 2. C/U  C for contractor , U for general user
                     * 3. E/V/A  E for edit tasks, V for view compliance,  A for add data
                     * 4  the persons id*/
                    JSONArray infoArray=jsonObject.getJSONArray("info");
                    JSONObject infoObject=infoArray.getJSONObject(0);

                    final int id=infoObject.getInt("id");
                    final String email=infoObject.getString("email");
                    final String username=infoObject.getString("username");
                    final String company=infoObject.getString("company");
                    final String position=infoObject.getString("position");
                    //seccesful
                    if(mPersona==0)
                    {


                        if(CSettingsActivity.tempContractorAccount.getPermissions().contentEquals("null"))
                        {
                            String permit=String.format("%d,%s,%s,%d:",1,"C",type,id);
                            if(CSettingsActivity.tempContractorAccount.getPermissions().contains(permit))
                            {
                                success=-3;
                                return false;
                            }
                            CSettingsActivity.tempContractorAccount.setPermissions(permit);
                            CSettingsActivity.settingsChanged=true;
                            CSettingsActivity.permissionsChanged=true;
                            //add permissions to changed
                            CSettingsActivity.permissions+=String.format("%d,%d,%s,%s,%d:",LoginActivity.contractorAccount.getId(),1,"C",type,id);
                            //add to the class
                            LoginActivity.permissionsList.add(new Permissions(1, 'C', type.charAt(0),id, email, username, company, position));
                        }
                        else
                        {
                            String permit=String.format("%d,%s,%s,%d:",1,"C",type,id);
                            if(CSettingsActivity.tempContractorAccount.getPermissions().contains(permit))
                            {
                                success=-3;
                                return false;
                            }
                            String permit_intial=CSettingsActivity.tempContractorAccount.getPermissions();
                            CSettingsActivity.tempContractorAccount.setPermissions(permit_intial+permit);
                            CSettingsActivity.settingsChanged=true;
                            CSettingsActivity.permissionsChanged=true;
                            //add permissions to changed
                            CSettingsActivity.permissions+=String.format("%d,%d,%s,%s,%d:",LoginActivity.contractorAccount.getId(),1,"C",type,id);
                            //add to the class
                            LoginActivity.permissionsList.add(new Permissions(1, 'C', type.charAt(0),id, email, username, company, position));
                        }
                    }
                    else
                    {
                        if(CSettingsActivity.tempContractorAccount.getPermissions().contentEquals("null"))
                        {
                            String permit=String.format("%d,%s,%s,%d:",1,"U",type,id);
                            if(CSettingsActivity.tempContractorAccount.getPermissions().contains(permit))
                            {
                                success=-3;
                                return false;
                            }
                            CSettingsActivity.tempContractorAccount.setPermissions(permit);
                            CSettingsActivity.settingsChanged=true;
                            CSettingsActivity.permissionsChanged=true;
                            //add permissions to changed
                            CSettingsActivity.permissions+=String.format("%d,%d,%s,%s,%d:",LoginActivity.contractorAccount.getId(),1,"U",type,id);
                            //add to the class
                            LoginActivity.permissionsList.add(new Permissions(1, 'U', type.charAt(0),id, email, username, company, position));
                        }
                        else
                        {
                            String permit=String.format("%d,%s,%s,%d:",1,"U",type,id);
                            if(CSettingsActivity.tempContractorAccount.getPermissions().contains(permit))
                            {
                                success=-3;
                                return false;
                            }
                            String permit_intial=CSettingsActivity.tempContractorAccount.getPermissions();
                            CSettingsActivity.tempContractorAccount.setPermissions(permit_intial+permit);
                            CSettingsActivity.settingsChanged=true;
                            CSettingsActivity.permissionsChanged=true;
                            //add permissions to changed
                            CSettingsActivity.permissions+=String.format("%d,%d,%s,%s,%d:",LoginActivity.contractorAccount.getId(),1,"U",type,id);
                            //add to the class
                            LoginActivity.permissionsList.add(new Permissions(1, 'U', type.charAt(0),id, email, username, company, position));
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

            if (successful)
            {
                if(mListener!=null)
                    mListener.onPermissionsAdded();
            }
            else
            {
                if (success==-1) {
                    Toast.makeText(getContext(),"Not found",Toast.LENGTH_LONG).show();
                }
                else if(success==-3)
                    Toast.makeText(getContext(),"Permissions already granted",Toast.LENGTH_SHORT).show();

            }
        }
        @Override
        protected void onCancelled()
        {
            Toast.makeText(getContext(),"Cancelled.",Toast.LENGTH_SHORT).show();
        }
    }

    private void addUser(String email,int id)
    {

    }
}
