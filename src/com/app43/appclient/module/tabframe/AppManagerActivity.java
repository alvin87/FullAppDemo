package com.app43.appclient.module.tabframe;

/*
 * 删除之后bug列表显示还是原来的长度
 * 
 */

import com.alvin.api.utils.LogOutputUtils;
import com.alvin.api.utils.UMengAnalyseUtils;
import com.app43.appclient.R;
import com.app43.appclient.bean.App;
import com.app43.appclient.bean.InstallApp;
import com.app43.appclient.bean.RecommendTitle;
import com.app43.appclient.module.abstracts.activity.SendDataWithExitBaseActivity;
import com.app43.appclient.module.adapter.DownloadAppAdapter;
import com.app43.appclient.module.adapter.InstallAppAdapter;
import com.app43.appclient.module.appmanager.ReadInstallAppThread;
import com.app43.appclient.module.appmanager.ReadInstallAppThread.OnClickInstallListener;
import com.app43.appclient.module.receive.UninstallAppReceive;
import com.app43.appclient.module.receive.UninstallAppReceive.OnClickUninstallListener;
import com.app43.appclient.module.utils.InstallAppUtils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class AppManagerActivity extends SendDataWithExitBaseActivity {

    private final String TAG = AppManagerActivity.class.getSimpleName();
    private List<InstallApp> installAppList = new ArrayList<InstallApp>(); // 扫描本地安装程序带图片
    // private List<App> installListNoDrb = new ArrayList<App>();// 扫描本地安装程序不带图片
    private ArrayList<App> downloadingApps = new ArrayList<App>();
    PackageManager pm;
    private ListView mListView;
    private InstallAppAdapter installAppAdapter;
    ProgressBar progressDialog;
    TextView downloadTextView, installTextView;
    // TextView readTextView;// 正在读取
    boolean readInstallAppFinish = true; // 读取安装app完成
    boolean readDownloadAppFinish = false;// 读取下载app完成
    boolean isInstall = false;// 选择了安装管理
    // private int delPosition; // 记录删除app的位置
    protected LinearLayout titleLayout;

    DownloadAppAdapter downloadAppAdapter;
    Bundle instanceState;
    final List<RecommendTitle> recommendTitles = new ArrayList<RecommendTitle>();
    boolean insideActivity = false;// 是否从内容页面返回
    FrameLayout frameLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        this.instanceState = savedInstanceState;
        LogOutputUtils.i("appmanager", "来多少次");
        super.onCreate(instanceState);
    }

    @Override
    public void handleViews(String jsonString) {
    }

    @Override
    protected Handler initHandle() {
        return null;
    }

    protected void loadingVisible() {
        progressDialog.setVisibility(View.VISIBLE);
        // readTextView.setVisibility(View.VISIBLE);
    }

    protected void loadingGone() {
        progressDialog.setVisibility(View.GONE);
        // readTextView.setVisibility(View.GONE);
    }

    @Override
    public void setupViews() {
        LogOutputUtils.i(TAG, "setupviews");
        setContentView(R.layout.manager_app_activity);
        frameLayout = (FrameLayout) findViewById(R.id.appManager_frame_layout);
        progressDialog = (ProgressBar) findViewById(R.id.applist__refresh_loadprogress);
        // readTextView = (TextView) findViewById(R.id.sacn);
        installAppAdapter = new InstallAppAdapter(this);
        downloadAppAdapter = new DownloadAppAdapter(this);
        mListView = (ListView) findViewById(R.id.commend_app_listview);
        setListView();
        mListView.setScrollbarFadingEnabled(true);
        mListView.setAdapter(installAppAdapter);
        TextView topTitle = (TextView) findViewById(R.id.information_top_title);
        topTitle.setText(this.getResources().getString(
                R.string.tab_manager_name));
        titleLayout = (LinearLayout) findViewById(R.id.information_title_view_layout);
        pm = AppManagerActivity.this.getPackageManager();
        downloadTextView = (TextView) findViewById(R.id.information_navi_download);
        installTextView = (TextView) findViewById(R.id.information_navi_install);
        downloadTextView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                isInstall = false;
                titleLayout.setBackgroundResource(R.drawable.manager_download);
                downloadManager();
            }
        });

        installTextView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                LogOutputUtils.i(TAG, "readInstall");
                isInstall = true;
                titleLayout.setBackgroundResource(R.drawable.manager_install);
                startReadApp();
            }
        });
        // updateNaviView();
    }

    /**
     * 作用:设置列表滑动监听滑动不加载图片,参考BaseListViewActivity中的滑动
     */
    private void setListView() {
        // TODO 第二版再上滑动不加载 图片
        mListView.setScrollbarFadingEnabled(true);
    }

    /**
     * 卸载监听
     */
    private OnClickUninstallListener uninstallListener = new OnClickUninstallListener() {

        @Override
        public void onClickUninstall(Intent intent, int index, Context context,
                UninstallAppReceive uninstallAppReceive) {
            String action = intent.getAction();
            final int lposition = index;
            if (action.equals(Intent.ACTION_PACKAGE_REMOVED)) {
                String packName = intent.getDataString();
                if (packName.equals("package:"
                        + installAppList.get(lposition).getPackageName())) {
                    // TODO 卸载之后还要更新各个列表图标
                    Handler handler = new Handler(getMainLooper()) {
                        @Override
                        public void handleMessage(Message msg) {
                            super.handleMessage(msg);
                            dbApp_download.delUpdateState(
                                    installAppList.get(lposition)
                                            .getPackageName(), 0);
                            installAppList.remove(lposition);
                            installAppAdapter
                                    .setAppcount(installAppList.size());
                            installAppAdapter.notifyDataSetChanged();
                        }
                    };
                    handler.sendMessage(new Message());
                    context.unregisterReceiver(uninstallAppReceive);
                }
            }
        }

    };

    /**
     * 加载Title
     */
    protected int scrollWidth;
    protected int navigationWidth;
    protected int scrollRightDistance;

    public void updateNaviView() {
        LogOutputUtils.i(TAG, "updateNaviView");

        TITLES = setTitles(this);
        DisplayMetrics dm;
        dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        scrollWidth = (int) (dm.widthPixels - (this.getResources()
                .getDimension(R.dimen.information_navi_arr_width)
                + this.getResources().getDimension(
                        R.dimen.infromation_navi_arr_margin_left) + this
                .getResources().getDimension(
                        R.dimen.information_navi_arr_margin_right)) * 2);
        navigationWidth = scrollWidth / TITLES.size();
        scrollRightDistance = navigationWidth * TITLES.size();
        // 初始化Title
        for (int i = 0; i < TITLES.size(); i++) {
            final int index = i;
            final RecommendTitle textView = new RecommendTitle(this);
            textView.setText(TITLES.get(index).getTitle());
            ViewGroup.MarginLayoutParams p = new ViewGroup.MarginLayoutParams(
                    navigationWidth, LinearLayout.LayoutParams.FILL_PARENT);
            textView.setTextSize(this.getResources().getDimension(
                    R.dimen.title_text_size));
            textView.setTextColor(Color.WHITE);
            textView.setGravity(Gravity.CENTER);
            textView.setTitleUrl(TITLES.get(index).getTitleUrl());
            titleLayout.addView(textView, p);
            recommendTitles.add(textView);
        }

        for (int i = 0; i < recommendTitles.size(); i++) {
            final int index = i;
            RecommendTitle textView = recommendTitles.get(i);
            textView.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    RecommendTitle tv = (RecommendTitle) v;
                    setTitleView(tv, recommendTitles);
                    if (index == 0) {
                        isInstall = false;
                        LogOutputUtils.e(TAG, "ca");
                        UMengAnalyseUtils.onEvents(AppManagerActivity.this,
                                UMengAnalyseUtils.VIEW_APP_MANAGERs,
                                UMengAnalyseUtils.manager_download);
                        downloadManager();

                    } else if (index == 1) {
                        LogOutputUtils.i(TAG, "readInstall");

                        isInstall = true;
                        startReadApp();
                    }
                }
            });
        }
        // setWhichButton();

    }

    protected void setWhichButton() {
        if (isInstall) {
            // setTitleView(recommendTitles.get(1), recommendTitles);
            titleLayout.setBackgroundResource(R.drawable.manager_install);
            startReadApp();
        } else {
            // setTitleView(recommendTitles.get(0), recommendTitles);
            titleLayout.setBackgroundResource(R.drawable.manager_download);
            downloadManager();
        }

    }

    /**
     * 设置选中title背景
     * 
     * @param v
     */
    public void setTitleView(RecommendTitle v, List<RecommendTitle> titles) {
        for (int i = 0; i < titles.size(); i++) {
            if (titles.get(i).getText().equals(v.getText())) {
                titles.get(i).setBackgroundResource(
                        R.drawable.information_top_navi_current);
                titles.get(i).setTextColor(
                        getResources().getColor(R.color.navi_text));
                titles.get(i).setSelected(true);
            } else {
                titles.get(i).setBackgroundResource(0);
                titles.get(i).setTextColor(Color.WHITE);
                titles.get(i).setSelected(false);
            }
        }
    }

    public List<RecommendTitle> TITLES;

    public List<RecommendTitle> setTitles(Context mContext) {
        List<RecommendTitle> titles = new ArrayList<RecommendTitle>();
        RecommendTitle title = new RecommendTitle(mContext);
        title.setTitle("下载管理");
        title.setTitleUrl("http://mrobot.pconline.com.cn/v2/cms/channels/999");
        titles.add(title);
        title = new RecommendTitle(mContext);
        title.setTitle("安装管理");
        title.setTitleUrl("http://mrobot.pconline.com.cn/v2/cms/channels/3");
        titles.add(title);
        return titles;
    }

    /**
     * 读取本地安装程序
     */
    public void startReadApp() {
        frameLayout.setBackgroundDrawable(null);
        LogOutputUtils.i("appmanager", "uptdate");
        UMengAnalyseUtils.onEvents(AppManagerActivity.this,
                UMengAnalyseUtils.VIEW_APP_MANAGERs,
                UMengAnalyseUtils.manager_install);

        installAppList.clear();
        installAppAdapter.setAppcount(0);
        mListView.setAdapter(installAppAdapter);
        mListView.setSelection(0);
        if (readInstallAppFinish) {// 如果之前去读安装程序的线程已经完成则可以再去读
            LogOutputUtils.i("appmanager", "再读");
            ReadInstallAppThread.getInstance(onClickInstallListener, this,
                    installHandler);
        }

        readInstallAppFinish = false;
        loadingVisible();
    }

    OnClickInstallListener onClickInstallListener = new OnClickInstallListener() {

        @Override
        public void onClickInstall(List<PackageInfo> list, Handler iHandler) {
            // LogOutput.e("onClickInstallListener", "list.size():" +
            // list.size());
            List<InstallApp> apps = new ArrayList<InstallApp>();
            for (int i = 0; i < list.size(); i++) {
                PackageInfo info = list.get(i);
                InstallApp appInfo = new InstallApp();
                appInfo.setAppName(info.applicationInfo.loadLabel(pm)
                        .toString());
                appInfo.setDrawable(info.applicationInfo.loadIcon(pm));
                appInfo.setPackageName(info.applicationInfo.packageName
                        .toString());
                appInfo.setVerName(info.versionName);
                File file = new File(info.applicationInfo.publicSourceDir);
                long lenth = file.length();
                appInfo.setSize(lenth);
                appInfo.setVerCode(info.versionCode);
                apps.add(appInfo);
                // LogOutput.e("onClickInstallListener", "appList:" + i);
            }
            Message message = iHandler.obtainMessage();
            message.obj = apps;
            iHandler.sendMessage(message);
        }
    };

    Handler installHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            readInstallAppFinish = true;
            // LogOutput.e("handler", "install"+isInstall);
            if (isInstall && msg.obj != null) {// 如果当前选项不是下载管理则显示
                installAppList = (List<InstallApp>) msg.obj;
                // LogOutput.e("handler", "appList.size():" + appList.size());
                loadingGone();
                installAppAdapter.setAppcount(installAppList.size());
                installAppAdapter.updateList(AppManagerActivity.this,
                        readInstallAppFinish, installAppList,
                        uninstallListener, false);
            }
        }
    };

    /**
     * 
     * 作用: 获取正在下载或已经的程序
     */

    private void downloadManager() {
        UMengAnalyseUtils.onEvents(AppManagerActivity.this,
                UMengAnalyseUtils.VIEW_APP_MANAGERs,
                UMengAnalyseUtils.manager_download);
        mListView.setAdapter(downloadAppAdapter);
        progressDialog.setVisibility(View.VISIBLE);
        readDownloadAppFinish = false;
        // downloadingApps.clear();
        // Map<String, App> db = dbApp_download.getAllFinishDownloadApk();
        // Map<String, App> tempMap = delRepert(ClickAdapter.nameMap, db);//
        // 去除重复的app
        // downloadingApps = mapToList(db, downloadingApps);
        // downloadAppAdapter.setAppcount(downloadingApps.size());
        progressDialog.setVisibility(View.VISIBLE);
        downloadAppAdapter.updateList(progressDialog, setupBackgroung);
    }

    protected void readInstallApp() {
        new InstallAppUtils.readInstallApp(this, installAppHandler).start();
    }

    // 本地安装程序handle
    protected Handler installAppHandler = new Handler() {
        public void handleMessage(Message msg) {
            downloadingApps.clear();
            // Map<String, App> db = dbApp_download.getAllDownloadApk();
            // Map<String, App> tempMap = delRepert(ClickAdapter.nameMap, db);//
            // 去除重复的app

            // downloadingApps = mapToList(tempMap, downloadingApps);
            // downloadingApps = mapToList(db, downloadingApps);
            // downloadAppAdapter.setAppcount(downloadingApps.size());
            downloadAppAdapter.updateList(progressDialog, setupBackgroung);
            loadingGone();
        };
    };

    /**
     * 
     * 作用:去除重复app
     * 
     * @param
     * 
     * @return String DOM对象
     */
    protected Map<String, App> delRepert(Map<String, App> clickMap,
            Map<String, App> dbMap) {
        Map<String, App> tempAppMap = new HashMap<String, App>();
        Iterator iterator = clickMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, App> entry = (Map.Entry<String, App>) iterator
                    .next();
            if (!dbMap.containsKey(entry.getKey())) {
                tempAppMap.put(entry.getKey(), entry.getValue());
            }
        }
        return tempAppMap;
    }

    @Override
    protected void onResume() {

        super.onResume();
        UMengAnalyseUtils.onResume(this);
        if (!insideActivity) {
            setWhichButton();
            // onCreate(instanceState);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        UMengAnalyseUtils.onPause(this);
        insideActivity = false;
    }

    // public static ArrayList<App> mapToList(Map<String, App> map,
    // ArrayList<App> apps) {
    //
    // Iterator iterator = map.entrySet().iterator();
    // while (iterator.hasNext()) {
    // Map.Entry<String, App> entry = (Map.Entry<String, App>) iterator
    // .next();
    // apps.add(entry.getValue());
    // }
    // return apps;
    // }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        insideActivity = true;
    }

    SetupBackgroung setupBackgroung = new SetupBackgroung() {

        @Override
        public void setbg(int size) {
            LogOutputUtils.e(TAG, "sssssize" + size);
            if (size > 0) {
                frameLayout.setBackgroundDrawable(null);
            } else {

                frameLayout.setBackgroundResource(R.drawable.no_content);
            }
        }
    };

    public interface SetupBackgroung {
        public void setbg(int size);
    }
}
