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

        String tempp = intent.getStringExtra("outputresponse");

        Log.d("abcjson", temp);
        try
        {
            JSONArray jsonarray = new JSONArray(tempp);
            for (int i = 0; i < jsonarray.length(); i++) {
                JSONObject jsonobject = jsonarray.getJSONObject(i);
                String name = jsonobject.getString("Vehicle_no");
                Log.d("jsonst", name);
                temp = temp + name + "@@";
                tempid = tempid + jsonobject.getInt("Id") + "@@";
                Log.d("jsonid", Integer.toString(jsonobject.getInt("Id")) );
                Log.d("jsons", name + "@@");
            }
        }
        catch(JSONException j)
        {
            Log.d("json", temp.toString());
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
