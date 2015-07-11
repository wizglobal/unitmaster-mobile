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
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.wizglobal.utils.Config;
import com.wizglobal.utils.Helper;
import com.wizglobal.utils.SecureServerConnect;
import com.wizglobal.utils.ServerConnect;
import com.wizglobal.utils.TripleEntryListAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;


public class TransferActivity extends ActionBarActivity {
    JSONObject res;
    TextView txtAccNo;
    TextView txtBal;
    JSONArray accountList;
    List<String> accountNoList;
    List<String> accountDescList;
    String selectedAccNo;
    String selectedAccDesc;
    String balance;
    String serverUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfer);
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
        ab.setSubtitle(R.string.title_activity_transfer);

        Intent callerIntent = getIntent();
        try {
            res = new JSONObject(callerIntent.getStringExtra("wizglobal.balRes"));
            accountList = new JSONArray(callerIntent.getStringExtra("wizglobal.accountList"));
            Log.d(Config.getDebugTag(), "Account List: " + accountList);
            selectedAccNo = res.getString("accNo");
            selectedAccDesc = res.getString("accDesc");
            Log.d(Config.getDebugTag(), "Account Desc: " + selectedAccDesc);
            balance = res.getString("balance");
            //Inputs
            txtAccNo = (TextView) findViewById(R.id.txtTransAccNo);
            txtBal = (TextView) findViewById(R.id.txtTransBal);
            //Set AccNo and Bal
            txtAccNo.setText(selectedAccDesc);
            txtBal.setText("KES " + balance);

            //Initialize Spinner
            accountNoList = new ArrayList<String>();
            accountDescList = new ArrayList<String>();
            for (int i = 0; i < accountList.length(); i++) {
                accountNoList.add(accountList.getJSONObject(i).getString("accountNo"));
                accountDescList.add(accountList.getJSONObject(i).getString("accountName"));
            }
            accountDescList.remove(selectedAccDesc);
            ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(this, R.layout.wizglobal_spinner_items, accountDescList.toArray(new String[accountDescList.size()]));
            Spinner spinnerAccountList = (Spinner) findViewById(R.id.accountSpinner);
            spinnerAccountList.setPrompt(getResources().getString(R.string.spinnerAccountPrompt));
            spinnerAccountList.setAdapter(adapter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void executeTransfer(View view) {
        try {
            EditText txtAmount = (EditText) findViewById(R.id.txtTransAmount);
            Spinner accountSpinner = (Spinner) findViewById(R.id.accountSpinner);
            final String amount = txtAmount.getText().toString();
            String accFrom = txtAccNo.getText().toString();
            for (int i = 0; i < accountList.length(); i++) {
                if (accFrom.equals(accountList.getJSONObject(i).getString("accountName"))) {
                    accFrom = accountList.getJSONObject(i).getString("accountNo");
                }
            }
            final String accountFrom = accFrom;
            Log.d(Config.getDebugTag(),"Account From: "+accountFrom);
            String accTo = accountSpinner.getSelectedItem().toString();
            for (int i = 0; i < accountList.length(); i++) {
                if (accTo.equals(accountList.getJSONObject(i).getString("accountName"))) {
                    accTo = accountList.getJSONObject(i).getString("accountNo");
                }
            }
            final String accountTo = accTo;
            Log.d(Config.getDebugTag(),"Account To: "+accountTo);
            if (amount.equals("")) {
                txtAmount.setError("Enter Valid Amount");
            } else {
                String message = "Are you sure you want to transfer KES: " + amount
                        + "\nFrom account " + accountFrom
                        + "\nTo account " + accountTo + "?";
                AlertDialog.Builder adBuilder = new AlertDialog.Builder(this);
                //Set title
                adBuilder.setTitle("Transfer");
                adBuilder.setMessage(message);
                adBuilder.setCancelable(false);
                adBuilder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            DecimalFormat df = new DecimalFormat("#,###,###,##0.00");
                            Double bal = df.parse(balance).doubleValue();
                            if (Double.valueOf(amount) > bal) {
                                Toast.makeText(TransferActivity.this, "Insufficient Balance", Toast.LENGTH_LONG).show();
                                dialog.cancel();
                            } else {
                                //Get Member No
                                SharedPreferences pref = getSharedPreferences("WizGlobalPreferences", 0);
                                String memberNo = pref.getString("member", null);
                                //Package Parameters in JSONObject
                                JSONObject params = new JSONObject();
                                params.put("memberNo", memberNo);
                                params.put("accountFrom", accountFrom);
                                params.put("accountTo", accountTo);
                                params.put("amount", amount);
                                serverUrl = Config.getServerUrl("transfer");
                                new DoExecuteTransfer(params).execute(serverUrl);
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

        } catch (Exception e) {

        }

    }

    private class DoExecuteTransfer extends AsyncTask<String, Void, String> {
        private JSONObject params;
        private ProgressDialog dialog = new ProgressDialog(TransferActivity.this);

        public DoExecuteTransfer(JSONObject params) {
            this.params = params;
        }

        protected void onPreExecute() {
            dialog.setMessage("Submitting Request");
            dialog.setTitle("Processing Transfer");
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
                    String message = res.getString("response_message");
                    AlertDialog.Builder adBuilder = new AlertDialog.Builder(TransferActivity.this);
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
                    Toast.makeText(TransferActivity.this, res.getString("response_message"), Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                Toast.makeText(TransferActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }
    }

}
