package com.reverselabs.samplestock;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.AlertDialog;
import android.os.StrictMode;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    public static String[] names = new String[] { };
    public static String apiurl;

    // Array of integers points to images stored in /res/drawable-ldpi/
    public static String[] symbol = new String[]{};
    public static String a;
    public static JSONObject json;
    // Array of strings to store currencies
    public static List<HashMap<String,String>> aList = new ArrayList<HashMap<String,String>>();

    public static final String PREFS_NAME = "Symbols";
    public static String[] multistring;
    public static LinkedList<String> symbolsList;
    public static MyListAdaper myAdapter;
    public static ArrayList<String> data;
    public static String prevSymbol;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        apiurl="http://stockapp-1273.appspot.com/nkapi.php?";
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        if(prevSymbol!=null)
        {
            TextView qt=(TextView) findViewById(R.id.editText);
            qt.setText(prevSymbol);
        }
        ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setIcon(R.drawable.stock);

        Button clr = (Button) findViewById(R.id.button);
        clr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText txtbox1=(EditText) findViewById(R.id.editText);
                txtbox1.setText("");
                Toast.makeText(getApplicationContext(), "Cleared", Toast.LENGTH_SHORT).show();
            }
        });
        // Keys used in Hashmap
        String[] from = { "symbol","name"};

        // Ids of views in listview_layout
        int[] to = { R.id.symbolname,R.id.description};

        // Instantiating an adapter to store each items
        final CustomAutoComplete autoComplete = (CustomAutoComplete) findViewById(R.id.editText);
        autoComplete.setThreshold(3);
        MySimpleAdapter adapter = new MySimpleAdapter(getBaseContext(), aList, R.layout.autocomplete_layout, from, to);
        OnItemClickListener itemClickListener = new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long id) {

                //HashMap<String, String> hm = (HashMap<String, String>) arg0.getAdapter().getItem(position);
                String symb= aList.get(position).get("symbol");
                /** Getting a reference to the TextView of the layout file activity_main to set Currency */
                CustomAutoComplete autoComplete = (CustomAutoComplete) findViewById(R.id.editText);
                autoComplete.setText(symb);
            }
        };

        /** Setting the itemclick event listener */
        autoComplete.setOnItemClickListener(itemClickListener);
        /** Setting the adapter to the listView */
        autoComplete.setAdapter(adapter);

        Button btnGetQuote=(Button) findViewById(R.id.button2);
        assert btnGetQuote != null;
        btnGetQuote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                a=autoComplete.getText().toString();
                if(a.isEmpty())
                {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
                    AlertDialog showit=alertDialog.create();
                    showit.setMessage("Please enter a Stock Name/Symbol");
                    showit.setButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // Write your code here to execute after dialog closed

                        }
                    });
                    showit.show();
                }
                else {


                    new AsyncTaskParseJson().execute();
                }
            }
        });
        String allSymbols =getSymbols();
        data = new ArrayList<String>();
        if (allSymbols != null && !allSymbols.isEmpty()) {
            MainActivity.multistring = allSymbols.split(",");
            data= new ArrayList<String>(Arrays.asList(MainActivity.multistring));
            symbolsList = new LinkedList<String>(Arrays.asList(MainActivity.multistring));
        }
        myAdapter = new MyListAdaper(this, R.layout.list_view, data);
        ListView lv = (ListView) findViewById(R.id.listView);
        lv.setAdapter(myAdapter);
        ListView listView=(ListView) findViewById(R.id.listView);
        SwipeDismissListViewTouchListener touchListener =
                new SwipeDismissListViewTouchListener(
                        listView,
                        new SwipeDismissListViewTouchListener.DismissCallbacks() {
                            @Override
                            public boolean canDismiss(int position) {
                                return true;
                            }

                            @Override
                            public void onDismiss(ListView listView, int[] reverseSortedPositions) {
                                for (final int position1 : reverseSortedPositions) {
                                    try {
                                        JSONObject symbolData=getJSONData(apiurl+"type=stock&symbol="+myAdapter.getItem(position1));
                                        final String StockName=symbolData.getString("Name");
                                        new AlertDialog.Builder(MainActivity.this)
                                                .setMessage("Want to delete "+StockName+" from favorites?")
                                                .setNegativeButton(android.R.string.cancel, null) // dismisses by default
                                                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                                    @Override public void onClick(DialogInterface dialog, int which) {
                                                        MainActivity.symbolsList.remove(myAdapter.getItem(position1));
                                                        myAdapter.remove(myAdapter.getItem(position1));
                                                        putSymbols();
                                                    }
                                                })
                                                .create()
                                                .show();
                                    }  catch (JSONException e) {
                                        e.printStackTrace();
                                    }



                                }
                                myAdapter.notifyDataSetChanged();
                            }
                        });
        listView.setOnTouchListener(touchListener);
        listView.setOnScrollListener(touchListener.makeScrollListener());

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                json=getJSONData(apiurl+"type=stock&symbol="+myAdapter.getItem(position));
                Intent intent = new Intent(MainActivity.this, ResultActivity.class);
                startActivity(intent);
            }

        });

    }

    public void onImageClick(View v)
    {
        myAdapter.notifyDataSetChanged();
        Toast.makeText(getApplicationContext(), "Refreshed", Toast.LENGTH_SHORT).show();
    }

    public String getSymbols(){
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        return settings.getString("allSymbols", null);
    }

    public void putSymbols()
    {
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        String result=android.text.TextUtils.join(",", symbolsList);
        editor.putString("allSymbols", result);
        editor.commit();
    }

    public static JSONObject getJSONData(String url)
    {
        JsonStock jParse=new JsonStock();
        JSONObject j=null;
        try {
            URL uri1=new URL(url);
            j=jParse.getJSONFromUrl(uri1);;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return j;
    }


    private class MyListAdaper extends ArrayAdapter<String> {
        private int layout;
        private List<String> mObjects;
        private MyListAdaper(Context context, int resource, List<String> objects) {
            super(context, resource, objects);
            mObjects = objects;
            layout = resource;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder mainViewholder = null;
            if(convertView == null) {
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(layout, parent, false);
                ViewHolder viewHolder = new ViewHolder();

                viewHolder.symbol = (TextView) convertView.findViewById(R.id.symbol);
                viewHolder.name = (TextView) convertView.findViewById(R.id.companyname);
                viewHolder.lastprice = (TextView) convertView.findViewById(R.id.stockprice);
                viewHolder.changeper=(TextView) convertView.findViewById(R.id.ChangePercentage);
                viewHolder.market=(TextView) convertView.findViewById(R.id.marketCap);
                convertView.setTag(viewHolder);
            }
            mainViewholder = (ViewHolder) convertView.getTag();
            try {
                JSONObject symbolData=getJSONData(apiurl+"type=stock&symbol="+getItem(position));
                String StockName=symbolData.getString("Name");
                DecimalFormat df = new DecimalFormat("###.##");
                mainViewholder.name.setText(StockName);
                Float marketCap=Float.parseFloat(symbolData.getString("MarketCap"));
                if(marketCap>=1000000000)
                {
                    mainViewholder.market.setText("Market Cap: "+Float.valueOf(df.format(marketCap/1000000000))+" Billion");
                }
                else if(marketCap>=1000000)
                {
                    mainViewholder.market.setText("Market Cap: "+Float.valueOf(df.format(marketCap/1000000))+" Million");
                }
                else
                {
                    mainViewholder.market.setText("Market Cap: "+Float.valueOf(df.format(marketCap)));
                }
                mainViewholder.lastprice.setText("$ "+symbolData.getString("LastPrice"));
                Float changePercent=Float.parseFloat(symbolData.getString("ChangePercent"));
                changePercent=Float.valueOf(df.format(changePercent));
                if(changePercent>0)
                {

                    mainViewholder.changeper.setText("+"+changePercent+"%");
                    mainViewholder.changeper.setBackgroundColor(Color.GREEN);
                }
                else if(changePercent<0)
                {

                    mainViewholder.changeper.setText(+changePercent+"%");
                    mainViewholder.changeper.setBackgroundColor(Color.RED);
                }
                else {
                    mainViewholder.changeper.setText(changePercent+"%");
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }

            mainViewholder.symbol.setText(getItem(position));

            return convertView;
        }
    }
    public class ViewHolder {
        TextView symbol;
        TextView name;
        TextView lastprice;
        TextView changeper;
        TextView market;
    }

    public class AsyncTaskParseJson extends AsyncTask<String, String, String> {

        final String TAG = "AsyncTaskParseJson.java";


        @Override
        protected void onPreExecute() {}

        @Override
        protected String doInBackground(String... arg0) {
            json=getJSONData(apiurl+"type=stock&symbol="+a);
            return null;
        }

        @Override
        protected void onPostExecute(String strFromDoInBg) {
            try {
                if(json.has("Message")|| !json.getString("Status").equals("SUCCESS"))
                {
                    // Log.e(TAG,"Error");
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
                    AlertDialog showit=alertDialog.create();

                    //showit.show();
                    //showit.setTitle("Alert");
                    showit.setMessage("Invalid Symbol");
                    showit.setButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // Write your code here to execute after dialog closed
                            }
                    });
                    showit.show();
                }
                else{
                    // Log.e(TAG, "success");
                    prevSymbol=json.getString("Symbol");
                    Intent intent = new Intent(MainActivity.this, ResultActivity.class);
                    startActivity(intent);}
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    @Override
    protected void onRestart() {
// TODO Auto-generated method stub
        super.onRestart();
        myAdapter.notifyDataSetChanged();
        //Do your code here
    }
}

