package com.wizglobal.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.wizglobal.app.PriceListActivity;
import com.wizglobal.app.PriceListInterestActivity;
import com.wizglobal.app.PriceListNavActivity;
import com.wizglobal.app.ProductListNavActivity;
import com.wizglobal.app.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by Mathew.Godia on 4/15/14.
 */
public class DoubleEntryListAdapter extends BaseAdapter{

    Context context;
    String action;
    String[] list1 ;
    String[] list2;

    private static LayoutInflater inflater = null;

    public DoubleEntryListAdapter(Context context, String[] list1, String[] list2) {
        this.context = context;
        this.list1 = list1;
        this.list2 = list2;
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
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        Holder holder = new Holder();
        View rowView;
        rowView = inflater.inflate(R.layout.double_entry_list, null);
        holder.item1Tv = (TextView) rowView.findViewById(R.id.deItem1);
        holder.item2Tv = (TextView) rowView.findViewById(R.id.deItem2);
        holder.item1Tv.setText(list1[position]);
        holder.item2Tv.setText(list2[position]);
        rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSelection(position);
            }
        });
        return rowView;
    }


    public void getSelection(int position){

    }


}
