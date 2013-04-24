package com.app43.appclient.module.receive;

import com.alvin.api.utils.SettingsUtils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ClickCancleReceive extends BroadcastReceiver {

    OnClickCancleListener onClickCancleListener;

    public ClickCancleReceive(OnClickCancleListener listener) {
        onClickCancleListener = listener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {

//        if (intent.getBooleanExtra(Settings.CANCLE_INTENT, false)) {
            
            if (intent.getStringExtra(SettingsUtils.APK_NAME) != null
                    && (!intent.getStringExtra(SettingsUtils.APK_NAME).equals(""))
                    && (intent.getStringExtra(SettingsUtils.APK_NAME) != "")) {

                onClickCancleListener.OnClickCancle(intent
                        .getStringExtra(SettingsUtils.APK_NAME),context);
            }
//        }
    }

    public interface OnClickCancleListener {
        public void OnClickCancle(String appName,Context context);
    }

}
