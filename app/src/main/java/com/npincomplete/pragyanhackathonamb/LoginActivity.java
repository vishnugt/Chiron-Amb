package com.npincomplete.pragyanhackathonamb;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;



import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class LoginActivity extends AppCompatActivity {


    EditText et1, et2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        et1 = (EditText)findViewById(R.id.email);
        et2 = (EditText)findViewById(R.id.password);
        et1.requestFocus();

        ActionBar actionBar = getActionBar();
//        actionBar.setDisplayHomeAsUpEnabled(true);

        SharedPreferences prefs = getSharedPreferences("db", MODE_PRIVATE);
        fcm = prefs.getString("fcm", "fcm");

    }



    ProgressDialog progress;
    JSONObject json;
    String outputresponse = "";

    GPSTracker tracker;

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        onBackPressed();
        finish();
        return true;

    }
    public void btnfunc(View v)
    {


        progress = new ProgressDialog(this);
        progress.setTitle("Loading");
        progress.setMessage("Wait while loading...");
        progress.setCancelable(false);
        progress.show();


        tracker = new GPSTracker(this);
        new LongOperation2().execute(et1.getText().toString(), et2.getText().toString());
    }


    String fcm;

    private class LongOperation2 extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {



            json = new JSONObject();
            try
            {
                json.put("Username", params[0] );
                json.put("Password", params[1] );
                json.put("Lat", String.valueOf(tracker.getLatitude()) );
                json.put("Long", String.valueOf(tracker.getLongitude()) );
                json.put("Token", fcm);

            }catch (JSONException j)

            {
                Log.d("Second_Fragment", "Err");
            }

            try {
                URL url = new URL("http://52.66.134.228:4000/vehicle/login");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                connection.setRequestProperty("Content-Type", "application/json");
                OutputStreamWriter osw = new OutputStreamWriter(connection.getOutputStream());
                osw.write(String.format( String.valueOf(json)));
                osw.flush();
                osw.close();


                InputStream stream = connection.getInputStream();
                InputStreamReader isReader = new InputStreamReader(stream );
                BufferedReader br = new BufferedReader(isReader );
                outputresponse = br.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            progress.dismiss();
            //Toast.makeText(getApplicationContext(), outputresponse, Toast.LENGTH_SHORT).show();
            aftercomplete();
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }


    public void aftercomplete()
    {

        String id = null;
        //Toast.makeText(this, outputresponse, Toast.LENGTH_SHORT).show();
        if( outputresponse != null)
        {
            try
            {
                JSONObject json = new JSONObject(outputresponse);
                id = json.getString("Id");
            } catch (JSONException j)
            {
                Log.d("error", "noob error");
            }

            if(id == null )
            {
                Toast.makeText(this, "Username password wrong combination!", Toast.LENGTH_SHORT).show();
                return;
            }

            SharedPreferences.Editor editor = getSharedPreferences("dbb", MODE_PRIVATE).edit();
            editor.putString("id", id);
            editor.commit();
            Log.d("fcmid", fcm);
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
    }

}
