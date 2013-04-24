package com.app43.appclient.module.menu;

import com.alvin.api.components.DialogAndToast;
import com.alvin.api.utils.LogOutputUtils;
import com.alvin.api.utils.SettingsUtils;
import com.alvin.api.utils.UMengAnalyseUtils;
import com.app43.appclient.R;
import com.app43.appclient.bean.App;
import com.app43.appclient.module.abstracts.activity.BaseListViewActivity;
import com.app43.appclient.module.adapter.FavAppListAdapter;
import com.app43.appclient.module.utils.ParseJsonUtils;

import org.json.JSONException;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FavouriteActivity extends BaseListViewActivity {
    public int[] appID; // 收藏数据库的appId
    boolean gotoDisplayFsh = true;// 显示的是否为删除按钮
    Button delButton;// 删除按钮,完成按钮
    private List<Integer> delId;// 要删除的appid
    FavouriteActivity instance;
    FrameLayout frameLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // LogOutput.i(FavouriteActivity.class.getSimpleName(), "oncrete");
        instance = this;
        // TODO 当收藏数小于20时去掉list foot
        // appID暂时先用网络获取的app
        // 待数据接口出来再读取收藏夹数据库
        // 待完善数据库数据
        // 客户端做分页处理
        appID = dbFavourite.getAppID();
        String test = "";
        for (int i = 0; i < appID.length; i++) {
            test += String.valueOf(appID[i]) + "||";
        }
        setInitData("FavouriteActivity", R.layout.favourite_app_activity,
                R.string.favourite_title, "");
        super.onCreate(savedInstanceState);
    }

    /**
     * 配置整个view
     */
    @Override
    public void setupViews() {
        super.setupViews();
        frameLayout = (FrameLayout) findViewById(R.id.favourite_frame_layout);
        delButton = (Button) findViewById(R.id.bt_del);
        if (gotoDisplayFsh) {
            // delButton.setText("删除");
            delButton.setBackgroundResource(R.drawable.delete);
        } else {
            // delButton.setText("完成");
            delButton.setBackgroundResource(R.drawable.delete_open);
        }
        delButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                if (gotoDisplayFsh) {
                    // delButton.setText("完成");
                    delButton.setBackgroundResource(R.drawable.delete_open);
                    gotoDisplayFsh = !gotoDisplayFsh;
                    if (isServerAppFinish && isLocalAppFinish
                            && isInstallAppFinish) {
                        aListAdapter.updateList(serverAppList,
                                isServerAppFinish, localAppList,
                                isLocalAppFinish, installAppList,
                                isInstallAppFinish, listProgressBar,
                                gotoDisplayFsh, delHandler, downloadUrl,
                                pageNo, appID, "点击删除");
                    }
                } else {
                    if (delId != null && !delId.isEmpty()) {
                        delApp();
                    } else {
                        // LogOutput.e(TAG, "test");
                        // delButton.setText("删除");
                        delButton.setBackgroundResource(R.drawable.delete);
                        gotoDisplayFsh = !gotoDisplayFsh;
                        aListAdapter.updateList(serverAppList,
                                isServerAppFinish, localAppList,
                                isLocalAppFinish, installAppList,
                                isInstallAppFinish, listProgressBar,
                                gotoDisplayFsh, delHandler, downloadUrl,
                                pageNo, appID, "点击完成");
                        DialogAndToast.showToast(FavouriteActivity.this,
                                "未选择任何项");
                    }
                }
            }
        });
        if (appID != null && appID.length > 0) {
            // 收藏夹不为空才发送请求
            listProgressBar.setVisibility(View.VISIBLE);
            downloadUrl = SettingsUtils.URL_FAVOURITE;
            for (int i = 0; i < appID.length; i++) {
                if (i == appID.length - 1) {
                    downloadUrl += appID[i];
                } else {
                    downloadUrl += appID[i] + ",";
                }
            }
            LogOutputUtils.i(TAG, "fav app.size():" + appID.length);
            frameLayout.setBackgroundDrawable(null);
        } else {
            LogOutputUtils.i(TAG, "fav app.size():" + appID.length);
            downloadUrl = "";
            isServerAppFinish = true;
            isFavNull = true;
            frameLayout.setBackgroundResource(R.drawable.no_content);
        }
    }

    @Override
    public void initData() {
        pageNo = -1;
        listView.setVisibility(View.VISIBLE);
        // TODO 测试时isRefresh为true,上线时为false;
        isRefresh = true;
        serverAppList.clear();
    }

    @Override
    protected void setupAdapter() {
        if (aListAdapter == null) {
            aListAdapter = new FavAppListAdapter(this);
        }
        positionListener = aListAdapter.getPositionListener();
    }

    @Override
    protected void setupHandler() {

        // 本地安装程序handle
        super.installAppHandler = new Handler() {
            public void handleMessage(Message msg) {
                LogOutputUtils.e("favouriteActivity", "installHandler");
                installAppList = (List<App>) msg.obj;
                isInstallAppFinish = true;
                // LogOutput.e(TAG, "installAppHandler"+appID.length);
                if (isServerAppFinish && isLocalAppFinish && isInstallAppFinish) {
                    aListAdapter.setAppcount(appID.length);
                    aListAdapter.updateList(serverAppList, isServerAppFinish,
                            localAppList, isLocalAppFinish, installAppList,
                            isInstallAppFinish, listProgressBar,
                            gotoDisplayFsh, delHandler, downloadUrl, pageNo,
                            appID, "读取本地安装完成");
                }
            }
        };
        // 读本地已下载程序handle
        super.localAppHandle = new Handler() {
            public void handleMessage(Message msg) {
                isLocalAppFinish = true;
                LogOutputUtils.e("favouriteActivity", "localAppHandle");
                // LogOutput.e(TAG, "localAppHandle"+appID.length);
                if (isServerAppFinish && isLocalAppFinish && isInstallAppFinish) {
                    aListAdapter.setAppcount(appID.length);
                    aListAdapter.updateList(serverAppList, isServerAppFinish,
                            localAppList, isLocalAppFinish, installAppList,
                            isInstallAppFinish, listProgressBar,
                            gotoDisplayFsh, delHandler, downloadUrl, pageNo,
                            appID, "读取本地下载完成");
                }
            };
        };
    };

    @Override
    protected void updateListView() {
        if (serverAppList != null && serverAppList.size() > 0) {
            isServerAppFinish = true;

            aListAdapter.setAppcount(appID.length);
            // 判断是否到达所请求app的总数
            // if (aListAdapter.getCount() < appID.length && appID.length >
            // 20) {
            // aListAdapter.setAppcount(appID.length);
            // footLayout.setVisibility(View.VISIBLE);
            // footerViewIsVisible = true;
            // } else {
            // footLayout.setVisibility(View.GONE);
            // footerViewIsVisible = false;
            // }
            // LogOutput.e(TAG, "isServerAppFinish" + isServerAppFinish
            // + ",isLocalAppFinish" + isLocalAppFinish
            // + ",isInstallAppFinish" + isInstallAppFinish);
            if (isServerAppFinish && isLocalAppFinish && isInstallAppFinish) {
                // LogOutput.e(TAG, "handleView"+appID.length);
                aListAdapter.setAppcount(appID.length);
                aListAdapter.updateList(serverAppList, isServerAppFinish,
                        localAppList, isLocalAppFinish, installAppList,
                        isInstallAppFinish, listProgressBar, gotoDisplayFsh,
                        delHandler, downloadUrl, pageNo, appID, "读取服务器完成");
            }
        }

    }

    Handler delHandler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.obj != null) {
                if (msg.what == 1) {
                    if (delId == null) {
                        delId = new ArrayList<Integer>();
                    }
                    if (!delId.contains((Integer) msg.obj)) {
                        delId.add((Integer) msg.obj);
                    }
                    // for (int i = 0; i < delId.size(); i++) {
                    // LogOutput.e(TAG, i+":"+delId.get(i));
                    // }
                } else {
                    if (delId != null) {
                        if (delId.contains((Integer) msg.obj)) {
                            delId.remove((Integer) msg.obj);
                        }
                    }

                }
            }
        };
    };

    public void delApp() {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        alertBuilder
                .setTitle(this.getString(R.string.dialog_app_exit_title))
                .setMessage(this.getString(R.string.dialog_del_app))
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        boolean error = false;
                        for (int i = 0; i < delId.size(); i++) {

                            // LogOutput.e(TAG, i + "i:  " + delId.get(i));
                            // LogOutput.e(TAG,i+"::"+
                            error = !dbFavourite.delete(delId.get(i))
                            // )
                            ;
                            if (error) {
                                LogOutputUtils.e(TAG, "del appId error at :"
                                        + delId.get(i));
                            }
                        }
                        if (error) {
                            DialogAndToast.showToast(FavouriteActivity.this,
                                    "删除遇到部分错误!");
                        } else {
                            DialogAndToast.showToast(FavouriteActivity.this,
                                    "删除成功!");
                        }
                        delId = null;
                        Intent intent = new Intent();
                        intent.setClass(instance, FavouriteActivity.class);
                        startActivity(intent);
                        instance.finish();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        alertBuilder.create().show();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            onBackPressed();
            return true;
        }
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        UMengAnalyseUtils.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        UMengAnalyseUtils.onPause(this);
    }

    @Override
    protected void setScrollChanedListen(int scrollState) {
    }
}
