package com.wizglobal.app;

import android.app.ActionBar;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.wizglobal.utils.Config;
import com.wizglobal.utils.Helper;
import com.wizglobal.utils.ImageListAdapter;


public class MenuActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        Log.d(Config.getDebugTag(), "Ready to call init");
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
        //Set Action Bar Subtitle
        ActionBar ab = getActionBar();
        ab.setSubtitle(R.string.title_activity_menu);

        Log.d(Config.getDebugTag(), "Loading main menu");
        SharedPreferences pref = getSharedPreferences("WizGlobalPreferences", 0);
        String userType = pref.getString("userType", null);
        Log.d(Config.getDebugTag(), "User type:"+ userType);

        int[] menuListImages = {R.drawable.ic_pricelist, R.drawable.ic_balance, R.drawable.ic_sell, R.drawable.ic_transfer, R.drawable.ic_mini_statement, R.drawable.ic_agents, R.drawable.ic_message};
        String[] menuList = {"Price List", "Balance Inquiry", "Sell", "Transfer", "Mini Statement", "Account Agents", "Messages"};
        if (userType.equalsIgnoreCase("agent")) {
            menuListImages = new int[]{R.drawable.ic_pricelist, R.drawable.ic_balance, R.drawable.ic_sell, R.drawable.ic_transfer, R.drawable.ic_mini_statement, R.drawable.ic_agents, R.drawable.ic_register_customer, R.drawable.ic_customer_list, R.drawable.ic_feedback};
            menuList = new String[]{"Price List", "Balance Inquiry", "Sell", "Transfer", "Mini Statement", "Account Agents", "Register Customer", "Customer List", "Feedback"};
        }

        ListView lvMenu = (ListView) findViewById(R.id.lvMenu);
        lvMenu.setAdapter(new ImageListAdapter(this, menuList, menuListImages));
        lvMenu.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        lvMenu.setTextFilterEnabled(true);
    }
}
