package com.olopez.instagramfollowers;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.olopez.instagramfollowers.adapters.CountersAdapter;
import com.olopez.instagramfollowers.model.Counter;
import com.olopez.instagramlib.Instagram;
import com.olopez.instagramlib.InstagramRequest;
import com.olopez.instagramlib.InstagramSession;
import com.olopez.instagramlib.InstagramUser;

/**
 * Instagram authentication.
 * 
 * 
 */
public class MyListActivity extends Activity {
	private InstagramSession mInstagramSession;
	private Instagram mInstagram;

	private ProgressBar mLoadingPb;
	private GridView mGridView;
	private TextView countFollowers, countGainFollowers, countLostFollowers;

	private FragmentManager fragmentManager;
	private SharedPreferences prefs;
	private ArrayList<Counter> counters = new ArrayList<Counter>();

	// private MainFragment mainFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		prefs = getSharedPreferences(ApplicationData.PREFERENCES,
				Context.MODE_PRIVATE);

		mInstagram = new Instagram(this, ApplicationData.CLIENT_ID,
				ApplicationData.CLIENT_SECRET, ApplicationData.REDIRECT_URI);

		mInstagramSession = mInstagram.getSession();

		if (mInstagramSession.isActive()) {

			InstagramUser instagramUser = mInstagramSession.getUser();
			new DownloadTask().execute();

		} else {
			setContentView(R.layout.activity_main_insta);

			((Button) findViewById(R.id.btn_connect))
					.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View arg0) {
							mInstagram.authorize(mAuthListener);
						}
					});
		}
	}

	private void showToast(String text) {
		Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
	}

	private Instagram.InstagramAuthListener mAuthListener = new Instagram.InstagramAuthListener() {
		@Override
		public void onSuccess(InstagramUser user) {
			finish();
			startActivity(new Intent(MyListActivity.this, MyListActivity.class));
		}

		@Override
		public void onError(String error) {
			showToast(error);
		}

		@Override
		public void onCancel() {
			showToast("OK. Maybe later?");

		}
	};

	public static class AnimateFirstDisplayListener extends
			SimpleImageLoadingListener {

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

	public class DownloadTask extends AsyncTask<URL, Integer, Long> {
		ArrayList<String> photoList;

		protected void onCancelled() {

		}

		protected void onPreExecute() {

		}

		protected Long doInBackground(URL... urls) {
			long result = 0;

			try {
				List<NameValuePair> params = new ArrayList<NameValuePair>(1);

				params.add(new BasicNameValuePair("count", "10"));

				InstagramRequest request = new InstagramRequest(
						mInstagramSession.getAccessToken());
				String response = request.createRequest("GET",
						"/users/self/feed", params);

				if (!response.equals("")) {
					JSONObject jsonObj = (JSONObject) new JSONTokener(response)
							.nextValue();
					JSONArray jsonData = jsonObj.getJSONArray("data");

					int length = jsonData.length();

					if (length > 0) {
						photoList = new ArrayList<String>();

						calculateLostGainFollowers(length);

						for (int i = 0; i < length; i++) {
							JSONObject jsonPhoto = jsonData.getJSONObject(i)
									.getJSONObject("images")
									.getJSONObject("low_resolution");

							photoList.add(jsonPhoto.getString("url"));
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			return result;
		}

		protected void onProgressUpdate(Integer... progress) {
		}

		protected void onPostExecute(Long result) {

			setCounters();
		}
	}

	public void calculateLostGainFollowers(Integer currentNumber) {
		prefs = getSharedPreferences(ApplicationData.PREFERENCES,
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
		Counter counterGainFollowers = new Counter(R.string.gain_followers,
				prefs.getInt(ApplicationData.KEY_GAIN_FOLLOWERS, 0));
		Counter counterLostFollowers = new Counter(R.string.lost_followers,
				prefs.getInt(ApplicationData.KEY_LOST_FOLLOWERS, 0));
		counters.add(counterFollowers);
		counters.add(counterGainFollowers);
		counters.add(counterLostFollowers);

		CountersAdapter adapter = new CountersAdapter(this, counters);

	//	setListAdapter(adapter);

		/*
		 * countFollowers.setText(String.valueOf(prefs.getInt(ApplicationData.
		 * KEY_FOLLOWERS,0)));
		 * countGainFollowers.setText(String.valueOf(prefs.getInt
		 * (ApplicationData.KEY_GAIN_FOLLOWERS,0)));
		 * countLostFollowers.setText(String
		 * .valueOf(prefs.getInt(ApplicationData.KEY_LOST_FOLLOWERS,0)));
		 */
	}

}