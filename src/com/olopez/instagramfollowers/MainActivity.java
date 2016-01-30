package com.olopez.instagramfollowers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.olopez.instagramfollowers.fragments.CountersFragment;
import com.olopez.instagramfollowers.fragments.CountersFragment.OnCountersListFragmentListener;
import com.olopez.instagramfollowers.fragments.FollowedByListFragment;
import com.olopez.instagramfollowers.fragments.UsersListFragment;
import com.olopez.instagramfollowers.model.Counter;
import com.olopez.instagramfollowers.model.User;
import com.olopez.instagramlib.Instagram;
import com.olopez.instagramlib.InstagramSession;
import com.olopez.instagramlib.InstagramUser;


public class MainActivity extends FragmentActivity implements
		OnCountersListFragmentListener {
	private InstagramSession mInstagramSession;
	private Instagram mInstagram;

	private ProgressBar mLoadingPb;

	private FragmentManager fragmentManager;
	private SharedPreferences prefs;
	private InstagramUser instagramUser;

	private CountersFragment countersFragment;
	private FollowedByListFragment followedByFragment;
	private UsersListFragment followingFragment;

	private ArrayList<User> following;
	private ArrayList<User> followers;
	private ArrayList<User> areNotFollowingBack;
	private ArrayList<User> youNotFollowingBack;
	private String userID;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		prefs = getSharedPreferences(ApplicationData.PREFERENCES,
				Context.MODE_PRIVATE);

		mInstagram = new Instagram(this, ApplicationData.CLIENT_ID,
				ApplicationData.CLIENT_SECRET, ApplicationData.REDIRECT_URI);

		mInstagramSession = mInstagram.getSession();
		instagramUser = mInstagramSession.getUser();

		Editor editor = prefs.edit();
		editor.putString(ApplicationData.KEY_SESSION_TOKEN,
				mInstagramSession.getAccessToken());
		editor.putString(ApplicationData.KEY_USER_ID, instagramUser.id);
		editor.commit();

		setContentView(R.layout.activity_main);

		fragmentManager = getSupportFragmentManager();

		countersFragment = (CountersFragment) fragmentManager
				.findFragmentByTag(CountersFragment.TAG);
/*
		DisplayImageOptions displayOptions = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.ic_user)
				.showImageForEmptyUri(R.drawable.ic_user)
				.showImageOnFail(R.drawable.ic_user).cacheInMemory(true)
				.cacheOnDisc(false).considerExifParams(true).build();

		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				this).writeDebugLogs()
				.defaultDisplayImageOptions(displayOptions).build();

		ImageLoader imageLoader = ImageLoader.getInstance();
		imageLoader.init(config);
*/
		Bundle args = new Bundle();
		userID = prefs.getString(ApplicationData.KEY_USER_ID, "");
		args.putString(ApplicationData.KEY_USER_ID, userID);

		if (countersFragment == null) {
			countersFragment = new CountersFragment();

			countersFragment.setArguments(args);
			fragmentManager
					.beginTransaction()
					.add(R.id.content_frame, countersFragment,
							CountersFragment.TAG).commit();
		}

		if (followedByFragment == null) {
			followedByFragment = new FollowedByListFragment();
		}

		if (followingFragment == null) {
			followingFragment = new UsersListFragment();
		}

		showFragments();

	}

	@Override
	public void onCountersListClickListener(Counter item, int position) {

		switch (position) {
		case 0:
			showFollowersFragment();
			break;
		case 1:
			showFollowingFragment();
			break;
		case 2:
			// showFollowedByFragment();
			break;
		default:
			// showFollowedByFragment();
			break;
		}
	}

	@Override
	public void onCountersListResume(ArrayList<Counter> counters) {

	}

	static class AnimateFirstDisplayListener extends SimpleImageLoadingListener {

		static final List<String> displayedImages = Collections
				.synchronizedList(new LinkedList<String>());

		@Override
		public void onLoadingComplete(String imageUri, View view,
				Bitmap loadedImage) {
			if (loadedImage != null) {
				ImageView imageView = (ImageView) view;
				boolean firstDisplay = !displayedImages.contains(imageUri);
				if (firstDisplay) {
					FadeInBitmapDisplayer.animate(imageView, 500);
					displayedImages.add(imageUri);
				}
			}
		}
	}

	private void showHome() {
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		fragmentManager.popBackStack(null,
				FragmentManager.POP_BACK_STACK_INCLUSIVE);
		ft.replace(R.id.content_frame, countersFragment).commit();
	}

	private void showFollowersFragment() {
		showUsersFragment(ApplicationData.KEY_FOLLOWERS);
	}

	private void showFollowingFragment() {
		showUsersFragment(ApplicationData.KEY_FOLLOWING);
	}

	private void showUsersFragment(String listType) {

		FragmentManager manager = getSupportFragmentManager();
		FragmentTransaction fts = manager.beginTransaction();

		if (listType == ApplicationData.KEY_FOLLOWERS) {
			fts.add(R.id.content_frame, followedByFragment)
					.addToBackStack(UsersListFragment.TAG).commit();

		} else if (listType == ApplicationData.KEY_FOLLOWING) {
			fts.add(R.id.content_frame, followingFragment)
					.addToBackStack(UsersListFragment.TAG).commit();

		}
	}

	public void showFragments() {
		if (countersFragment.isAdded() && countersFragment.isVisible()) {
			getSupportFragmentManager().beginTransaction()
					.show(countersFragment).commit();
		} else if (followedByFragment.isAdded()
				&& followedByFragment.isVisible()) {
			getSupportFragmentManager().beginTransaction()
					.show(followedByFragment).commit();
		} else if (followingFragment.isAdded() && followingFragment.isVisible()) {
			getSupportFragmentManager().beginTransaction()
					.show(followingFragment).commit();
		}
	}

	@Override
	public void onShowCountersListFragment() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onDestroyCountersListFragment() {
		// TODO Auto-generated method stub

	}

	public ArrayList<User> getFollowing() {
		return following;
	}

	public void setFollowing(ArrayList<User> following) {
		this.following = following;
	//	reCalculateLists();
	}

	public ArrayList<User> getFollowers() {
		return followers;
	}

	public void setFollowers(ArrayList<User> followers) {
		this.followers = followers;
	//	reCalculateLists();
	}

	public InstagramUser getInstagramUser() {
		return instagramUser;
	}
	
	public ArrayList<User> getAreNotFollowingBack() {
		return areNotFollowingBack;
	}

	public void setAreNotFollowingBack(ArrayList<User> areNotFollowingBack) {
		this.areNotFollowingBack = areNotFollowingBack;
	}

	public ArrayList<User> getYouNotFollowingBack() {
		return youNotFollowingBack;
	}

	public void setYouNotFollowingBack(ArrayList<User> youNotFollowingBack) {
		this.youNotFollowingBack = youNotFollowingBack;
	}

	public void reCalculateLists(){
		if(following!=null && followers!=null){
			
			areNotFollowingBack = following;		
			areNotFollowingBack.removeAll(followers);
		
			youNotFollowingBack = followers;
			youNotFollowingBack.removeAll(following);
			Log.d("Main","areNotFollowingBack "+areNotFollowingBack.size()+" youNotFollowingBack"+youNotFollowingBack.size());
		}
	}

}