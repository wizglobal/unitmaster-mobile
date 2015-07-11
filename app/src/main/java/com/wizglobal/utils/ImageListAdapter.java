package com.wizglobal.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.wizglobal.app.AccountAgentsActivity;
import com.wizglobal.app.AccountList;
import com.wizglobal.app.ChangePasswordActivity;
import com.wizglobal.app.MessagesActivity;
import com.wizglobal.app.CustomerListActivity;
import com.wizglobal.app.PriceListActivity;
import com.wizglobal.app.ProductListInterestActivity;
import com.wizglobal.app.ProductListNavActivity;
import com.wizglobal.app.R;
import com.wizglobal.app.RegisterCustomerActivity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;

/**
 * Created by Mathew.Godia on 4/7/14.
 */
public class ImageListAdapter extends BaseAdapter {

    String[] items;
    Context context;
    int[] imageId;
    private static LayoutInflater inflater = null;
    public int LIST_REQUEST = 0;
    String serverUrl;

    public ImageListAdapter(Context context, String[] items, int[] imageId) {
        this.context = context;
        this.items = items;
        this.imageId = imageId;
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
        ImageView img;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        Holder holder = new Holder();
        View rowView;
        rowView = inflater.inflate(R.layout.image_list, null);
        holder.tv = (TextView) rowView.findViewById(R.id.itemName);
        holder.img = (ImageView) rowView.findViewById(R.id.itemImage);
        holder.tv.setText(items[position]);
        holder.img.setImageResource(imageId[position]);
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
        Intent intent;
        if (items[position].equalsIgnoreCase("Price List")) {
            intent = new Intent(context, PriceListActivity.class);
            context.startActivity(intent);
        } else if (items[position].equalsIgnoreCase("Balance Inquiry")) {
            try {
                if (Config.isConnected(context)) {
                    SharedPreferences pref = context.getSharedPreferences("WizGlobalPreferences", 0);
                    String memberNo = pref.getString("member", null);
                    JSONObject params = new JSONObject();
                    params.put("memberNo", memberNo);
                    LIST_REQUEST = 300;
                    serverUrl =Config.getServerUrl("accountList");
                    new DoGetProductListTask(params).execute(serverUrl);
                } else {
                    Toast.makeText(context, "No Network Connectivity", Toast.LENGTH_LONG).show();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (items[position].equalsIgnoreCase("Sell")) {
            try {
                if (Config.isConnected(context)) {
                    SharedPreferences pref = context.getSharedPreferences("WizGlobalPreferences", 0);
                    String memberNo = pref.getString("member", null);
                    JSONObject params = new JSONObject();
                    params.put("memberNo", memberNo);
                    LIST_REQUEST = 400;
                    serverUrl = Config.getServerUrl("accountList");
                    new DoGetProductListTask(params).execute(serverUrl);
                } else {
                    Toast.makeText(context, "No Network Connectivity", Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (items[position].equalsIgnoreCase("Transfer")) {
            try {
                if (Config.isConnected(context)) {
                    SharedPreferences pref = context.getSharedPreferences("WizGlobalPreferences", 0);
                    String memberNo = pref.getString("member", null);
                    JSONObject params = new JSONObject();
                    params.put("memberNo", memberNo);
                    LIST_REQUEST = 500;
                    serverUrl = Config.getServerUrl("accountList");
                    new DoGetProductListTask(params).execute(serverUrl);
                } else {
                    Toast.makeText(context, "No Network Connectivity", Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else if (items[position].equalsIgnoreCase("Mini Statement")) {
            try {
                if (Config.isConnected(context)) {
                    SharedPreferences pref = context.getSharedPreferences("WizGlobalPreferences", 0);
                    String memberNo = pref.getString("member", null);
                    JSONObject params = new JSONObject();
                    params.put("memberNo", memberNo);
                    LIST_REQUEST = 600;
                    serverUrl = Config.getServerUrl("accountList");
                    new DoGetProductListTask(params).execute(serverUrl);
                } else {
                    Toast.makeText(context, "No Network Connectivity", Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (items[position].equalsIgnoreCase("Account Agents")) {
            try {
                if (Config.isConnected(context)) {
                    SharedPreferences pref = context.getSharedPreferences("WizGlobalPreferences", 0);
                    String memberNo = pref.getString("member", null);
                    JSONObject params = new JSONObject();
                    params.put("memberNo", memberNo);
                    LIST_REQUEST = 800;
                    serverUrl = Config.getServerUrl("accountAgent");
                    new DoGetProductListTask(params).execute(serverUrl);
                } else {
                    Toast.makeText(context, "No Network Connectivity", Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (items[position].equalsIgnoreCase("Register Customer")) {
            intent = new Intent(context, RegisterCustomerActivity.class);
            context.startActivity(intent);
        } else if (items[position].equalsIgnoreCase("Customer List")) {
            try {
                if (Config.isConnected(context)) {
                    SharedPreferences pref = context.getSharedPreferences("WizGlobalPreferences", 0);
                    String agentNo = pref.getString("member", null);
                    JSONObject params = new JSONObject();
                    params.put("agentNo", agentNo);
                    params.put("action","GetAgentCustomerList");
                    LIST_REQUEST = 700;
                    serverUrl = Config.getServerUrl("agent");
                    new DoGetProductListTask(params).execute(serverUrl);
                } else {
                    Toast.makeText(context, "No Network Connectivity", Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else if (items[position].equalsIgnoreCase("Messages")) {
            intent = new Intent(context, MessagesActivity.class);
            context.startActivity(intent);
        } else if (items[position].equalsIgnoreCase("Feedback")) {
            intent = new Intent(context, MessagesActivity.class);
            context.startActivity(intent);
        } else if (items[position].equalsIgnoreCase("Change Password")) {
            intent = new Intent(context, ChangePasswordActivity.class);
            context.startActivity(intent);
        } else if (items[position].equalsIgnoreCase("Net Asset Value")) {
            try {
                if (Config.isConnected(context)) {
                    JSONObject params = new JSONObject();
                    params.put("action", "GetProductListNAV");
                    LIST_REQUEST = 100;
                    serverUrl = Config.getServerUrl("productList");
                    new DoGetProductListTask(params).execute(serverUrl);
                } else {
                    Toast.makeText(context, "No Network Connectivity", Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (items[position].equalsIgnoreCase("Interest Rate")) {
            try {
                if (Config.isConnected(context)) {
                    JSONObject params = new JSONObject();
                    params.put("action", "GetProductListInterestRate");
                    LIST_REQUEST = 200;
                    serverUrl = Config.getServerUrl("productList");
                    new DoGetProductListTask(params).execute(serverUrl);
                } else {
                    Toast.makeText(context, "No Network Connectivity", Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private class DoGetProductListTask extends AsyncTask<String, Void, String> {
        private JSONObject params;
        private ProgressDialog dialog = new ProgressDialog(context);

        public DoGetProductListTask(JSONObject params) {
            this.params = params;
        }

        protected void onPreExecute() {
            dialog.setMessage("Submitting Request");
            dialog.setTitle("Fetching Product List");
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
                    if (LIST_REQUEST == 100) {
                        JSONArray productList = res.getJSONArray("response_message");
                        Intent intent = new Intent(context, ProductListNavActivity.class);
                        intent.putExtra("wizglobal.productListNav", productList.toString());
                        context.startActivity(intent);
                    } else if (LIST_REQUEST == 200) {
                        JSONArray productList = res.getJSONArray("response_message");
                        Intent intent = new Intent(context, ProductListInterestActivity.class);
                        intent.putExtra("wizglobal.productListNav", productList.toString());
                        context.startActivity(intent);
                    } else if (LIST_REQUEST == 300) {
                        JSONArray accountList = res.getJSONArray("response_message");
                        Intent intent = new Intent(context, AccountList.class);
                        intent.putExtra("wizglobal.accountList", accountList.toString());
                        intent.putExtra("wizglobal.listAction", "balance");
                        context.startActivity(intent);
                    } else if (LIST_REQUEST == 400) {
                        JSONArray accountList = res.getJSONArray("response_message");
                        Intent intent = new Intent(context, AccountList.class);
                        intent.putExtra("wizglobal.accountList", accountList.toString());
                        intent.putExtra("wizglobal.listAction", "sell");
                        context.startActivity(intent);
                    } else if (LIST_REQUEST == 500) {
                        JSONArray accountList = res.getJSONArray("response_message");
                        Intent intent = new Intent(context, AccountList.class);
                        intent.putExtra("wizglobal.accountList", accountList.toString());
                        intent.putExtra("wizglobal.listAction", "transfer");
                        context.startActivity(intent);
                    } else if (LIST_REQUEST == 600) {
                        JSONArray accountList = res.getJSONArray("response_message");
                        Intent intent = new Intent(context, AccountList.class);
                        intent.putExtra("wizglobal.accountList", accountList.toString());
                        intent.putExtra("wizglobal.listAction", "mini_statement");
                        context.startActivity(intent);
                    } else if (LIST_REQUEST == 700) {
                        JSONArray customerList = res.getJSONArray("response_message");
                        Intent intent = new Intent(context, CustomerListActivity.class);
                        intent.putExtra("wizglobal.customerList", customerList.toString());
                        context.startActivity(intent);
                    } else if (LIST_REQUEST == 800) {
                        JSONArray agentList = res.getJSONArray("response_message");
                        Intent intent = new Intent(context, AccountAgentsActivity.class);
                        intent.putExtra("wizglobal.agentList", agentList.toString());
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
