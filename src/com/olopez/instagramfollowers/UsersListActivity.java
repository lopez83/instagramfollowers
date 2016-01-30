package com.olopez.instagramfollowers;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.olopez.instagramfollowers.fragments.UsersListFragment;
import com.olopez.instagramfollowers.model.User;

public class UsersListActivity extends FragmentActivity {
	String screenName;
	SharedPreferences prefs;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		prefs = getSharedPreferences(ApplicationData.PREFERENCES,
				Context.MODE_PRIVATE);

		setContentView(R.layout.fragment_users_new);
		loadProfileInfo();
		setFragment();
	}

	public void populateProfileHeader(User u) {
		/*
		 * TextView tvName = (TextView) findViewById(R.id.tvName); TextView
		 * tvTagline = (TextView) findViewById(R.id.tvTagline); TextView
		 * tvFollowers = (TextView) findViewById(R.id.tvFollowers); TextView
		 * tvFollowing = (TextView) findViewById(R.id.tvFollowing); ImageView
		 * ivProfileImage = (ImageView) findViewById(R.id.ivProfileImage);
		 * 
		 * tvName.setText(u.getName()); tvTagline.setText(u.getTagline());
		 * tvFollowers.setText(u.getFollowersCount() + " Followers");
		 * tvFollowing.setText(u.getFriendsCount() + " Following");
		 * ImageLoader.getInstance().displayImage(u.getProfileImageUrl(),
		 * ivProfileImage);
		 */
	}

	public void loadProfileInfo() {
		Intent i = getIntent();

		/*
		 * MyTwitterApp.getRestClient().getUserInfo(screenName, new
		 * JsonHttpResponseHandler() {
		 * 
		 * @Override public void onSuccess(JSONObject jsonUser) { User u =
		 * User.fromJson(jsonUser); getActionBar().setTitle("@" +
		 * u.getScreenName()); populateProfileHeader(u); } });
		 */
	}

	public void setFragment() {
		FragmentManager manager = getSupportFragmentManager();
		FragmentTransaction fts = manager.beginTransaction();

		Bundle args = new Bundle();
		UsersListFragment mFrag = new UsersListFragment();

		String userId = prefs.getString(ApplicationData.KEY_USER_ID, "");
		args.putString(ApplicationData.KEY_USER_ID, userId);
		mFrag.setArguments(args);

		fts.replace(R.id.users_fragment, mFrag);
		fts.commit();

		/*
		 * if (screenName != null) { UserTimelineFragment frag = new
		 * UserTimelineFragment(); fts.replace(R.id.profile_frame_container,
		 * frag); fts.commit(); frag.setTimeline(screenName); } else {
		 * fts.replace(R.id.profile_frame_container, new MyTimelineFragment());
		 * fts.commit(); }
		 */
	}

}
