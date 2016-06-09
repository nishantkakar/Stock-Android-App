package com.reverselabs.samplestock;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import org.json.JSONException;

/**
 * Created by Nishant on 4/21/2016.
 */
public class TabFragment2 extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.historical, container, false);

        try {
            WebView webview=(WebView) v.findViewById(R.id.webView);
            webview.getSettings().setJavaScriptEnabled(true);
            webview.setWebViewClient(new WebViewClient() {
                public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                    Toast.makeText(getContext(), "Oh no! " + description, Toast.LENGTH_SHORT).show();
                }
            });
            webview.loadUrl("http://www.kakarnishant.com/HW8/histchart.php?symbol="+MainActivity.json.getString("Symbol"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return v;
    }
}