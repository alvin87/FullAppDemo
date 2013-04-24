package com.alvin.api.components;

import cn.com.pcgroup.common.android.utils.DisplayUtils;

import com.alvin.api.utils.SettingsUtils;
import com.alvin.api.utils.UMengAnalyseUtils;
import com.app43.appclient.R;
import com.app43.appclient.module.tabframe.AppManagerActivity;
import com.app43.appclient.module.tabframe.GuessInterestActivity;
import com.app43.appclient.module.tabframe.HotGridActivity;
import com.app43.appclient.module.tabframe.NewsGridActivity;
import com.app43.appclient.module.tabframe.RecommendAppActivity;

import android.app.ActivityGroup;
import android.content.Intent;
import android.view.Display;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;
import android.widget.TabWidget;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * 项目名称：app43 类名称：TabHostActivityGroup 类描述：Tabhost的父类 创建人：APP43 创建时间：2012-3-13
 * 下午1:06:42 修改人：APP43 修改时间：2012-3-13 下午1:06:42 修改备注：
 * 
 * @version
 * 
 */
public class TabHostActivityGroup extends ActivityGroup {

    protected final static String TAG = TabHostActivityGroup.class
            .getSimpleName();

    protected static Map<Integer, int[]> tabIconMap = new HashMap<Integer, int[]>();// tanhost的item图片ID
    protected static int flag = 0;// 判断是否为从通知栏点击进来应用管理界面

    // 设置各个item图片
    static {
        tabIconMap.put(0, new int[] { R.drawable.main_tab_frame_news_current,
                R.drawable.main_tab_frame_news });
        tabIconMap.put(1, new int[] {
                R.drawable.main_tab_frame_gallery_current,
                R.drawable.main_tab_frame_gallery });
        tabIconMap.put(2, new int[] {
                R.drawable.main_tab_frame_product_category_current,
                R.drawable.main_tab_frame_product_category });
        tabIconMap.put(3, new int[] { R.drawable.main_tab_frame_bbs_current,
                R.drawable.main_tab_frame_bbs });
        tabIconMap.put(4, new int[] { R.drawable.main_tab_frame_more_current,
                R.drawable.main_tab_frame_more });
    }

    // 各个item标题
    protected int textId[] = new int[] { R.string.tab_commend_name,
            R.string.tab_hot_name, R.string.tab_guess_name,
            R.string.tab_manager_name, R.string.tab_news_name };

