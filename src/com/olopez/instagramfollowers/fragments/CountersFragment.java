package com.olopez.instagramfollowers.fragments;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
//import com.loopj.android.http.JsonHttpResponseHandler;
import com.olopez.instagramfollowers.ApplicationData;
import com.olopez.instagramfollowers.MainActivity;
import com.olopez.instagramfollowers.MyListActivity.AnimateFirstDisplayListener;
import com.olopez.instagramfollowers.R;
import com.olopez.instagramfollowers.adapters.CountersAdapter;
import com.olopez.instagramfollowers.helpers.APIClient;
import com.olopez.instagramfollowers.model.Counter;
import com.olopez.instagramfollowers.model.User;

public class CountersFragment extends Fragment implements OnItemClickListener {

	public static final String TAG = "CountersFragment";
	CountersAdapter adapter;
	private SharedPreferences prefs;
	private ArrayList<Counter> counters = new ArrayList<Counter>(4);
	private ListView mListView;
	private TextView tvName;
	private String userID;
	private OnCountersListFragmentListener onCountersListFragmentListener;
	private ArrayList<User> followers = new ArrayList<User>();
	private ArrayList<User> following = new ArrayList<User>();

	@Override
	public View onCreateView(LayoutInflater inf, ViewGroup parent,
			Bundle savedInstanceState) {

		View rootView = inf.inflate(R.layout.fragment_main_activity, parent,
				false);

		prefs = getActivity().getSharedPreferences(ApplicationData.PREFERENCES,
				Context.MODE_PRIVATE);

		userID = getArguments().getString(ApplicationData.KEY_USER_ID);
		mListView = (ListView) rootView.findViewById(R.id.countersListView);
		tvName = (TextView) rootView.findViewById(R.id.tv_name);
		mListView.setOnItemClickListener(this);

		Counter counterFollowersEmpty = new Counter(R.string.followers, 0);
		Counter counterFollowingEmpty = new Counter(R.string.following, 0);
		Counter counterAreNotFollowingEmpty = new Counter(
				R.string.arenotfollowingback, 0);
		Counter counterYouAreNotFollowingEmpty = new Counter(
				R.string.younotfollowingback, 0);
		if (counters.size() == 0) {
			counters.add(counterFollowersEmpty);
			counters.add(counterFollowingEmpty);
			counters.add(counterAreNotFollowingEmpty);
			counters.add(counterYouAreNotFollowingEmpty);
		}

		ImageView userIv = (ImageView) rootView.findViewById(R.id.iv_user);

		DisplayImageOptions displayOptions = new DisplayImageOptions.Builder()
				.displayer(new RoundedBitmapDisplayer(100))
				// .displayer(new RoundedBitmapDisplayer((int) 27.5f))
				.showStubImage(R.drawable.ic_user)
				.showImageForEmptyUri(R.drawable.ic_user)
				.showImageOnFail(R.drawable.ic_user).cacheInMemory(true)
				.cacheOnDisc().build();

		tvName.setText(((MainActivity) getActivity()).getInstagramUser().username);
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				getActivity()).writeDebugLogs()
				.defaultDisplayImageOptions(displayOptions).build();

		ImageLoader imageLoader = ImageLoader.getInstance();
		imageLoader.init(config);

		AnimateFirstDisplayListener animate = new AnimateFirstDisplayListener();

		imageLoader
				.displayImage(
						((MainActivity) getActivity()).getInstagramUser().profilPicture,
						userIv, animate);

		adapter = new CountersAdapter(getActivity());

