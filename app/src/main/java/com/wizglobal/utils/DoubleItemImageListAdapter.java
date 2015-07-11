package com.wizglobal.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.wizglobal.app.R;

/**
 * Created by Mathew.Godia on 4/15/14.
 */
public class DoubleItemImageListAdapter extends BaseAdapter {
    Context context;
    String action;
    String[] list1;
    String[] list2;
    int[] imageId;
    String accounts;
    private static LayoutInflater inflater = null;

    public DoubleItemImageListAdapter(Context context, String action, String[] list1, String[] list2, int[] imageId) {
        this.context = context;
        this.action = action;
        this.list1 = list1;
        this.list2 = list2;
        this.imageId = imageId;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public DoubleItemImageListAdapter(Context context, String action, String[] list1, String[] list2, String[] list3, int[] imageId, String accounts) {
        this.context = context;
        this.action = action;
        this.list1 = list1;
        this.list2 = list2;
        this.accounts = accounts;
        this.imageId = imageId;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return list1.length;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public class Holder {
        TextView item1Tv;
        TextView item2Tv;
        TextView item3Tv;
        ImageView img;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        Holder holder = new Holder();
        View rowView;
        rowView = inflater.inflate(R.layout.double_text_image_list, null);
        holder.item1Tv = (TextView) rowView.findViewById(R.id.qeItem1);
        holder.item2Tv = (TextView) rowView.findViewById(R.id.qeItem2);
        holder.img = (ImageView) rowView.findViewById(R.id.qeImage);
        holder.item1Tv.setText(list1[position]);
        holder.item2Tv.setText(list2[position]);
        holder.img.setImageResource(imageId[position]);
        rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(context, descriptions[position], Toast.LENGTH_LONG).show();
//                goToAction(position);
            }
        });
        return rowView;
    }
}
