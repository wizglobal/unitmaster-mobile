package com.wizglobal.app;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.wizglobal.app.R;
import com.wizglobal.utils.Config;
import com.wizglobal.utils.SecureServerConnect;
import com.wizglobal.utils.ServerConnect;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class MainActivity extends ActionBarActivity {
String serverUrl;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void init(){
        ActionBar ab = getActionBar();
        ab.setSubtitle(R.string.title_activity_main);
    }

    public void loadLogin(View view) {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    public void loadSelfRegister(View view) {
        Intent intent = new Intent(this, RegisterCustomerActivity.class);
        startActivity(intent);
    }

    public void loadPriceList(View view) {
        Intent intent = new Intent(this, PriceListActivity.class);
        startActivity(intent);
    }

    public void loadProductList(View view) {
        try {
            if (Config.isConnected(this)) {
                JSONObject params = new JSONObject();
                params.put("action", "GetProductsList");
                serverUrl = Config.getServerUrl("productList");
                new DoGetProductListTask(params).execute(serverUrl);
            } else {
                Toast.makeText(this, "No Network Connectivity", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class DoGetProductListTask extends AsyncTask<String, Void, String> {
        private JSONObject params;
        private ProgressDialog dialog = new ProgressDialog(MainActivity.this);

        public DoGetProductListTask(JSONObject params) {
            this.params = params;
        }

        protected void onPreExecute() {
            dialog.setMessage("Submitting Request");
            dialog.setTitle("Getting Product List");
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
                    JSONArray productList = res.getJSONArray("response_message");
                    Intent intent = new Intent(MainActivity.this, ProductsList.class);
                    intent.putExtra("wizglobal.productsList", productList.toString());
                    startActivity(intent);
                } else {
                    Toast.makeText(MainActivity.this, res.getString("response_message"), Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }
    }
}
