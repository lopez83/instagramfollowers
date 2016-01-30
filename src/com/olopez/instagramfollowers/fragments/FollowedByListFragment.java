package com.olopez.instagramfollowers.fragments;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.olopez.instagramfollowers.MainActivity;
import com.olopez.instagramfollowers.R;
import com.olopez.instagramfollowers.adapters.UsersListAdapter;
import com.olopez.instagramfollowers.model.User;

public class FollowedByListFragment extends Fragment {

	public static final String TAG = "FollowedByListFragment";
	UsersListAdapter adapter;
	private ListView usersList;
	private ArrayList<User> usersFollowing;

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

		//people following me
		retrieveFollowingList();
		adapter.setData(usersFollowing);

		usersList.setAdapter(adapter);
		adapter.notifyDataSetChanged();

		setRetainInstance(true);
	}

	private void retrieveFollowingList() {
		usersFollowing = ((MainActivity) getActivity()).getFollowing();
	}

	public UsersListAdapter getAdapter() {
		return adapter;
	}

	public ListView getListView() {
		return usersList;
	}
}