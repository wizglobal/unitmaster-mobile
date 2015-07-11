package com.wizglobal.app;

import android.app.ActionBar;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.wizglobal.utils.Helper;
import com.wizglobal.utils.TripleEntryListAdapter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class AccountAgentsActivity extends ActionBarActivity {
    JSONArray agentList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_agents);
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
        //Set Action Bar Sub menu
        ActionBar ab = getActionBar();
        ab.setSubtitle(R.string.title_activity_account_agents);

        //Get Agent List
        Intent callerIntent = getIntent();
        try {
            agentList = new JSONArray(callerIntent.getStringExtra("wizglobal.agentList"));
            List<String> names = new ArrayList<String>();
            List<String> accNos = new ArrayList<String>();
            List<String> gsmNos = new ArrayList<String>();
            for (int i = 0; i < agentList.length(); i++) {
                JSONObject agent = agentList.getJSONObject(i);
                names.add(agent.getString("agentName"));
                accNos.add(agent.getString("accNo"));
                gsmNos.add(agent.getString("gsmNo"));
            }
            ListView lvMenu = (ListView) findViewById(R.id.lvAccountAgents);
            lvMenu.setAdapter(new TripleEntryListAdapter(this, null, names.toArray(new String[names.size()]), accNos.toArray(new String[accNos.size()]), gsmNos.toArray(new String[gsmNos.size()])));
            lvMenu.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
            lvMenu.setTextFilterEnabled(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
