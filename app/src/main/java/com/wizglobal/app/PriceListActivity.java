package com.wizglobal.app;

import android.app.ActionBar;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.wizglobal.utils.Helper;
import com.wizglobal.utils.ImageListAdapter;


public class PriceListActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_price_list);
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
        //Set Action Bar Subtitle
        ActionBar ab = getActionBar();
        ab.setSubtitle(R.string.title_activity_price_list);

        int[] priceListImages = {R.drawable.ic_nav, R.drawable.ic_interest};
        String[] priceList = {"Net Asset Value", "Interest Rate"};
        ListView lvPriceList = (ListView) findViewById(R.id.lvPriceList);
//        ArrayAdapter<String> adapter = new ArrayAdapter<String>();
        lvPriceList.setAdapter(new ImageListAdapter(this, priceList, priceListImages));
        lvPriceList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        lvPriceList.setTextFilterEnabled(true);
//        lvPriceList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                loadSelection(view);
//            }
//        });
    }


}
