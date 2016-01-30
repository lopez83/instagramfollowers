package com.olopez.instagramfollowers;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.olopez.instagramfollowers.helpers.APIClient;
import com.olopez.instagramlib.Instagram;
import com.olopez.instagramlib.InstagramSession;
import com.olopez.instagramlib.InstagramUser;

public class LoginActivity extends Activity {

	private InstagramSession mInstagramSession;
	private Instagram mInstagram;
	private APIClient api;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_insta);

		api = APIClient.getInstance(this);
		mInstagramSession = api.getSession();
		
		/*
		mInstagram = new Instagram(this, ApplicationData.CLIENT_ID,
				ApplicationData.CLIENT_SECRET, ApplicationData.REDIRECT_URI);
		mInstagramSession = mInstagram.getSession();
		 */  	
		if (mInstagramSession.isActive()) {
			finish();
			Intent i = new Intent(LoginActivity.this, MainActivity.class);
	    	startActivity(i);
			
		}else{

			((Button) findViewById(R.id.btn_connect))
			.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View arg0) {
					api.authorize(mAuthListener);
				//	mInstagram.authorize(mAuthListener);
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
			startActivity(new Intent(LoginActivity.this, MainActivity.class));
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
}