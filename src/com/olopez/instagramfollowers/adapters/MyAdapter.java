package com.olopez.instagramfollowers.adapters;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.olopez.instagramfollowers.R;
import com.olopez.instagramfollowers.model.Model;


public class MyAdapter extends ArrayAdapter<Model> {

       private final Context context;
       private final ArrayList<Model> modelsArrayList;

       public MyAdapter(Context context, ArrayList<Model> modelsArrayList) {

           super(context, R.layout.target_item, modelsArrayList);

           this.context = context;
           this.modelsArrayList = modelsArrayList;
       }

       @Override
       public View getView(int position, View convertView, ViewGroup parent) {

           // 1. Create inflater 
           LayoutInflater inflater = (LayoutInflater) context
               .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

           // 2. Get rowView from inflater

           View rowView = null;

               rowView = inflater.inflate(R.layout.target_item, parent, false);

               // 3. Get icon,title & counter views from the rowView
               ImageView imgView = (ImageView) rowView.findViewById(R.id.item_icon); 
               TextView titleView = (TextView) rowView.findViewById(R.id.item_title);
               TextView counterView = (TextView) rowView.findViewById(R.id.item_counter);

               // 4. Set the text for textView 
               imgView.setImageResource(modelsArrayList.get(position).getIcon());
               titleView.setText(modelsArrayList.get(position).getTitle());
               counterView.setText(modelsArrayList.get(position).getCounter());

           // 5. retrn rowView
           return rowView;
       }
}