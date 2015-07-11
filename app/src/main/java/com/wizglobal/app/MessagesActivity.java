package com.wizglobal.app;


import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ListView;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.BootstrapEditText;
import com.wizglobal.utils.Config;
import com.wizglobal.utils.Helper;
import com.wizglobal.utils.ImageListAdapter;
import com.wizglobal.utils.MessageListAdapter;
import com.wizglobal.utils.SecureServerConnect;
import com.wizglobal.utils.ServerConnect;
import com.wizglobal.utils.TabsPagerAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class MessagesActivity extends ActionBarActivity implements ActionBar.TabListener {
    String serverUrl;
    JSONArray inboxMessages;
    JSONArray outboxMessages;

    private ViewPager viewPager;
    private TabsPagerAdapter pAdapter;
    private ActionBar actionBar;

    BootstrapEditText txtSubject;
    BootstrapEditText txtMessage;
    //Tab titles
    String[] tabs = {"Compose", "Inbox", "Outbox"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_messages);
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
        ActionBar ab =  getSupportActionBar();
        ab.setSubtitle(R.string.title_activity_customer_feedback_messages_avctivity);


        viewPager = (ViewPager) findViewById(R.id.pager);
        actionBar = getSupportActionBar();
        pAdapter = new TabsPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pAdapter);
        actionBar.setHomeButtonEnabled(false);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        //Add Tabs
        for (String tabName : tabs) {
            actionBar.addTab(actionBar.newTab().setText(tabName).setTabListener(this));
        }

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                //make respective tab selected
                actionBar.setSelectedNavigationItem(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, android.support.v4.app.FragmentTransaction fragmentTransaction) {
        //Show respective fragment view
        viewPager.setCurrentItem(tab.getPosition());
        if (tab.getPosition() == 1) {
            loadInbox();
        } else if (tab.getPosition() == 2) {
            loadOutbox();
        }
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, android.support.v4.app.FragmentTransaction fragmentTransaction) {

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, android.support.v4.app.FragmentTransaction fragmentTransaction) {

    }

    public void loadInbox() {
        if (Config.isConnected(this)) {
            SharedPreferences pref = getSharedPreferences("WizGlobalPreferences", 0);
            String memberNo = pref.getString("member", null);
            JSONObject params = new JSONObject();
            try {
                params.put("memberNo", memberNo);
                params.put("action", "loadInbox");
                serverUrl = Config.getServerUrl("message");
                new DoLoadInboxTask(params).execute(serverUrl);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(this, "No Network Connection", Toast.LENGTH_LONG).show();
        }
    }

    public void loadOutbox() {
        if (Config.isConnected(this)) {
            SharedPreferences pref = getSharedPreferences("WizGlobalPreferences", 0);
            String memberNo = pref.getString("member", null);
            JSONObject params = new JSONObject();
            try {
                params.put("memberNo", memberNo);
                params.put("action", "loadOutbox");
                serverUrl = Config.getServerUrl("message");
                new DoLoadOutboxTask(params).execute(serverUrl);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(this, "No Network Connection", Toast.LENGTH_LONG).show();
        }
    }


    public void postMessage(View view) {
        txtSubject = (BootstrapEditText) findViewById(R.id.txtSubject);
        txtMessage = (BootstrapEditText) findViewById(R.id.txtMessage);

        String subject = txtSubject.getText().toString();
        String message = txtMessage.getText().toString();

        if (subject.isEmpty()) {
            txtSubject.setError("Blank Field");
            txtSubject.setDanger();
        } else if (message.isEmpty()) {
            txtMessage.setError("Blank Field");
            txtMessage.setDanger();
        } else {
            try {
                if (Config.isConnected(this)) {
                    SharedPreferences pref = getSharedPreferences("WizGlobalPreferences", 0);
                    String memberNo = pref.getString("member", null);
                    String userType = pref.getString("userType", null);
                    JSONObject params = new JSONObject();
                    params.put("action", "postMessage");
                    params.put("category", userType);
                    params.put("memberNo", memberNo);
                    params.put("subject", subject);
                    params.put("description", message);
                    serverUrl = Config.getServerUrl("message");
                    new DoPostMessage(params).execute(serverUrl);
                } else {
                    Toast.makeText(this, "Network Not Available", Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    private class DoPostMessage extends AsyncTask<String, Void, String> {
        private JSONObject params;

        public DoPostMessage(JSONObject params) {
            this.params = params;
        }

        protected void onPreExecute() {
            setSupportProgressBarIndeterminateVisibility(true);
        }

        @Override
        protected String doInBackground(String... urls) {
            try {
                if (serverUrl.contains("https")) {
                    Log.d(Config.getDebugTag(), "SecureServerConnect Called");
                    SecureServerConnect sc = new SecureServerConnect();
                    return sc.processRequest(urls[0], params);
                } else {
                    Log.d(Config.getDebugTag(), "ServerConnect Called");
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
            setSupportProgressBarIndeterminateVisibility(false);
            try {
                JSONObject res = new JSONObject(result);
                Log.d(Config.getDebugTag(), "Response Code: " + res.get("response_code") + "\nResponse Message: " + res.get("response_message").toString());
                if (res.getString("response_code").equalsIgnoreCase("0")) {
                    String resp = res.getString("response_message");
                    Toast.makeText(MessagesActivity.this, resp, Toast.LENGTH_LONG).show();
                    txtSubject.setText(null);
                    txtSubject.setHint(R.string.hintSubject);
                    txtMessage.setText(null);
                    txtMessage.setHint(R.string.hintCompose);
                } else {
                    Toast.makeText(MessagesActivity.this, res.getString("response_message"), Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                Toast.makeText(MessagesActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }
    }


    private class DoLoadInboxTask extends AsyncTask<String, Void, String> {
        private JSONObject params;

        public DoLoadInboxTask(JSONObject params) {
            this.params = params;
        }

        @Override
        protected void onPreExecute() {
            setSupportProgressBarIndeterminateVisibility(true);
        }

        @Override
        protected String doInBackground(String... urls) {
            try {
                if (serverUrl.contains("https")) {
                    Log.d(Config.getDebugTag(), "SecureServerConnect Called");
                    SecureServerConnect sc = new SecureServerConnect();
                    return sc.processRequest(urls[0], params);
                } else {
                    Log.d(Config.getDebugTag(), "ServerConnect Called");
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
            setSupportProgressBarIndeterminateVisibility(false);
            try {
                JSONObject res = new JSONObject(result);
                Log.d(Config.getDebugTag(), "Response Code: " + res.get("response_code") + "\nResponse Message: " + res.get("response_message").toString());
                if (res.getString("response_code").equalsIgnoreCase("0")) {
                    inboxMessages = res.getJSONArray("response_message");
                    List<String> subjects = new ArrayList<String>();
                    List<String> contents = new ArrayList<String>();
                    List<String> dates = new ArrayList<String>();
                    for (int i = 0; i < inboxMessages.length(); i++) {
                        JSONObject msg = inboxMessages.getJSONObject(i);
                        subjects.add(msg.getString("subject"));
                        contents.add(msg.getString("content"));
                        dates.add(msg.getString("date"));
                    }
                    ListView lvMenu = (ListView) findViewById(R.id.lvInbox);
                    lvMenu.setAdapter(new MessageListAdapter(getApplicationContext(), subjects.toArray(new String[subjects.size()]), contents.toArray(new String[contents.size()]), dates.toArray(new String[dates.size()])));
                    lvMenu.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
                    lvMenu.setTextFilterEnabled(true);

                } else {
                    inboxMessages = res.getJSONArray("response_message");
                    List<String> subjects = new ArrayList<String>();
                    List<String> contents = new ArrayList<String>();
                    List<String> dates = new ArrayList<String>();
                    for (int i = 0; i < inboxMessages.length(); i++) {
                        JSONObject msg = inboxMessages.getJSONObject(i);
                        subjects.add(msg.getString("subject"));
                        contents.add(msg.getString("content"));
                        dates.add(msg.getString("date"));
                    }
                    ListView lvMenu = (ListView) findViewById(R.id.lvInbox);
                    lvMenu.setAdapter(new MessageListAdapter(getApplicationContext(), subjects.toArray(new String[subjects.size()]), contents.toArray(new String[contents.size()]), dates.toArray(new String[dates.size()])));
                    lvMenu.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
                    lvMenu.setTextFilterEnabled(true);
                }
            } catch (JSONException e) {
                Toast.makeText(MessagesActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }
    }

    private class DoLoadOutboxTask extends AsyncTask<String, Void, String> {
        private JSONObject params;

        public DoLoadOutboxTask(JSONObject params) {
            this.params = params;
        }

        @Override
        protected void onPreExecute() {
            setSupportProgressBarIndeterminateVisibility(true);
        }


        @Override
        protected String doInBackground(String... urls) {
            try {
                if (serverUrl.contains("https")) {
                    Log.d(Config.getDebugTag(), "SecureServerConnect Called");
                    SecureServerConnect sc = new SecureServerConnect();
                    return sc.processRequest(urls[0], params);
                } else {
                    Log.d(Config.getDebugTag(), "ServerConnect Called");
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
            setSupportProgressBarIndeterminateVisibility(false);
            try {
                JSONObject res = new JSONObject(result);
                Log.d(Config.getDebugTag(), "Response Code: " + res.get("response_code") + "\nResponse Message: " + res.get("response_message").toString());
                if (res.getString("response_code").equalsIgnoreCase("0")) {
                    outboxMessages = res.getJSONArray("response_message");
                    List<String> subjects = new ArrayList<String>();
                    List<String> contents = new ArrayList<String>();
                    List<String> dates = new ArrayList<String>();
                    for (int i = 0; i < outboxMessages.length(); i++) {
                        JSONObject msg = outboxMessages.getJSONObject(i);
                        subjects.add(msg.getString("subject"));
                        contents.add(msg.getString("content"));
                        dates.add(msg.getString("date"));
                    }
                    ListView lvMenu = (ListView) findViewById(R.id.lvOutbox);
                    lvMenu.setAdapter(new MessageListAdapter(getApplicationContext(), subjects.toArray(new String[subjects.size()]), contents.toArray(new String[contents.size()]), dates.toArray(new String[dates.size()])));
                    lvMenu.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
                    lvMenu.setTextFilterEnabled(true);
                } else {
                    outboxMessages = res.getJSONArray("response_message");
                    List<String> subjects = new ArrayList<String>();
                    List<String> contents = new ArrayList<String>();
                    List<String> dates = new ArrayList<String>();
                    for (int i = 0; i < outboxMessages.length(); i++) {
                        JSONObject msg = outboxMessages.getJSONObject(i);
                        subjects.add(msg.getString("subject"));
                        contents.add(msg.getString("content"));
                        dates.add(msg.getString("date"));
                    }
                    ListView lvMenu = (ListView) findViewById(R.id.lvOutbox);
                    lvMenu.setAdapter(new MessageListAdapter(getApplicationContext(), subjects.toArray(new String[subjects.size()]), contents.toArray(new String[contents.size()]), dates.toArray(new String[dates.size()])));
                    lvMenu.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
                    lvMenu.setTextFilterEnabled(true);
                }
            } catch (JSONException e) {
                Toast.makeText(MessagesActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }
    }

}
