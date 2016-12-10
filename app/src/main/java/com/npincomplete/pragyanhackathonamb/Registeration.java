package com.npincomplete.pragyanhackathonamb;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Registeration extends AppCompatActivity {

    String temp = "";
    String tempid = "";

    JSONObject json;
    ListView listview;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registeration);

        Intent intent  = getIntent();


        Log.d("json", intent.getStringExtra("outputresponse"));
        try
        {
            JSONArray jsonarray = new JSONArray(intent.getStringExtra("outputresponse"));
            for (int i = 0; i < jsonarray.length(); i++) {
                JSONObject jsonobject = jsonarray.getJSONObject(i);
                String name = jsonobject.getString("vehicle_no");
                temp = temp + name + "@@";
                tempid = tempid + jsonobject.getString("id") + "@@";
                Log.d("jsons", name + "@@");
            }
        }
        catch(JSONException j)
        {
            Log.d("json", "error");
        }

        listview = (ListView)findViewById(R.id.listview);
        ArrayAdapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1 ,temp.split("@@"));
        listview.setAdapter(adapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), FormFill.class);
                intent.putExtra("id", tempid.split("@@")[position]);
                startActivity(intent);
            }
        });
    }
}
