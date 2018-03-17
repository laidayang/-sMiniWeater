package org.laiyang.miniweather;


import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.readystatesoftware.systembartint.SystemBarTintManager;

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

    private static final int UPDATE_TODAY_WEATHER = 1;

    private ImageView mUpdateBtn;
    private ImageView mcitySelect;
    private TextView cityTV, timeTV, humidityTV, WeekTV, pmDateTV, pmQualityTv, temperatureTv, climateTv, windTv, city_name_tv;
    private ImageView weatherImg, pmImg;

    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case UPDATE_TODAY_WEATHER:
                    updateTodayWeather((TodayWeather) msg.obj);
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weather_info);
        /*SystemBarTintManager tintManager = new SystemBarTintManager(this);*/
        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //透明导航栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            // 激活状态栏
            tintManager.setStatusBarTintEnabled(true);
            // enable navigation bar tint 激活导航栏
            tintManager.setNavigationBarTintEnabled(true);
            //设置系统栏设置颜色
            //tintManager.setTintColor(R.color.red);
            //给状态栏设置颜色
            /*tintManager.setStatusBarTintResource(R.color.mask_tags_1);*/
            //Apply the specified drawable or color resource to the system navigation bar.
            //给导航栏设置资源
          /*  tintManager.setNavigationBarTintResource(R.color.mask_tags_1);*/
        }

        mUpdateBtn = findViewById(R.id.title_update_btn);
        mUpdateBtn.setOnClickListener(this);

        mcitySelect = findViewById(R.id.title_city_manager);
        mcitySelect.setOnClickListener(this);
        if (Netutil.getNetworkState(MainActivity.this) != Netutil.NETWOEN_NONE) {
            Log.d("myWeather", "网络连接ok");
            Toast.makeText(this, "网络OJBK！", Toast.LENGTH_LONG).show();
        } else {
            Log.d("myWeather", "网络挂了");
            Toast.makeText(MainActivity.this, "网络挂了！", Toast.LENGTH_LONG).show();
        }
        View view = findViewById(R.id.title);
        view.getBackground().setAlpha(0);
    }
    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.title_city_manager){
            Intent i = new Intent(this,city_MainActivity.class);
            startActivityForResult(i,1);}

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

    protected void onActivityResult(int requestCode,int resultCode,Intent date){
        Log.d("myweather","<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
        if(requestCode == 1 && resultCode == RESULT_OK){
            String newCityCode = date.getStringExtra("cityCode");
            Log.d("myWeather","选择的城市代码为>>>>>>>>>>>>>>>>>>>>>>>>>>>"+newCityCode);
            if (Netutil.getNetworkState(this) != Netutil.NETWOEN_NONE){
                Log.d("myWeather","网络OK");
                queryWeatherCode(newCityCode);
            }else {
                Log.d("myWeather","网络挂了");
                Toast.makeText(MainActivity.this,"网络挂了",Toast.LENGTH_LONG).show();

        }
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
                    Log.d("myWeather", responseStr);
                    todayWeather = parseXML(responseStr);
                    if (todayWeather != null) {
                        Log.d("myWeather", todayWeather.toString());

                        Message msg = new Message();
                        msg.what = UPDATE_TODAY_WEATHER;
                        msg.obj = todayWeather;
                        mHandler.sendMessage(msg);
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

    private TodayWeather parseXML(String xmldate) {
        TodayWeather todayWeather = null;
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
                        if (xmlPullParser.getName().equals("resp")) {
                            todayWeather = new TodayWeather();
                        }
                        if (todayWeather != null) {

                            if (xmlPullParser.getName().equals("city")) {
                                eventType = xmlPullParser.next();
                                todayWeather.setCity(xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("updatetime")) {
                                eventType = xmlPullParser.next();
                                todayWeather.setUpdatetime(xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("shidu")) {
                                eventType = xmlPullParser.next();
                                Log.d("shidu", xmlPullParser.getText());
                                todayWeather.setShidu(xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("wendu")) {
                                eventType = xmlPullParser.next();
                                todayWeather.setWendu(xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("pm25")) {
                                eventType = xmlPullParser.next();
                                todayWeather.setPm25(xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("quality")) {
                                eventType = xmlPullParser.next();
                                todayWeather.setQuality(xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("fengxiang") && fengxiangCount == 0) {
                                eventType = xmlPullParser.next();
                                todayWeather.setFengxiang(xmlPullParser.getText());
                                fengxiangCount++;
                            } else if (xmlPullParser.getName().equals("fengli") && fengliCount == 0) {
                                eventType = xmlPullParser.next();
                                todayWeather.setFengli(xmlPullParser.getText());
                                fengliCount++;
                            } else if (xmlPullParser.getName().equals("date") && dateCount == 0) {
                                eventType = xmlPullParser.next();
                                todayWeather.setDate(xmlPullParser.getText());
                                dateCount++;
                            } else if (xmlPullParser.getName().equals("high") && highCount == 0) {
                                eventType = xmlPullParser.next();
                                todayWeather.setHigh(xmlPullParser.getText());
                                highCount++;
                            } else if (xmlPullParser.getName().equals("low") && lowCount == 0) {
                                eventType = xmlPullParser.next();
                                todayWeather.setLow(xmlPullParser.getText());
                                lowCount++;
                            } else if (xmlPullParser.getName().equals("type") && typeCount == 0) {
                                eventType = xmlPullParser.next();
                                todayWeather.setType(xmlPullParser.getText());
                                typeCount++;
                            }
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        break;
                }
                eventType = xmlPullParser.next();
            }
        } catch (XmlPullParserException | IOException e) {
            e.printStackTrace();
        }
        return todayWeather;
    }

    private class TodayWeather {
        private String city;
        private String updatetime;
        private String wendu;
        private String shidu;
        private String pm25;
        private String quality;
        private String fengxiang;
        private String fengli;
        private String date;
        private String high;
        private String low;
        private String type;

        public String getCity() {
            return city;
        }

        public String getupdatetime() {
            return updatetime;
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

        public void setCity(String city) {
            this.city = city;
        }

        public void setUpdatetime(String updatetime) {
            this.updatetime = updatetime;
        }

        public void setWendu(String wendu) {
            this.wendu = wendu;
        }

        public void setShidu(String shidu) {
            this.shidu = shidu;
        }

        public void setPm25(String pm25) {
            this.pm25 = pm25;
        }

        public void setQuality(String quality) {
            this.quality = quality;
        }

        public void setFengxiang(String fengxiang) {
            this.fengxiang = fengxiang;
        }

        public void setFengli(String fengli) {
            this.fengli = fengli;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public void setHigh(String high) {
            this.high = high;
        }

        public void setLow(String low) {
            this.low = low;
        }

        private void setType(String type) {
            this.type = type;
        }

        @Override
        public String toString() {
            return "TodayWeather{" + "city:" + city + ",updatetime=" + updatetime + ",wendu=" + wendu + "," +
                    "shidu=" + shidu + ",pm2.5" + pm25 + ",quality=" + quality + ",fengli="
                    + fengli + ",date=" + date + ",high=" + high + ",low=" + low + ",type=" + type;
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
        pmDateTV.setText("N/A");
        pmQualityTv.setText("N/A");
        temperatureTv.setText("N/A");
        climateTv.setText("N/A");
        windTv.setText("N/A");
    }

    void updateTodayWeather(TodayWeather todayWeather) {
        city_name_tv.setText(todayWeather.getCity() + "天气");
        cityTV.setText(todayWeather.getCity());
        timeTV.setText("今日" + todayWeather.getupdatetime() + "更新");
        humidityTV.setText("今日湿度=" + todayWeather.getShidu());
        WeekTV.setText(todayWeather.getDate());
        pmDateTV.setText(todayWeather.getPm25());
        pmQualityTv.setText(todayWeather.getQuality());
        temperatureTv.setText(todayWeather.getHigh() + "~" + todayWeather.getLow());
        climateTv.setText(todayWeather.getType());
        windTv.setText(todayWeather.getFengli() + todayWeather.getFengxiang());
        switch (todayWeather.getQuality()){
            case "良":
                pmImg.setImageResource(R.drawable.biz_plugin_weather_101_150);
                break;
            case "优":
                pmImg.setImageResource(R.drawable.biz_plugin_weather_51_100);
                break;
            case "轻度污染":
                pmImg.setImageResource(R.drawable.biz_plugin_weather_151_200);
                break;
            case"中度污染":
                pmImg.setImageResource(R.drawable.biz_plugin_weather_201_300);
                break;
            case"重度污染":
                pmImg.setImageResource(R.drawable.biz_plugin_weather_greater_300);
                break;
        }

        switch (todayWeather.getType()){
            case"阴":weatherImg.setImageResource(R.drawable.biz_plugin_weather_yin);
            break;
            case"晴":weatherImg.setImageResource(R.drawable.biz_plugin_weather_qing);
            break;
            case"多云":weatherImg.setImageResource(R.drawable.biz_plugin_weather_duoyun);
            break;
            case"晴转多云":weatherImg.setImageResource(R.drawable.biz_plugin_weather_duoyun);
            break;
            case "雾":weatherImg.setImageResource(R.drawable.biz_plugin_weather_wu);
            break;
            case "小雨":weatherImg.setImageResource(R.drawable.biz_plugin_weather_xiaoyu);
            break;
            case "中雨":weatherImg.setImageResource(R.drawable.biz_plugin_weather_zhenyu);
            break;
            case"暴雨":weatherImg.setImageResource(R.drawable.biz_plugin_weather_baoyu);
            break;
            case "雷阵雨":weatherImg.setImageResource(R.drawable.biz_plugin_weather_leizhenyu);
            break;
            case "雨夹雪":weatherImg.setImageResource(R.drawable.biz_plugin_weather_yujiaxue);
            break;
            case "小雪":weatherImg.setImageResource(R.drawable.biz_plugin_weather_xiaoxue);
            break;
            case "中雪":weatherImg.setImageResource(R.drawable.biz_plugin_weather_zhongxue);
            break;
            case "大雪":weatherImg.setImageResource(R.drawable.biz_plugin_weather_daxue);
            break;
            case "冰雹":weatherImg.setImageResource(R.drawable.biz_plugin_weather_leizhenyubingbao);
            break;
        }
        Toast.makeText(MainActivity.this, "The update is successful", Toast.LENGTH_LONG).show();
    }
}



