package com.app43.appclient.module.launch;

import cn.com.pcgroup.common.android.utils.DisplayUtils;

import com.alvin.api.components.DialogAndToast;
import com.alvin.api.utils.LogOutputUtils;
import com.alvin.api.utils.SettingsUtils;
import com.alvin.api.utils.UMengAnalyseUtils;
import com.app43.appclient.R;
import com.app43.appclient.bean.App;
import com.app43.appclient.module.abstracts.activity.BaseListViewActivity;
import com.app43.appclient.module.abstracts.activity.SendDataNoMenuActivity;
import com.app43.appclient.module.install_introduce.InfoGroup;
import com.app43.appclient.module.install_introduce.SuperTreeViewNewAdapter;
import com.app43.appclient.module.install_introduce.SuperTreeViewNewAdapter.CheckBoxSelectListener;
import com.app43.appclient.module.tabframe.MainTabFrameActivity;
import com.app43.appclient.module.utils.ParseJsonUtils;

import org.json.JSONException;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 暂时不要第三极菜单
 * 
 * 项目名称：mac_app43 类名称：NewInstallRecommendActivity 类描述： 创建人：pc 创建时间：2012-2-8
 * 下午3:05:14 修改人：pc 修改时间：2012-2-8 下午3:05:14 修改备注：
 * 
 * @version
 * 
 */
public class InstallRecommendNewActivity extends SendDataNoMenuActivity {

    private final static String TAG = InstallRecommendNewActivity.class
            .getSimpleName();
    public static int GroupHeight; // 装机推荐 第二级菜单高度
    public static int ChildHeight; // 装机推荐 第三级菜单的高度
    public static int TitleHeight; // 装机推荐 顶级菜单高度
    public static int PaddingLeft; // 间距
    ExpandableListView expandableList;
    SuperTreeViewNewAdapter superTreeViewNewAdapter;
    ProgressBar progressBar;
    private String url = SettingsUtils.URL_USERINFO;
    public static List<Map<String, Object>> nessaryAppsList = new ArrayList<Map<String, Object>>();
    public static List<Map<String, Object>> interestAppsList = new ArrayList<Map<String, Object>>();

    public String[] types = { "装机必备", "兴趣推荐" };
    Map<Object, App> downLoadMap = new HashMap<Object, App>();

    Map<String, Map<String, List<App>>> kindMap = new HashMap<String, Map<String, List<App>>>();// 装机或兴趣的每个分类,一个分类有一个app的list
    Map<String, List<App>> categoriesNecessMap = new HashMap<String, List<App>>();// 装机列表
                                                                                  // 每个分类有app的列表
    Map<String, List<App>> categoriesIntreMap = new HashMap<String, List<App>>();// 兴趣列表
    int totlaAppLength = 0;

    List<InfoGroup> infoGroups = new ArrayList<InfoGroup>();

    Map<String, App> selAppMap = new HashMap<String, App>();

    TextView selectAll;// 全选textView
    CheckBoxSelectListener checkBoxSelect;// 点击checkBox监听
    boolean isSelectAll = true;// 只有全选和反选

    public void settingDisplay() {
        GroupHeight = DisplayUtils.convertDIP2PX(this, 84);
        ChildHeight = DisplayUtils.convertDIP2PX(this, 70);
        TitleHeight = DisplayUtils.convertDIP2PX(this, 50);
        PaddingLeft = DisplayUtils.convertDIP2PX(this, 50);
    }

