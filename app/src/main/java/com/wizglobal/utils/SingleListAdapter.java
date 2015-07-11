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

import com.wizglobal.app.PriceListInterestActivity;
import com.wizglobal.app.PriceListNavActivity;
import com.wizglobal.app.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Mathew.Godia on 4/7/14.
 */
public class SingleListAdapter extends BaseAdapter {

    String[] items;
    Context context;
    JSONArray productList;
    String action;
    String serverUrl;
    private static LayoutInflater inflater = null;
    public int LIST_REQUEST = 0;

    public SingleListAdapter(Context context, String[] items, JSONArray productList, String action) {
        this.context = context;
        this.items = items;
        this.productList = productList;
        this.action = action;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return items.length;
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
        TextView tv;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        Holder holder = new Holder();
        View rowView;
        rowView = inflater.inflate(R.layout.single_entry_list, null);
        holder.tv = (TextView) rowView.findViewById(R.id.sItemName);
        holder.tv.setText(items[position]);
        rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(context,"You Clicked: "+items[position],Toast.LENGTH_LONG).show();
                getSelection(position);
            }
        });
        return rowView;
    }

    public void getSelection(int position) {
        String selected = items[position];
        String secCode = "";
        try {
            for (int i = 0; i < productList.length(); i++) {
                String descript = productList.getJSONObject(i).getString("descript");
                if (descript.equalsIgnoreCase(selected)) {
                    secCode = productList.getJSONObject(i).getString("security_code");
                }
            }
            if (action == null) {
                Log.d(Config.getDebugTag(), "No Action");
            } else {
                if (action.equals("GetPriceListNAV")) {
                    LIST_REQUEST = 100;
                } else if (action.equals("GetPriceListInterestRate")) {
                    LIST_REQUEST = 200;
                }
                if (Config.isConnected(context)) {
                    JSONObject params = new JSONObject();
                    params.put("action", action);
                    params.put("secCode", secCode);
                    serverUrl = Config.getServerUrl("priceList");
                    new DoGetPriceListTask(params).execute(serverUrl);
                } else {
                    Toast.makeText(context, "No Network Connectivity", Toast.LENGTH_LONG).show();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class DoGetPriceListTask extends AsyncTask<String, Void, String> {
        private JSONObject params;
        private ProgressDialog dialog = new ProgressDialog(context);

        public DoGetPriceListTask(JSONObject params) {
            this.params = params;
        }

        protected void onPreExecute() {
            dialog.setMessage("Submitting Request");
            dialog.setTitle("Fetching Price List");
            dialog.show();
        }

        @Override
        protected String doInBackground(String... urls) {
            try {
                if (serverUrl.contains("https")) {
                    SecureServerConnect sc = new SecureServerConnect();
                    return sc.processRequest(urls[0], params);
                } else {
                    ServerConnect sc = new ServerConnect();
                    return sc.processRequest(urls[0], params);
                }
            } catch (Exception e) {
                Log.d(Config.getDebugTag(), e.toString());
                e.printStackTrace();
                return "{'response_code':'400','message':'errorServerUnreachable'}";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            dialog.dismiss();
            try {
                JSONObject res = new JSONObject(result);
                Log.d(Config.getDebugTag(), "Response Code: " + res.get("response_code") + "\nResponse Message: " + res.get("response_message").toString());
                if (res.getString("response_code").equalsIgnoreCase("0")) {
                    JSONArray priceList = res.getJSONArray("response_message");
                    Intent intent;
                    switch (LIST_REQUEST) {
                        case 100:
                            intent = new Intent(context, PriceListNavActivity.class);
                            intent.putExtra("wizglobal.priceListNav", priceList.toString());
                            context.startActivity(intent);
                            break;
                        case 200:
                            intent = new Intent(context, PriceListInterestActivity.class);
                            intent.putExtra("wizglobal.priceListInterest", priceList.toString());
                            context.startActivity(intent);
                            break;
                    }
                } else {
                    Toast.makeText(context, res.getString("response_message"), Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }
    }


}
