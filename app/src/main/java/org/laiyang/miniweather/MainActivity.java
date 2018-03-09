package org.laiyang.miniweather;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import org.laiyang.util.Netutil;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by 小米笔记本Pro on 2018/3/7.
 */

public class MainActivity extends Activity implements View.OnClickListener{

    private ImageView mUpdateBtn;

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
    public  void onClick(View view) {
        if(view.getId() == R.id.title_update_btn){
            SharedPreferences sharedPreferences = getSharedPreferences("config",MODE_PRIVATE);
            String CityCode = sharedPreferences.getString("main_city","101040100");
            Log.d("myWeather",CityCode);
            if(Netutil.getNetworkState(this) !=Netutil.NETWOEN_NONE){
                Log.d("myWeather","网络OJBK");
              queryWeatherCode(CityCode);
            }else{
                Log.d("myWeather","网络挂了");
                Toast.makeText(MainActivity.this,"网络挂了！",Toast.LENGTH_LONG).show();
                sharedPreferences.edit();
            }
        }
    }
    private  void queryWeatherCode(String cityCode){
        final String address = "http://www.weather.com.cn/data/sk/"+cityCode+".html";
        Log.d("myWeather",address);
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection con=null;
                try{
                    URL url = new URL(address);
                    con = (HttpURLConnection)url.openConnection();
                    con.setRequestMethod("GET");
                    con.setConnectTimeout(8000);
                    con.setReadTimeout(8000);
                    InputStream in = con.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder response = new StringBuilder();
                    String str;
                    while ((str = reader.readLine()) != null){
                        response.append(str);
                        Log.d("myWeather",str);
                    }
                    String responseStr = response.toString();
                    Log.d("myWeather",responseStr);
                }catch (Exception e){
                    e.printStackTrace();
                }finally {
                    if(con !=null){
                        con.disconnect();
                    }
                }
            }
        }).start();
    }
}
