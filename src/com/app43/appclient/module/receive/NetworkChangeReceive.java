package com.app43.appclient.module.receive;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkChangeReceive extends BroadcastReceiver {

    OnStateChangeListener onStateChangeListener;

    public NetworkChangeReceive(OnStateChangeListener mChangeListener) {
        onStateChangeListener = mChangeListener;
    }

    public interface OnStateChangeListener {
        public void onStateChange(boolean networkEnable);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mobNetInfo = connectivityManager
                .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        NetworkInfo wifiNetInfo = connectivityManager
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        boolean enable = false;
        if (mobNetInfo.isConnected() || wifiNetInfo.isConnected()) {
            enable = true;
        }
        onStateChangeListener.onStateChange(enable);
    }

}
