package com.wizglobal.app;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.wizglobal.utils.Config;
import com.wizglobal.utils.SecureServerConnect;
import com.wizglobal.utils.ServerConnect;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class LoginActivity extends ActionBarActivity {
    EditText txtUsername;
    EditText txtPassword;
    String serverUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        init();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.login, menu);
        return true;
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
        //Set Action Bar Subtitle
        ActionBar ab = getActionBar();
        ab.setSubtitle(R.string.title_activity_login);

        txtUsername = (EditText) findViewById(R.id.txtUsername);
        txtPassword = (EditText) findViewById(R.id.txtPassword);
//        txtUsername.setText("Zack");
//        txtPassword.setText("12345");
    }


    public void login(View view) {
        String username = txtUsername.getText().toString();
        String password = txtPassword.getText().toString();
        if (username.equals("")) {
            txtUsername.setError("Enter Valid Username");
        } else if (password.equals("")) {
            txtPassword.setError("Enter Valid Password");
        } else {
            try {
                JSONObject params = new JSONObject();
                params.put("username", username);
                params.put("password", password);
                params.put("app_version", Config.getAppVersion(this));
                Log.d(Config.getDebugTag(),"APP_VERSION: "+Config.getAppVersion(this));
                if (Config.isConnected(this)) {
                    serverUrl = Config.getServerUrl("authenticate");
                    new DoLoginTask(params).execute(serverUrl);
                } else {
                    Toast.makeText(this, "No Internet Connection", Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private class DoLoginTask extends AsyncTask<String, Void, String> {
        private JSONObject params;
        private ProgressDialog dialog = new ProgressDialog(LoginActivity.this);

        public DoLoginTask(JSONObject params) {
            this.params = params;
        }

        protected void onPreExecute() {
            dialog.setMessage("Submitting Request");
            dialog.setTitle("Authenticating");
            dialog.show();
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
            dialog.dismiss();
            try {
                JSONObject res = new JSONObject(result);
                Log.d(Config.getDebugTag(), "Response Code: " + res.get("response_code") + "\nResponse Message: " + res.get("response_message").toString());
                if (res.getString("response_code").equalsIgnoreCase("0")) {

                    JSONObject resp = res.getJSONObject("response_message");
                    //Create a sharedPreferences File
                    SharedPreferences pref = getSharedPreferences("WizGlobalPreferences", 0);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putString("username", resp.getString("username"));
                    editor.putString("email", resp.getString("email"));
                    editor.putString("member", resp.getString("member"));
                    editor.putString("userType",resp.getString("userType"));

                    editor.commit();


                    Intent intent = new Intent(getApplicationContext(), MenuActivity.class);
                    startActivity(intent);
                    finish();
                } else if (res.getString("response_code").equalsIgnoreCase("2")) {
                    JSONObject resp = res.getJSONObject("response_message");
                    Log.d(Config.getDebugTag(), "Response: " + res);
                    params.put("fileSize", resp.getString("fileSize"));
                    params.put("fileHash", resp.getString("fileHash"));
                    params.put("fileVersion", resp.getString("fileVersion"));
                    AlertDialog.Builder adBuilder = new AlertDialog.Builder(LoginActivity.this);
                    //Set title
                    adBuilder.setTitle("New Update Available");
                    adBuilder.setMessage("A New Update has been found. Click on the button below to update");
                    adBuilder.setCancelable(true);
                    adBuilder.setPositiveButton("Download", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            downloadUpdate(params);
                        }
                    });

                    AlertDialog alertDialog = adBuilder.create();
                    alertDialog.show();

                } else {
                    Toast.makeText(LoginActivity.this, res.getString("response_message"), Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }
    }

    public void downloadUpdate(JSONObject params) {
        String url = Config.getServerUrl("update");
        new DoUpdateTask(params).execute(url);
    }

    private class DoUpdateTask extends AsyncTask<String, Integer, String> {
        private JSONObject params;
        private ProgressDialog dialog = new ProgressDialog(LoginActivity.this);
        String res = "{'response_code':403,'message':'Unexpected_Error'}";

        public DoUpdateTask(JSONObject params) {
            this.params = params;
        }

        @Override
        protected void onPreExecute() {
            dialog.setTitle("Downloading Update");
            dialog.setMessage("Updating...");
            dialog.setIndeterminate(false);
            dialog.setMax(100);
            dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            dialog.setCancelable(true);
            dialog.show();
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);
            dialog.setProgress(progress[0]);
            if (progress[0] == 100) {
                dialog.hide();
            }
        }

        @Override
        protected String doInBackground(String... urls) {
            try {
                String fileHash = params.getString("fileHash");
                int fileSize = Integer.parseInt(params.getString("fileSize"));
                String fileVersion = params.getString("fileVersion");
                InputStream is = null;
                OutputStream os = null;
                FileOutputStream fos = null;
                try {
                    //Proxy Settings
                    URL url = new URL(urls[0]);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setReadTimeout(60000);//Milliseconds
                    conn.setConnectTimeout(15000);//Milliseconds
                    conn.setRequestMethod("GET");
                    conn.setDoInput(true);
                    conn.setDoOutput(true);
                    conn.setFixedLengthStreamingMode(params.toString().length());

                    //Set Http Header
                    conn.setRequestProperty("Content-Type", "application/json;charset=utf-8");
                    conn.setRequestProperty("X-Requested-With", "XMLHttpRequest");

                    //starts the query - Open
                    conn.connect();

                    //Send Parameters
                    os = new BufferedOutputStream(conn.getOutputStream());
                    os.write(params.toString().getBytes());

                    //Clean up
                    os.flush();

                    //Get Response
                    int response = conn.getResponseCode();//200 = Success
                    Log.d(Config.getDebugTag(), "Response Code: " + response);
                    if (response == 404) {
                        res = "{'response_code':404,'response_message':'Server_Unreachable_404'}";
                    } else {
                        is = conn.getInputStream();
                        //Read Response
                        String filename = "Wizglobal" + fileVersion + ".apk";
                        File outputfile = new File(getDownloadDir(), filename);
                        fos = new FileOutputStream(outputfile);
                        Log.d(Config.getDebugTag(), "Server File Size: " + fileSize);
                        byte[] buffer = new byte[1024];
                        int bufferLength = 0;
                        long downloadedSize = 0;

                        MessageDigest md = MessageDigest.getInstance("MD5");
                        while ((bufferLength = is.read(buffer)) !=  -1) {
                            fos.write(buffer, 0, bufferLength);
                            md.update(buffer, 0, bufferLength);
                            downloadedSize += bufferLength;
                            publishProgress((int) ((downloadedSize * 100) / fileSize));
                        }
                        fos.close();
                        byte[] md5Bytes = md.digest();
                        String md5String = Base64.encodeToString(md5Bytes, Base64.NO_WRAP);
                        Log.d(Config.getDebugTag(), "Downloaded File Size: "+downloadedSize);
                        Log.d(Config.getDebugTag(), "File Hash: " + fileHash);
                        Log.d(Config.getDebugTag(), "md5Sting: " + md5String);
                        if (md5String.equals(fileHash)) {
                            res = "{'response_code':0,'response_message':'" + outputfile.getAbsolutePath() + "'}";
                            Log.d(Config.getDebugTag(), "File Verified");
                        } else {
                            res = "{'response_code':400,'response_message':'fileMisMatchError'}";
                        }
                    }
                    return res;
                } finally {
                    if (is != null) {
                        is.close();
                    }
                    if (os != null) {
                        os.close();
                    }
                }
            } catch (IOException e) {
                Log.d(Config.getDebugTag(), e.toString());
                e.printStackTrace();
                return "{'response_code':'400','response_message':'Unexpected_Error'}";
            } catch (JSONException e) {
                e.printStackTrace();
                return "{'response_code':'401','response_message':'Unexpected_Error'}";
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
                return "{'response_code':'402','response_message':'Unexpected_Error'}";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            dialog.dismiss();
            try {
                JSONObject res = new JSONObject(result);
                if (res.getString("response_code").equalsIgnoreCase("0")) {
                    String filePath = res.getString("response_message");
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.setDataAndType(Uri.fromFile(new File(filePath)), "application/vnd.android.package-archive");
                    startActivity(intent);
                    finish();
                } else {
                    Log.d(Config.getDebugTag(), "Response Code: " + res.get("response_code") + " " + "Message: " + res.getString("response_message"));
                    Toast.makeText(getApplicationContext(), res.getString("response_message"), Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }
    }

    private File getDownloadDir() {
        File downloadDir = null;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            downloadDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            if (!downloadDir.exists()) {
                downloadDir.mkdirs();
            } else {
                Log.d(Config.getDebugTag(), "Download Directory Exists");
            }
        } else {
            Toast.makeText(this, "Storage Not Mounted", Toast.LENGTH_LONG).show();
            Log.d(Config.getDebugTag(), "External Storage is not mounted READ/WRITE");
        }
        return downloadDir;
    }
}
