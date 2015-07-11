package com.wizglobal.app;

import android.app.ActionBar;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.wizglobal.utils.Config;
import com.wizglobal.utils.DoubleItemImageListAdapter;
import com.wizglobal.utils.Helper;

import org.json.JSONArray;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;


public class PriceListInterestActivity extends ActionBarActivity {
    JSONArray priceList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_price_list_interest);

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

    public void init() {
//        Set Action Bar Subtitle
        ActionBar ab = getActionBar();
        ab.setSubtitle(R.string.title_activity_price_list_interest);

        Intent callingIntent = getIntent();
        String list = callingIntent.getStringExtra("wizglobal.priceListInterest");
        Log.d(Config.getDebugTag(), list);
        try {
            priceList = new JSONArray(list);
            DateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
            String[] rate_date = new String[priceList.length()];
            String[] rate = new String[priceList.length()];
            int[] imageId = new int[priceList.length()];
            for (int i = 0; i < priceList.length(); i++) {
                rate_date[i] = priceList.getJSONObject(i).getString("rate_date");
                rate[i] = "KES " + priceList.getJSONObject(i).getString("rate");
                DecimalFormat dcf = new DecimalFormat("#,###,###,##0.00");
                if (i != priceList.length() - 1) {
                    String pa = priceList.getJSONObject(i + 1).getString("rate");
                    String am = priceList.getJSONObject(i).getString("rate");
                    Double prevAmt = dcf.parse(pa).doubleValue();
                    Double amt = dcf.parse(am).doubleValue();
                    if (amt > prevAmt) {
                        imageId[i] = R.drawable.ic_green_up;
                    } else if (amt < prevAmt) {
                        imageId[i] = R.drawable.ic_red_down;
                    } else if (amt.equals(prevAmt)) {
                        imageId[i] = R.drawable.ic_remove_yellow;
                    }
                } else {
                    imageId[priceList.length() - 1] = R.drawable.ic_remove_yellow;
                }
            }
            ListView lvPLNav = (ListView) findViewById(R.id.lvPriceListInterest);
            lvPLNav.setAdapter(new DoubleItemImageListAdapter(this, null, rate_date, rate, imageId));
            lvPLNav.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
            lvPLNav.setTextFilterEnabled(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
