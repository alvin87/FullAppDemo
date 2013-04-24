package com.app43.appclient.module.abstracts.activity;

import com.app43.appclient.AppClient;

/*
 * 发送数据,退出,以及菜单功能的基类
 */
public abstract class SendDataWithExitNomenuBaseActivity extends
        SendDataNoMenuActivity {

    @Override
    public void onBackPressed() {
        ((AppClient) this.getApplication()).exit(this);
    }

}
