package com.wizglobal.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.wizglobal.app.R;

/**
 * Created by Mathew.Godia on 4/15/14.
 */
public class MessageListAdapter extends BaseAdapter{

    Context context;
    String[] subjectList;
    String[] messageList;
    String[] dateList;

    private static LayoutInflater inflater = null;

    public MessageListAdapter(Context context, String[] subjectList, String[] messageList,String[] dateList) {
        this.context = context;
        this.subjectList = subjectList;
        this.messageList = messageList;
        this.dateList = dateList;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return subjectList.length;
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
        TextView subjectTV;
        TextView previewTV;
        TextView dateTV;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        Holder holder = new Holder();
        View rowView;
        rowView = inflater.inflate(R.layout.message_list, null);
        holder.subjectTV = (TextView) rowView.findViewById(R.id.mSubject);
        holder.previewTV = (TextView) rowView.findViewById(R.id.mPreview);
        holder.dateTV = (TextView) rowView.findViewById(R.id.mDate);
        holder.subjectTV.setText(subjectList[position]);
        holder.previewTV.setText(messageList[position]);
        holder.dateTV.setText(dateList[position]);
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
