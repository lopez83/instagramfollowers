package com.olopez.instagramfollowers.fragments;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.olopez.instagramfollowers.MainActivity;
import com.olopez.instagramfollowers.R;
import com.olopez.instagramfollowers.adapters.UsersListAdapter;
import com.olopez.instagramfollowers.helpers.APIClient;
import com.olopez.instagramfollowers.model.User;

//import com.loopj.android.http.JsonHttpResponseHandler;

public class UsersListFragment extends Fragment {

	public static final String TAG = "UsersListFragment";
	private UsersListAdapter adapter;
	private ListView usersList;
	private ArrayList<User> usersFollowers;

	@Override
	public View onCreateView(LayoutInflater inf, ViewGroup parent,
			Bundle savedInstanceState) {

		View rootView = inf.inflate(R.layout.fragment_tweets_list, parent,
				false);
		usersList = (ListView) rootView.findViewById(R.id.lvTweets);
		adapter = new UsersListAdapter(getActivity());
		return rootView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		// people I'm following
		retrieveFollowersList();
		adapter.setData(usersFollowers);

		usersList.setAdapter(adapter);
		adapter.notifyDataSetChanged();

		setRetainInstance(true);
	}

	private void retrieveFollowersList() {
		usersFollowers = ((MainActivity) getActivity()).getFollowers();
	}

	public UsersListAdapter getAdapter() {
		return adapter;
	}

	public ListView getListView() {
		return usersList;
	}


}