    // uuid=1&e=2&c=3&os=4&av=5&pv=6
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Bundle bundle = getIntent().getExtras();
        String uuid = bundle.getString(SettingsUtils.uuid);
        String imei = bundle.getString(SettingsUtils.IMEI);
        String phone = bundle.getString(SettingsUtils.PHONENUM);
        String email = bundle.getString(SettingsUtils.EMAIL);
        String id = bundle.getString(SettingsUtils.ID_LABLE);
        String os = bundle.getString(SettingsUtils.OS);
        String av = bundle.getString(SettingsUtils.AV);
        String pv = bundle.getString(SettingsUtils.PV);
        String send = "u=" + uuid + "&e=" + email + "&c=" + id + "&ov=" + os
                + "&av=" + av + "&pv=" + pv;
        LogOutputUtils.e(TAG, "send userinfo: " + send);
        url = url + send;
        super.onCreate(savedInstanceState);
        setupViews();
        sendJsonData(url, -1, getJsonHandler, this, true);
    }

    @Override
    public void handleViews(String jsonString) {
        progressBar.setVisibility(View.GONE);
        Map<String, List<Map<String, Object>>> appMap;
        try {
            // TODO 需要修改为响应的json对象--装机推荐和兴趣推荐
            appMap = ParseJsonUtils.getUserInfoMap(jsonString);
            if (appMap != null) {
                if (appMap.get(SettingsUtils.JSON_NECESSARY) != null) {
                    nessaryAppsList = (List<Map<String, Object>>) appMap
                            .get(SettingsUtils.JSON_NECESSARY);
                } else {
                    LogOutputUtils.i(TAG, "nessaryApps parse error");
                    DialogAndToast.showError(InstallRecommendNewActivity.this);
                    return;
                }
                if (appMap.get(SettingsUtils.JSON_INTERESTED) != null) {
                    interestAppsList = (List<Map<String, Object>>) appMap
                            .get(SettingsUtils.JSON_INTERESTED);

                } else {
                    LogOutputUtils.i(TAG, "interestApps parse error");
                    DialogAndToast.showError(InstallRecommendNewActivity.this);
                    return;
                }
            } else {
                LogOutputUtils.i(TAG, "appMap is Null");
                DialogAndToast.showError(InstallRecommendNewActivity.this);
                return;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            LogOutputUtils.i(TAG, "parse Json error");
            return;
        }
        json2App(nessaryAppsList, interestAppsList);
        setExpandListView();
    }

    @Override
    public void setupViews() {
        settingDisplay();
        setContentView(R.layout.install_recommend_activity);
        selectAll = (TextView) findViewById(R.id.information_top_name);
        progressBar = (ProgressBar) findViewById(R.id.applist__refresh_loadprogress);
        progressBar.setVisibility(View.VISIBLE);
        expandableList = (ExpandableListView) findViewById(R.id.ExpandableListView01);
        expandableList.setGroupIndicator(null);

        selectAll.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (isSelectAll) {
                    for (int i = 0; i < infoGroups.size(); i++) {
                        for (int j = 0; j < infoGroups.get(i).getAppList()
                                .size(); j++) {
                            infoGroups.get(i).getAppList().get(j)
                                    .setSelected(1);
                            putSelectMap(infoGroups.get(i).getAppList().get(j));
                        }
                    }
                    selectAll.setBackgroundResource(R.drawable.not_all_select);
                } else {
                    for (int i = 0; i < infoGroups.size(); i++) {
                        for (int j = 0; j < infoGroups.get(i).getAppList()
                                .size(); j++) {
                            int select = infoGroups.get(i).getAppList().get(j)
                                    .isSelected() == 1 ? 0 : 1;
                            infoGroups.get(i).getAppList().get(j)
                                    .setSelected(select);
                            putSelectMap(infoGroups.get(i).getAppList().get(j));
                        }
                    }
                    selectAll.setBackgroundResource(R.drawable.all_select);
                }

                superTreeViewNewAdapter.updateExpandList(infoGroups);
                isSelectAll = !isSelectAll;

            }
        });

        checkBoxSelect = new CheckBoxSelectListener() {

            @Override
            public void checkBoxSelOnClick(final App app, int fatherPostion,
                    int childPosition) {
                // LogOutput.e(TAG,
                // app.getTitle() + " caoni等于:" + app.isSelected() + "fa:"
                // + fatherPostion + " child:" + childPosition);

                infoGroups.get(fatherPostion).getAppList().get(childPosition)
                        .setSelected(app.isSelected());
                putSelectMap(app);

            }
        };
        setButtonView();
    }

    private void onkeyDwonload() {
        if (selAppMap.isEmpty()) {
            DialogAndToast.showToast(InstallRecommendNewActivity.this,
                    SettingsUtils.SELNULL);
        } else {

            UMengAnalyseUtils.onEvents(InstallRecommendNewActivity.this,
                    UMengAnalyseUtils.VIEW_USERINFO_BUTTON,
                    UMengAnalyseUtils.download);
            ArrayList<App> arrayList = new ArrayList<App>();
            Iterator iterator = selAppMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry entry = (Map.Entry) iterator.next();
                App app = (App) entry.getValue();
                LogOutputUtils.e("下载", "下载名:" + app.getTitle());
                UMengAnalyseUtils.onEvents(InstallRecommendNewActivity.this,
                        UMengAnalyseUtils.DOWNLOAD_USERINFO, app.getTitle());
                // ProgressListViewAdapter.downloadApk(app,
                // InstallRecommendNewActivity.this);
                arrayList.add(app);
            }
            Intent intent = new Intent(InstallRecommendNewActivity.this,
                    MainTabFrameActivity.class);
            intent.putParcelableArrayListExtra(SettingsUtils.INSTALL_RECOMMEND,
                    arrayList);
            startActivity(intent);
            setResult(SettingsUtils.LauncherUserInfo_Activity_RESPONE_CODE);
            InstallRecommendNewActivity.this.finish();
        }
    }

    public void setButtonView() {
        // TODO 一键下载按钮触发没写
        Button downLoadButton = (Button) findViewById(R.id.user_info_downLoad);
        Button gotoButton = (Button) findViewById(R.id.user_info_goto);
        LinearLayout downloadLayout = (LinearLayout) findViewById(R.id.user_info_download_layout);
        LinearLayout gotoLinearLayout = (LinearLayout) findViewById(R.id.user_info_goto_layout);
        downLoadButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                onkeyDwonload();
            }
        });
        downloadLayout.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                onkeyDwonload();
            }
        });
        gotoLinearLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                toMain();
            }
        });
        gotoButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                toMain();
            }
        });
    }

    private void toMain() {
        UMengAnalyseUtils
                .onEvents(InstallRecommendNewActivity.this,
                        UMengAnalyseUtils.VIEW_USERINFO_BUTTON,
                        UMengAnalyseUtils.goto2);
        // gotoDialog();
        setResult(SettingsUtils.LauncherUserInfo_Activity_RESPONE_CODE);
        Intent intent = new Intent(InstallRecommendNewActivity.this,
                MainTabFrameActivity.class);
        startActivity(intent);
        InstallRecommendNewActivity.this.finish();
    }

    public void setExpandListView() {

        // TODO 重新配置数据为App

        Iterator kindIterator = kindMap.entrySet().iterator();
        while (kindIterator.hasNext()) {// 装机必备,兴趣推荐
            List<App> appList = new ArrayList<App>();
            Map.Entry kindEntry = (Map.Entry) kindIterator.next();
            InfoGroup infoGroup = new InfoGroup();
            infoGroup.setName((String) kindEntry.getKey());// 设置装机推荐
            Map<String, List<App>> tempMap = (Map<String, List<App>>) (kindEntry
                    .getValue());
            Iterator cateIterator = tempMap.entrySet().iterator();
            while (cateIterator.hasNext()) {// 分类
                Map.Entry cateEntry = (Map.Entry) cateIterator.next();
                List<App> tempList = (List<App>) cateEntry.getValue();
                // for (int i = 0; i < tempList.size(); i++) {
                // appList.add(tempList.get(i));
                // }
                appList.add(tempList.get(0));
            }

            infoGroup.setAppList(appList);
            infoGroups.add(infoGroup);
        }

        superTreeViewNewAdapter = new SuperTreeViewNewAdapter(this, infoGroups,
                checkBoxSelect);
        expandableList.setAdapter(superTreeViewNewAdapter);
        expandableList.setScrollbarFadingEnabled(true);
        for (int i = 0; i < infoGroups.size(); i++) {
            expandableList.expandGroup(i);
        }
        for (int i = 0; i < infoGroups.size(); i++) {
            for (int j = 0; j < infoGroups.get(i).getAppList().size(); j++) {
                infoGroups.get(i).getAppList().get(j).setSelected(0);
            }
        }
        superTreeViewNewAdapter.updateExpandList(infoGroups);
    }

    // 跳转推荐对话框
    public void gotoDialog() {
        AlertDialog.Builder builder = new Builder(
                InstallRecommendNewActivity.this);
        builder.setMessage(this.getResources().getString(
                R.string.user_info_goto_recommend));
        builder.setTitle(this.getResources().getString(
                R.string.user_info_goto_tips));
        builder.setPositiveButton("确认",
                new android.content.DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        setResult(SettingsUtils.LauncherUserInfo_Activity_RESPONE_CODE);
                        Intent intent = new Intent(
                                InstallRecommendNewActivity.this,
                                MainTabFrameActivity.class);
                        startActivity(intent);
                        InstallRecommendNewActivity.this.finish();
                        dialog.dismiss();
                    }
                });
        builder.setNegativeButton("取消",
                new android.content.DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        builder.create().show();
    }

    protected Handler initHandle() {

        return normallHandle(TAG);
    };

    // 获取服务器Json消息
    Handler getJsonHandler = initHandle();

    /**
     * 作用: 在这里根据装机和兴趣推荐来适配kindmap
     */
    private void json2App(List<Map<String, Object>> nessList,
            List<Map<String, Object>> intresList) {

        setList(nessList, categoriesNecessMap, totlaAppLength, kindMap,
                types[0]);
        setList(intresList, categoriesIntreMap, totlaAppLength, kindMap,
                types[1]);

        LogOutputUtils.e(TAG, "   总的app个数:" + totlaAppLength);
    }

    /**
     * 作用:
     * 
     * @param source
     *            源列表 如 兴趣列表或装机列表
     * @param dest
     *            目标列表
     * @param typeApp
     */
    private void setList(List<Map<String, Object>> source,
            Map<String, List<App>> dest, int total,
            Map<String, Map<String, List<App>>> typeApp, String typeString) {
        for (int i = 0; i < source.size(); i++) {
            Map<String, Object> list = source.get(i);
            LogOutputUtils.e(
                    TAG,
                    "分类:"
                            + (String) list
                                    .get(SettingsUtils.JSON_CATEGORY_TITLE));
            List<App> artList = (List<App>) source.get(i).get(
                    SettingsUtils.JSON_APPS);
            dest.put((String) list.get(SettingsUtils.JSON_CATEGORY_TITLE),
                    artList);
            for (int j = 0; j < artList.size(); j++) {
                LogOutputUtils.e(TAG, "    app名字:" + artList.get(j).getTitle());
                totlaAppLength++;
            }
        }
        typeApp.put(typeString, dest);
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

    private void putSelectMap(App app) {
        // LogOutput.e(TAG,
        // "name:" + app.getTitle() + "isSelect: " + app.isSelected());
        if (app.isSelected() == 0) {// 如果没有选择则把之前包含有的对应app删除
            if (selAppMap.containsKey(app.getTitle())) {
                selAppMap.remove(app.getTitle());
                LogOutputUtils.i(TAG, "删除:" + app.getTitle());
            }
            return;
        }
        selAppMap.put(app.getTitle(), app);
        LogOutputUtils.i(TAG, "添加:" + app.getTitle());
    }
}
