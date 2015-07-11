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
import android.widget.Toast;

import com.beardedhen.androidbootstrap.BootstrapEditText;
import com.wizglobal.utils.Config;
import com.wizglobal.utils.Helper;
import com.wizglobal.utils.SecureServerConnect;
import com.wizglobal.utils.ServerConnect;

import org.json.JSONException;
import org.json.JSONObject;


public class ChangePasswordActivity extends ActionBarActivity {
    String serverUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
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

    public void init(){
        ActionBar ab = getActionBar();
        ab.setSubtitle(R.string.title_activity_change_password);
    }

    public void changePass(View view) {
        BootstrapEditText txtExistingPass = (BootstrapEditText) findViewById(R.id.txtExistingPass);
        BootstrapEditText txtNewPass = (BootstrapEditText) findViewById(R.id.txtNewPass);
        BootstrapEditText txtConfirmPass = (BootstrapEditText) findViewById(R.id.txtConfirmPass);

        String existingPass = txtExistingPass.getText().toString();
        String newPass = txtNewPass.getText().toString();
        String confirmPass = txtConfirmPass.getText().toString();

        if (existingPass.isEmpty()) {
            txtExistingPass.setError("Blank Field");
            txtExistingPass.setDanger();
        } else if (newPass.isEmpty()) {
            txtNewPass.setError("Blank Field");
            txtNewPass.setDanger();
        } else if (confirmPass.isEmpty()) {
            txtConfirmPass.setError("Blank Field");
            txtConfirmPass.setDanger();
        } else if (!confirmPass.equals(newPass)) {
            txtNewPass.setError("Password Mismatch");
            txtNewPass.setDanger();
            txtConfirmPass.setError("Password Mismatch");
            txtConfirmPass.setDanger();
        } else {
            try {
                if (Config.isConnected(this)) {
                    JSONObject params = new JSONObject();
                    SharedPreferences pref = getSharedPreferences("WizGlobalPreferences", 0);
                    String username = pref.getString("username", null);
                    params.put("username", username);
                    params.put("password", existingPass);
                    params.put("newPass", newPass);
                    serverUrl = Config.getServerUrl("changePassword");
                    new DoChangePassTask(params).execute(serverUrl);
                } else {
                    Toast.makeText(this, "Network Not Available", Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private class DoChangePassTask extends AsyncTask<String, Void, String> {
        private JSONObject params;
        private ProgressDialog dialog = new ProgressDialog(ChangePasswordActivity.this);

        public DoChangePassTask(JSONObject params) {
            this.params = params;
        }

        protected void onPreExecute() {
            dialog.setMessage("Submitting Request");
            dialog.setTitle("Updating Password");
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
                    String resp = res.getString("response_message");
                    AlertDialog.Builder adBuilder = new AlertDialog.Builder(ChangePasswordActivity.this);
                    //Set title
                    adBuilder.setTitle("Success");
                    adBuilder.setMessage(resp);
                    adBuilder.setCancelable(true);
                    adBuilder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Helper.logout(ChangePasswordActivity.this);
                            finish();
                        }
                    });
                    AlertDialog alertDialog = adBuilder.create();
                    alertDialog.show();
                } else {
                    Toast.makeText(ChangePasswordActivity.this, res.getString("response_message"), Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                Toast.makeText(ChangePasswordActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }
    }

}
