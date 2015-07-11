package com.wizglobal.app;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.support.v4.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.BootstrapEditText;
import com.wizglobal.utils.Config;
import com.wizglobal.utils.Helper;
import com.wizglobal.utils.SecureServerConnect;
import com.wizglobal.utils.ServerConnect;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class RegisterCustomerActivity extends ActionBarActivity {
    //Get Form Views
    BootstrapEditText txtFname;
    BootstrapEditText txtSurname;
    BootstrapEditText txtOnames;
    BootstrapEditText txtTown;
    BootstrapEditText txtPhone;
    BootstrapEditText txtEmail;
    BootstrapEditText txtIdno;
    static BootstrapEditText txtDob;
    static FragmentManager fragManager;
    String serverUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_customer);
        init();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        if (Helper.isLoggedIn(this)) {
            getMenuInflater().inflate(R.menu.menu, menu);
            return true;
        }
        return false;
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

    public static void setDob(View view) {
        DialogFragment frag = new DatePickerFragment();
        frag.show(fragManager, "datePicker");

    }

    public void init() {
        //Set ActionBar Subtitle
        ActionBar ab = getActionBar();
        ab.setSubtitle(R.string.title_activity_register_customer);

        //Get Form Views
        txtFname = (BootstrapEditText) findViewById(R.id.txtFname);
        txtSurname = (BootstrapEditText) findViewById(R.id.txtSurname);
        txtOnames = (BootstrapEditText) findViewById(R.id.txtOnames);
        txtTown = (BootstrapEditText) findViewById(R.id.txtTown);
        txtPhone = (BootstrapEditText) findViewById(R.id.txtPhone);
        txtEmail = (BootstrapEditText) findViewById(R.id.txtEmail);
        txtIdno = (BootstrapEditText) findViewById(R.id.txtId);
        txtDob = (BootstrapEditText) findViewById(R.id.txtDob);
        txtDob.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    setDob(v);
                }
            }
        });
        fragManager = getSupportFragmentManager();
    }

    public void registerCustomer(View view) {
        //Get Text Values
        final String fname = txtFname.getText().toString();
        final String surname = txtSurname.getText().toString();
        final String onames = txtOnames.getText().toString();
        final String town = txtTown.getText().toString();
        final String phone = txtPhone.getText().toString();
        final String email = txtEmail.getText().toString();
        final String idno = txtIdno.getText().toString();
        final String dob = txtDob.getText().toString();

        if (!Helper.isValidName(fname)) {
            txtFname.setError("Enter Valid Name");
            txtFname.setDanger();
        } else if (!Helper.isValidName(surname)) {
            txtSurname.setError("Enter Valid Name");
            txtSurname.setDanger();
        } else if (!Helper.isValidOName(onames)) {
            txtOnames.setError("Enter Valid Name");
            txtOnames.setDanger();
        } else if (!Helper.isValidName(town)) {
            txtTown.setError("Enter Valid Town Name");
            txtTown.setDanger();
        } else if (!Helper.isValidPhone(phone)) {
            txtPhone.setError("Enter Valid Phone Number");
            txtPhone.setDanger();
        } else if (!Helper.isValidEmail(email)) {
            txtEmail.setError("Enter Valid Email Address");
            txtEmail.setDanger();
        } else if (!Helper.isValidIdNo(idno)) {
            txtIdno.setError("Enter Valid ID Number");
            txtIdno.setDanger();
        } else if (dob.isEmpty()) {
            txtDob.setError("Enter Valid Date Of Birth");
            txtDob.setDanger();
        } else {
            //Alert Dialog
            AlertDialog.Builder adBuilder = new AlertDialog.Builder(RegisterCustomerActivity.this);
            //Set title
            adBuilder.setTitle("Confirm Save");
            adBuilder.setMessage("Are You Sure You Want To Register This Customer?");
            adBuilder.setCancelable(false);
            adBuilder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    try {
                        //Customer Details
                        JSONObject customerDetails = new JSONObject();
                        customerDetails.put("fname", fname);
                        customerDetails.put("surname", surname);
                        customerDetails.put("onames", onames);
                        customerDetails.put("town", town);
                        customerDetails.put("phone", phone);
                        customerDetails.put("email", email);
                        customerDetails.put("idno", idno);
                        customerDetails.put("dob", dob);
                        //Agent Details
                        SharedPreferences pref = getSharedPreferences("WizGlobalPreferences", 0);
                        String agentNo = pref.getString("member", null);
                        agentNo = agentNo == null ? "Self" : agentNo;
                        customerDetails.put("agentNo", agentNo);
                        //Http Request Parameters

                        JSONObject params = new JSONObject();
                        params.put("action", "RegisterCustomer");
                        params.put("customerDetails", customerDetails);
                        serverUrl = Config.getServerUrl("agent");
                        new DoRegisterCustomerTask(params).execute(serverUrl);
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

    private class DoRegisterCustomerTask extends AsyncTask<String, Void, String> {
        private JSONObject params;
        private ProgressDialog dialog = new ProgressDialog(RegisterCustomerActivity.this);

        public DoRegisterCustomerTask(JSONObject params) {
            this.params = params;
        }

        protected void onPreExecute() {
            dialog.setMessage("Submitting Request");
            dialog.setTitle("Saving Customer Details");
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
                    AlertDialog.Builder adBuilder = new AlertDialog.Builder(RegisterCustomerActivity.this);
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
                    Toast.makeText(RegisterCustomerActivity.this, res.getString("response_message"), Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                Toast.makeText(RegisterCustomerActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }
    }

    public static class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
        @Override
        public Dialog onCreateDialog(Bundle savedInstance) {
            //Use the current date as the default date in the date picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            //Create new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        @Override
        public void onDateSet(DatePicker view, int year, int month, int day) {
            String date = day + "-" + month + "-" + year;
            DateFormat df = new SimpleDateFormat("dd-MM-yyyy");
            DateFormat df2 = new SimpleDateFormat("dd-MMM-yyyy");
            try {
                Date d = df.parse(date);
                txtDob.setText(df2.format(d));
            } catch (Exception e) {
                e.printStackTrace();
            }

            txtDob.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setDob(v);
                }
            });

        }

    }


}
