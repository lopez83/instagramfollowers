package com.olopez.instagramfollowers.adapters;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.olopez.instagramfollowers.ApplicationData;
import com.olopez.instagramfollowers.R;
import com.olopez.instagramfollowers.helpers.APIClient;
import com.olopez.instagramfollowers.model.User;

public class UsersListAdapter extends ArrayAdapter<User> {

	private final Context context;
	private ArrayList<User> users;
	private LayoutInflater inflater;

	public UsersListAdapter(Context context, ArrayList<User> users) {

		super(context, R.layout.target_item, users);
		this.context = context;
		this.users = users;
		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public UsersListAdapter(Context context) {
		super(context, R.layout.target_item);
		this.context = context;
		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		Log.d("UListAdapter", "getView position " + position);
		View view = null;
		view = inflater.inflate(R.layout.user_item, parent, false);

		User user = users.get(position);
		ImageView ivProfile = (ImageView) view.findViewById(R.id.ivProfile);

		TextView nameView = (TextView) view.findViewById(R.id.tvName);
		// TextView bodyView = (TextView) view.findViewById(R.id.tvBody);

		ImageLoader.getInstance().displayImage(user.getProfilePicture(),
				ivProfile);
		nameView.setText(user.getUsername());
		// bodyView.setText(Html.fromHtml(user.getWebsite()));

		TextView textViewClick = (TextView) view.findViewById(R.id.item_action);
		/*
		if(user.isFollowedByMe()){
			textViewClick.setText("Unfollow");
		}else{
			textViewClick.setText("Follow");
		}
		*/
		textViewClick.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {

				User user = users.get(position);
				// User item = (User) usersList.getAdapter().getItem(position);
				Log.i("tag", "click on position " + position + " user name is "
						+ user.getUsername());
				SharedPreferences prefs = context.getSharedPreferences(
						ApplicationData.PREFERENCES, Context.MODE_PRIVATE);
				String userID = prefs
						.getString(ApplicationData.KEY_USER_ID, "");
				if (user.isFollowedByMe()){
					changeUserRelationship(userID, user.getId(), "unfollow");	
				}else{
					changeUserRelationship(userID, user.getId(), "follow");
				}
			}
		});

		return view;
	}

	public void setData(ArrayList<User> data) {
		users = data;
	}

	public int getCount() {
		return users.size();
	}

	public User getItem(int position) {
		return users.get(position);
	}

	private void changeUserRelationship(String myUserId, String extUserId,
			String action) {

		RequestParams params = new RequestParams();

		params.put("user_id", extUserId);
		params.put("action", action);
		
		Log.d("TAG", action+" user "+extUserId);
		
		APIClient.changeUserRelationship(extUserId, params,
				new JsonHttpResponseHandler() {

					@Override
					public void onSuccess(JSONObject jsonResp) {
						try {

							JSONObject meta = jsonResp.getJSONObject("meta");
							String code = meta.getString("code");
							if (code.equals("200")) {
								Log.d("TAG", "Relationship changed");
							} else {
								Log.e("TAG", "Error changing relationship");
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

}
