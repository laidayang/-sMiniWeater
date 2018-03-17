package org.laiyang.miniweather;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.readystatesoftware.systembartint.SystemBarTintManager;

import static android.content.ContentValues.TAG;

public class city_MainActivity extends Activity {

    private ImageView select_back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.city_select_layout);
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
        View view = findViewById(R.id.select_city_title);
        view.getBackground().setAlpha(0);
        select_back=findViewById(R.id.select_city_back);
        select_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(city_MainActivity.this,MainActivity.class);
                i.putExtra("cityCode","101160101");
                setResult(RESULT_OK,i);
                Log.d("myweather","citycode"+1234567);
                finish();
            }
        });
    }
}
