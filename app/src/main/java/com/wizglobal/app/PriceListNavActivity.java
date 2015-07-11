package com.wizglobal.app;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
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


public class PriceListNavActivity extends ActionBarActivity {
    JSONArray priceList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_price_list_nav);
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
        //Set Action Bar Sub title
        ActionBar ab = getActionBar();
        ab.setSubtitle(R.string.title_activity_price_list_nav);

        Intent callingIntent = getIntent();
        String list = callingIntent.getStringExtra("wizglobal.priceListNav");
        Log.d(Config.getDebugTag(), list);
        try {
            priceList = new JSONArray(list);
            DateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
            String[] priceNavDates = new String[priceList.length()];
            String[] priceNavPrices = new String[priceList.length()];
            int[] imageId = new int[priceList.length()];
            Log.d(Config.getDebugTag(), "Array Size: " + priceList.length());
            for (int i = 0; i < priceList.length(); i++) {
                priceNavDates[i] = priceList.getJSONObject(i).getString("nav_date");
                priceNavPrices[i] = "KES " + priceList.getJSONObject(i).getString("p_price");
                DecimalFormat dcf = new DecimalFormat("#,###,###,##0.00");
                if (i != priceList.length() - 1) {
                    String pa = priceList.getJSONObject(i + 1).getString("p_price");
                    String am = priceList.getJSONObject(i).getString("p_price");
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
/*
Log.d(Config.getDebugTag(), "Price Nav Dates: " + Arrays.toString(priceNavDates));
Log.d (Config.getDebugTag(),"Actual Prices Prices"+Arrays.toString(priceNavPrices));
Log.d(Config.getDebugTag(), "ImageId: " + Arrays.toString(imageId));
*/
            ListView lvPLNav = (ListView) findViewById(R.id.lvPriceListNav);
            lvPLNav.setAdapter(new DoubleItemImageListAdapter(this, null, priceNavDates, priceNavPrices, imageId));
            lvPLNav.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
            lvPLNav.setTextFilterEnabled(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
