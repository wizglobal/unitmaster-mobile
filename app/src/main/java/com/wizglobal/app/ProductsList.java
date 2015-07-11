package com.wizglobal.app;

import android.app.ActionBar;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.wizglobal.app.R;
import com.wizglobal.utils.Helper;
import com.wizglobal.utils.SingleListAdapter;

import org.json.JSONArray;

public class ProductsList extends ActionBarActivity {
    JSONArray productList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products_list);

        init();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        if (Helper.isLoggedIn(this)) {
            getMenuInflater().inflate(R.menu.products_list, menu);
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
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void init() {
        //Set ActionBar Subtitle
        ActionBar ab = getActionBar();
        ab.setSubtitle(R.string.title_activity_products_list);

        Intent callingIntent = getIntent();
        String list = callingIntent.getStringExtra("wizglobal.productsList");
        try {
            productList = new JSONArray(list);
            String[] descriptions = new String[productList.length()];
            for (int i = 0; i < productList.length(); i++) {
                descriptions[i] = productList.getJSONObject(i).getString("description");
            }
            ListView lvPLNav = (ListView) findViewById(R.id.lvProductsList);
            lvPLNav.setAdapter(new SingleListAdapter(this, descriptions, productList, null));
            lvPLNav.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
            lvPLNav.setTextFilterEnabled(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
