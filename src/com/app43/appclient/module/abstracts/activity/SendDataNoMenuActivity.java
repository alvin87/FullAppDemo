package com.app43.appclient.module.abstracts.activity;

import android.view.KeyEvent;

/*
 * 发送数据,退出,以及菜单功能的基类
 */
public abstract class SendDataNoMenuActivity extends SendDataBaseActivity {
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            onBackPressed();
            return true;
        }
        return false;
    }
}
