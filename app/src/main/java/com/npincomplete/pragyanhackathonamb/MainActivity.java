package com.npincomplete.pragyanhackathonamb;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import static android.R.id.toggle;

public class MainActivity extends AppCompatActivity {

    String id;

    MapView mapView;
    GoogleMap map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        SharedPreferences prefs = getSharedPreferences("dbb", MODE_PRIVATE);
        id = prefs.getString("id", null);

        if (id == null)
        {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }


        mapView = (MapView) findViewById(R.id.mapview);
        mapView.onCreate(savedInstanceState);

        // Gets to GoogleMap from the MapView and does initialization stuff
        map = mapView.getMap();
        map.getUiSettings().setMyLocationButtonEnabled(false);
        map.setMyLocationEnabled(true);

        MapsInitializer.initialize(this);

        tracker = new GPSTracker(this);
        // Updates the location and zoom of the MapView
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(tracker.getLatitude(), tracker.getLongitude()), 15F);
        map.animateCamera(cameraUpdate);
        new MasterTask().execute();

        toggle = (ToggleButton) findViewById(R.id.toggleButton);
        toggle.setChecked(true);

    }

    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    GPSTracker tracker;
    JSONObject json;
    String outputresponse;
    public boolean status = true;

    private class MasterTask extends AsyncTask<String, Integer, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }
        @Override
        protected String doInBackground(String... params) {
            try {
                json = new JSONObject();
                try
                {
                    json.put("Id", Integer.parseInt(id));
                    json.put("Lat", Double.toString(tracker.getLatitude()));
                    json.put("Long", Double.toString(tracker.getLongitude()));
                    json.put("Status", status);
                }
                catch(JSONException e)
                {
                    Log.d("error", "jsonexception");
                }

                Log.d("in async", json.toString());
                try {
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
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "";
        }
        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            //Toast.makeText(getApplicationContext(), "Posted GPS", Toast.LENGTH_SHORT).show();
            new MasterTask().execute();
            //tv.setText("Sending Data every 5 seconds...      Latest Lat Long is (" + tracker.getLatitude() +  ", "+tracker.getLongitude() + ")");
        }

    }

    ToggleButton toggle;
    public void togglebtn(View view)
    {
        boolean stat = toggle.isChecked();
        if(stat)
            status = true;
        else
            status = false;
        }
    }