		return rootView;
	}

	private void populateCountersOne() {
		ArrayList<User> usersFollowing = ((MainActivity) getActivity())
				.getFollowing();
		ArrayList<User> usersFollowers = ((MainActivity) getActivity())
				.getFollowers();
		prefs = getActivity().getSharedPreferences(ApplicationData.PREFERENCES,
				Context.MODE_PRIVATE);
		Editor editor = prefs.edit();
		editor.putInt(ApplicationData.KEY_FOLLOWERS, usersFollowers.size());
		editor.putInt(ApplicationData.KEY_FOLLOWING, usersFollowing.size());
		editor.commit();
	}

	private void populateFollowers(final String nextCursorIn) {

		RequestParams params = new RequestParams();

		if (nextCursorIn != null && !nextCursorIn.isEmpty()) {
			params.put("cursor", nextCursorIn);
		}

		APIClient.getFollowers(userID, params, new JsonHttpResponseHandler() {

			@Override
			public void onSuccess(JSONObject jsonResp) {
				try {

					JSONArray jsonData = jsonResp.getJSONArray("data");
					JSONObject jsonPagination = jsonResp
							.getJSONObject("pagination");
					boolean lastPage = true;
					if (jsonPagination != null) {
						if (jsonPagination.has("next_cursor")) {
							String nextCursor = jsonPagination
									.optString("next_cursor");
							if (!nextCursor.isEmpty()) {
								lastPage = false;
								populateFollowers(nextCursor);
							}
						}
					}

					Log.d(TAG,"Adding "+jsonData.length()+" users to followers list, followers size is "+followers.size());
					for (int i = 0; i < jsonData.length(); i++) {
						try {
							User user = new User((JSONObject) jsonData.get(i));
							user.setFollowedByMe(true);
							followers.add(user);
						} catch (Exception e) {
						}
					}

					Log.d(TAG, "followers size is " + followers.size());
					
					((MainActivity) getActivity()).setFollowers(followers);

					Counter counterFollowers = new Counter(R.string.followers,
							followers.size());
					counters.set(0, counterFollowers);
					adapter.notifyDataSetChanged();

					Log.d(TAG, "followers size2 is " + followers.size());
					
					if (lastPage) {
						updateCounters();
					}

				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onFailure(Throwable e, JSONObject error) {
				Log.e("ERROR", e.toString());
			}

		});
	}

	private void populateFollowing(final String nextCursorIn) {

		RequestParams params = new RequestParams();

		if (nextCursorIn != null && !nextCursorIn.isEmpty()) {
			params.put("cursor", nextCursorIn);
		}

		APIClient.getFollowing(userID, params, new JsonHttpResponseHandler() {

			@Override
			public void onSuccess(JSONObject jsonResp) {
				try {

					JSONArray jsonData = jsonResp.getJSONArray("data");
					JSONObject jsonPagination = jsonResp
							.getJSONObject("pagination");

					boolean lastPage = true;
					if (jsonPagination != null) {
						if (jsonPagination.has("next_cursor")) {
							String nextCursor = jsonPagination
									.optString("next_cursor");
							if (!nextCursor.isEmpty()) {
								lastPage = false;
								populateFollowing(nextCursor);
							}
						}
					}

					for (int i = 0; i < jsonData.length(); i++) {
						try {
							User user = new User((JSONObject) jsonData.get(i));
							user.setFollowedByMe(false);
							following.add(user);
						} catch (Exception e) {
						}
					}

					((MainActivity) getActivity()).setFollowing(following);

					Counter counterFollowing = new Counter(R.string.following,
							prefs.getInt(ApplicationData.KEY_FOLLOWING, 0));
					counters.set(1, counterFollowing);

					adapter.notifyDataSetChanged();

					if (lastPage) {
						updateCounters();
					}

				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onFailure(Throwable e, JSONObject error) {
				Log.e("ERROR", e.toString());
			}

		});
	}

	public void calculateLostGainFollowers(Integer currentNumber) {
		prefs = getActivity().getSharedPreferences(ApplicationData.PREFERENCES,
				Context.MODE_PRIVATE);
		Integer previousFollowers = prefs.getInt(ApplicationData.KEY_FOLLOWERS,
				0);

		Editor editor = prefs.edit();
		if (previousFollowers > currentNumber) {
			editor.putInt(ApplicationData.KEY_LOST_FOLLOWERS, previousFollowers
					- currentNumber);
		} else {
			editor.putInt(ApplicationData.KEY_GAIN_FOLLOWERS, currentNumber
					- previousFollowers);
		}
		editor.putInt(ApplicationData.KEY_FOLLOWERS, currentNumber);
		editor.commit();
	}

	public void setCounters() {

		Counter counterFollowers = new Counter(R.string.followers,
				prefs.getInt(ApplicationData.KEY_FOLLOWERS, 0));
		Counter counterFollowing = new Counter(R.string.following,
				prefs.getInt(ApplicationData.KEY_FOLLOWING, 0));
		Counter counterGainFollowers = new Counter(R.string.gain_followers,
				prefs.getInt(ApplicationData.KEY_GAIN_FOLLOWERS, 0));
		Counter counterLostFollowers = new Counter(R.string.lost_followers,
				prefs.getInt(ApplicationData.KEY_LOST_FOLLOWERS, 0));

		counters.add(counterFollowers);
		counters.add(counterFollowing);
		counters.add(counterGainFollowers);
		counters.add(counterLostFollowers);

		adapter.setData(counters);
		mListView.setAdapter(adapter);
		adapter.notifyDataSetChanged();
	}

	public void updateCounters() {
		((MainActivity) getActivity()).reCalculateLists();

		ArrayList<User> areNotFollowingBack = ((MainActivity) getActivity())
				.getAreNotFollowingBack();
		if (areNotFollowingBack != null) {
			Counter counterAreNotFollowingBack = new Counter(
					R.string.arenotfollowingback, areNotFollowingBack.size());
			counters.set(2, counterAreNotFollowingBack);
		}
		ArrayList<User> youAreNotFollowingBack = ((MainActivity) getActivity())
				.getYouNotFollowingBack();
		if (youAreNotFollowingBack != null) {
			Counter counterYouAreNotFollowingBack = new Counter(
					R.string.younotfollowingback, youAreNotFollowingBack.size());
			counters.set(3, counterYouAreNotFollowingBack);
		}
		adapter.notifyDataSetChanged();
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		adapter.setData(counters);
		mListView.setAdapter(adapter);

		populateFollowers(null);
		populateFollowing(null);

		setRetainInstance(true);
	}

	public interface OnCountersListFragmentListener {
		public void onCountersListClickListener(Counter item, int position);

		public void onShowCountersListFragment();

		public void onDestroyCountersListFragment();

		public void onCountersListResume(ArrayList<Counter> counters);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			onCountersListFragmentListener = (OnCountersListFragmentListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement onMerchantsListFragmentListener");
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		if (onCountersListFragmentListener != null) {
			if (counters != null) {
				onCountersListFragmentListener.onCountersListResume(counters);
			}
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View view, int position,
			long id) {
		Counter item = (Counter) mListView.getAdapter().getItem(position);
		onCountersListFragmentListener.onCountersListClickListener(item,
				position);
	}

	public CountersAdapter getAdapter() {
		return adapter;
	}

	public ListView getListView() {
		return mListView;
	}

}