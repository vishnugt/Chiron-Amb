package com.npincomplete.pragyanhackathonamb;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
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

import de.tavendo.autobahn.WebSocketConnection;
import de.tavendo.autobahn.WebSocketException;
import de.tavendo.autobahn.WebSocketHandler;

import static android.R.id.toggle;

public class MainActivity extends Activity {

    String id;

    MapView mapView;
    GoogleMap map;

    String latt;
    String longg;

    String fcm;
    public void btnn(View view)
    {
        Intent intent = new Intent(this, Webdisplay.class);
        startActivity(intent);
    }

    private static final String TAG = "de.tavendo.test1";

    private final WebSocketConnection mConnection = new WebSocketConnection();

    private void start() {
        final String wsuri = "ws://4e16c88d.ngrok.io/vehicle/update";
        try {
            mConnection.connect(wsuri, new WebSocketHandler() {
                @Override
                public void onOpen() {
                    Log.d(TAG, "Status: Connected to " + wsuri);
                    new TickerAsync().execute();

                    //mConnection.sendTextMessage("Hello, world!");
                }
                @Override
                public void onTextMessage(String payload)
                {
                    Log.d(TAG, "Got echo: " + payload);
                }

                @Override
                public void onClose(int code, String reason) {
                    Log.d(TAG, "Connection lost.");
                }
            });
        } catch (WebSocketException e) {

            Log.d(TAG, e.toString());
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tracker = new GPSTracker(this);
        SharedPreferences prefs = getSharedPreferences("dbb", MODE_PRIVATE);
        id = prefs.getString("id", "0");
        fcm = prefs.getString("fcm", "fcm");

        if (id.matches("0"))
        {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }

        start();

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
        //new MasterTask().execute();

    }
/*

    JSONObject json = new JSONObject();
    try {
        json.put("Id", id);
        json.put("Lat", tracker.getLatitude());
        json.put("Long", tracker.getLocation());
    }
    catch(JSONException j)
    {

    }
    mConnection.sendTextMessage(json.toString());
*/



    private class TickerAsync extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            JSONObject json = new JSONObject();
            try {
                json.put("Id", Integer.parseInt(id));
                json.put("Lat", String.valueOf(tracker.getLatitude()) );
                json.put("Long", String.valueOf(tracker.getLongitude()) );
                json.put("Token", fcm);
            }
            catch(JSONException j)
            {

            }

            Log.d("jsonprint", json.toString());
            mConnection.sendTextMessage( json.toString() );
            try{

                Thread.sleep(30000);
            }
            catch(InterruptedException r)
            {

            }
            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            //  progress.dismiss();
            Toast.makeText(getApplicationContext(), outputresponse, Toast.LENGTH_SHORT).show();
            new TickerAsync().execute();
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
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
                    URL url = new URL("http://02a4ba0f.ngrok.io/amb/update");
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

    }
