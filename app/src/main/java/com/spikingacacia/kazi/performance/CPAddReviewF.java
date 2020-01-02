package com.spikingacacia.kazi.performance;

import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.spikingacacia.kazi.JSONParser;
import com.spikingacacia.kazi.LoginActivity;
import com.spikingacacia.kazi.R;
import com.spikingacacia.kazi.database.CReviews;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.spikingacacia.kazi.LoginActivity.base_url;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CPAddReviewF.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CPAddReviewF#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CPAddReviewF extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_REVIEW_ID = "review_id";
    private static final String ARG_REVIEW_INDEX = "review_index";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private String url_update_review=base_url+"update_review.php";
    private String TAG_SUCCESS="success";
    private String TAG_MESSAGE="message";
    private JSONParser jsonParser;

    private OnFragmentInteractionListener mListener;
    private TextView t_reviewer;
    private EditText e_review;
    private EditText e_to_improve;
    private RatingBar rating_bar;
    private int reviewIndex;
    private int reviewId;

    public CPAddReviewF() {
        // Required empty public constructor
    }
    public static CPAddReviewF newInstance(int reviewId, int reviewIndex) {
        CPAddReviewF fragment = new CPAddReviewF();
        Bundle args = new Bundle();
        args.putInt(ARG_REVIEW_ID, reviewId);
        args.putInt(ARG_REVIEW_INDEX, reviewIndex);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            reviewId = getArguments().getInt(ARG_REVIEW_ID);
            reviewIndex = getArguments().getInt(ARG_REVIEW_INDEX);
        }
        jsonParser=new JSONParser();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view= inflater.inflate(R.layout.f_cpadd_review, container, false);
        t_reviewer=view.findViewById(R.id.reviewer);
        e_review=view.findViewById(R.id.review);
        e_to_improve=view.findViewById(R.id.to_improve);
        rating_bar=view.findViewById(R.id.rating_bar);
        rating_bar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                int rate=(int)v;
                ((TextView)view.findViewById(R.id.rating)).setText(String.format("%d/5",rate));
            }
        });
        ((Button)view.findViewById(R.id.update)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onUpdateClicked();
            }
        });
        return view;
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
    public void onResume()
    {
        super.onResume();
        setReview();
    }
    public interface OnFragmentInteractionListener
    {
        void onReviewUpdated(int id);
    }
    private void setReview()
    {
        if(CPerformanceA.reviewer.contentEquals("manager"))
        {
            final android.app.AlertDialog dialog;
            android.app.AlertDialog.Builder builderPass=new android.app.AlertDialog.Builder(getContext());
            builderPass.setTitle("Enter the reviwer's names");
            TextInputLayout textInputLayout=new TextInputLayout(getContext());
            textInputLayout.setPadding(10,0,10,0);
            textInputLayout.setGravity(Gravity.CENTER);
            final EditText editText=new EditText(getContext());
            editText.setPadding(20,10,20,10);
            editText.setTextSize(14);
            textInputLayout.addView(editText,0,new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
            editText.setHint("Names");
            editText.setError(null);
            LinearLayout layout=new LinearLayout(getContext());
            layout.setOrientation(LinearLayout.VERTICAL);
            layout.addView(textInputLayout);
            builderPass.setView(layout);
            builderPass.setPositiveButton("Proceed", null);
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
                                CPerformanceA.reviewer=name;
                                t_reviewer.setText(CPerformanceA.reviewer);
                                dialog.dismiss();
                            }
                        }
                    });
                }
            });
            dialog.show();
        }
        else
            t_reviewer.setText(CPerformanceA.reviewer);
    }
    private void onUpdateClicked()
    {
        String review,to_improve;
        int rating;
        review=e_review.getText().toString();
        to_improve=e_to_improve.getText().toString();
        rating=(int)rating_bar.getRating();
        if(review.isEmpty() || review.length()<5)
        {
            //Toast.makeText(getContext(),"review is too short",Toast.LENGTH_SHORT).show();
            e_review.setError("review is too short");
            return;
        }
        else if(to_improve.isEmpty() || to_improve.length()<5)
        {
            e_to_improve.setError("where to improve is too short");
            return;
        }
        else if(rating==0)
        {
            Toast.makeText(getContext(),"Please give a rating",Toast.LENGTH_SHORT).show();
            return;
        }
        new UpdateReviewTask(review,to_improve,rating).execute((Void)null);
    }
    public class UpdateReviewTask extends AsyncTask<Void, Void, Boolean>
    {
        private int success=0;
        private String mReview;
        private String mToImprove;
        private int mRating;
        UpdateReviewTask( String review, String toImprove, int rating)
        {
            mReview=review;
            mToImprove=toImprove;
            mRating=rating;
        }
        @Override
        protected void onPreExecute()
        {
            Log.d("UPDATING REVIEW: ","updating....");
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            //logIn=handler.LogInStaff(mEmail,mPassword);
            //building parameters
            List<NameValuePair> info=new ArrayList<NameValuePair>();
            info.add(new BasicNameValuePair("bossid",Integer.toString(LoginActivity.contractorAccount.getId())));
            info.add(new BasicNameValuePair("id",String.valueOf(reviewId)));
            info.add(new BasicNameValuePair("classes","1"));
            info.add(new BasicNameValuePair("reviewer", CPerformanceA.reviewer));
            info.add(new BasicNameValuePair("review",mReview));
            info.add(new BasicNameValuePair("toimprove",mToImprove));
            info.add(new BasicNameValuePair("rating",String.valueOf(mRating)));
            //getting all account details by making HTTP request
            JSONObject jsonObject= jsonParser.makeHttpRequest(url_update_review,"POST",info);
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
        protected void onPostExecute(final Boolean successful) {
            Log.d("UPDATING REVIEW: ","finished....");
            if (successful)
            {
                Toast.makeText(getContext(),"Updated Successfully",Toast.LENGTH_SHORT).show();
                int count=0;
                Iterator<CReviews> iterator= LoginActivity.cReviewsList.iterator();
                while (iterator.hasNext())
                {
                    CReviews cReviews = iterator.next();
                    int id=cReviews.getId();
                    if(id==reviewId)
                    {
                        //update the review in the cReviewList
                        cReviews.setClasses(1);
                        cReviews.setReviewer(CPerformanceA.reviewer);
                        cReviews.setReview(mReview);
                        cReviews.setToImprove(mToImprove);
                        cReviews.setRating(mRating);
                        LoginActivity.cReviewsList.set(count,cReviews);
                        //remove the item from pendingReviews
                    }
                }
                mListener.onReviewUpdated(reviewIndex);
            }
            else
            {
                Toast.makeText(getContext(),"There was an error. Please try again.",Toast.LENGTH_SHORT).show();
            }
        }

    }
}
