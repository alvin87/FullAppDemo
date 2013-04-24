package com.app43.appclient.module.receive;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * 监听卸载程序的服务
 * 
 * 项目名称：appclient 类名称：UninstallAppReceive 类描述： 创建人：APP43 创建时间：2011-12-6
 * 上午9:42:48 修改人：APP43 修改时间：2011-12-6 上午9:42:48 修改备注：
 * 
 * @version
 * 
 */
public class UninstallAppReceive extends BroadcastReceiver {
    private OnClickUninstallListener listener;
    private int position;

    public UninstallAppReceive(OnClickUninstallListener iListener, int index) {
        position = index;
        listener = iListener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        listener.onClickUninstall(intent, position, context, this);
    }

    public interface OnClickUninstallListener {
        public void onClickUninstall(Intent intent, int index, Context context,
                UninstallAppReceive uninstallAppReceive);
    }

}
