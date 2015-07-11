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
import com.wizglobal.utils.Helper;
import com.wizglobal.utils.TripleEntryListAdapter;

import org.json.JSONArray;


public class AccountList extends ActionBarActivity {
JSONArray accountList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_list);

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
        //Set Action Bar Sub title
        ActionBar ab = getActionBar();
        ab.setSubtitle(R.string.title_activity_account_list);

        Intent callingIntent = getIntent();
        String accounts = callingIntent.getStringExtra("wizglobal.accountList");
        String listAction = callingIntent.getStringExtra("wizglobal.listAction");
        Log.d(Config.getDebugTag(), accounts);
        try {
            accountList = new JSONArray(accounts);
            String[] list1 = new String[accountList.length()];
            String[] list2 = new String[accountList.length()];
            String[] list3 = new String[accountList.length()];
            for (int i = 0; i < accountList.length(); i++) {
                list1[i] = accountList.getJSONObject(i).getString("descript");
                list2[i] = accountList.getJSONObject(i).getString("fundType");
                list3[i] = accountList.getJSONObject(i).getString("accountNo");
            }
            ListView lvPLNav = (ListView) findViewById(R.id.lvAccList);
            lvPLNav.setAdapter(new TripleEntryListAdapter(this, listAction,list1, list2, list3,accounts));
            lvPLNav.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
            lvPLNav.setTextFilterEnabled(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
