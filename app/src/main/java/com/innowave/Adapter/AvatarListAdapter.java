package com.innowave.Adapter;

import android.app.Activity;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.innowave.R;

import java.util.ArrayList;

public class AvatarListAdapter extends ArrayAdapter<String> {

    RelativeLayout relativeLayout;;
     Activity context;
     ArrayList<String> cropName;
     ArrayList<Bitmap> cropPic;

    public AvatarListAdapter(Activity context, ArrayList<String> cropName, ArrayList<Bitmap> cropPic) {
        super(context, R.layout.listitem, cropName);
        // TODO Auto-generated constructor stub

        this.context=context;
        this.cropName =cropName;
        this.cropPic=cropPic;

    }

    public View getView(final int position, View view, ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.listitem, null,true);

       // BitmapDrawable background = new BitmapDrawable(cropPic.get(position));
        //rowView.setBackground(background);



        TextView titleText = (TextView) rowView.findViewById(R.id.title);
        ImageView speaker = (ImageView) rowView.findViewById(R.id.icon);

        speaker.setImageBitmap(cropPic.get(position));

        titleText.setText(cropName.get(position));

        return rowView;

    };

}