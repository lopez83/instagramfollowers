package com.olopez.instagramfollowers.adapters;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.olopez.instagramfollowers.R;
import com.olopez.instagramfollowers.UsersListActivity;
import com.olopez.instagramfollowers.model.Counter;

public class CountersAdapter extends ArrayAdapter<Counter> {

	private final Context context;
	private ArrayList<Counter> countersArrayList;

	public CountersAdapter(Context context, ArrayList<Counter> countersArrayList) {
		super(context, R.layout.target_item, countersArrayList);
		this.context = context;
		this.countersArrayList = countersArrayList;
	}

	public CountersAdapter(Context context) {
		super(context, R.layout.target_item);
		this.context = context;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		  LayoutInflater inflater = ((Activity) context).getLayoutInflater();
		  

          
		// 1. Create inflater
		//LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		// 2. Get rowView from inflater

		View rowView = null;
		rowView = inflater.inflate(R.layout.target_item, parent, false);

		//rowView = inflater.inflate(R.layout.target_item, null);
		//rowView = inflater.inflate(R.layout.target_item, parent, false);

		// 3. Get icon,title & counter views from the rowView
		ImageView imgView = (ImageView) rowView.findViewById(R.id.item_icon);
		TextView titleView = (TextView) rowView.findViewById(R.id.item_title);
		TextView counterView = (TextView) rowView
				.findViewById(R.id.item_counter);

		/*
		convertView.setOnClickListener(new AdapterView.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i = new Intent(getContext(), UsersListActivity.class);
				getContext().startActivity(i);
			}
		});
		*/
		// 4. Set the text for textView
		imgView.setImageResource(countersArrayList.get(position).getIcon());
		titleView.setText(countersArrayList.get(position).getTitle());
		counterView.setText(String.valueOf(countersArrayList.get(position)
				.getCounter()));

		return rowView;
	}

	
	public void setData(ArrayList<Counter> data) {
		countersArrayList = data;
	}

	public int getCount() {
		return countersArrayList.size();
	}

	public Counter getItem(int position) {
		return countersArrayList.get(position);
	}

}