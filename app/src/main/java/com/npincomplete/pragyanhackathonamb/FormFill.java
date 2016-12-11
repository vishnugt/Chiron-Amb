package com.npincomplete.pragyanhackathonamb;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class FormFill extends AppCompatActivity {

    ProgressDialog progress;
    String id;
    String outputresponse;
    EditText et1, et2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form);

        Intent intent = getIntent();
        id = intent.getStringExtra("id");

        et1 = (EditText)findViewById(R.id.et1);
        et2 = (EditText)findViewById(R.id.et2);

    }

    GPSTracker tracker;

    public void btnfunc(View v)
    {

        tracker = new GPSTracker(this);

        progress = new ProgressDialog(this);
        progress.setTitle("Loading");
        progress.setMessage("Wait while loading...");
        progress.setCancelable(false);
        progress.show();
        new LongOperation2().execute(id, et1.getText().toString(), et2.getText().toString());
    }

    JSONObject json;
    private class LongOperation2 extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {


            json = new JSONObject();
            try
            {
                json.put("Id",Integer.parseInt(params[0]));
                json.put("Lat", Double.toString(tracker.getLatitude()));
                json.put("Long", Double.toString(tracker.getLongitude()));
                json.put("Phone", params[2]);
                json.put("Driver", params[1]);
                json.put("Status", true);

            }catch (JSONException j)

            {
                Log.d("Second_Fragment", "Err");
            }

            try {
                Log.d("gg", json.toString());
                URL url = new URL("http://23b8e3b4.ngrok.io/amb/update");
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
            Toast.makeText(getApplicationContext(), outputresponse, Toast.LENGTH_SHORT).show();
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
        progress.dismiss();


        SharedPreferences.Editor editor = getSharedPreferences("dbb", MODE_PRIVATE).edit();
        editor.putString("id", id);
        editor.commit();

    Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

}


