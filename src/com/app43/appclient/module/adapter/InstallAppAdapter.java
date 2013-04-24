package com.app43.appclient.module.adapter;

import com.alvin.api.utils.LogOutputUtils;
import com.app43.appclient.R;
import com.app43.appclient.bean.InstallApp;
import com.app43.appclient.module.receive.UninstallAppReceive;
import com.app43.appclient.module.receive.UninstallAppReceive.OnClickUninstallListener;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class InstallAppAdapter extends BaseAdapter {

    private static String TAG = InstallAppAdapter.class.getSimpleName();
    int appcount = 0;
    private static Activity activity;
    private static boolean readAppFinish = false;
    private static List<InstallApp> appList = new ArrayList<InstallApp>();
    OnClickUninstallListener onClickUninstallListener;
    static InstallViewHolder installView;

    public InstallAppAdapter(Activity iaActivity) {
        activity = iaActivity;
    }

    @Override
    public int getCount() {
        return appcount;
    }

    @Override
    public Object getItem(int arg0) {
        return arg0;
    }

    public void setAppcount(int appcount) {
        this.appcount = appcount;
    }

    @Override
    public long getItemId(int pt) {
        return pt;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final int index = position;
        if (convertView == null) {
            installView = new InstallViewHolder();
            convertView = LayoutInflater.from(activity).inflate(
                    R.layout.manager_app_list_item, null);
            installView.icon = (ImageView) convertView
                    .findViewById(R.id.manager_app_icon);
            installView.name = (TextView) convertView
                    .findViewById(R.id.manager_app_name);
            installView.size = (TextView) convertView
                    .findViewById(R.id.manager_app_size);
            installView.version = (TextView) convertView
                    .findViewById(R.id.manager_app_version);
            installView.intro = (TextView) convertView
                    .findViewById(R.id.manager_app_introduce);
            installView.delButton = (Button) convertView
                    .findViewById(R.id.manager_app_button);
            installView.linearLayout = (LinearLayout) convertView
                    .findViewById(R.id.app_similar_layout);
            installView.icon1 = (ImageView) convertView
                    .findViewById(R.id.manager_app_icon_similar);
            installView.name1 = (TextView) convertView
                    .findViewById(R.id.manager_app_name_similar);
            installView.size1 = (TextView) convertView
                    .findViewById(R.id.manager_app_size_similar);
            installView.version1 = (TextView) convertView
                    .findViewById(R.id.manager_app_version_similar);
            // viewHolder.intro1 = (TextView) convertView
            // .findViewById(R.id.manager_app_introduce_similar);
            installView.installButton = (Button) convertView
                    .findViewById(R.id.manager_app_button_similar);
            convertView.setTag(installView);
        } else {
            installView = (InstallViewHolder) convertView.getTag();
            installView.linearLayout.setVisibility(View.GONE);
        }

        // 程序加载完成
        // LogOutput.e("app.size", "size:" + appList.size() + "pp:" + index);
        if (readAppFinish && (index <= (appList.size() - 1))) {
            installView.icon.setBackgroundDrawable(appList.get(index)
                    .getDrawable());
            installView.name.setText(appList.get(index).getAppName());
            float fappsize;
            long lenth = appList.get(index).getSize();
            String lenString = "";
            if (lenth > 1024 * 1024) {
                fappsize = (float) lenth / 1024 / 1024;
                fappsize = (float) (Math.round(fappsize * 100) / 100.0);
                lenString = fappsize + "MB";
            } else if (lenth > 1024) {
                lenth = lenth / 1024;
                lenString = lenth + "KB";
            } else {
                lenString = lenth + "B";
            }
            installView.size.setText("大小: " + lenString);
            installView.version.setText("版本: "
                    + appList.get(index).getVerName());
            installView.delButton.setVisibility(View.VISIBLE);
            installView.delButton.setBackgroundResource(R.drawable.delete_icon);
            installView.delButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    LogOutputUtils.i(TAG, appList.get(index).getPackageName()
                            .toString());
                    UninstallAppReceive mUninstallReceiver = new UninstallAppReceive(
                            onClickUninstallListener, index);
                    IntentFilter filter = new IntentFilter(
                            Intent.ACTION_PACKAGE_REMOVED);
                    filter.addDataScheme("package");
                    activity.registerReceiver(mUninstallReceiver, filter);

                    Uri packageURI = Uri.parse("package:"
                            + appList.get(index).getPackageName().toString());
                    Intent uninstallIntent = new Intent(Intent.ACTION_DELETE,
                            packageURI);
                    activity.startActivity(uninstallIntent);

                }
            });
            // LogOutput.e(TAG, index + "posiition" +
            // appList.get(index).getName());
            // if (appList.get(index).getAppName().equals("手机QQ")) {
            // installView.linearLayout.setVisibility(View.VISIBLE);
            // installView.icon1.setBackgroundDrawable(appList.get(index)
            // .getDrawable());
            // installView.name1.setText(installView.name.getText());
            // installView.size1.setText("大小: " + lenString);
            // installView.version1.setText("版本: "
            // + appList.get(index).getVerName());
            // installView.intro.setVisibility(View.VISIBLE);
            // installView.intro.setText("可以下载我们为您提供的应用程序");
            // installView.installButton
            // .setBackgroundResource(R.drawable.item_btn_download);
            // installView.installButton
            // .setOnClickListener(new OnClickListener() {
            // @Override
            // public void onClick(View v) {
            // // TODO 下载
            //
            // }
            // });
            // } else {
            // installView.intro.setVisibility(View.GONE);
            // }

        }

        return convertView;
    }

    private class InstallViewHolder {
        private ImageView icon = null, icon1;
        private TextView name = null, name1 = null, size = null, size1 = null,
                version = null, version1 = null, intro = null// 推荐应用介绍
                // , intro1 = null
                ;
        private Button delButton, installButton;
        private LinearLayout linearLayout;
    }

    public void updateList(Activity iActivity, boolean iReadAppFinish,
            List<InstallApp> iAppList,
            OnClickUninstallListener iClickUninstallListener, boolean download) {
        onClickUninstallListener = iClickUninstallListener;
        activity = iActivity;
        readAppFinish = iReadAppFinish;
        appList = iAppList;
        notifyDataSetChanged();
    }

}