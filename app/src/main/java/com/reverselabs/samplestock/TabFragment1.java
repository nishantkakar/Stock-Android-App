package com.reverselabs.samplestock;

/**
 * Created by Nishant on 4/20/2016.
 */
import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.reverselabs.samplestock.photoview.PhotoViewAttacher;

import org.json.JSONException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Admin on 11-12-2015.
 */
public class TabFragment1 extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.activity_current_tab, container, false);
        TextView tv = (TextView) v.findViewById(R.id.StockName1);
        TextView stocksymbol = (TextView) v.findViewById(R.id.StockSymbol);
        TextView stockslastprice = (TextView) v.findViewById(R.id.StockLastPrice);
        TextView stockchange=(TextView) v.findViewById(R.id.StockChange);
        TextView stocktimestamp=(TextView) v.findViewById(R.id.StockTime);
        TextView stockmarketcap=(TextView) v.findViewById(R.id.StockMarketCap);
        TextView stockvolume=(TextView) v.findViewById(R.id.StockVolume);
        TextView stockchangeytd=(TextView) v.findViewById(R.id.StockChangeYTD);
        TextView stockhigh=(TextView) v.findViewById(R.id.StockHigh);
        TextView stocklow=(TextView) v.findViewById(R.id.StockLow);
        TextView stockopen=(TextView) v.findViewById(R.id.StockOpen);

        SimpleDateFormat formatter =new SimpleDateFormat("dd MMMM y, HH:mm:ss");
        SimpleDateFormat date1 =new SimpleDateFormat("EEE MMM dd hh:mm:ss zZ yyyy");
        DecimalFormat df = new DecimalFormat("###.##");

        try {
            Float change= Float.valueOf(MainActivity.json.getString("Change"));
            Float changePercent=Float.valueOf(MainActivity.json.getString("ChangePercent"));
            Float marketCap=Float.valueOf(MainActivity.json.getString("MarketCap"));
            Float volume=Float.valueOf(MainActivity.json.getString("Volume"));
            Float changeYTD= Float.valueOf(MainActivity.json.getString("ChangeYTD"));
            Float changePercentYTD=Float.valueOf(MainActivity.json.getString("ChangePercentYTD"));
            change= Float.valueOf(df.format(change));
            changePercent=Float.valueOf(df.format(changePercent));
            changeYTD= Float.valueOf(df.format(changeYTD));
            changePercentYTD=Float.valueOf(df.format(changePercentYTD));
            Date date = date1.parse(MainActivity.json.getString("Timestamp"));
            String datetime = formatter.format(date);

            if(changePercent>0)
            {
                ImageView arrow=(ImageView) v.findViewById(R.id.imageView2);
                arrow.setImageResource(R.drawable.up);
                stockchange.setText(change+"(+"+changePercent+"%)");
            }
            else if(changePercent<0)
            {
                ImageView arrow=(ImageView) v.findViewById(R.id.imageView2);
                arrow.setImageResource(R.drawable.down);
                stockchange.setText(change+"("+changePercent+"%)");
            }
            else {
                stockchange.setText(change+"("+changePercent+"%)");
            }

            if(marketCap>=1000000000)
            {
                stockmarketcap.setText(Float.valueOf(df.format(marketCap/1000000000))+" Billion");
            }
            else if(marketCap>=1000000)
            {
                stockmarketcap.setText(Float.valueOf(df.format(marketCap/1000000))+" Million");
            }
            else
            {
                stockmarketcap.setText(Float.valueOf(df.format(marketCap))+"");
            }

            if(volume>=1000000000)
            {
                stockvolume.setText(Float.valueOf(df.format(volume/1000000000))+" Billion");
            }
            else if(volume>=1000000)
            {
                stockvolume.setText(Float.valueOf(df.format(volume/1000000))+" Million");
            }
            else
            {
                stockvolume.setText(volume+"");
            }

            if(changePercentYTD>0)
            {
                ImageView arrow=(ImageView) v.findViewById(R.id.imageView3);
                arrow.setImageResource(R.drawable.up);
                stockchangeytd.setText(changeYTD+"(+"+changePercentYTD+"%)");
            }
            else if(changePercentYTD<0)
            {
                ImageView arrow=(ImageView) v.findViewById(R.id.imageView3);
                arrow.setImageResource(R.drawable.down);
                stockchangeytd.setText(changeYTD+"("+changePercentYTD+"%)");
            }
            else {
                stockchangeytd.setText(changeYTD+"("+changePercentYTD+"%)");
            }

            //Setting text values
            tv.setText(MainActivity.json.getString("Name"));
            stocksymbol.setText(MainActivity.json.getString("Symbol"));
            stockslastprice.setText("$"+MainActivity.json.getString("LastPrice"));
            stocktimestamp.setText(datetime);
            stockhigh.setText(MainActivity.json.getString("High"));
            stocklow.setText(MainActivity.json.getString("Low"));
            stockopen.setText(MainActivity.json.getString("Open"));

            URL url = new URL("http://chart.finance.yahoo.com/t?s="+MainActivity.json.getString("Symbol")+"&lang=en-US&width=300&height=200");
            Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
            ImageView yahoochart=(ImageView) v.findViewById(R.id.yahoochart);
            yahoochart.setImageBitmap(bmp);

            //PhotoViewAttacher mAttacher;
            Drawable drawable = new BitmapDrawable(getResources(), bmp);
            yahoochart.setImageDrawable(drawable);
            //mAttacher = new PhotoViewAttacher(yahoochart);
            yahoochart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PhotoViewAttacher mAttacher;
                    URL url = null;
                    try {
                        url = new URL("http://chart.finance.yahoo.com/t?s="+ MainActivity.json.getString("Symbol")+"&lang=en-US&width=800&height=600");
                        Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                        Dialog alertadd = new Dialog(v.getContext());
                        LayoutInflater factory = LayoutInflater.from(v.getContext());
                        final View view = factory.inflate(R.layout.popup_img, null);
                        ImageView iva = (ImageView) view.findViewById(R.id.imageView4);
                        iva.setImageBitmap(bmp);
                        //iva.setFocusable(true);
                        mAttacher = new PhotoViewAttacher(iva);
                        alertadd.setContentView(view);
                        alertadd.show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });

        }
        catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return v;
    }
}