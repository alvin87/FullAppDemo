package com.alvin.api.utils;

import com.app43.appclient.bean.App;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import java.util.ArrayList;
import java.util.List;

public class LocalAppUtils {
    
    /**
     * 读本地已下载程序
     * 
     * @author new
     */
    public static class readLocalApp extends Thread {
        Context context;
        Handler handler;
        Message message;
        List<App> localAppList=new ArrayList<App>();

      

        public readLocalApp(Context mContext,Handler hd) {
            context = mContext;
            handler=hd;
            message=handler.obtainMessage();
        }

        @Override
        public void run() {
            // TODO 读取本地已下载程序
            super.run(); 
         
            handler.sendMessage(message);
        }
    }

}
