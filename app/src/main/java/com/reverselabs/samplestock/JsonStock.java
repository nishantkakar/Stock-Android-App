package com.reverselabs.samplestock;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Nishant on 4/20/2016.
 */
public class JsonStock {
    final String TAG = "JsonParser.java";

    InputStream is = null;
    JSONObject jObj = null;
    String json = "";

    public JSONObject getJSONFromUrl(URL url) {

        // make HTTP request
        try {

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.connect();
            //DefaultHttpClient httpClient = new DefaultHttpClient();
            //   HttpPost httpPost = new HttpPost(url);
            // HttpResponse httpResponse = httpClient.execute(httpPost);
            //HttpEntity httpEntity = httpResponse.getEntity();
            //is = httpEntity.getContent();
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            connection.disconnect();
            json = sb.toString();
        } catch (Exception e) {
            Log.e(TAG, "Error converting result " + e.toString());
        }
        // try parse the string to a JSON object
        try {
            jObj = new JSONObject(json);
        } catch (JSONException e) {
            Log.e(TAG, "Error parsing data " + e.toString());
        }
        // return JSON String
        return jObj;
    }
}
