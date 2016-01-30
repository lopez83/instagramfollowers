package com.olopez.instagramfollowers.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.olopez.instagramfollowers.R;

public class InsightsFragment extends Fragment {

    private TextView textViewVisits;
    private TextView textViewTimeInBusiness;

    private ProgressBar progressSpinner;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_insights, null);


        textViewVisits = (TextView) view.findViewById(R.id.textViewVisits);
        //textViewVisits.setTypeface(We2Application.font);

        textViewTimeInBusiness = (TextView) view.findViewById(R.id.textViewTimeInBusiness);
        //textViewTimeInBusiness.setTypeface(We2Application.font);

        progressSpinner = (ProgressBar) view.findViewById(R.id.progressSpinner);

        return view;
    }
  
    private LinearLayout.LayoutParams lp;

    @Override
    public void onPause() {
        super.onPause();
    }
}