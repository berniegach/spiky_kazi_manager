package com.spikingacacia.kazi.requirements;

import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import com.google.android.material.textfield.TextInputLayout;
import androidx.fragment.app.Fragment;
import androidx.core.view.MenuItemCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.spikingacacia.kazi.JSONParser;
import com.spikingacacia.kazi.LoginActivity;
import com.spikingacacia.kazi.R;
import com.spikingacacia.kazi.requirements.CRSQualiFContent.Item;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CRSQualiF extends Fragment
{

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;
    private String url_add_quali= LoginActivity.base_url+"create_qualification.php";
    private JSONParser jsonParser;
    private String TAG_SUCCESS="success";
    private String TAG_MESSAGE="message";
    private  RecyclerView recyclerView;

    public CRSQualiF()
    {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static CRSQualiF newInstance(int columnCount)
    {
        CRSQualiF fragment = new CRSQualiF();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
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
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
        jsonParser=new JSONParser();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.f_crsquali_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView)
        {
            Context context = view.getContext();
            recyclerView = (RecyclerView) view;
            recyclerView.addItemDecoration(new DividerItemDecoration(getContext(),DividerItemDecoration.VERTICAL));
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            CRSQualiFContent content=new CRSQualiFContent();
            recyclerView.setAdapter(new CRSQualiRecyclerViewAdapter(content.ITEMS, mListener,getContext()));
        }
        return view;
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu,inflater);
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.crsquali_menu, menu);
        final MenuItem add=menu.findItem(R.id.action_add);
        add.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener()
        {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem)
            {
                new AlertDialog.Builder(getContext())
                        .setItems(new String[]{"Mandatory", "Job Specific"}, new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i)
                            {
                                final int which=i;
                                final android.app.AlertDialog dialog;
                                android.app.AlertDialog.Builder builderPass=new android.app.AlertDialog.Builder(getContext());
                                builderPass.setTitle("Name?");
                                TextInputLayout textInputLayout=new TextInputLayout(getContext());
                                textInputLayout.setPadding(10,0,10,0);
                                textInputLayout.setGravity(Gravity.CENTER);
                                final EditText editText=new EditText(getContext());
                                editText.setPadding(20,10,20,10);
                                editText.setTextSize(14);
                                textInputLayout.addView(editText,0,new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
                                editText.setHint("New Qualification");
                                editText.setError(null);
                                editText.setSingleLine(true);
                                LinearLayout layout=new LinearLayout(getContext());
                                layout.setOrientation(LinearLayout.VERTICAL);
                                layout.addView(textInputLayout);
                                builderPass.setView(layout);
                                builderPass.setPositiveButton("Add", null);
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
                                        Button button=((android.app.AlertDialog)dialog).getButton(android.app.AlertDialog.BUTTON_POSITIVE);
                                        button.setOnClickListener(new View.OnClickListener()
                                        {
                                            @Override
                                            public void onClick(View view)
                                            {
                                                String name=editText.getText().toString();
                                                if(name.length()<3)
                                                {
                                                    editText.setError("Name too short");
                                                }

                                                else
                                                {
                                                    new CreateQualiTask(name,which==0?"m":"j").execute((Void)null);
                                                    dialog.dismiss();
                                                }
                                            }
                                        });
                                    }
                                });
                                dialog.show();

                            }
                        }).create().show();

                return true;
            }
        });

        final MenuItem searchItem=menu.findItem(R.id.action_search);
        final SearchView searchView=(SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener()
        {
            @Override
            public boolean onQueryTextSubmit(String query)
            {
                CRSQualiRecyclerViewAdapter adapter=(CRSQualiRecyclerViewAdapter) recyclerView.getAdapter();
                adapter.filter(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText)
            {
                CRSQualiRecyclerViewAdapter adapter=(CRSQualiRecyclerViewAdapter) recyclerView.getAdapter();
                adapter.filter(newText);
                return true;
            }
        });
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
        void onListFragmentInteraction(Item item);
    }
    public class CreateQualiTask extends AsyncTask<Void, Void, Boolean>
    {
        private String name;
        private String mWhich;
        private int success;
        CreateQualiTask(final String name,final String which)
        {
            Toast.makeText(getContext(),"Adding qualification started. Please wait...",Toast.LENGTH_SHORT).show();            String name_temp=name.toLowerCase();
            name_temp=name_temp.replace(" ","_");
            this.name=name_temp;
            mWhich=which;
            Log.d("CRATEQUALI"," started...");
        }
        @Override
        protected Boolean doInBackground(Void... params)
        {
            //building parameters
            List<NameValuePair>info=new ArrayList<NameValuePair>();
            info.add(new BasicNameValuePair("id",Integer.toString(LoginActivity.contractorAccount.getId())));
            info.add(new BasicNameValuePair("name",name));
            info.add(new BasicNameValuePair("which",mWhich));
            JSONObject jsonObject= jsonParser.makeHttpRequest(url_add_quali,"POST",info);
            try
            {
                success=jsonObject.getInt(TAG_SUCCESS);
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
        protected void onPostExecute(final Boolean successful)
        {
            if(successful)
            {

                Log.d("adding new quali", "done...");
                Toast.makeText(getContext(),"Successful",Toast.LENGTH_SHORT).show();
                CRSQualiRecyclerViewAdapter adapter=(CRSQualiRecyclerViewAdapter) recyclerView.getAdapter();
                LoginActivity.personnelColumnsList.put(name,mWhich.charAt(0));
                adapter.notifyChange(LoginActivity.personnelColumnsList.size()+1,name,mWhich.charAt(0));

            }
            else if(success==-2)
            {
                Log.e("settings permissions", "error");
                Toast.makeText(getContext(),"qualification already defined",Toast.LENGTH_SHORT).show();
            }

        }
    }
}
