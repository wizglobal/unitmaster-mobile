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

import com.wizglobal.app.BalanceInquiryActivity;
import com.wizglobal.app.MiniStatementActivity;
import com.wizglobal.app.R;
import com.wizglobal.app.SellActivity;
import com.wizglobal.app.TransferActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Mathew.Godia on 4/15/14.
 */
public class TripleEntryListAdapter extends BaseAdapter {
    String serverUrl;
    Context context;
    String action;
    String[] list1;
    String[] list2;
    String[] list3;
    String accounts;

    private static LayoutInflater inflater = null;

    public TripleEntryListAdapter(Context context, String action, String[] list1, String[] list2, String[] list3) {
        this.context = context;
        this.action = action;
        this.list1 = list1;
        this.list2 = list2;
        this.list3 = list3;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public TripleEntryListAdapter(Context context, String action, String[] list1, String[] list2, String[] list3, String accounts) {
        this.context = context;
        this.action = action;
        this.list1 = list1;
        this.list2 = list2;
        this.list3 = list3;
        this.accounts = accounts;
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
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        Holder holder = new Holder();
        View rowView;
        rowView = inflater.inflate(R.layout.triple_entry_list, null);
        holder.item1Tv = (TextView) rowView.findViewById(R.id.teItem1);
        holder.item2Tv = (TextView) rowView.findViewById(R.id.teItem2);
        holder.item3Tv = (TextView) rowView.findViewById(R.id.teItem3);
        holder.item1Tv.setText(list1[position]);
        holder.item2Tv.setText(list2[position]);
        holder.item3Tv.setText(list3[position]);
        rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(context, descriptions[position], Toast.LENGTH_LONG).show();
                goToAction(position);
            }
        });
        return rowView;
    }

    public void goToAction(int position) {
        String accNo = list3[position];
        try {
            JSONObject params = new JSONObject();
            if (action.equalsIgnoreCase("balance")) {
                String accType = list2[position];
                if (Config.isConnected(context)) {
                    if (accType.equalsIgnoreCase("Admin Fee")) {
                        String secCode = "";
                        JSONArray accountList = new JSONArray(accounts);
                        for (int i = 0; i < accountList.length(); i++) {
                            JSONObject acc = accountList.getJSONObject(i);
                            if (acc.getString("accountNo").equalsIgnoreCase(accNo)) {
                                secCode = acc.getString("securityCode");
                            }
                        }
                        params.put("action", "GetNavAccountBalance");
                        params.put("accNo", accNo);
                        params.put("secCode", secCode);
                        serverUrl = Config.getServerUrl("accountBalance");
                        new DoGetActionTask(params).execute(serverUrl);
                    } else if (accType.equalsIgnoreCase("Rate Fee")) {
                        params.put("action", "GetInterestAccountBalance");
                        params.put("accNo", accNo);
                        serverUrl = Config.getServerUrl("accountBalance");
                        new DoGetActionTask(params).execute(serverUrl);
                    }
                } else {
                    Toast.makeText(context, "No Network Connectivity", Toast.LENGTH_LONG).show();
                }
            } else if (action.equalsIgnoreCase("sell")) {
                String accType = list2[position];
                if (accType.equalsIgnoreCase("Admin Fee")) {
                    String secCode = "";
                    JSONArray accountList = new JSONArray(accounts);
                    for (int i = 0; i < accountList.length(); i++) {
                        JSONObject acc = accountList.getJSONObject(i);
                        if (acc.getString("accountNo").equalsIgnoreCase(accNo)) {
                            secCode = acc.getString("securityCode");
                        }
                    }
                    params.put("action", "GetNavAccountBalance");
                    params.put("accNo", accNo);
                    params.put("secCode", secCode);
                    serverUrl = Config.getServerUrl("accountBalance");
                    new DoGetActionTask(params).execute(serverUrl);
                } else if (accType.equalsIgnoreCase("Rate Fee")) {
                    params.put("action", "GetInterestAccountBalance");
                    params.put("accNo", accNo);
                    serverUrl = Config.getServerUrl("accountBalance");
                    new DoGetActionTask(params).execute(serverUrl);
                }

            } else if (action.equalsIgnoreCase("transfer")) {
                String accType = list2[position];
                if (accType.equalsIgnoreCase("Admin Fee")) {
                    String secCode = "";
                    JSONArray accountList = new JSONArray(accounts);
                    for (int i = 0; i < accountList.length(); i++) {
                        JSONObject acc = accountList.getJSONObject(i);
                        if (acc.getString("accountNo").equalsIgnoreCase(accNo)) {
                            secCode = acc.getString("securityCode");
                        }
                    }
                    params.put("action", "GetNavAccountBalance");
                    params.put("accNo", accNo);
                    params.put("secCode", secCode);
                    serverUrl = Config.getServerUrl("accountBalance");
                    new DoGetActionTask(params).execute(serverUrl);
                } else if (accType.equalsIgnoreCase("Rate Fee")) {
                    params.put("action", "GetInterestAccountBalance");
                    params.put("accNo", accNo);
                    serverUrl = Config.getServerUrl("accountBalance");
                    new DoGetActionTask(params).execute(serverUrl);
                }
            } else if (action.equalsIgnoreCase("mini_statement")) {
                if (Config.isConnected(context)) {
                    params.put("action", "GetAccountMinistatement");
                    params.put("accNo", accNo);
                    serverUrl = Config.getServerUrl("miniStatement");
                    new DoGetActionTask(params).execute(serverUrl);
                } else {
                    Toast.makeText(context, "No Network Connectivity", Toast.LENGTH_LONG).show();
                }
            } else {
                Log.d(Config.getDebugTag(), "End Of Line");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class DoGetActionTask extends AsyncTask<String, Void, String> {
        private JSONObject params;
        private ProgressDialog dialog = new ProgressDialog(context);

        public DoGetActionTask(JSONObject params) {
            this.params = params;
        }

        protected void onPreExecute() {
            dialog.setMessage("Submitting Request");
            dialog.setTitle("Retrieving Data");
            dialog.show();
        }

        @Override
        protected String doInBackground(String... urls) {
            try {
                if (serverUrl.contains("https")) {
                    Log.d(Config.getDebugTag(), "SecureServerConnect Called");
                    SecureServerConnect sc = new SecureServerConnect();
                    return sc.processRequest(urls[0], params);
                } else {
                    Log.d(Config.getDebugTag(), "ServerConnect Called");
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
                    if (action.equalsIgnoreCase("balance")) {
                        String rs = res.getString("response_message");
                        Intent intent = new Intent(context, BalanceInquiryActivity.class);
                        intent.putExtra("wizglobal.balRes", rs);
                        context.startActivity(intent);
                    } else if (action.equalsIgnoreCase("sell")) {
                        String rs = res.getString("response_message");
                        Intent intent = new Intent(context, SellActivity.class);
                        intent.putExtra("wizglobal.balRes", rs);
                        context.startActivity(intent);
                    } else if (action.equalsIgnoreCase("transfer")) {
                        String rs = res.getString("response_message");
                        Intent intent = new Intent(context, TransferActivity.class);
                        intent.putExtra("wizglobal.balRes", rs);

                        JSONArray accountList = new JSONArray();
                        for (int i = 0; i < list1.length; i++) {
                            JSONObject account = new JSONObject();
                            account.put("accountName", list1[i]);
                            account.put("accountNo", list3[i]);
                            accountList.put(account);
                        }
                        intent.putExtra("wizglobal.accountList", accountList.toString());
                        context.startActivity(intent);
                    } else if (action.equalsIgnoreCase("mini_statement")) {
                        JSONArray statementList = res.getJSONArray("response_message");
                        Intent intent = new Intent(context, MiniStatementActivity.class);
                        intent.putExtra("wizglobal.statementList", statementList.toString());
                        context.startActivity(intent);
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