    /**
     * 作用:设置子activity的高度
     */
    protected void setItemActivityHeight() {
        Display display = getWindowManager().getDefaultDisplay();
        int ScreenDipHeight = DisplayUtils.convertPX2DIP(this,
                display.getHeight());
        int tabContentHeight = ScreenDipHeight - SettingsUtils.TOPBOTTOM; // 设置tab子Activity的高度

        this.setContentView(R.layout.main_tabhost_frame_activity);
        FrameLayout tabFrame = (FrameLayout) this
                .findViewById(android.R.id.tabcontent);
        tabFrame.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.FILL_PARENT, DisplayUtils
                        .convertDIP2PX(this, tabContentHeight)));
    }

    /**
     * 作用:设置tabhost各个子item
     */
    public void updateMainTabView() {

        // 设置Tabhost
        final TabHost tabHost = (TabHost) findViewById(R.id.tabhost);
        tabHost.setup(this.getLocalActivityManager());
        final TabWidget tabWidget = tabHost.getTabWidget();

        // 设置item0tab
        tabhostAddTab(tabHost, R.layout.main_tabhost_frame_tab_item,
                R.string.tab_commend_name, R.drawable.main_tab_frame_news, 0,
                "item0", RecommendAppActivity.class);

        tabhostAddTab(tabHost, R.layout.main_tabhost_frame_tab_item,
                R.string.tab_hot_name, R.drawable.main_tab_frame_gallery, 1,
                "item1", HotGridActivity.class);

        tabhostAddTab(tabHost, R.layout.main_tabhost_frame_tab_item,
                R.string.tab_guess_name,
                R.drawable.main_tab_frame_product_category, 2, "item2",
                GuessInterestActivity.class);

        tabhostAddTab(tabHost, R.layout.main_tabhost_frame_tab_item,
                R.string.tab_manager_name, R.drawable.main_tab_frame_more, 3,
                "item3", AppManagerActivity.class);

        tabhostAddTab(tabHost, R.layout.main_tabhost_frame_tab_item,
                R.string.tab_news_name, R.drawable.main_tab_frame_bbs, 4,
                "item4", NewsGridActivity.class);

        // 设置初始化样式.
        RelativeLayout tabWidgetLayout = null;
        
        //设置第0个tab为默认
        tabHost.setCurrentTab(flag);
        forTabSet(tabWidget, tabWidgetLayout, tabHost);

        tabHost.setOnTabChangedListener(new OnTabChangeListener() {
            @Override
            public void onTabChanged(String arg0) {

                //  统计查看数
                UMengAnalyseUtils.onEvents(
                        TabHostActivityGroup.this,
                        UMengAnalyseUtils.VIEW_TABs,
                        getResources().getString(
                                textId[tabHost.getCurrentTab()]));

                // 设置item
                RelativeLayout tabWidgetLayout = null;
                forTabSet(tabWidget, tabWidgetLayout, tabHost);
            }
        });
    }

    /**
     * 作用: 为每个item设置样式
     */
    protected void tabhostAddTab(TabHost tabHost, int itemLayoutId, int textId,
            int iconID, int itemId, String itemIdString, Class activityClass) {
        // 设置tabLayout
        RelativeLayout tabLayout = (RelativeLayout) LayoutInflater.from(this)
                .inflate(itemLayoutId, null);
        TextView item0TabText = (TextView) tabLayout
                .findViewById(R.id.tab_text);
        ImageView item0TabIcon = (ImageView) tabLayout
                .findViewById(R.id.tab_icon);
        item0TabText.setText(this.getString(textId));
        item0TabIcon.setBackgroundResource(iconID);

        // 初始化精品intent 和tabspec并添加到tabhost
        Intent tabIntent = new Intent();
        tabIntent.setClass(this, activityClass);
        tabIntent.putExtra("item", itemId);
        TabSpec tabSpec = tabHost.newTabSpec(itemIdString);
        tabSpec.setIndicator(tabLayout);// 加载view
        tabSpec.setContent(tabIntent);
        tabHost.addTab(tabSpec);
    }

    /**
     * 作用:遍历所有item,并且设置当前选中item样式
     */
    private void forTabSet(TabWidget tabWidget, RelativeLayout tabWidgetLayout,
            TabHost tabHost) {
        for (int i = 0; i < tabWidget.getChildCount(); i++) {
            tabWidgetLayout = (RelativeLayout) tabWidget.getChildAt(i);
            TextView text = (TextView) tabWidgetLayout
                    .findViewById(R.id.tab_text);
            ImageView icon = (ImageView) tabWidgetLayout
                    .findViewById(R.id.tab_icon);
            int[] tabIcon = tabIconMap.get(i);

            // 当前tab为选中时高亮图片
            if (tabHost.getCurrentTab() == i) {
                icon.setBackgroundResource(tabIcon[0]);
                text.setTextColor(this.getResources().getColorStateList(
                        R.color.white));
                tabWidgetLayout
                        .setBackgroundDrawable(getResources()
                                .getDrawable(
                                        R.drawable.main_tab_frame_tabspec_background_current));
            } else {
                icon.setBackgroundResource(tabIcon[1]);
                text.setTextColor(this.getResources().getColorStateList(
                        R.color.gray));
                tabWidgetLayout.setBackgroundDrawable(null);
            }
        }
    }
}
