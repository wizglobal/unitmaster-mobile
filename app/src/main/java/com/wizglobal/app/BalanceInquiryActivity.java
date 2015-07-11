package com.wizglobal.app;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.wizglobal.utils.Helper;

import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class BalanceInquiryActivity extends ActionBarActivity {
    JSONObject res;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_balance_inquiry);

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
        //Set Action bar Sub menu
        ActionBar ab = getActionBar();
        ab.setSubtitle(R.string.title_activity_balance_inquiry);
        Intent callerIntent = getIntent();
        try {
            res = new JSONObject(callerIntent.getStringExtra("wizglobal.balRes"));
            String accNo = res.getString("accNo");
            String balance = res.getString("balance");
            DateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
            String message = "Account Balance As of\n" + df.format(new Date()) + "\nIs KSH: " + balance;
            AlertDialog.Builder adBuilder = new AlertDialog.Builder(this);
            //Set title
            adBuilder.setTitle("Account Number: " + accNo);
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
