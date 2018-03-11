package org.laiyang.miniweather;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.laiyang.util.Netutil;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;


/**
 * Created by 小米笔记本Pro on 2018/3/7.
 */

public class MainActivity extends Activity implements View.OnClickListener {

    private ImageView mUpdateBtn;
    private TextView cityTV, timeTV, humidityTV, WeekTV, pmDateTV, pmQualityTv, temperatureTv, climateTv, windTv, city_name_tv;
    private ImageView weatherImg, pmImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weather_info);

        mUpdateBtn = (ImageView) findViewById(R.id.title_update_btn);
        mUpdateBtn.setOnClickListener(this);

        if (Netutil.getNetworkState(MainActivity.this) != Netutil.NETWOEN_NONE) {
            Log.d("myWeather", "网络连接ok");
            Toast.makeText(this, "网络OJBK！", Toast.LENGTH_LONG).show();
        } else {
            Log.d("myWeather", "网络挂了");
            Toast.makeText(MainActivity.this, "网络挂了！", Toast.LENGTH_LONG).show();
        }


    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.title_update_btn) {
            SharedPreferences sharedPreferences = getSharedPreferences("config", MODE_PRIVATE);
            String CityCode = sharedPreferences.getString("main_city", "101040100");
            Log.d("myWeather", CityCode);
            if (Netutil.getNetworkState(this) != Netutil.NETWOEN_NONE) {
                Log.d("myWeather", "网络OJBK");
                queryWeatherCode(CityCode);
            } else {
                Log.d("myWeather", "网络挂了");
                Toast.makeText(MainActivity.this, "网络挂了！", Toast.LENGTH_LONG).show();
            }
        }
        initView();

    }

    private class TodayWeather {
        public String city, budatetime, wendu, shidu, pm25, quality, fengxiang, fengli, date, high, low, type;

        public String getCity() {
            return city;
        }

        public String getBudatetime() {
            return budatetime;
        }

        public String getWendu() {
            return wendu;
        }

        public String getShidu() {
            return shidu;
        }

        public String getPm25() {
            return pm25;
        }

        public String getQuality() {
            return quality;
        }

        public String getFengxiang() {
            return fengxiang;
        }

        public String getFengli() {
            return fengli;
        }

        public String getDate() {
            return date;
        }

        public String getHigh() {
            return high;
        }

        public String getLow() {
            return low;
        }

        public String getType() {
            return type;
        }

        @Override
        public String toString() {
            return "TodayWeather{" + "city" + city + ",updatetime=" + ",wendu=" + wendu + "," +
                    "shidu=" + shidu + ",pm2.5" + pm25 + ",quality=" + quality + ",fengli="
                    + fengli + ",date=" + date + ",high=" + high + ",low=" + low + ",type=" + type;
        }
    }

    private void queryWeatherCode(String cityCode) {
        final String address = "http://wthrcdn.etouch.cn/WeatherApi?citykey=" + cityCode;
        Log.d("myWeather", address);
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection con = null;
                TodayWeather todayWeather = null;
                try {
                    URL url = new URL(address);
                    con = (HttpURLConnection) url.openConnection();
                    con.setRequestMethod("GET");
                    con.setConnectTimeout(8000);
                    con.setReadTimeout(8000);
                    InputStream in = con.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder response = new StringBuilder();
                    String str;
                    while ((str = reader.readLine()) != null) {
                        response.append(str);
                        Log.d("myWeather", str);
                    }
                    String responseStr = response.toString();
                    parseXML(responseStr);
                    todayWeather =parseXML(responseStr);
                    if (todayWeather !=null){
                        Log.d("myWeather",todayWeather.toString());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (con != null) {
                        con.disconnect();
                    }
                }
            }
        }).start();
    }

    private void parseXML(String xmldate) {
        int fengxiangCount = 0;
        int fengliCount = 0;
        int dateCount = 0;
        int highCount = 0;
        int lowCount = 0;
        int typeCount = 0;
        try {
            XmlPullParserFactory fac = XmlPullParserFactory.newInstance();
            XmlPullParser xmlPullParser = fac.newPullParser();
            xmlPullParser.setInput(new StringReader(xmldate));
            int eventType = xmlPullParser.getEventType();
            Log.d("myWeather4", "ParseXML" + eventType);
            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    case XmlPullParser.START_TAG:
                        if (xmlPullParser.getName().equals("city")) {
                            eventType = xmlPullParser.next();
                            Log.d("myWeather", "city:  " + xmlPullParser.getText());
                        } else if (xmlPullParser.getName().equals("updatetime")) {
                            eventType = xmlPullParser.next();
                            Log.d("myWeather", "updatetime:  " + xmlPullParser.getText());
                        } else if (xmlPullParser.getName().equals("shidu")) {
                            Log.d("myWeather", "shidu:  " + xmlPullParser.getText());
                        } else if (xmlPullParser.getName().equals("wendu")) {
                            eventType = xmlPullParser.next();
                            Log.d("myWeather", "wendu:  " + xmlPullParser.getText());
                        } else if (xmlPullParser.getName().equals("pm25")) {
                            eventType = xmlPullParser.next();
                            Log.d("myWeather", "pm25:   " + xmlPullParser.getText());
                        } else if (xmlPullParser.getName().equals("quality")) {
                            eventType = xmlPullParser.next();
                            Log.d("myWeather", "quality:   " + xmlPullParser.getText());
                        } else if (xmlPullParser.getName().equals("fengxiang") && fengxiangCount == 0) {
                            eventType = xmlPullParser.next();
                            Log.d("myWeather", "fengxiang:  " + xmlPullParser.getText() + fengxiangCount);
                            fengxiangCount++;
                        } else if (xmlPullParser.getName().equals("fengli") && fengliCount == 0) {
                            eventType = xmlPullParser.next();
                            Log.d("myWeather", "fengli:   " + xmlPullParser.getText());
                            fengliCount++;
                        } else if (xmlPullParser.getName().equals("date") && dateCount == 0) {
                            eventType = xmlPullParser.next();
                            Log.d("myWeather", "date:   " + xmlPullParser.getText());
                            dateCount++;
                        } else if (xmlPullParser.getName().equals("high") && highCount == 0) {
                            eventType = xmlPullParser.next();
                            Log.d("myWeather", "high:  " + xmlPullParser.getText());
                            highCount++;
                        } else if (xmlPullParser.getName().equals("low") && lowCount == 0) {
                            eventType = xmlPullParser.next();
                            Log.d("myWeather", "low:  " + xmlPullParser.getText());
                            lowCount++;
                        } else if (xmlPullParser.getName().equals("type") && typeCount == 0) {
                            eventType = xmlPullParser.next();
                            Log.d("myWeather", "type:  " + xmlPullParser.getText());
                            typeCount++;
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        break;
                }
                eventType = xmlPullParser.next();
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void initView() {
        city_name_tv = findViewById(R.id.title_city_name);
        cityTV = findViewById(R.id.Notice_Today_Weather);
        timeTV = findViewById(R.id.Notice_Update_Time);
        humidityTV = findViewById(R.id.Notice_Today_Humidity);
        WeekTV = findViewById(R.id.Notice_Date);
        pmDateTV = findViewById(R.id.Notice_Pm2_5_Number);
        pmImg = findViewById(R.id.Notice_Picture_boy);
        pmQualityTv = findViewById(R.id.Notice_pm2_5_Pollution);
        temperatureTv = findViewById(R.id.Notice_Temperature);
        climateTv = findViewById(R.id.Notice_Weather);
        windTv = findViewById(R.id.Notice_Wind);
        weatherImg = findViewById(R.id.Notice_Picture);

        city_name_tv.setText("N/A");
        cityTV.setText("N/A");
        timeTV.setText("N/A");
        humidityTV.setText("N/A");
        WeekTV.setText("N/A");
        pmDateTV.setText("100");
        pmQualityTv.setText("N/A");
        temperatureTv.setText("N/A");
        climateTv.setText("N/A");
        windTv.setText("N/A");
    }
}



