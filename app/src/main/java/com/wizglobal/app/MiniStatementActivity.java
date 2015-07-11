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


public class MiniStatementActivity extends ActionBarActivity {
    JSONArray statementList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mini_statement);
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
        ActionBar ab = getActionBar();
        ab.setSubtitle(R.string.title_activity_mini_statement);

        Intent callingIntent = getIntent();
        String list = callingIntent.getStringExtra("wizglobal.statementList");
        Log.d(Config.getDebugTag(), list);
        try {
            statementList = new JSONArray(list);
            String[] date = new String[statementList.length()];
            String[] amount = new String[statementList.length()];
            String[] transType = new String[statementList.length()];
            for (int i = 0; i < statementList.length(); i++) {
                date[i] = statementList.getJSONObject(i).getString("date");
                transType[i] = statementList.getJSONObject(i).getString("trans_type");
                amount[i] = statementList.getJSONObject(i).getString("amount");
            }
            ListView lvPLNav = (ListView) findViewById(R.id.lvMiniStatement);
            lvPLNav.setAdapter(new TripleEntryListAdapter(this, "", date, transType, amount));
            lvPLNav.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
            lvPLNav.setTextFilterEnabled(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
