package com.reverselabs.samplestock;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.design.widget.TabLayout;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;


import org.json.JSONException;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.LinkedList;

public class ResultActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private CustomViewPager viewPager;
    private ViewPagerAdapter viewPagerAdapter;
    private Menu menu;
    ShareDialog shareDialog;
    private CallbackManager callbackManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_info);

        String allSymbols =getSymbols();
        callbackManager = CallbackManager.Factory.create();
        AppEventsLogger.activateApp(this);
        shareDialog = new ShareDialog(this);
        if (ShareDialog.canShow(ShareLinkContent.class)) {
            shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {
                @Override
                public void onSuccess(Sharer.Result result) {
                    makeToast("You shared this post.");
                    Log.d("DEBUG", "SHARE SUCCESS");
                    //finish();
                }

                @Override
                public void onCancel() {
                    makeToast("Post Cancelled");
                    Log.d("DEBUG", "SHARE CANCELLED");
                    //finish();
                }

                @Override
                public void onError(FacebookException exception) {
                    Log.e("DEBUG", "Share: " + exception.getMessage());
                    makeToast( exception.getMessage());
                    exception.printStackTrace();
                    //finish();
                }
            });
        }
        MainActivity.symbolsList =new LinkedList<String>();
        if(allSymbols !=null && !allSymbols.isEmpty())
        {
            MainActivity.multistring= allSymbols.split(",");
            MainActivity.symbolsList = new LinkedList<String>(Arrays.asList(MainActivity.multistring));
        }
        try {
            setTitle(MainActivity.json.getString("Name"));
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        viewPager = (CustomViewPager) findViewById(R.id.viewpager);

        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.setPagingEnabled(false);

        final TabLayout.Tab home = tabLayout.newTab();
        final TabLayout.Tab inbox = tabLayout.newTab();
        final TabLayout.Tab star = tabLayout.newTab();


        home.setText("CURRENT");
        inbox.setText("HISTORICAL");
        star.setText("NEWS");
        tabLayout.addTab(home, 0);
        tabLayout.addTab(inbox, 1);
        tabLayout.addTab(star, 2);

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(onTabSelectedListener(viewPager));


    }

    public void makeToast(String msg)
    {
        Toast.makeText(ResultActivity.this,msg , Toast.LENGTH_SHORT).show();
    }
    private TabLayout.OnTabSelectedListener onTabSelectedListener(final ViewPager pager) {
        return new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                pager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        };
    }

    public boolean checkSymbol(String str)
    {
        if(MainActivity.symbolsList.contains(str))
        {
            return true;
        }
        else
            return false;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            case R.id.action_favorite:
                try {
                    if(checkSymbol(MainActivity.json.getString("Symbol")))
                    {
                        MainActivity.symbolsList.remove(MainActivity.json.getString("Symbol"));
                        MainActivity.data.remove(MainActivity.json.getString("Symbol"));
                        putSymbols();
                        menu.getItem(0).setIcon(getResources().getDrawable(R.drawable.ic_star_border_white_24dp));
                        makeToast("Removed "+MainActivity.json.getString("Name")+" from Bookmarks.");
                    }
                    else
                    {
                        MainActivity.symbolsList.add(MainActivity.json.getString("Symbol"));
                        MainActivity.data.add(MainActivity.json.getString("Symbol"));
                        putSymbols();
                        menu.getItem(0).setIcon(getResources().getDrawable(R.drawable.ic_star_white_24dp));
                        makeToast("Bookmarked "+MainActivity.json.getString("Name")+"!!");
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return true;
            case R.id.action_fb:
                String url = null;
                String url1= null;


                try {
                    url1 = MainActivity.json.getString("Symbol");
                    makeToast("Sharing "+MainActivity.json.getString("Name")+"!!");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    url = "http://chart.finance.yahoo.com/t?s=" + URLEncoder.encode(url1, "UTF-8") + "&lang=en-US&width=" + 800 + "&height=" + 650;
                }
                catch (UnsupportedEncodingException e) {
                    url = "http://chart.finance.yahoo.com/t?s=" +url1 + "&lang=en-US&width=" + 800 + "&height=" + 650;}
                ShareLinkContent content = null;
                try {
                    content = new ShareLinkContent.Builder()
                            .setContentUrl(Uri.parse("http://finance.yahoo.com/q?s="+MainActivity.json.getString("Symbol")))
                            .setContentDescription("Stock Information for "+ MainActivity.json.getString("Name")+"("+MainActivity.json.getString("Symbol")+")")
                            .setContentTitle("Current Stock Price of "+MainActivity.json.getString("Name")+", $"+MainActivity.json.getString("LastPrice"))
                            .setImageUrl(Uri.parse(url))
                            .build();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                shareDialog.show(content);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public String getSymbols(){
        SharedPreferences settings = getSharedPreferences(MainActivity.PREFS_NAME, 0);
        return settings.getString("allSymbols", null);
    }

    public void putSymbols()
    {
        SharedPreferences settings = getSharedPreferences(MainActivity.PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        String result=android.text.TextUtils.join(",",MainActivity.symbolsList);
        editor.putString("allSymbols", result);
        editor.commit();
    }


    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_1, menu);
        this.menu=menu;
        try {
            if(checkSymbol(MainActivity.json.getString("Symbol")))
                menu.getItem(0).setIcon(getResources().getDrawable(R.drawable.ic_star_white_24dp));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return true;
    }
    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        super.onBackPressed();
        finish();
    }

}
