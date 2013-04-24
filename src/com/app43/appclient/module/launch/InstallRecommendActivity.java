package com.app43.appclient.module.launch;

/**
 * 装机推荐需要接口确定才可以写,因为接口数据太复杂,现在写到时候修改幅度太大
 */
import cn.com.pcgroup.common.android.utils.DisplayUtils;

import com.alvin.api.components.DialogAndToast;
import com.alvin.api.utils.LogOutputUtils;
import com.alvin.api.utils.SettingsUtils;
import com.alvin.api.utils.UMengAnalyseUtils;
import com.app43.appclient.R;
import com.app43.appclient.bean.App;
import com.app43.appclient.module.abstracts.activity.SendDataBaseActivity;
import com.app43.appclient.module.install_introduce.SuperTreeViewAdapter;
import com.app43.appclient.module.install_introduce.TreeViewAdapter;
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
import android.widget.ProgressBar;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class InstallRecommendActivity extends SendDataBaseActivity {

    private final static String TAG = InstallRecommendActivity.class
            .getSimpleName();
    public static int GroupHeight; // 装机推荐 第二级菜单高度
    public static int ChildHeight; // 装机推荐 第三级菜单的高度
    public static int TitleHeight; // 装机推荐 顶级菜单高度
    public static int PaddingLeft; // 间距
    ExpandableListView expandableList;
    SuperTreeViewAdapter superAdapter;
    ProgressBar progressBar;
    private String url = SettingsUtils.URL_USERINFO;
    public static List<Map<String, Object>> nessaryAppsList = new ArrayList<Map<String, Object>>();
    public static List<Map<String, Object>> interestAppsList = new ArrayList<Map<String, Object>>();

    public String[] types = { "装机必备", "兴趣推荐" };
    // public String[][][] type_apps = { { {"a"}, { "AA", "AAA" ,"",""} },
    // { {"b"}, { "BBB", "BBBB", "BBBBB" } }, { {"c"}, { "CCC", "CCCC","" } },
    // { {"d"}, { "DDD", "DDDD", "DDDDD" } }};
    // public static App nessaryApps[][][];
    // public static App interestApps[][][];
    Map<Object, App> downLoadMap = new HashMap<Object, App>();

    Map<String, Map<String, List<App>>> kindMap = new HashMap<String, Map<String, List<App>>>();// 装机或兴趣的每个分类,一个分类有一个app的list
    Map<String, List<App>> categoriesNecessMap = new HashMap<String, List<App>>();// 装机列表
                                                                                  // 每个分类有app的列表
    Map<String, List<App>> categoriesIntreMap = new HashMap<String, List<App>>();// 兴趣列表
    // Map<String , List<App>>appMap=new HashMap<String, List<App>>();
    int totlaAppLength = 0;

    public App[][][] apps(App[][][] apps, List<App> appList) {
        return apps;
    }

    // TODO 选中与否的处理
    public Handler downLoadSelected = new Handler() {
        public void handleMessage(android.os.Message msg) {
            Bundle bundle = msg.getData();
            if (bundle != null) {
                boolean isSelected = (Boolean) bundle.get("isSelected");
                App app = (App) bundle.get("app");
                if (app != null) {
                    if (isSelected) {
                        if (downLoadMap.get(app.getId()) == null) {
                            downLoadMap.put(app.getId(), app);
                        }
                    } else {
                        if (downLoadMap.get(app.getId()) != null) {
                            downLoadMap.remove(app.getId());
                        }
                    }
                }
            }
        };
    };

    public void settingDisplay() {
        GroupHeight = DisplayUtils.convertDIP2PX(this, 84);
        ChildHeight = DisplayUtils.convertDIP2PX(this, 70);
        TitleHeight = DisplayUtils.convertDIP2PX(this, 50);
        PaddingLeft = DisplayUtils.convertDIP2PX(this, 50);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
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
                    DialogAndToast.showError(InstallRecommendActivity.this);
                    return;
                }
                if (appMap.get(SettingsUtils.JSON_INTERESTED) != null) {
                    interestAppsList = (List<Map<String, Object>>) appMap
                            .get(SettingsUtils.JSON_NECESSARY);

                } else {
                    LogOutputUtils.i(TAG, "interestApps parse error");
                    DialogAndToast.showError(InstallRecommendActivity.this);
                    return;
                }
            } else {
                LogOutputUtils.i(TAG, "appMap is Null");
                DialogAndToast.showError(InstallRecommendActivity.this);
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
        progressBar = (ProgressBar) findViewById(R.id.applist__refresh_loadprogress);
        progressBar.setVisibility(View.VISIBLE);
        setButtonView();
    }

    public void setButtonView() {
        // TODO 一键下载按钮触发没写
        Button downLoadButton = (Button) findViewById(R.id.user_info_downLoad);
        Button gotoButton = (Button) findViewById(R.id.user_info_goto);
        gotoButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoDialog();
            }
        });
    }

    public void setExpandListView() {

        // TODO 重新配置数据为App
        superAdapter = new SuperTreeViewAdapter(this);
        // expandableList = (ExpandableListView) InstallRecommendActivity.this
        // .findViewById(R.id.ExpandableListView01);
        superAdapter.RemoveAll();
        superAdapter.notifyDataSetChanged();
        List<SuperTreeViewAdapter.SuperTreeNode> superTreeNode = superAdapter
                .GetTreeNode();

        Iterator kindIterator = kindMap.entrySet().iterator();
        while (kindIterator.hasNext()) {// 装机必备,兴趣推荐
            Map.Entry kindEntry = (Map.Entry) kindIterator.next();
            SuperTreeViewAdapter.SuperTreeNode superNode = new SuperTreeViewAdapter.SuperTreeNode();
            superNode.parent = kindEntry.getKey();
            Map<String, List<App>> tempMap = (Map<String, List<App>>) (kindEntry
                    .getValue());
            Iterator cateIterator = tempMap.entrySet().iterator();
            while (cateIterator.hasNext()) {// 分类
                Map.Entry cateEntry = (Map.Entry) cateIterator.next();
                TreeViewAdapter.TreeNode node = new TreeViewAdapter.TreeNode();

                List<App> tempList = (List<App>) cateEntry.getValue();
                node.parent = tempList.get(0).getTitle();// 第二级菜单的标题
                for (int i = 1; i < tempList.size(); i++) {
                    node.childs.add(tempList.get(i).getTitle());
                }
                superNode.childs.add(node);
            }
            superTreeNode.add(superNode);
        }

        // for (int i = 0; i < types.length; i++)// 第一层
        // {
        // SuperTreeViewAdapter.SuperTreeNode superNode = new
        // SuperTreeViewAdapter.SuperTreeNode();
        // superNode.parent = types[i];
        // // 第二层
        // Iterator iterator = categoriesNecessMap.entrySet().iterator();
        //
        // while (iterator.hasNext()) {
        // Map.Entry entry = (Map.Entry) iterator.next();
        // List<App> list = (List<App>) entry.getValue();
        // TreeViewAdapter.TreeNode node = new TreeViewAdapter.TreeNode();
        // node.parent = list.get(0).getTitle();// 第二级菜单的标题
        // }
        // for (int ii = 0; ii < categoriesIntreMap.size(); ii++) {
        // TreeViewAdapter.TreeNode node = new TreeViewAdapter.TreeNode();
        // node.parent = type_apps[ii][0][0];// 第二级菜单的标题
        //
        // for (int iii = 0; iii < type_apps[ii][1].length; iii++)// 第三级菜单
        // {
        // node.childs.add(type_apps[ii][1][iii]);
        // }
        // superNode.childs.add(node);
        // }
        // superTreeNode.add(superNode);
        // }
        expandableList.setScrollbarFadingEnabled(true);
        superAdapter.UpdateTreeNode(superTreeNode);
        expandableList.setAdapter(superAdapter);
        for (int i = 0; i < types.length; i++) {
            expandableList.expandGroup(i);
        }
    }

    // 跳转推荐对话框
    public void gotoDialog() {
        AlertDialog.Builder builder = new Builder(InstallRecommendActivity.this);
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
                                InstallRecommendActivity.this,
                                MainTabFrameActivity.class);
                        startActivity(intent);
                        InstallRecommendActivity.this.finish();
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
                total++;
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
}
