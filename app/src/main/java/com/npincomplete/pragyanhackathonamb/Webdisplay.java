package com.npincomplete.pragyanhackathonamb;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class Webdisplay extends Activity {

    WebView webview;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webdisplay);


        GPSTracker tracker = new GPSTracker(this);
        webview = (WebView)findViewById(R.id.webview);
        String movdestlat = "12.9716";
        String movdestlong = "77.5946";
        webview.setWebViewClient(new WebViewClient());
        webview.getSettings().setJavaScriptEnabled(true);
        String temp = "http://maps.google.com/maps?" + "saddr=" + Double.toString(tracker.getLatitude()) + "," + Double.toString(tracker.getLongitude()) + "&daddr=" + movdestlat + "," + movdestlong;
        webview.loadUrl(temp);
        Log.d("webview", temp);

    }
}
