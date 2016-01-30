package com.olopez.instagramfollowers.helpers;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.olopez.instagramfollowers.ApplicationData;
import com.olopez.instagramlib.Instagram.InstagramAuthListener;
import com.olopez.instagramlib.InstagramDialog;
import com.olopez.instagramlib.InstagramRequest;
import com.olopez.instagramlib.InstagramSession;
import com.olopez.instagramlib.InstagramUser;
import com.olopez.instagramlib.util.Cons;

public class APIClient {
	private static AsyncHttpClient client = new AsyncHttpClient();
	private static String mAccessToken;
	private static APIClient cInstance = null;

	private InstagramDialog mDialog;
	private InstagramAuthListener mListener;
	private static InstagramSession mSession;

	private Context mContext;

	private String mClientId;
	private String mClientSecret;
	private String mRedirectUri;

	//https://github.com/Instagram/python-instagram/blob/master/instagram/client.py
	
	public APIClient(Context context, String clientId, String clientSecret,
			String redirectUri) {

		mClientId = clientId;
		mClientSecret = clientSecret;
		mRedirectUri = redirectUri;

		String authUrl = Cons.AUTH_URL + "client_id=" + mClientId
				+ "&redirect_uri=" + mRedirectUri + "&response_type=code";

		mSession = new InstagramSession(context);		
		mDialog = new InstagramDialog(context, authUrl, redirectUri,
				new InstagramDialog.InstagramDialogListener() {

					@Override
					public void onSuccess(String code) {
						retreiveAccessToken(code);
					}

					@Override
					public void onError(String error) {
						mListener.onError(error);
					}

					@Override
					public void onCancel() {
						mListener.onCancel();

					}
				});
	}

	public static APIClient getInstance(Context context) {
		if (cInstance == null) {
			cInstance = new APIClient(context, ApplicationData.CLIENT_ID,
					ApplicationData.CLIENT_SECRET, ApplicationData.REDIRECT_URI);
		}
		return cInstance;
	}

	/**
	 * Get the list of users this user follows.
	 * @param userId
	 * @param respHandler
	 */
	public static void getFollowers(String userId, RequestParams params,
			JsonHttpResponseHandler respHandler) {
		get("/users/" + userId + "/followed-by", params, respHandler);
	}
	

	public static void getRelationShip(String userId,
			JsonHttpResponseHandler respHandler) {
		get("/users/" + userId + "/relationship", null, respHandler);
	}	
	
	
	/**
	 * Get the list of users this user is followed by.
	 * @param userId
	 * @param respHandler
	 */
	public static void getFollowing(String userId, RequestParams params,
			JsonHttpResponseHandler respHandler) {
		get("/users/" + userId + "/follows", params, respHandler);
	}
	
	/**
	 * Unfollow user
	 * @param userId
	 * @param respHandler
	 */
	public static void changeUserRelationship(String userId, RequestParams params,
			JsonHttpResponseHandler respHandler) { //params-> user_id, action=unfollow
		post("/users/" + userId + "/relationship", params, respHandler);
	}
	
	public static void get(String endpoint, RequestParams params,
			AsyncHttpResponseHandler responseHandler) {
		String requestUrl = getRequestUrl(endpoint);
		String at = mSession.getAccessToken();
		if(params==null)
			params = new RequestParams();
		params.put("access_token", at);

		try {
			Log.d("DEBUG", "About to GET " + endpoint);
			client.get(requestUrl, params, responseHandler);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void post(String endpoint, RequestParams params,
			AsyncHttpResponseHandler responseHandler) {
		String requestUrl = getRequestUrl(endpoint);
		String at = mSession.getAccessToken();
		if(params==null)
			params = new RequestParams();
		params.put("access_token", at);

		try {
			Log.d("DEBUG", "About to POST " + endpoint);
			client.post(requestUrl, params, responseHandler);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static String getRequestUrl(String endpoint) {
		return Cons.API_BASE_URL
				+ ((endpoint.indexOf("/") == 0) ? endpoint : "/" + endpoint);
	}

	private void retreiveAccessToken(String code) {
		new AccessTokenTask(code).execute();
	}

	public void authorize(InstagramAuthListener listener) {
		mListener = listener;

		mDialog.show();
	}

	public InstagramSession getSession() {
		return mSession;
	}

	public class AccessTokenTask extends AsyncTask<URL, Integer, Long> {
		// ProgressDialog progressDlg;
		InstagramUser user;
		String code;

		public AccessTokenTask(String code) {
			this.code = code;

			// progressDlg = new ProgressDialog(mContext);
			// progressDlg.setMessage("Getting access token...");
		}

		protected void onCancelled() {
			// progressDlg.cancel();
		}

		protected void onPreExecute() {
			// progressDlg.show();
		}

		protected Long doInBackground(URL... urls) {
			long result = 0;

			try {
				List<NameValuePair> params = new ArrayList<NameValuePair>(5);

				params.add(new BasicNameValuePair("client_id", mClientId));
				params.add(new BasicNameValuePair("client_secret",
						mClientSecret));
				params.add(new BasicNameValuePair("grant_type",
						"authorization_code"));
				params.add(new BasicNameValuePair("redirect_uri", mRedirectUri));
				params.add(new BasicNameValuePair("code", code));

				InstagramRequest request = new InstagramRequest();
				String response = request.post(Cons.ACCESS_TOKEN_URL, params);

				if (!response.equals("")) {
					JSONObject jsonObj = (JSONObject) new JSONTokener(response)
							.nextValue();
					JSONObject jsonUser = jsonObj.getJSONObject("user");

					user = new InstagramUser();

					user.accessToken = jsonObj.getString("access_token");

					user.id = jsonUser.getString("id");
					user.username = jsonUser.getString("username");
					user.fullName = jsonUser.getString("full_name");
					user.profilPicture = jsonUser.getString("profile_picture");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			return result;
		}

		protected void onProgressUpdate(Integer... progress) {
		}

		protected void onPostExecute(Long result) {
			// progressDlg.dismiss();

			if (user != null) {
				mSession.store(user);
				mListener.onSuccess(user);
			} else {
				mListener.onError("Failed to get access token");
			}
		}
	}
}