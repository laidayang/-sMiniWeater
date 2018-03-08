package org.laiyang.util;

/**
 * Created by 小米笔记本Pro on 2018/3/8.
 */

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
public class Netutil {
    public static final int NETWOEN_NONE = 0;
    public static final int NETWORN_WIFI = 1;
    public static final int NETWORN_MOBILE = 2;
    public static int getNetworkState(Context context){
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
       NetworkInfo networkInfo = connManager.getActiveNetworkInfo();
       if(networkInfo == null){
           return  NETWOEN_NONE;
       }
       int nType = networkInfo.getType();
       if(nType == ConnectivityManager.TYPE_MOBILE){
           return NETWORN_MOBILE;
       }
       else if(nType == ConnectivityManager.TYPE_WIFI){
           return NETWORN_WIFI;
       }
       return NETWOEN_NONE;
    }
}
