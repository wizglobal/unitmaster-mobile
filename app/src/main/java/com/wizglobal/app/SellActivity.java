package com.wizglobal.app;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.wizglobal.utils.Config;
import com.wizglobal.utils.Helper;
import com.wizglobal.utils.SecureServerConnect;
import com.wizglobal.utils.ServerConnect;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DecimalFormat;

public class SellActivity extends ActionBarActivity {
    JSONObject res;
    TextView txtAccNo;
    TextView txtBal;
    String accNo;
    String balance;
    String serverUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sell);

        init();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_change_password) {
            Helper.changePin(this);
        } else if (id == R.id.action_logout) {
            Helper.logout(this);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }


    public void init() {
        //Set ActionBar Subtitle
        ActionBar ab = getActionBar();
        ab.setSubtitle(R.string.title_activity_sell);

        Intent callerIntent = getIntent();
        try {
            res = new JSONObject(callerIntent.getStringExtra("wizglobal.balRes"));
            accNo = res.getString("accNo");
            balance = res.getString("balance");
            //Inputs
            txtAccNo = (TextView) findViewById(R.id.txtSellAccNo);
            txtBal = (TextView) findViewById(R.id.txtSellBal);
            //Set AccNo and Bal
            txtAccNo.setText(accNo);
            txtBal.setText("KES " + balance);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void executeSell(View view) {
        EditText txtAmount = (EditText) findViewById(R.id.txtSellAmount);
        final String amount = txtAmount.getText().toString();
        if (amount.equals("")) {
            txtAmount.setError("Enter Valid Amount");
        } else {
            String message = "Are you sure you want to Withdraw KSH " + amount + "" +
                    "\nFrom Account No. " + accNo + "?";
            AlertDialog.Builder adBuilder = new AlertDialog.Builder(SellActivity.this);
            //Set title
            adBuilder.setTitle("Withdraw");
            adBuilder.setMessage(message);
            adBuilder.setCancelable(false);
            adBuilder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    try {
                        DecimalFormat df = new DecimalFormat("#,###,###,##0.00");
                        Double bal = df.parse(balance).doubleValue();
                        if (Double.valueOf(amount) > bal) {
                            Toast.makeText(SellActivity.this, "Insufficient Balance", Toast.LENGTH_LONG).show();
                            dialog.cancel();
                        } else {
                            //Get Member No
                            SharedPreferences pref = getSharedPreferences("WizGlobalPreferences", 0);
                            String memberNo = pref.getString("member", null);
                            //Package Parameters in JSONObject
                            JSONObject params = new JSONObject();
                            params.put("memberNo", memberNo);
                            params.put("accountNo", txtAccNo.getText().toString());
                            params.put("amount", amount);
                            serverUrl = Config.getServerUrl("sell");
                            new DoExecuteSell(params).execute(serverUrl);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            adBuilder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            final AlertDialog alertDialog = adBuilder.create();
            alertDialog.show();
        }
    }


    private class DoExecuteSell extends AsyncTask<String, Void, String> {
        private JSONObject params;
        private ProgressDialog dialog = new ProgressDialog(SellActivity.this);

        public DoExecuteSell(JSONObject params) {
            this.params = params;
        }

        protected void onPreExecute() {
            dialog.setMessage("Submitting Request");
            dialog.setTitle("Processing Sale");
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
                    String message = res.getString("response_message");
                    AlertDialog.Builder adBuilder = new AlertDialog.Builder(SellActivity.this);
                    //Set title
                    adBuilder.setTitle("Success");
                    adBuilder.setMessage(message);
                    adBuilder.setCancelable(false);
                    adBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            finish();
                        }
                    });
//            adBuilder.setNegativeButton(getResources().getString(R.string.msgConfirmNo), new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    dialog.cancel();
//                }
//            });
                    final AlertDialog alertDialog = adBuilder.create();
                    alertDialog.show();

                } else {
                    Toast.makeText(SellActivity.this, res.getString("response_message"), Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                Toast.makeText(SellActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }
    }
}
