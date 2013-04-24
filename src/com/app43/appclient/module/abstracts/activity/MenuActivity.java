package com.app43.appclient.module.abstracts.activity;

import com.alvin.api.utils.UMengAnalyseUtils;
import com.app43.appclient.AppClient;
import com.app43.appclient.R;
import com.app43.appclient.module.menu.AboutUsActivity;
import com.app43.appclient.module.menu.FavouriteActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

/*
 * 带有菜单功能的Activity
 */
public abstract class MenuActivity extends BaseActivity {
    protected PopupWindow mPopupWindow;
    protected RelativeLayout favouriteMenu;
    protected RelativeLayout exitMenu;
    protected RelativeLayout feedbackMenu;
    protected RelativeLayout aboutMenu;
    View menuView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        settingMenu();
        setupViews();
    }

    /**
     * 菜单设置
     */
    private void settingMenu() {
        menuView = getLayoutInflater().inflate(R.layout.recommend_menu, null);
        mPopupWindow = new PopupWindow(menuView,
                LinearLayout.LayoutParams.FILL_PARENT,
                LinearLayout.LayoutParams.FILL_PARENT);
        favouriteMenu = (RelativeLayout) menuView
                .findViewById(R.id.recommend_menu_child_favourite);
        exitMenu = (RelativeLayout) menuView
                .findViewById(R.id.recommend_menu_child_setting);
        feedbackMenu = (RelativeLayout) menuView
                .findViewById(R.id.recommend_menu_child_select_feedback);
        aboutMenu = (RelativeLayout) menuView
                .findViewById(R.id.recommend_menu_child_about);
        favouriteMenu.setOnClickListener(listener);
        exitMenu.setOnClickListener(listener);
        feedbackMenu.setOnClickListener(listener);
        aboutMenu.setOnClickListener(listener);
        menuView.setOnClickListener(listener);
        mPopupWindow.setAnimationStyle(R.style.menuPopupWindowAnimation);
    }

    private View.OnClickListener listener = new View.OnClickListener() {
        public void onClick(View v) {
            if (v.equals(favouriteMenu)) {
                Intent intent = new Intent();
                intent.setClass(getApplicationContext(),
                        FavouriteActivity.class);
                startActivity(intent);
            } else if (v.equals(aboutMenu)) {
                Intent intent = new Intent();
                intent.setClass(getApplicationContext(), AboutUsActivity.class);
                startActivity(intent);
            } else if (v.equals(exitMenu)) {
                AppClient.isMenuExit = true;
                ((AppClient) MenuActivity.this.getApplication())
                        .exit(MenuActivity.this);
            } else if (v.equals(feedbackMenu)) {
                // Intent intent = new Intent();
                // intent.setClass(getApplicationContext(),
                // FeedbackActivity.class);
                // startActivity(intent);
                UMengAnalyseUtils.openUmengFeedbackSdk(MenuActivity.this);
            }
            mPopupWindow.dismiss();
        }
    };

    /**
     * 显示菜单
     */
    private void showMenuView() {
        if (!mPopupWindow.isShowing()) {
            mPopupWindow.showAtLocation(menuView, Gravity.BOTTOM, 0, 0);
        } else {
            mPopupWindow.dismiss();
        }
    }

    /**
     * 监听 菜单键点击事件与返回键有区别
     */
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_MENU
                || (mPopupWindow.isShowing() && keyCode == KeyEvent.KEYCODE_BACK)) {
            showMenuView();
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_BACK) {
            onBackPressed();
            return true;
        }
        return false;
    }

    public abstract void setupViews();

}
