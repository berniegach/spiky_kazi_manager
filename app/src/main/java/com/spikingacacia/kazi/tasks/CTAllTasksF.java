package com.spikingacacia.kazi.tasks;

import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.spikingacacia.kazi.R;
import com.spikingacacia.kazi.tasks.CTAllTasksContent.Task;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class CTAllTasksF extends Fragment
{

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    private static final String ARG_WHICH_TASK = "which-task";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private int mWhichTask=0;
    private OnListFragmentInteractionListener mListener;
    private  RecyclerView recyclerView;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public CTAllTasksF()
    {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static CTAllTasksF newInstance(int columnCount,int whichTask)
    {
        CTAllTasksF fragment = new CTAllTasksF();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        args.putInt(ARG_WHICH_TASK,whichTask);
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
            mWhichTask=getArguments().getInt(ARG_WHICH_TASK);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.f_ctall_tasks_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView)
        {
            Context context = view.getContext();
            recyclerView = (RecyclerView) view;
            recyclerView.addItemDecoration(new DividerItemDecoration(getContext(),DividerItemDecoration.VERTICAL));
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            CTAllTasksContent content=new CTAllTasksContent(mWhichTask);
            recyclerView.setAdapter(new CTAllTasksTaskRecyclerViewAdapter(content.ITEMS, mListener,getContext(),mWhichTask));
        }
        return view;
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.ctall_tasks_menu, menu);
        final MenuItem calender=menu.findItem(R.id.action_calender);
        calender.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener()
        {
            @Override
            public boolean onMenuItemClick(MenuItem item)
            {
                if(mListener!=null)
                    mListener.onCalenderClicked();
                return false;
            }
        });
        final MenuItem searchItem=menu.findItem(R.id.action_search);
        final SearchView searchView=(SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener()
        {
            @Override
            public boolean onQueryTextSubmit(String query)
            {
                CTAllTasksTaskRecyclerViewAdapter adapter=(CTAllTasksTaskRecyclerViewAdapter) recyclerView.getAdapter();
                adapter.filter(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText)
            {
                CTAllTasksTaskRecyclerViewAdapter adapter=(CTAllTasksTaskRecyclerViewAdapter) recyclerView.getAdapter();
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
        void onTaskItemClicked(Task item);
        void onCalenderClicked();
    }
}
