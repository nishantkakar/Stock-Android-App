package com.reverselabs.samplestock;

import android.annotation.TargetApi;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Nishant on 4/21/2016.
 */
public class TabFragment3 extends Fragment {
    public static JSONObject jsonnews;
    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.news_feed, container, false);
        SimpleDateFormat formatter =new SimpleDateFormat("dd MMMM y, HH:mm:ss");
        SimpleDateFormat date1 =new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        try {
            jsonnews = MainActivity.getJSONData(MainActivity.apiurl+"type=news&symbol="+MainActivity.json.getString("Symbol"));
            JSONObject data = jsonnews.getJSONObject("d");
            JSONArray results=data.getJSONArray("results");
            LinearLayout newscontent = (LinearLayout) v.findViewById(R.id.newscontent);
            for (int i = 0; i < results.length(); i++) {
                JSONObject result = results.getJSONObject(i);
                TextView tv = new TextView(getContext());
                tv.setText(Html.fromHtml("<a href=" +result.getString("Url") + "> " + result.getString("Title")) );
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                params.setMargins(10,15,10,10);
                tv.setLayoutParams(params);
                tv.setMovementMethod(LinkMovementMethod.getInstance());
                tv.setLinkTextColor(Color.BLACK);
                tv.setTextSize(TypedValue.COMPLEX_UNIT_SP,27);
                tv.setTypeface(null, Typeface.BOLD);
                newscontent.addView(tv);
                TextView tvdesc = new TextView(getContext());
                tvdesc.setText(result.getString("Description"));
                tvdesc.setTextSize(TypedValue.COMPLEX_UNIT_SP,18);
                newscontent.addView(tvdesc);
                TextView tvpub = new TextView(getContext());
                tvpub.setText("Publisher: "+result.getString("Source"));
                params.setMargins(10,10,10,0);
                tvpub.setLayoutParams(params);
                newscontent.addView(tvpub);
                TextView tvdate = new TextView(getContext());
                Date date = date1.parse(result.getString("Date"));
                String datetime = formatter.format(date);
                tvdate.setText("Date: "+datetime);
                params.setMargins(0,10,10,15);
                tvdate.setLayoutParams(params);
                newscontent.addView(tvdate);
                View line=new View(getContext());
                line.setMinimumHeight(2);
                line.setBackgroundColor(Color.GRAY);
                newscontent.addView(line);
            }

        }  catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return v;
    }
}


