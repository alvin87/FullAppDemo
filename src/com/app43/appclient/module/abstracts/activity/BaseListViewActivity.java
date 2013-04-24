package com.app43.appclient.module.abstracts.activity;

/**
 * 这个是带lietView下拉自动刷新的公共类
 * 
 * 每个继承的Activity类都要调用setInitData()
 */
import com.alvin.api.utils.LocalAppUtils;
import com.alvin.api.utils.LogOutputUtils;
import com.alvin.api.utils.SettingsUtils;
import com.app43.appclient.R;
import com.app43.appclient.bean.App;
import com.app43.appclient.module.adapter.AppListAdapter;
import com.app43.appclient.module.adapter.ProgressListViewAdapter;
import com.app43.appclient.module.receive.ProgressChangeReceive;
import com.app43.appclient.module.utils.InstallAppUtils;
import com.app43.appclient.module.utils.ParseJsonUtils;

import org.json.JSONException;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BaseListViewActivity extends SendDataBaseActivity {
    protected String TAG = BaseListViewActivity.class.getSimpleName();
    protected ListView listView;
    protected ProgressBar listProgressBar;
    protected int lastItem; // 列表末item的id
    protected View footerView; // 列表底部view
    protected View footLayout;
    protected LayoutInflater mInflater;
    protected boolean footerViewIsVisible = true; // 列表底部是否可见
    // protected boolean isFirstLoadListData = true; // 是否第一次加载
    protected int pageNo = 1; // 向服务器读取页码
    // protected String appUrl = ""; // 读取程序列表URL
    protected String downloadUrl = "";
    protected boolean isRefresh; // 是否刷新
    protected int pageSize = 20;// 每页显示数目
    protected ArrayList<App> serverAppList = new ArrayList<App>(); // 网络程序列表
    protected static List<App> installAppList = new ArrayList<App>(); // 已安装程序
    protected List<App> localAppList = new ArrayList<App>(); // 本地下载程序
    protected ProgressListViewAdapter aListAdapter;
    protected Boolean isServerAppFinish = false;
    protected Boolean isLocalAppFinish = false;
    protected Boolean isInstallAppFinish = false;
    protected Boolean isFocusFinish = false;
    protected int ViewId;
    protected int TextId;
    // protected int totalCount;// 每个列表的总数
    protected PositionListener positionListener;
    protected boolean insideActivity = true;// 用来判断是不是点击item的activity跳转回来的,如果不是则刷新
    protected boolean isFavNull = false;
    protected ArrayList<App> installRecommendApps = null;// 只针对第一次装机推荐列表点击下载跳转到精品推荐

    public static int noChange = 107;
    public static int isClickInstall = 7;// 点击了安装
    public static int isClickUninstall = 77;// 点击了卸载
    public static int isClickDelApp = 17;// 点击了删除
    public static int isChange = noChange;// 是否在终端页里面点击了安装
    protected boolean isGuess = false;// 猜你喜欢不用分页

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!isFavNull) {
            setupHandler();
            readInstallApp();
            readLocalApp();
            // LogOutput.e(TAG, "在oncreat");
            sendJsonData(downloadUrl, pageNo, getJsonHandler, this, isRefresh);
        }

    }

    /**
     * 
     *作用:     读取已安装程序列表
       
     * @return String    
     */
    protected void readInstallApp() {
        isInstallAppFinish = false;
        aListAdapter.setAppcount(0);
        if (!isGuess) {
            footLayout.setVisibility(View.GONE);
        }
        listView.setAdapter(aListAdapter);
        listProgressBar.setVisibility(View.VISIBLE);
        new InstallAppUtils.readInstallApp(this, installAppHandler).start();
    }

    
    /**
     * 
     *作用:读取本地已下载程序 
       
     * @param     
       
     * @return String    DOM对象
     */
    protected void readLocalApp() {
        isLocalAppFinish = false;
        new LocalAppUtils.readLocalApp(this, localAppHandle).start();
    }

    // protected void progressBarGone(){
    // listProgressBar.setVisibility(View.GONE);
    // }
    /**
     * 添加listview尾
     */
    public void addFooterView() {
        footerView = mInflater
                .inflate(R.layout.recommend_app_list_footer, null);
        footLayout = footerView.findViewById(R.id.footer_layout);
        listView.addFooterView(footerView, null, false);
        footLayout.setVisibility(View.GONE);// 不显示,也不占据空间
    }

    /**
     * 配置整个view
     */
    @Override
    public void setupViews() {
        mInflater = this.getLayoutInflater();
        setContentView(ViewId);
        listProgressBar = (ProgressBar) findViewById(R.id.applist__refresh_loadprogress);
        TextView topTitle = (TextView) findViewById(R.id.information_top_title);
        topTitle.setText(this.getResources().getString(TextId));
        setupListView();
        setupAdapter();
        aListAdapter.setAppcount(0);
        listView.setAdapter(aListAdapter);
        // 抽象后的修改
        initData();
    }

    protected void setupAdapter() {
        if (aListAdapter == null) {
            aListAdapter = new AppListAdapter(this, installRecommendApps);
            aListAdapter.setAppcount(0);
        }
        positionListener = aListAdapter.getPositionListener();
    }

    protected void setupListView() {
        listView = (ListView) findViewById(R.id.commend_app_listview);
        listView.setScrollbarFadingEnabled(true);
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            // 添加滚动条滚到最底部，加载余下的元素
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (!isGuess) {
                    setScrollChanedListen(scrollState);
                }

            }

            public void onScroll(AbsListView view, int firstVisibleItem,
                    int visibleItemCount, int totalItemCount) {
                if (!isGuess) {
                    setOnScroll(firstVisibleItem, visibleItemCount, 1);
                }
            }
        });
        if (!isGuess) {
            addFooterView(); // 加载listview底部
        }
    }

    /**
     * 作用:设置监听滑动判断第一个和最后一个的位置坐标是多少
     * 
     * @param reduce
     *            1则没有listView头,2,则是有listView头.
     */
    protected void setOnScroll(int firstVisibleItem, int visibleItemCount,
            int reduce) {

        lastItem = visibleItemCount + firstVisibleItem - reduce;

        int firstPosition = firstVisibleItem == 0 ? 0 : firstVisibleItem - 1;
        int lastPosition = lastItem;
        if (positionListener != null) {
            positionListener.positionChange(firstPosition, lastPosition);
        }

        // LogOutput.e(TAG, "firstVisibleItem" + firstPosition +
        // "||visibleItemCount"
        // + visibleItemCount);
    }

    /**
     * 作用:判断是否滑动加载,以及是否在滑动,如果是fling滑动则只显示默认图片
     * 
     * @param 滑动的状态
     */
    protected void setScrollChanedListen(int scrollState) {
        LogOutputUtils.e(TAG, "scrollState" + scrollState);

        if (footerViewIsVisible && aListAdapter.getCount() == lastItem) {
            if (null != serverAppList && serverAppList.size() > 0) {
                ProgressListViewAdapter.scrollStop = true;
                if (positionListener != null) {
                    positionListener.notifyView();
                }
                loadListItemAsy();
            }
        } else {
            if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_FLING) {
                ProgressListViewAdapter.scrollStop = false;
            } else if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                if (ProgressListViewAdapter.scrollStop == false) {
                    ProgressListViewAdapter.scrollStop = false;
                } else {
                    ProgressListViewAdapter.scrollStop = true;
                }
            } else {
                if (ProgressListViewAdapter.scrollStop == false) {
                    ProgressListViewAdapter.scrollStop = true;
                    if (positionListener != null) {
                        positionListener.notifyView();
                    }
                }
            }
        }

        // LogOutput.e(TAG,
        // "footerVisible:"+footerViewIsVisible+"||lastItem:"+lastItem+"||aListAdapter:"+aListAdapter.getCount()
        // +"\n"+"=="+(scrollState ==
        // AbsListView.OnScrollListener.SCROLL_STATE_IDLE)+"||serverAppList:"+serverAppList.size());
    }

    // 初始化数据
    public void initData() {
        pageNo = 1;
        if (isGuess) {
            pageNo = -1;
        }
        listView.setVisibility(View.VISIBLE);
        // TODO 测试时isRefresh为true,上线时为false;
        isRefresh = true;
        serverAppList.clear();
        // isFirstLoadListData = true;
        listProgressBar.setVisibility(View.VISIBLE);
    }

    /**
     * 滚动异步加载后续列表
     */
    public synchronized void loadListItemAsy() {
        int downloadPage = ((int) Math.ceil(aListAdapter.getCount()
                / (float) pageSize)) + 1;
        // System.out.println("#### downloadPage=" + downloadPage
        // + " pageNo=" + pageNo);
        if (downloadPage > pageNo) {
            pageNo = downloadPage;
            // LogOutput.e(TAG, "pageno"+pageNo);
            sendJsonData(downloadUrl, pageNo, getJsonHandler, this, isRefresh);
        }
    }

    // 本地安装程序handle
    protected Handler installAppHandler = new Handler() {
        public void handleMessage(Message msg) {
            installAppList = (List<App>) msg.obj;
            isInstallAppFinish = true;
            // LogOutput.e(TAG, "install" + isServerAppFinish + isLocalAppFinish
            // + isInstallAppFinish);
            updateListViewContent("扫描安装程序回来更新");
            updateFocues();
        };
    };
    // 读本地已下载程序handle
    protected Handler localAppHandle = new Handler() {
        public void handleMessage(Message msg) {
            isLocalAppFinish = true;
            // LogOutput.e(TAG, "islocal" + isServerAppFinish + isLocalAppFinish
            // + isInstallAppFinish);
            updateListViewContent("本地文件夹更新");
            updateFocues();
        };
    };

    protected Handler initHandle() {
        return normallHandle(TAG);
    };

    // 获取服务器Json消息
    protected Handler getJsonHandler = initHandle();

    /**
     * 作用: 把服务器Json解释为Data
     */
    protected void parseJson2Data(String jsonString) {
        Map<String, Object> inforMap = new HashMap<String, Object>();
        try {
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
                }
                // if (inforMap.containsKey(SettingsUtils.JSON_TOTAL)) {
                // totalCount = (Integer) inforMap
                // .get(SettingsUtils.JSON_TOTAL);
                // }

            } else {
                LogOutputUtils.i(TAG, "informap is null");
            }
            // LogOutput.e(TAG, "serverAppList"+serverAppList.size());
        } catch (JSONException e) {
            e.printStackTrace();
            LogOutputUtils.i(TAG, "parse Json error");
            // TODO 和RecommendAppActivity的处理不一样,因为此url超出页数时也不为空
            // http://mrobot.conline.com.cn/v2/cms/channels/999?pageNo=9为空的
        }
    }

    /**
     * 作用:更新列表视图
     */
    protected void updateListView() {
        if (serverAppList != null && serverAppList.size() > 0) {
            isServerAppFinish = true;
            updateListViewContent("普通列表更新");
        }
    }

    public void handleViews(String jsonString) {
        parseJson2Data(jsonString);
        updateListView();
    }

    /**
     * 每个继承的Activity都要调用该方法来配置名字视图和url
     * 
     * @param tag
     *            Activity标记
     * @param vid
     *            view的ID
     * @param tid
     *            banner的文字
     * @param aurl
     *            请求数据的url
     */
    protected void setInitData(String tag, int vid, int tid, String aurl) {
        TAG = tag;
        ViewId = vid;
        TextId = tid;
        downloadUrl = aurl;
    }

    /**
     * 只适合通用列表和焦点图终端页的返回指定到某个position 先到OnActivity再到onResume
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // LogOutput.e(TAG, "requestCode" + requestCode);
        // LogOutput.e(TAG, "resultCode" + resultCode);
        if (resultCode == SettingsUtils.ACTIVITY_RESULT) {

            if (requestCode == SettingsUtils.LISTVIEW_ACTIVITY_REQ_CODE) {
                insideActivity = true;

                // 如果点击有变化则更新列表
                int local = data.getExtras().getInt(SettingsUtils.POSITION);
                serverAppList = data
                        .getParcelableArrayListExtra(SettingsUtils.SERVERAPPLIST);

                clickChangeUpdateListView(
                        data.getExtras().getInt(
                                SettingsUtils.CONTENT_CLICK_ISCHANGE),
                        serverAppList.get(local), local);

                // -----------------以下是带上一个下一个点击跳转到url第二页请求的
                if (!data.getExtras().getBoolean(SettingsUtils.ISTURN_NEXT)) {
                    return;
                }
                int position = data.getExtras().getInt(SettingsUtils.POSITION);
                serverAppList = data
                        .getParcelableArrayListExtra(SettingsUtils.SERVERAPPLIST);

                aListAdapter.setAppcount(serverAppList.size());
                downloadUrl = data.getExtras().getString(SettingsUtils.PAGEURL);
                pageNo = data.getExtras().getInt(SettingsUtils.PAGENO);
                updateListViewContent("普通列表详情页返回");
                LogOutputUtils.e(TAG, "普通列表页返回");
                listView.setSelection(position);
                if (position > (serverAppList.size() - 5)) {
                    loadListItemAsy();
                    return;
                }
                // ---------------------------------
                return;
            }
        }
    }

    /**
     * 作用:终端页的按钮是否有变化.ContentViewActivity.isClickInstall
     * 
     * @param change
     *            是否已经变化
     * @param app
     *            当前终端页的app
     * @param index
     *            当前列表要定位到的位置
     * @param isUpdateFocus
     *            true:为更新焦点图, false:更新列表
     */
    protected void clickChangeUpdateListView(int change, App app, int index) {
        int isContentClickInstall = 0;
        isContentClickInstall = change;

        if (isContentClickInstall != BaseListViewActivity.noChange) {// 从终端页返回按钮有变化
            LogOutputUtils.e(TAG, "状态是否变化:" + isContentClickInstall);
            onClickChange(change, app);
            listView.setAdapter(aListAdapter);
            aListAdapter.notifyDataSetChanged();
            listView.setSelection(index);
        }
    }

    /**
     * 作用: 判断是点了那个按钮.
     */
    protected void onClickChange(int change, App app) {
        if (change == BaseListViewActivity.isClickInstall) {// 点击了安装
            if (ProgressListViewAdapter.isInstall(this, app)) {
                boolean isInstallListHave = false;
                for (int i = 0; i < installAppList.size(); i++) {
                    if (installAppList.get(i).getPackageName()
                            .equals(app.getPackageName())) {
                        isInstallListHave = true;
                        break;
                    }
                }
                LogOutputUtils.e(TAG, "isInstall: " + isInstallListHave
                        + "  name:" + app.getTitle());
                if (!isInstallListHave) {
                    installAppList.add(app);
                }
            }
        } else if (change == BaseListViewActivity.isClickUninstall) {// 点击了卸载
            boolean isUninstall = false;
            int local = -1;
            for (int i = 0; i < installAppList.size(); i++) {
                if (installAppList.get(i).getPackageName()
                        .equals(app.getPackageName())) {
                    isUninstall = true;
                    local = i;
                    break;
                }
            }
            LogOutputUtils.e(TAG, "isUnInstall: " + isUninstall + "  name:"
                    + app.getTitle());
            if (isUninstall) {
                installAppList.remove(local);
            }
        } else if (change == BaseListViewActivity.isClickDelApp) {
            boolean isDelApp = false;
            int local = -1;
            for (int i = 0; i < installAppList.size(); i++) {
                if (installAppList.get(i).getPackageName()
                        .equals(app.getPackageName())) {
                    isDelApp = true;
                    local = i;
                    break;
                }
            }
            LogOutputUtils.e(TAG,
                    "isDelApp: " + isDelApp + "  name:" + app.getTitle());
            if (isDelApp) {
                installAppList.remove(local);
            }
        }
    }

    protected void setupHandler() {
    }

    public interface PositionListener {
        public void notifyView();

        public void positionChange(int firstPosition, int lastPosition);
    }

    /**
     * 作用:设置ListView的脚部
     */
    protected void updateListViewContent(String who) {

        if (isServerAppFinish && isLocalAppFinish && isInstallAppFinish) {
            if (!isGuess) {
                if (aListAdapter.getCount() < serverAppList.size()) {
                    footLayout.setVisibility(View.VISIBLE);
                    footerViewIsVisible = true;
                } else {
                    footLayout.setVisibility(View.GONE);
                    footerViewIsVisible = false;
                }
            }
            aListAdapter.setAppcount(serverAppList.size());
            aListAdapter.updateList(serverAppList, isServerAppFinish,
                    localAppList, isLocalAppFinish, installAppList,
                    isInstallAppFinish, listProgressBar, downloadUrl, pageNo,
                    who);
        }
        // else {
        // footLayout.setVisibility(View.INVISIBLE);
        // }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        aListAdapter.unRegisterReceive();
        ProgressChangeReceive.reuseRegisCount();
    }

    protected void updateFocues() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!isGuess) {

            if (!insideActivity) {
                LogOutputUtils.i(TAG, "重新进入");

                // 和导航栏点击是一样的
                isServerAppFinish = false;
                footerViewIsVisible = true;
                pageNo = 1;
                serverAppList.clear();
                aListAdapter.setAppcount(0);// 设置count为0,则没有数据显示
                if (footerViewIsVisible) {
                    if (!isGuess) {
                        footLayout.setVisibility(View.GONE);
                    }
                    footerViewIsVisible = false;
                }

                setFoot();
                listView.setAdapter(aListAdapter);
                listProgressBar.setVisibility(View.VISIBLE);
                // System.out.println("listscroll: change column");
                sendJsonData(downloadUrl, pageNo, getJsonHandler, this, true);
                listView.setSelection(0);

                readInstallApp();

                // onCreate(instanceState);
            }
        }
    }

    protected void setFoot() {

    }

    @Override
    protected void onPause() {
        super.onPause();
        insideActivity = false;
    }

}
