package com.npincomplete.pragyanhackathonamb;

import android.app.Activity;
import android.app.ProgressDialog;
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

public class LoginActivity extends Activity {


    EditText et1, et2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        et1 = (EditText)findViewById(R.id.email);
        et1.requestFocus();
    }



    ProgressDialog progress;
    JSONObject json;
    String outputresponse = "";

    public void btnfunc(View v)
    {
/*
        progress = new ProgressDialog(getApplicationContext());
        progress.setTitle("Loading");
        progress.setMessage("Wait while loading...");
        progress.setCancelable(false);
        progress.show();
*/
        new LongOperation2().execute(et1.getText().toString());
    }


    private class LongOperation2 extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {


            json = new JSONObject();
            try
            {
                json.put("Auth",Integer.parseInt(params[0]) );
                json.put("Token", "asdfasdfa");

            }catch (JSONException j)

            {
                Log.d("Second_Fragment", "Err");
            }

            try {
                URL url = new URL("https://4e16c88d.ngrok.io/vehicle/register");
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
            //  progress.dismiss();
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


       /* if( FirebaseInstanceId.getInstance().getToken() != null)
            Log.d("fcmid", FirebaseInstanceId.getInstance().getToken());
*/
        Toast.makeText(this, outputresponse, Toast.LENGTH_SHORT).show();
        //progress.dismiss();
        if( outputresponse != null) {
            try
            {
                JSONObject json = new JSONObject(outputresponse);
                SharedPreferences.Editor editor = getSharedPreferences("dbb", MODE_PRIVATE).edit();
                editor.putString("id", json.getString("Id"));
                editor.commit();

            } catch (JSONException j)
            {
                Log.d("error", "noob error");
            }

            Log.d("fcmid", "" );
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
        }
    }

}
