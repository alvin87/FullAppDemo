package com.app43.appclient.module.tabframe;

/**
 * 
 *  listView滑动网络问题 ,数据库app扫描对比没有做
 */

import cn.com.pcgroup.common.android.utils.DisplayUtils;

import com.alvin.api.utils.LogOutputUtils;
import com.alvin.api.utils.SettingsUtils;
import com.alvin.api.utils.UMengAnalyseUtils;
import com.app43.appclient.R;
import com.app43.appclient.bean.App;
import com.app43.appclient.bean.RecommendTitle;
import com.app43.appclient.module.abstracts.activity.BaseListViewActivity;
import com.app43.appclient.module.abstracts.activity.BaseListViewWithExitActivity;
import com.app43.appclient.module.adapter.FocusAppListAdapter;
import com.app43.appclient.module.common.activity.FocusContentViewActivity;
import com.app43.appclient.module.utils.ParseJsonUtils;

import org.json.JSONException;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.Gallery;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecommendAppActivity extends BaseListViewWithExitActivity {

    // TODO 需重写抽象的方法如HotActivity
    protected ArrayList<App> focusList = new ArrayList<App>();
    // protected List<App> oldFocusList = new ArrayList<App>(); //
    // 因为从第二页起没有focus对象
    // 焦点图会为空,所以要把旧的对象赋值
    protected View headerView; // 列表头部view
    protected Gallery focusGallery;
    protected ProgressBar progressBar;// 焦点图的菊花
    protected FrameLayout headerLayout;
    protected FocusAppListAdapter imageAdapter;
    protected LinearLayout titleLayout;

    protected static int FOCUS_POSITION = 300;

    static Bundle instanceState;
    protected static int titleFlag = 0;// 0:全部,1:游戏 2:软件 3:装机必备
    TextView allTextView, gameTextView, softwaveTextView, musTextView;
    int navigationIconId[] = { R.drawable.navigation1, R.drawable.navigation2,
            R.drawable.navigation3, R.drawable.navigation4 };
    String urlString[] = { SettingsUtils.URL_RECOMMEND_ALL,
            SettingsUtils.URL_RECOMMEND_GAME,
            SettingsUtils.URL_RECOMMEND_SOFTWAVE,
            SettingsUtils.URL_RECOMMEND_MUST };

    String navigationString[] = new String[] { "全部", "游戏", "软件", "装机" };
    int first, last;// 只针对焦点图而已,根据焦点图进入到终端页如果点击有变化返回列表是若有此app则列表要更新

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // LogOutput.e(TAG, "onCreate");
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            installRecommendApps = bundle
                    .getParcelableArrayList(SettingsUtils.INSTALL_RECOMMEND);
        }

        instanceState = savedInstanceState;
        String tempUrl = "";

        switch (titleFlag) {
        case 0:
            tempUrl = SettingsUtils.URL_RECOMMEND_ALL;
            break;
        case 1:
            tempUrl = SettingsUtils.URL_RECOMMEND_GAME;
            break;
        case 2:
            tempUrl = SettingsUtils.URL_RECOMMEND_SOFTWAVE;
            break;
        case 3:
            tempUrl = SettingsUtils.URL_RECOMMEND_MUST;
            break;

        default:
            break;
        }
        LogOutputUtils.i(TAG, "titleFlag:" + titleFlag + "||  tempurl:"
                + tempUrl);
        setInitData("RecommendAppActivity",
                R.layout.recommend_app_list_activity,
                R.string.tab_commend_name, tempUrl);
        super.onCreate(savedInstanceState);
    }

    /**
     * 添加listview头
     */
    protected void addHeaderView() {
        headerView = mInflater
                .inflate(R.layout.recommend_app_list_header, null);
        headerLayout = (FrameLayout) headerView
                .findViewById(R.id.show_picture_layout);
        focusGallery = (Gallery) headerView.findViewById(R.id.show_picture);
        progressBar = (ProgressBar) headerView
                .findViewById(R.id.header_gallery_loadprogress);
        focusGallery.setFadingEdgeLength(0);
        listView.addHeaderView(headerView);

        headerLayout.setVisibility(View.VISIBLE);
        focusGallery.setVisibility(View.VISIBLE);

    }

    @Override
    protected void setupListView() {
        listView = (ListView) findViewById(R.id.commend_app_listview);
        listView.setScrollbarFadingEnabled(true);
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            // 添加滚动条滚到最底部，加载余下的元素
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                setScrollChanedListen(scrollState);
            }

            public void onScroll(AbsListView view, int firstVisibleItem,
                    int visibleItemCount, int totalItemCount) {
                // --------只针对焦点图进入到终端页返回列表时使用
                lastItem = visibleItemCount + firstVisibleItem - 2;
                first = firstVisibleItem == 0 ? 0 : firstVisibleItem - 1;
                last = lastItem;

                // ---------
                setOnScroll(firstVisibleItem, visibleItemCount, 2);
            }
        });
        addHeaderView();
        addFooterView(); // 加载listview底部
    }

    /**
     * 配置整个view
     */
    @Override
    public void setupViews() {
        super.setupViews();
        titleLayout = (LinearLayout) findViewById(R.id.information_title_view_layout);

        allTextView = (TextView) findViewById(R.id.information_navi_all);
        gameTextView = (TextView) findViewById(R.id.information_navi_game);
        softwaveTextView = (TextView) findViewById(R.id.information_navi_softwave);
        musTextView = (TextView) findViewById(R.id.information_navi_must);
        if (titleFlag != 0) {
            headerLayout.setVisibility(View.GONE);
            focusGallery.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);
        } else {
            headerLayout.setVisibility(View.VISIBLE);
            focusGallery.setVisibility(View.VISIBLE);
        }
        titleLayout.setBackgroundResource(navigationIconId[titleFlag]);

        setOnclick(new TextView[] { allTextView, gameTextView,
                softwaveTextView, musTextView });
    }

    private void setOnclick(TextView[] textViews) {
        for (int i = 0; i < textViews.length; i++) {
            final int tempFlag = i;
            final TextView textView = textViews[i];
            textView.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    UMengAnalyseUtils.lableClick = tempFlag;
                    titleFlag = tempFlag;
                    titleLayout
                            .setBackgroundResource(navigationIconId[tempFlag]);
                    UMengAnalyseUtils.onEvents(RecommendAppActivity.this,
                            UMengAnalyseUtils.VIEW_RECOMMEND_LABLEs,
                            navigationString[tempFlag]);
                    downloadUrl = urlString[tempFlag];
                    footerViewIsVisible = true;
                    pageNo = 1;
                    serverAppList.clear();
                    aListAdapter.setAppcount(0);// 设置count为0,则没有数据显示
                    if (footerViewIsVisible) {
                        footLayout.setVisibility(View.GONE);
                        footerViewIsVisible = false;
                    }
                    // 如果选中则高亮
                    if (titleFlag != 0) {
                        headerLayout.setVisibility(View.GONE);
                        focusGallery.setVisibility(View.GONE);
                        progressBar.setVisibility(View.GONE);
                        headerView.setVisibility(View.GONE);
                    } else {
                        headerView.setVisibility(View.VISIBLE);
                        headerLayout.setVisibility(View.VISIBLE);
                        focusGallery.setVisibility(View.VISIBLE);
                        focusGallery.setVisibility(View.VISIBLE);
                        focusGallery.setAdapter(imageAdapter);
                    }

                    listView.setAdapter(aListAdapter);
                    listProgressBar.setVisibility(View.VISIBLE);
                    // System.out.println("listscroll: change column");
                    sendJsonData(urlString[tempFlag], pageNo, getJsonHandler,
                            RecommendAppActivity.this, false);
                    listView.setSelection(0);
                }
            });
        }
    }

    @Override
    protected void parseJson2Data(String jsonString) {
        try {
            Map<String, Object> inforMap = new HashMap<String, Object>();
            inforMap = ParseJsonUtils.getFocusApp(inforMap, jsonString,
                    SettingsUtils.JSON_FOCUS);
            inforMap = ParseJsonUtils.getNormalApp(inforMap, jsonString,
                    SettingsUtils.JSON_APPS, SettingsUtils.JSON_TOTAL);

            if (inforMap != null) {
                if (inforMap.containsKey(SettingsUtils.JSON_APPS)) {
                    List<App> artList = (List<App>) inforMap
                            .get(SettingsUtils.JSON_APPS);
                    for (App itemInfo : artList) {
                        // 判断重复添加
                        serverAppList.add(itemInfo);
                    }
                    // LogOutput.e(TAG, "serverAppList"+serverAppList.size());
                    // LogOutput.e(TAG, "aListAdapter"+aListAdapter.getCount());
                }
                if (inforMap.containsKey(SettingsUtils.JSON_FOCUS)
                        && inforMap.get(SettingsUtils.JSON_FOCUS) != null) {
                    focusList = (ArrayList<App>) inforMap
                            .get(SettingsUtils.JSON_FOCUS);
                    // oldFocusList = focusList;
                }
            } else {
                LogOutputUtils.i(TAG, "informap is null");
            }

        } catch (JSONException e) {
            e.printStackTrace();
            LogOutputUtils.i(TAG, "parse Json error");
            // TODO 当翻到空的页数时有异常,待接口整理好有最大页数则可以避免
            // http://mrobot.conline.com.cn/v2/cms/channels/999?pageNo=9为空的
            footLayout.setVisibility(View.GONE);
            footerViewIsVisible = false;
        }
    }

    @Override
    protected void updateListView() {
        if (serverAppList != null && serverAppList.size() > 0) {
            isServerAppFinish = true;
            // LogOutput.e(TAG,
            // "isLocalAppFinish"+isLocalAppFinish);
            // LogOutput.e(TAG,
            // "isInstallAppFinish"+isInstallAppFinish);
            // LogOutput.e(TAG,
            // "isServerAppFinish"+isServerAppFinish);
            updateListViewContent("服务器读取更新");
        }
        if (null != focusList && focusList.size() > 0) {
            // 选中处理

            focusGallery.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                        int position, long id) {
                    position = position % focusList.size();
                    UMengAnalyseUtils.onEvents(RecommendAppActivity.this,
                            UMengAnalyseUtils.VIEW_FOCUSs,
                            focusList.get(position).getTitle());
                    UMengAnalyseUtils.activity2Content = 6;
                    // TODO 跳转webview
                    position = position % focusList.size();
                    ;
                    Intent intent = new Intent();
                    intent.setClass(RecommendAppActivity.this,
                            FocusContentViewActivity.class);
                    intent.putParcelableArrayListExtra(
                            SettingsUtils.SERVERAPPLIST, focusList);
                    intent.putExtra(SettingsUtils.POSITION, position);
                    intent.putExtra(SettingsUtils.ACTIVITY_FLAG,
                            SettingsUtils.FOUCS_ACTIVITY_REQ_CODE);
                    RecommendAppActivity.this.startActivityForResult(intent,
                            SettingsUtils.FOUCS_ACTIVITY_REQ_CODE);
                }
            });
            int width = DisplayUtils.convertDIP2PX(this,
                    SettingsUtils.FOCUS_GALLERY_WIDTH);
            int heigh = DisplayUtils.convertDIP2PX(this,
                    SettingsUtils.FOCUS_GALLERY_HEIGHT);
            int padding = DisplayUtils.convertDIP2PX(this,
                    SettingsUtils.FOCUS_GALLERY_PADDING);
            imageAdapter = new FocusAppListAdapter(this, width, heigh, padding,
                    R.drawable.app_thumb_default_640_330,
                    SettingsUtils.FOCUS_SCALTYPE);
            focusGallery.setAdapter(imageAdapter);
            focusGallery.setSelection(FOCUS_POSITION);
            isFocusFinish = true;
            updateFocues();
        }
    }

    @Override
    protected void updateFocues() {
        super.updateFocues();
        if (isServerAppFinish && isLocalAppFinish && isInstallAppFinish) {
            imageAdapter.setAppcount(FocusAppListAdapter.MAX);
            imageAdapter.updateList(focusList, isServerAppFinish, localAppList,
                    isLocalAppFinish, installAppList, isInstallAppFinish,
                    progressBar, downloadUrl, pageNo, "焦点图");
        }

    }

    /**
     * 设置选中title背景
     * 
     * @param v
     */
    public void setTitleView(List<RecommendTitle> titleList, RecommendTitle v) {
        for (int i = 0; i < titleList.size(); i++) {
            if (titleList.get(i).getText().equals(v.getText())) {
                titleList.get(i).setBackgroundResource(
                        R.drawable.information_top_navi_current);
                titleList.get(i).setTextColor(
                        getResources().getColor(R.color.navi_text));
                titleList.get(i).setSelected(true);
            } else {
                titleList.get(i).setBackgroundResource(0);
                titleList.get(i).setTextColor(Color.WHITE);
                titleList.get(i).setSelected(false);
            }
        }
    }

    public List<RecommendTitle> TITLES;

    public List<RecommendTitle> setTitles(Context mContext) {
        List<RecommendTitle> titles = new ArrayList<RecommendTitle>();
        RecommendTitle title = new RecommendTitle(mContext);
        title.setTitle("全部");
        title.setTitleUrl(SettingsUtils.URL_RECOMMEND_ALL);
        titles.add(title);
        title = new RecommendTitle(mContext);
        title.setTitle("游戏");
        title.setTitleUrl(SettingsUtils.URL_RECOMMEND_GAME);
        titles.add(title);
        title = new RecommendTitle(mContext);
        title.setTitle("软件");
        title.setTitleUrl(SettingsUtils.URL_RECOMMEND_SOFTWAVE);
        titles.add(title);
        title = new RecommendTitle(mContext);
        title.setTitle("装机必备");
        title.setTitleUrl(SettingsUtils.URL_RECOMMEND_MUST);
        titles.add(title);
        return titles;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        // LogOutput.e(TAG,
        // "onActivityResult"+data.getExtras().getInt(Settings.POSITION));
        if (resultCode == SettingsUtils.ACTIVITY_RESULT) {
            if (requestCode == SettingsUtils.FOUCS_ACTIVITY_REQ_CODE) {
                insideActivity = true;
                // ------焦点图点击返回
                int local = data.getExtras().getInt(SettingsUtils.POSITION);
                focusList = data
                        .getParcelableArrayListExtra(SettingsUtils.SERVERAPPLIST);
                for (int i = first; i < last; i++) {
                    if (focusList.get(local).getTitle()
                            .equals(serverAppList.get(i).getTitle())) {
                        LogOutputUtils.e(TAG,
                                "列表中有焦点图的app:"
                                        + focusList.get(local).getTitle());
                        // 列表可视范围有此apk更新列表
                        clickChangeUpdateListView(
                                data.getExtras().getInt(
                                        SettingsUtils.CONTENT_CLICK_ISCHANGE),
                                focusList.get(local), i);
                        break;
                    }
                }

                int isContentClickInstall = 0;
                isContentClickInstall = data.getExtras().getInt(
                        SettingsUtils.CONTENT_CLICK_ISCHANGE);
                LogOutputUtils.e(TAG, "状态是否变化:" + isContentClickInstall);
                if (isContentClickInstall != BaseListViewActivity.noChange) {// 从终端页返回按钮有变化
                    onClickChange(isContentClickInstall, focusList.get(local));
                    updateFocues();
                    focusGallery.setSelection(local + focusList.size() * 100);
                }

                // ----------------以下是带上一个,下一个按钮的更新
                // updateFocues();
                // focusGallery.setSelection(data.getExtras().getInt(
                // Settings.POSITION)
                // + focusList.size() * 100);
                // updateFoot("精品返回");
                // ----------------
            } else if (requestCode == SettingsUtils.LISTVIEW_ACTIVITY_REQ_CODE) {
                insideActivity = true;
                super.onActivityResult(requestCode, resultCode, data);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        UMengAnalyseUtils.onResume(this);
    }

    @Override
    protected void setFoot() {
        super.setFoot();
        // 如果选中则高亮
        if (titleFlag != 0) {
            headerLayout.setVisibility(View.GONE);
            focusGallery.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);
            headerView.setVisibility(View.GONE);
        } else {
            headerView.setVisibility(View.VISIBLE);
            headerLayout.setVisibility(View.VISIBLE);
            focusGallery.setVisibility(View.VISIBLE);
            focusGallery.setVisibility(View.VISIBLE);
            focusGallery.setAdapter(imageAdapter);
        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        UMengAnalyseUtils.onPause(this);
    }

}
