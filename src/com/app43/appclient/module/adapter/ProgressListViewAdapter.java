package com.app43.appclient.module.adapter;

import cn.com.pcgroup.common.android.utils.NetworkUtils;

import com.alvin.api.components.DialogAndToast;
import com.alvin.api.utils.AsyncImageLoader;
import com.alvin.api.utils.AsyncImageLoader.ImageCallback;
import com.alvin.api.utils.LogOutputUtils;
import com.alvin.api.utils.SettingsUtils;
import com.alvin.api.utils.SyncImageLoader;
import com.alvin.api.utils.SyncImageLoader.SyncImageCallback;
import com.app43.appclient.R;
import com.app43.appclient.bean.App;
import com.app43.appclient.module.abstracts.activity.BaseActivity;
import com.app43.appclient.module.abstracts.activity.BaseListViewActivity;
import com.app43.appclient.module.abstracts.activity.BaseListViewActivity.PositionListener;
import com.app43.appclient.module.common.activity.FocusContentViewActivity;
import com.app43.appclient.module.common.activity.FocusContentViewActivity.ContentViewHolder;
import com.app43.appclient.module.db.DBApp_download;
import com.app43.appclient.module.receive.ProgressChangeReceive;
import com.app43.appclient.module.receive.ProgressChangeReceive.DataChange;
import com.app43.appclient.module.receive.UninstallAppReceive;
import com.app43.appclient.module.receive.UninstallAppReceive.OnClickUninstallListener;
import com.app43.appclient.module.tabframe.AppManagerActivity.SetupBackgroung;
import com.app43.download.test.ClickAdapter;
import com.app43.download.test.ShowNotifycation;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 带进度条的列表Adapter, 每个使用这个Adapter定位activity都是实现位置变化的接口positionListener =
 * aListAdapter.getPositionListener();
 * 
 * 项目名称：appclient 类名称：ProgressListViewAdapter 类描述： 创建人：APP43 创建时间：2011-12-15
 * 下午11:33:49 修改人：APP43 修改时间：2011-12-15 下午11:33:49 修改备注：
 * 
 * @version
 * 
 */
public class ProgressListViewAdapter extends BaseOnClickListItemAdapter {
    public static final String TAG = ProgressListViewAdapter.class
            .getSimpleName();
    protected Drawable defaultDrawable;
    protected boolean isServerAppFinish = true; // 是否下载完服务器数据
    protected boolean isLocalAppFinish = false;// 是否扫描完本地文件夹
    protected boolean isInstallAppFinish = false;// 是否扫描完安装的程序列表
    protected ArrayList<App> serverAppList = new ArrayList<App>();
    public static List<App> installAppList = new ArrayList<App>(); // 已安装程序
    protected List<App> localAppList = new ArrayList<App>(); // 本地下载程序
    protected ProgressBar progressBar;
    protected String pageUrl;
    protected int pageNo;
    protected PositionListener positionListener;
    public int firstPosition = 0, lastPosition = 0; // 获得屏幕显示区域item是否有下载的apk,有则更新listView,无则不更新.
    // 通过activity的PositionListener接口来监听位置的变化.在每次new ListAdapter时并且要注册接口
    public static Map<String, App> dbMap;// 读取下载数据库映射
    public static Map<String, ProgressData> progressMap = new HashMap<String, ProgressData>();// 获取进度广播映射
    String btnText = "";// 按钮显示的文字
    String flag = "";// getView下载按钮动作的标记
    ProgressChangeReceive progressChangeReceive;
    InstallReceiver installReceiver;
    boolean isAppManaActivity = false; // 判断是否为应用管理Activity
    boolean readDbFinish = false;// 是否从数据库读完数据
    public static boolean scrollStop = true;// 列表是否停止滑动

    public static Map<String, String> contentNameMap = new HashMap<String, String>();// 从列表跳转到终端页对应按钮的状态
    public static Map<String, String> contentStateMap = new HashMap<String, String>();// 下载前的状态是请更新还是请下载,只是终端页使用

    public ProgressListViewAdapter(final Activity activitys, String tag) {
        super(activitys, tag);
        positionListener = new PositionListener() {
            @Override
            public void positionChange(int firstPosition, int lastPosition) {
                ProgressListViewAdapter.this.firstPosition = firstPosition;
                ProgressListViewAdapter.this.lastPosition = lastPosition;
            }

            @Override
            public void notifyView() {
                notifyDataSetChanged();
            }
        };
        ClickAdapter.getInstances(activity);
        defaultDrawable = activity.getResources().getDrawable(
                R.drawable.app_thumb_default_80_60);
        if (dbMap == null) {
            dbMap = BaseActivity.dbApp_download.getAllDownloadApk();
        }
        DataChange dataChange = new DataChange() {
            @Override
            public void cancle(String apkName) {
                // 如果是取消按钮的广播就直接更新
                cancleDbDelete(apkName, "接收到ClickAdapter的广播点击取消下载线程");
                // setAppManaActivity(apkName);
                removeProgreMap(apkName);
                setPositionUpdate(apkName);
            }

            @Override
            public void OnDataChange(String sApkName, int iProgress,
                    Long lCurrentTime) {
                LogOutputUtils.i(TAG + ":onreceive", sApkName + "size:"
                        + serverAppList.size());
                LogOutputUtils.i(
                        "once",
                        "first" + firstPosition + "|| last" + lastPosition
                                + "||timeMap:"
                                + ClickAdapter.timeMap.get(sApkName)
                                + "/current:" + lCurrentTime);
                // LogOutput.e(TAG,
                // "name"+sApkName+"|| 是否包含名字 "+(!ClickAdapter.timeMap.containsKey(sApkName))+"|| 时间等不等"+(ClickAdapter.timeMap.get(sApkName)-lCurrentTime!=0));

                if ((!ClickAdapter.timeMap.containsKey(sApkName))
                        || (ClickAdapter.timeMap.get(sApkName) - lCurrentTime != 0)) {
                    // 如果下载adapter没有此apk名字或者时间戳不匹配则只负责显示

                    return;
                }
                //
                // LogOutput.e(TAG,
                // iProgress
                // + "progress"
                // + "更新"
                // + BaseActivity.dbApp_download
                // .updateAppProgress(sApkName,
                // lCurrentTime, iProgress));

                if (iProgress >= 100) {
                    String tempName = sApkName;
                    removeProgreMap(tempName);
                    ProgressChangeReceive.removeCount(tempName, activitys);
                    LogOutputUtils.e(TAG, "下载完成" + "isAppmanaActivity:"
                            + isAppManaActivity);
                    if (isAppManaActivity) {
                        serverAppList = BaseActivity.dbApp_download
                                .getDownloadApp();
                    } else {
                        dbMap = BaseActivity.dbApp_download.getAllDownloadApk();
                    }

                } else {
                    ProgressData progressData = new ProgressData();
                    progressData.setProgress(iProgress);
                    progressData.setApkName(sApkName);
                    progressMap.put(progressData.getApkName(), progressData);
                }

                setPositionUpdate(sApkName);
            }
        };

        progressChangeReceive = new ProgressChangeReceive(dataChange);
        IntentFilter filter = new IntentFilter();
        filter.addAction(SettingsUtils.PROGRESS_BROADCAST_RECEIVE);
        activity.registerReceiver(progressChangeReceive, filter);
    }

    /**
     * 
     * 作用:根据滑动的位置来判断是否要更新列表
     */
    private void setPositionUpdate(String sApkName) {
        int totalCount = lastPosition == 0 ? serverAppList.size()
                : lastPosition + 1;// 如果lastPosition==0说明是下载管理Activity
        int min = totalCount < serverAppList.size() ? totalCount
                : serverAppList.size();// 当所要显示的数据小于一个屏幕时取最小的
        LogOutputUtils.i(TAG, "notifyView()");
        if (isAppManaActivity) {// 如果是安装管理就立即显示
            notifyDataSetChanged();
            return;
        }
        notifyView(firstPosition, min, serverAppList, sApkName);

    }

    public PositionListener getPositionListener() {
        return positionListener;
    }

    public static void removeProgreMap(String tempName) {
        if (progressMap.containsKey(tempName)) {
            progressMap.remove(tempName);
        }
    }

    /**
     * 作用:通用列表设置下载进度显示状态
     * 
     * @param
     * 
     * @return
     */

    public void setButtonText(final int localId, final ViewHolder holder) {
        holder.progressBar.setVisibility(View.GONE);
        // TODO 需要改成从数据库读取进度,判断是否已经在下载队列,如果是则直接动态显示
        if (ClickAdapter.timeMap.containsKey(serverAppList.get(localId)
                .getTitle())) {
            getDownloadMap(localId, holder);
        } else {
            // 扫描数据库和本地已安装
            getDbAndInstall(localId, holder);
            if (dbMap.containsKey(serverAppList.get(localId).getTitle())) {
                setBtnIcon(btnText, holder.imageButton,
                        dbMap.get(serverAppList.get(localId).getTitle())
                                .getProgress(), true);
            } else {
                setBtnIcon(btnText, holder.imageButton, -1, false);
            }

            holder.imageButton.setText("");
            holder.state.setText(btnText
            // + "name:"
            // + serverAppList.get(localId).getName()
                    );
        }
    }

    private void getDbAndInstall(final int localId, final ViewHolder holder) {
        setBtnText("", btnText, contentNameMap, serverAppList.get(localId)
                .getTitle());
        flag = "";
        LogOutputUtils.e(TAG, "程序名:" + serverAppList.get(localId).getTitle());
        if (dbMap.containsKey((serverAppList.get(localId).getTitle()))) {
            if (dbMap.get(serverAppList.get(localId).getTitle()).getVerCode() < serverAppList
                    .get(localId).getVerCode()) {
                // 去下载
                setToDownload(localId, false);
                LogOutputUtils.i(TAG, "从数据库判断删除");
                dbDelete(serverAppList.get(localId).getTitle(), "从数据库判断删除");
            } else {
                if (dbMap.get(serverAppList.get(localId).getTitle()).getState() == 1) {
                    setInstalled(localId, "数据库版本大于服务器而且已经安装");
                } else {
                    if (dbMap.get(serverAppList.get(localId).getTitle())
                            .getProgress() >= 100) {
                        setToInstall(localId, "数据库版本大于服务器但是没有安装");
                    } else {
                        setToLoading(localId, "续传");
                    }
                }
            }
        } else {
            // 去下载
            setToDownload(localId, false);
        }
        if (!flag.equals(SettingsUtils.FLAG_TOLOADING)) {
            if (installAppList != null) {
                boolean jump = false;
                // LogOutput.e(TAG, "install.size:" + installAppList.size());

                for (int i = 0; i < installAppList.size(); i++) {
                    if (serverAppList.get(localId).getPackageName()
                            .equals(installAppList.get(i).getPackageName())) {
                        // serverAppList含有本地安装的apk
                        if (serverAppList.get(localId).getVerCode() <= (installAppList
                                .get(i).getVerCode())) {
                            setBtnText(SettingsUtils.TEXT_ALINSTALL, btnText,
                                    contentNameMap, serverAppList.get(localId)
                                            .getTitle());
                            flag = SettingsUtils.FLAG_INSTALLED;
                            if (dbMap.containsKey(serverAppList.get(localId)
                                    .getTitle())) {
                                if (dbMap.get(
                                        serverAppList.get(localId).getTitle())
                                        .getState() != 1) {
                                    if (dbMap.get(
                                            serverAppList.get(localId)
                                                    .getTitle()).getProgress() < 100) {
                                        LogOutputUtils.i(TAG, "从本地列表判断删除");
                                        dbDelete(serverAppList.get(localId)
                                                .getTitle(), "从本地列表删除");
                                    }
                                }
                            }
                        } else {
                            if (!dbMap.containsKey(serverAppList.get(localId)
                                    .getTitle())) {
                                setToDownload(localId, true);
                            }
                        }
                        jump = true;
                        break;
                    }
                }
                if (!jump) {
                    if (flag.equals(SettingsUtils.FLAG_INSTALLED)) {
                        setToInstall(localId, "本地列表没有有此apk但是数据库显示已经安装"
                                + serverAppList.get(localId).getTitle());
                    }
                }
            } else {
                if (flag.equals(SettingsUtils.FLAG_INSTALLED)) {
                    setToInstall(localId, "本地列表为空,但是数据库显示已经安装"
                            + serverAppList.get(localId).getTitle());
                }
            }
        }

        LogOutputUtils.e(TAG, "flag: " + flag);
        if (flag.equals(SettingsUtils.FLAG_INSTALLED)) {
            // setNullOnclick(holder.imageButton);
            openApk(holder.imageButton, activity, serverAppList.get(localId));
        } else if (flag.equals(SettingsUtils.FLAG_TOLOADING)) {
            // 断点续传功能判断
            LogOutputUtils.i("TOLOADING", "TOLOADING");
            if (isSdEnable(activity)) {
                // loadingApk(serverAppList.get(localId),
                // dbMap.get(serverAppList.get(localId).getTitle())
                // .getProgress(), activity);
                setBtnText(SettingsUtils.TEXT_WAIT_DOWNLOAD, btnText,
                        contentNameMap, serverAppList.get(localId).getTitle());
                setCancle(holder.imageButton, activity,
                        serverAppList.get(localId).getTitle());
                downloadApk(serverAppList.get(localId), activity);
            }
            setNullOnclick(holder.imageButton);
        } else if (flag.equals(SettingsUtils.FLAG_TODOWNLOAD)) {
            holder.imageButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isSdEnable(activity)) {
                        LogOutputUtils.e("clickDownload", "locaoid" + localId);
                        downloadApk(serverAppList.get(localId), activity);
                        // btnText = Settings.WAIT_DOWNLOAD;
                        // ((Button) v).setText(btnText); //字体已经在setCancle里面设置了
                        v.setBackgroundResource(R.drawable.downloading1);
                        setCancle((Button) v, activity,
                                serverAppList.get(localId).getTitle());
                        setBtnText(SettingsUtils.TEXT_WAIT_DOWNLOAD, btnText,
                                contentNameMap, serverAppList.get(localId)
                                        .getTitle());
                        holder.state.setText(btnText);
                    }
                }
            });
        } else if (flag.equals(SettingsUtils.FLAG_TOINSTALL)) {
            holder.imageButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {

                    goInstallApk(localId, holder.imageButton, activity,
                            serverAppList.get(localId).getTitle(),
                            updateHandler);
                }
            });
        } else {
            setNullOnclick(holder.imageButton);
        }
        contentStateMap.put(serverAppList.get(localId).getTitle(), btnText);
    }

    /**
     * 作用:下载管理和通用列表公用
     */

    private void getDownloadMap(final int localId, final ViewHolder holder) {
        setBtnText("", btnText, contentNameMap, serverAppList.get(localId)
                .getTitle());
        flag = "";

        int progress = progressMap.get(serverAppList.get(localId).getTitle()) == null
                || progressMap.get(serverAppList.get(localId).getTitle())
                        .getProgress() < 0 ? 0 : progressMap.get(
                serverAppList.get(localId).getTitle()).getProgress();
        LogOutputUtils.i(TAG + "||getView", "DownLoadListener.map:"
                + serverAppList.get(localId).getDownloadUrl() + "||progess"
                + progress);
        if (progress == 0) {
            setBtnText(SettingsUtils.TEXT_WAIT_DOWNLOAD, btnText,
                    contentNameMap, serverAppList.get(localId).getTitle());
            setCancle(holder.imageButton, activity, serverAppList.get(localId)
                    .getTitle());
        }
        // else if (progress == 100) {
        // btnText = Settings.INSTALL_APK;
        // holder.imageButton.setOnClickListener(new OnClickListener() {
        // @Override
        // public void onClick(View v) {
        // goInstallApk(localId, holder.imageButton);
        // }
        // });
        // }
        else if (progress < 0) {
            // progressBarVisible(holder.progressBar, progress);
            setBtnText(SettingsUtils.TEXT_WAIT_DOWNLOAD, btnText,
                    contentNameMap, serverAppList.get(localId).getTitle());
            setCancle(holder.imageButton, activity, serverAppList.get(localId)
                    .getTitle());
        } else if (progress < 100) {
            // progressBarVisible(holder.progressBar, progress);
            setBtnText(String.valueOf(progress) + "%", btnText, contentNameMap,
                    serverAppList.get(localId).getTitle());
            setCancle(holder.imageButton, activity, serverAppList.get(localId)
                    .getTitle());
        }
        setDownloadingBtnIcon(progress, holder.imageButton);
        holder.state.setText(
        // "从下载:" +
                btnText
                // + "..."
                // + serverAppList.get(localId).getName()
                );

    }

    // 取消按钮
    private static void setCancle(Button button, final Activity aacActivity,
            final String appName) {
        // btnText = btnText + "\n" + "/" + Settings.CANCLE;
        button.setText(SettingsUtils.CANCLE);
        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(SettingsUtils.CANCLE_INTENT);
                intent.putExtra(SettingsUtils.APK_NAME, appName);
                aacActivity.sendBroadcast(intent);
                deleteFile(appName);
                // Toast.makeText(
                // activity,
                // serverAppList.get(localId).getTitle() + Settings.CANCLE,
                // Toast.LENGTH_LONG).show();
            }
        });
    }

    private void progressBarVisible(ProgressBar progressBar, int progress) {
        progressBar.setVisibility(View.VISIBLE);
        progressBar.setProgress(progress);
    }

    /**
     * 
     * 作用: 应用管理下载进度显示
     * 
     * @param
     * 
     * @return
     */
    public void setAppManagerButtonText(final int localId,
            final ViewHolder holder, View convertView,
            SetupBackgroung setupBackgroung) {
        holder.progressBar.setVisibility(View.GONE);
        // TODO 需要改成从数据库读取进度,判断是否已经在下载队列,如果是则直接动态显示
        if (ClickAdapter.timeMap.containsKey(serverAppList.get(localId)
                .getTitle())) {
            getDownloadMap(localId, holder);
            setNullOnclick(convertView);
            setLongClickNull(convertView);
        } else {
            LogOutputUtils.i(TAG, "从数据库判断");
            // 扫描本地已安装
            int tempProgress = serverAppList.get(localId).getProgress();

            // 没下载完成,续传
            if (tempProgress < 100) {
                LogOutputUtils.i(TAG, serverAppList.get(localId).getTitle()
                        + ": " + tempProgress);
                setToLoading(localId, "续传");
                if (isSdEnable(activity)) {
                    // loadingApk(serverAppList.get(localId),
                    // dbMap.get(serverAppList.get(localId).getTitle())
                    // .getProgress(), activity);
                    downloadApk(serverAppList.get(localId), activity);
                }
                setCancle(holder.imageButton, activity,
                        serverAppList.get(localId).getTitle());
                setNullOnclick(convertView);// converview和holer.imageButton的点击和取消要绑定一起
                setNullOnclick(holder.imageButton);
                setDownloadingBtnIcon(tempProgress, holder.imageButton);
                setLongClickNull(convertView);
            } else if (tempProgress == 100) {// 下载完成,提示安装
                holder.summary.setVisibility(View.VISIBLE);
                if (serverAppList.get(localId).getState() != 1) {
                    holder.summary.setText(SettingsUtils.INSTALL_DEL);
                    setBtnText(SettingsUtils.TEXT_INSTALL_APK, btnText,
                            contentNameMap, serverAppList.get(localId)
                                    .getTitle());
                    holder.imageButton
                            .setOnClickListener(new OnClickListener() {

                                @Override
                                public void onClick(View v) {
                                    ShowNotifycation
                                            .installCancle(serverAppList.get(
                                                    localId).getTitle());
                                    goInstallApk(localId, holder.imageButton,
                                            activity, serverAppList
                                                    .get(localId).getTitle(),
                                            updateHandler);
                                }
                            });
                    convertView.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // TODO WebView跳转
                            // 点击跳转
                            // onClickItemToContent(pageUrl, pageNo, localId,
                            // serverAppList,
                            // activity);
                            ShowNotifycation.installCancle(serverAppList.get(
                                    localId).getTitle());
                            goInstallApk(localId, holder.imageButton, activity,
                                    serverAppList.get(localId).getTitle(),
                                    updateHandler);
                        }
                    });
                    holder.imageButton
                            .setBackgroundResource(R.drawable.item_btn_install);
                } else {
                    holder.imageButton
                            .setBackgroundResource(R.drawable.item_btn_alinstall);
                    holder.summary.setText(SettingsUtils.DEL);
                    setBtnText(SettingsUtils.TEXT_ALINSTALL, btnText,
                            contentNameMap, serverAppList.get(localId)
                                    .getTitle());
                    openApk(holder.imageButton, activity,
                            serverAppList.get(localId));
                    openApk(convertView, activity, serverAppList.get(localId));
                }
                setLongDel(convertView, serverAppList.get(localId), localId,
                        setupBackgroung);

            }
            LogOutputUtils.e(TAG, "是什么状态" + btnText);

            holder.imageButton.setText("");
            holder.state.setText(btnText
            // + "name:"
            // + serverAppList.get(localId).getName()
                    );
            // item点击监听
        }
    }

    private void setLongDel(View convertView, final App app, final int localId,
            final SetupBackgroung setupBackgroung) {
        convertView.setOnLongClickListener(new OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
                // if (isInstall(activity, app)) {
                // OnClickUninstallListener onClickUninstallListener = new
                // OnClickUninstallListener() {
                //
                // @Override
                // public void onClickUninstall(Intent intent, int index,
                // Context context,
                // UninstallAppReceive uninstallAppReceive) {
                // String action = intent.getAction();
                // if (action.equals(Intent.ACTION_PACKAGE_REMOVED)) {
                // String packName = intent.getDataString();
                // if (packName.equals("package:"
                // + app.getPackageName())) {
                // LogOutput.e(TAG, "卸载了:" + app.getTitle());
                // // TODO 卸载之后还要更新各个列表图标
                // Handler handler = new Handler() {
                // @Override
                // public void handleMessage(Message msg) {
                // super.handleMessage(msg);
                // DBApp_download dbApp_download = new DBApp_download(
                // activity,
                // Settings.DATABASEVERSION);
                // dbApp_download.delUpdateState(
                // app.getPackageName(), 0);
                // justNotifyView();
                // }
                // };
                // handler.sendMessage(new Message());
                // context.unregisterReceiver(uninstallAppReceive);
                // }
                // }
                // }
                // };
                //
                // UninstallAppReceive mUninstallReceiver = new
                // UninstallAppReceive(
                // onClickUninstallListener, localId);
                // IntentFilter filter = new IntentFilter(
                // Intent.ACTION_PACKAGE_REMOVED);
                // filter.addDataScheme("package");
                // activity.registerReceiver(mUninstallReceiver, filter);
                //
                // Uri packageURI = Uri.parse("package:"
                // + app.getPackageName().toString());
                // Intent uninstallIntent = new Intent(Intent.ACTION_DELETE,
                // packageURI);
                // activity.startActivity(uninstallIntent);
                // return true;
                // }

                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(
                        activity);
                alertBuilder
                        .setTitle("删除提示")
                        .setMessage("是否删除 " + app.getTitle() + " ?")
                        .setPositiveButton("确定",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                            int id) {

                                        BaseActivity.dbApp_download.delete(
                                                app.getTitle(), "下载完成,长按删除");
                                        deleteFile(app.getTitle());
                                        if (isAppManaActivity) {
                                            serverAppList = BaseActivity.dbApp_download
                                                    .getDownloadApp();
                                            setAppcount(serverAppList.size());
                                            setupBackgroung.setbg(serverAppList
                                                    .size());
                                        } else {
                                            dbMap = BaseActivity.dbApp_download
                                                    .getAllDownloadApk();
                                        }
                                        ShowNotifycation.installCancle(app
                                                .getTitle());
                                        justNotifyView();
                                    }
                                })
                        .setNegativeButton("取消",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                            int id) {
                                        dialog.cancel();
                                    }
                                });
                alertBuilder.create().show();
                return true;
            }
        });
    }

    // 去下载
    public static void downloadApk(App app, Activity aactivity) {
        ClickAdapter clickAdapter = ClickAdapter.getInstances(aactivity);
        clickAdapter.download(app, aactivity);
    }

    // 去更新,目前是删除原来的重新下载,后期再断点续传,但是第一版不做
    // public void loadingApk(App app, int progress, Activity aactivity) {
    // ClickAdapter clickAdapter = ClickAdapter.getInstances(aactivity);
    // // clickAdapter.loading(app, progress, activity);
    // clickAdapter.download(app, aactivity);
    //
    // }

    // 去安装
    public void goInstallApk(final int localId, final Button imageButton,
            Activity aaActivity, String appName, Handler handler) {
        if (!isSdEnable(aaActivity)) {
            return;
        }
        String filePath = SettingsUtils.getRootPath() + appName + ".apk";
        File file = new File(filePath);
        if (file.exists() && file.isFile()) {// 如果apk文件存在

            PackageManager pm = aaActivity.getPackageManager();
            PackageInfo info = pm.getPackageArchiveInfo(filePath,
                    PackageManager.GET_ACTIVITIES);
            if (info != null) {
                ApplicationInfo appInfo = info.applicationInfo;

                installReceiver = new InstallReceiver(
                        serverAppList.get(localId), handler);
                IntentFilter filter = new IntentFilter();
                filter.addAction(Intent.ACTION_PACKAGE_REPLACED);
                filter.addAction(Intent.ACTION_PACKAGE_ADDED);
                filter.addDataScheme("package");
                activity.registerReceiver(installReceiver, filter);
                // 安装apk方法
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setDataAndType(Uri.fromFile(file),
                        "application/vnd.android.package-archive");
                activity.startActivity(i);
                // setNullOnclick(imageButton);
            }
        } else {// 如果apk文件不存在
            DialogAndToast.showToast(activity, "文件不存在,重新下载");
            imageButton.setText(SettingsUtils.TEXT_DOWNLOAD_APK);
            if (ClickAdapter.timeMap.containsKey(serverAppList.get(localId)
                    .getTitle())) {
                ClickAdapter.removeMap(serverAppList.get(localId).getTitle());
            }
            cancleDbDelete(serverAppList.get(localId).getTitle(), "安装不存在");
            notifyDataSetChanged();
        }
    }

    /**
     * 
     * 作用: 删除数据库apk记录
     */
    public static void dbDelete(String appName, String where) {

        BaseActivity.dbApp_download.delete(appName, where);
        dbMap = BaseActivity.dbApp_download.getAllDownloadApk();
    }

    /**
     * 作用:点击取消
     */
    private void cancleDbDelete(String appName, String where) {
        if (isAppManaActivity) {
            BaseActivity.dbApp_download.delete(appName, where);
            serverAppList = BaseActivity.dbApp_download.getDownloadApp();
            setAppcount(serverAppList.size());
        } else {
            dbDelete(appName, where);
        }
    }

    // 接收安装完成广播设置数据库apk安装状态
    public static class InstallReceiver extends BroadcastReceiver {
        String passPackName = "", appName = "";
        Handler handler;
        App app;

        public InstallReceiver(App app, Handler handler) {
            passPackName = app.getPackageName();
            this.handler = handler;
            appName = app.getTitle();
            this.app = app;
        }

        public void registerReceiver() {

        }

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            LogOutputUtils.i("added", intent.getDataString() + "||action:"
                    + action);
            if (action.equals(Intent.ACTION_PACKAGE_ADDED)
                    || action.equals(Intent.ACTION_PACKAGE_REPLACED)) {
                String installName = intent.getDataString();
                LogOutputUtils.e("installName", "现在安装的apk: " + installName
                        + "|| 点击的apk: " + passPackName);
                if (installName.equals("package:" + passPackName)) {
                    // TODO 卸载之后还要更新各个列表图标
                    if (ClickAdapter.timeMap.containsKey(appName)) {
                        ClickAdapter.removeMap(appName);
                    }
                    ShowNotifycation.installCancle(appName);
                    installAppList.add(app);
                    updateDb(appName, 1);// 已安装
                    handler.sendMessage(new Message());
                }
            }
        }
    }

    /**
     * 
     * 作用: 修改数据库apk安装状态
     * 
     * @state 1:安装完成;0:未安装
     */
    private static void updateDb(String appName, int state) {
        // LogOutput.i(TAG + "安装完服务通知", appName);
        LogOutputUtils.i("安装完服务通知", appName);
        BaseActivity.dbApp_download.updateInstallState(appName, state);
        // dbMap = BaseActivity.dbApp_download.getAllDownloadApk();
        dbMap.get(appName).setState(state);
    }

    /**
     * 
     * 作用: 去下载而且不续传
     * 
     * isUpdate:true 去更新,false:显示去下载
     */
    public void setToDownload(int localId, boolean isUpdate) {
        if (isUpdate) {
            setBtnText(SettingsUtils.TEXT_UPDATED_APK, btnText, contentNameMap,
                    serverAppList.get(localId).getTitle());
        } else {
            setBtnText(SettingsUtils.TEXT_DOWNLOAD_APK, btnText,
                    contentNameMap, serverAppList.get(localId).getTitle());
        }

        flag = SettingsUtils.FLAG_TODOWNLOAD;
        serverAppList.get(localId).setAppend(0);
    }

    /**
     * 
     * 作用: 重新进入程序显示进度,并重新下载,后期再做续传
     */
    public void setToLoading(int localId, String where) {
        LogOutputUtils.i(TAG + "setLoading", where);
        if (dbMap.get(serverAppList.get(localId).getTitle()).getProgress() < 0) {
            setBtnText(SettingsUtils.TEXT_WAIT_DOWNLOAD, btnText,
                    contentNameMap, serverAppList.get(localId).getTitle());
        } else if (dbMap.get(serverAppList.get(localId).getTitle())
                .getProgress() == 0) {
            setBtnText(SettingsUtils.TEXT_WAIT_DOWNLOAD, btnText,
                    contentNameMap, serverAppList.get(localId).getTitle());
        } else {

            setBtnText(dbMap.get(serverAppList.get(localId).getTitle())
                    .getProgress() + "%", btnText, contentNameMap,
                    serverAppList.get(localId).getTitle());
        }
        LogOutputUtils.e(TAG, "续传的progress:" + btnText);
        flag = SettingsUtils.FLAG_TOLOADING;
        serverAppList.get(localId).setAppend(0);
    }

    /**
     * 
     * 作用: 设置为已经安装
     */
    public void setInstalled(int localId, String log) {
        LogOutputUtils.i(TAG, log + "设置为1");
        setBtnText(SettingsUtils.TEXT_ALINSTALL, btnText, contentNameMap,
                serverAppList.get(localId).getTitle());
        flag = SettingsUtils.FLAG_INSTALLED;
        updateDb(serverAppList.get(localId).getTitle(), 1);
    }

    /**
     * 
     * 作用: 设置为请安装
     */
    public void setToInstall(int localId, String log) {
        LogOutputUtils.i(TAG, log + "设置为0");
        setBtnText(SettingsUtils.TEXT_INSTALL_APK, btnText, contentNameMap,
                serverAppList.get(localId).getTitle());
        flag = SettingsUtils.FLAG_TOINSTALL;
        updateDb(serverAppList.get(localId).getTitle(), 0);
    }

    public static boolean isSdEnable(Context context) {

        if (!NetworkUtils.isNetworkAvailable(context)) {
            DialogAndToast.showToast(context, "网络不可用,请重新设置网络");
            return false;
        }

        // sdcard不可访问
        if (!Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED)) {
            DialogAndToast.showToast(context, "请插入Sd卡");
            return false;
        }
        return true;
    }

    /**
     * 
     * 作用: 因为用的View.getTag()所以,button会与之前的View绑定,所以每个结果都要设置当前按钮的动作
     */
    protected static void setNullOnclick(View view) {
        view.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
    }

    public void unRegisterReceive() {
        if (activity != null && progressChangeReceive != null) {
            activity.unregisterReceiver(progressChangeReceive);
        }
        if (activity != null && installReceiver != null) {
            activity.unregisterReceiver(installReceiver);
        }
    }

    public static void unRegisterReceive(Activity aActivity,
            ProgressChangeReceive pcr) {
        if (aActivity != null && pcr != null) {
            aActivity.unregisterReceiver(pcr);
        }
    }

    protected Map<String, Object> setupViews(View view, int layoutId,
            ViewHolder holder) {
        Map<String, Object> map = new HashMap<String, Object>();
        if (view == null) {
            holder = new ViewHolder();
            view = LayoutInflater.from(activity).inflate(layoutId, null);
            holder.icon = (ImageView) view
                    .findViewById(R.id.recommend_app_icon);
            holder.name = (TextView) view.findViewById(R.id.recommend_app_name);
            holder.version = (TextView) view
                    .findViewById(R.id.recommend_app_version_name);
            holder.size = (TextView) view.findViewById(R.id.recommend_app_size);
            holder.summary = (TextView) view
                    .findViewById(R.id.recommend_app_introduce);
            holder.imageButton = (Button) view
                    .findViewById(R.id.recommend_app_button);
            holder.progressBar = (ProgressBar) view
                    .findViewById(R.id.loadProgressBar);
            holder.checkBox = (CheckBox) view
                    .findViewById(R.id.recommend_app_select_button);
            holder.state = (TextView) view.findViewById(R.id.download_state);
            view.setTag(holder);
            holder.safeImageView = (ImageView) view
                    .findViewById(R.id.recommend_app_safe);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        map.put(SettingsUtils.VIEW, view);
        map.put(SettingsUtils.VIEWHOLDE, holder);
        return map;
    }

    public static void AsyncImage(String iconUrl, final ViewHolder holder) {
        AsyncImageLoader.loadDrawable(iconUrl, new ImageCallback() {
            public void imageLoaded(Drawable imageDrawable, String imageUrl) {
                if (null != imageDrawable) {
                    if (holder.iconurl.equals(imageUrl)) {
                        holder.icon.setImageDrawable(imageDrawable);
                    }
                }
            }
        });
    }

    protected void setImage(List<App> list, int position,
            final ViewHolder holder, int ifirstPosition, int ilastPosition,
            boolean bscrollStop, Drawable dDrawable) {
        String icon = list.get(position).getImage();
        // 异步下载图片
        if (null != icon && !"".equals(icon)) {
            holder.iconurl = icon;
            holder.icon.setImageDrawable(dDrawable);
            // TODO 判断是否已经下载过而且下载成功,是的话直接读缓存,否则下载.ps:可以改成用队列下载和从缓存读.后期修改
            // LogOutput.e(TAG,
            // "position:"+position+"last:"+lastPosition+"fir:"+firstPosition);
            // if (ifirstPosition == 0 || (ilastPosition == list.size())
            // || bscrollStop) {
            Drawable drawable = AsyncImageLoader.loadBitmap(holder.iconurl);
            if (drawable != null) {
                holder.icon.setImageDrawable(drawable);
            } else {
                AsyncImage(list.get(position).getImage(), holder);
            }
            // }
        }
    }

    /**
     * 作用:同步图片
     */
    protected void syncImage(List<App> list, int position,
            final ViewHolder holder) {
        SyncImageLoader.loadDrawable(list.get(position).getImage(),
                new SyncImageCallback() {
                    public void imageLoaded(Drawable imageDrawable,
                            String imageUrl) {
                        LogOutputUtils.i("getView()", "imageurl:" + imageUrl
                                + "drawable" + (imageDrawable == null));
                        if (null != imageDrawable) {
                            if (holder.iconurl.equals(imageUrl)) {
                                holder.icon.setImageDrawable(imageDrawable);
                            }
                        }
                    }
                }, activity);
    }

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

    /**
     * 作用:判断是否为应用管理从而获取不同的列表数据
     */
    private void setServerList() {
        if (isAppManaActivity) {
            serverAppList = BaseActivity.dbApp_download.getDownloadApp();
        } else {
            dbMap = BaseActivity.dbApp_download.getAllDownloadApk();
        }
    }

    private static void deleteFile(String apkName) {
        File file = new File(SettingsUtils.getRootPath(), apkName + ".apk");
        if (file.exists() && file.isFile()) {
            file.delete();
        }
    }

    /**
     * 作用:是否为显示进度
     */
    public static void setBtnIcon(String text, Button button, int progress,
            boolean isLoading) {
        if (text.equals(SettingsUtils.TEXT_INSTALL_APK)) {
            button.setBackgroundResource(R.drawable.item_btn_install);
        } else if (text.equals(SettingsUtils.TEXT_ALINSTALL)) {
            button.setBackgroundResource(R.drawable.item_btn_alinstall);
        } else if (text.equals(SettingsUtils.TEXT_DOWNLOAD_APK)) {
            button.setBackgroundResource(R.drawable.item_btn_download);
        } else if (text.equals(SettingsUtils.TEXT_UPDATED_APK)) {
            button.setBackgroundResource(R.drawable.item_btn_update);
        } else {
            if (isLoading) {
                setDownloadingBtnIcon(progress, button);
            } else {
                button.setBackgroundResource(R.drawable.item_btn_download);
            }
        }
    }

    private static void setFavVisible(ContentViewHolder viewHolder) {
        viewHolder.favImageView.setVisibility(View.VISIBLE);
        viewHolder.delImageView.setVisibility(View.GONE);
        viewHolder.linearLayoutRightProgress.setVisibility(View.GONE);
    }

    private static void setDelVisible(final ContentViewHolder viewHolder,
            final App app, final Activity activity, final Button button) {
        viewHolder.delImageView.setVisibility(View.VISIBLE);
        viewHolder.favImageView.setVisibility(View.GONE);
        viewHolder.delImageView
                .setBackgroundResource(R.drawable.content_delete);
        viewHolder.linearLayoutRightProgress.setVisibility(View.GONE);
        viewHolder.delImageView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                deleteFile(app.getTitle());
                BaseActivity.dbApp_download.delete(app.getTitle(), "终端页点击删除");
                dbMap = BaseActivity.dbApp_download.getAllDownloadApk();
                ShowNotifycation.installCancle(app.getTitle());
                if (contentStateMap.containsKey(app.getTitle())
                        && contentStateMap.get(app.getTitle()).equals(
                                SettingsUtils.TEXT_UPDATED_APK)) {
                    contentNameMap.put(app.getTitle(),
                            SettingsUtils.TEXT_UPDATED_APK);
                } else {
                    contentNameMap.put(app.getTitle(),
                            SettingsUtils.TEXT_DOWNLOAD_APK);
                    contentStateMap.put(app.getTitle(),
                            SettingsUtils.TEXT_DOWNLOAD_APK);
                }
                BaseListViewActivity.isChange = BaseListViewActivity.isClickDelApp;
                setContentBtnIcon(button, app, activity, 0, viewHolder);
            }
        });
    }

    private static void setUninsatllVisible(final ContentViewHolder viewHolder,
            final App app, final Activity activity, final Button button) {
        viewHolder.delImageView.setVisibility(View.VISIBLE);
        viewHolder.favImageView.setVisibility(View.GONE);
        viewHolder.delImageView
                .setBackgroundResource(R.drawable.content_uninstall);
        viewHolder.linearLayoutRightProgress.setVisibility(View.GONE);
        viewHolder.delImageView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                OnClickUninstallListener onClickUninstallListener = new OnClickUninstallListener() {

                    @Override
                    public void onClickUninstall(Intent intent, int index,
                            Context context,
                            UninstallAppReceive uninstallAppReceive) {
                        String action = intent.getAction();
                        if (action.equals(Intent.ACTION_PACKAGE_REMOVED)) {
                            String packName = intent.getDataString();
                            if (packName.equals("package:"
                                    + app.getPackageName())) {
                                // TODO 卸载之后还要更新各个列表图标
                                Handler handler = new Handler() {
                                    @Override
                                    public void handleMessage(Message msg) {
                                        super.handleMessage(msg);
                                        DBApp_download dbApp_download = new DBApp_download(
                                                activity,
                                                SettingsUtils.DATABASEVERSION);
                                        dbApp_download.delUpdateState(
                                                app.getPackageName(), 0);
                                        Map<String, App> map = dbApp_download
                                                .getAllDownloadApk();
                                        if (map.containsKey(app.getTitle())) {
                                            contentNameMap.put(
                                                    app.getTitle(),
                                                    SettingsUtils.TEXT_INSTALL_APK);
                                            contentStateMap.put(
                                                    app.getTitle(),
                                                    SettingsUtils.TEXT_INSTALL_APK);

                                        } else {
                                            contentNameMap.put(
                                                    app.getTitle(),
                                                    SettingsUtils.TEXT_DOWNLOAD_APK);
                                            contentStateMap.put(
                                                    app.getTitle(),
                                                    SettingsUtils.TEXT_DOWNLOAD_APK);
                                        }
                                        BaseListViewActivity.isChange = BaseListViewActivity.isClickUninstall;
                                        setContentBtnIcon(button, app,
                                                activity, 0, viewHolder);
                                    }
                                };
                                handler.sendMessage(new Message());
                                context.unregisterReceiver(uninstallAppReceive);
                            }
                        }
                    }
                };
                UninstallAppReceive mUninstallReceiver = new UninstallAppReceive(
                        onClickUninstallListener, 0);
                IntentFilter filter = new IntentFilter(
                        Intent.ACTION_PACKAGE_REMOVED);
                filter.addDataScheme("package");
                activity.registerReceiver(mUninstallReceiver, filter);

                Uri packageURI = Uri.parse("package:"
                        + app.getPackageName().toString());
                Intent uninstallIntent = new Intent(Intent.ACTION_DELETE,
                        packageURI);
                activity.startActivity(uninstallIntent);
            }
        });
    }

    private static void setProgressVisible(ContentViewHolder viewHolder) {
        viewHolder.linearLayoutRightProgress.setVisibility(View.VISIBLE);
        viewHolder.delImageView.setVisibility(View.GONE);
        viewHolder.favImageView.setVisibility(View.GONE);
    }

    /**
     * 作用:只是终端页显示
     */
    public static void setContentBtnIcon(Button button, App app,
            Activity activity, int progress, final ContentViewHolder viewHolder) {
        LogOutputUtils.e(TAG,
                "comtentNameMap:" + contentNameMap.containsKey(app.getTitle())
                        + contentNameMap.get(app.getTitle()));
        if (contentNameMap.get(app.getTitle()).equals(
                SettingsUtils.TEXT_INSTALL_APK)) {
            button.setBackgroundResource(R.drawable.content_install);
            contentToInstallApk(button, activity, app, viewHolder);
            setDelVisible(viewHolder, app, activity, button);
        } else if (contentNameMap.get(app.getTitle()).equals(
                SettingsUtils.TEXT_ALINSTALL)) {
            button.setBackgroundResource(R.drawable.content_alinstall);
            openApk(button, activity, app);
            setUninsatllVisible(viewHolder, app, activity, button);
        } else if (contentNameMap.get(app.getTitle()).equals(
                SettingsUtils.TEXT_DOWNLOAD_APK)) {
            setFavVisible(viewHolder);
            button.setBackgroundResource(R.drawable.content_download);
            contentBtnDownload(button, app, activity, progress, viewHolder);
        } else if (contentNameMap.get(app.getTitle()).equals(
                SettingsUtils.TEXT_UPDATED_APK)) {
            setFavVisible(viewHolder);
            button.setBackgroundResource(R.drawable.content_update);
            contentBtnDownload(button, app, activity, progress, viewHolder);
        } else {

            if (progress < 100) {
                setProgressVisible(viewHolder);
                viewHolder.progressBar.setProgress(progress);
                viewHolder.textView.setText(String.valueOf(progress) + "%");
                button.setBackgroundResource(R.drawable.content_cancle);
                contentSetCancle(button, app, activity, progress, viewHolder);
            } else if (progress >= 100) {
                contentNameMap.put(app.getTitle(),
                        SettingsUtils.TEXT_INSTALL_APK);
                setContentBtnIcon(button, app, activity, progress, viewHolder);
            }
        }
    }

    private static void contentToInstallApk(final Button button,
            final Activity activity, final App app,
            final ContentViewHolder viewHolder) {
        button.setText("");
        if (!isSdEnable(activity)) {
            return;
        }
        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                contentInstallApk(button, activity, app, viewHolder);
                BaseListViewActivity.isChange = BaseListViewActivity.isClickInstall;
            }
        });

    }

    private static void contentInstallApk(final Button button,
            final Activity activity, final App app,
            final ContentViewHolder viewHolder) {
        String filePath = SettingsUtils.getRootPath() + app.getTitle() + ".apk";
        File file = new File(filePath);
        if (file.exists() && file.isFile()) {// 如果apk文件存在

            PackageManager pm = activity.getPackageManager();
            PackageInfo info = pm.getPackageArchiveInfo(filePath,
                    PackageManager.GET_ACTIVITIES);
            if (info != null) {
                ApplicationInfo appInfo = info.applicationInfo;
                Handler handler = new Handler() {
                    public void handleMessage(Message msg) {

                        setUninsatllVisible(viewHolder, app, activity, button);
                        button.setBackgroundResource(R.drawable.content_alinstall);
                        button.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                unregisterInstallService(activity);
                                openApk(button, activity, app);
                            }
                        });
                    };
                };
                registerService(app, handler);
                IntentFilter filter = new IntentFilter();
                filter.addAction(Intent.ACTION_PACKAGE_REPLACED);
                filter.addAction(Intent.ACTION_PACKAGE_ADDED);
                filter.addDataScheme("package");
                activity.registerReceiver(
                        FocusContentViewActivity.installReceiver, filter);
                // 安装apk方法
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setDataAndType(Uri.fromFile(file),
                        "application/vnd.android.package-archive");
                activity.startActivity(i);
                // setNullOnclick(imageButton);
            }
        } else {// 如果apk文件不存在
            DialogAndToast.showToast(activity, "文件不存在,重新下载");
            button.setText(SettingsUtils.TEXT_DOWNLOAD_APK);
            if (ClickAdapter.timeMap.containsKey(app.getTitle())) {
                ClickAdapter.removeMap(app.getTitle());
            }
            dbDelete(app.getTitle(), "终端页安装从数据库删除");
        }
    }

    public static boolean isInstall(Activity activity, App app) {

        PackageInfo packageInfo;
        try {
            packageInfo = activity.getPackageManager().getPackageInfo(
                    app.getPackageName(), 0);

        } catch (NameNotFoundException e) {
            packageInfo = null;
            e.printStackTrace();
        }
        if (packageInfo == null) {
            System.out.println("没有安装" + app.getTitle());
            return false;
        } else {
            System.out.println("已经安装" + app.getTitle());
            return true;
        }
    }

    public static void openApk(View view, final Activity activity, final App app) {
        view.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isInstall(activity, app)) {
                    activity.startActivity(activity.getPackageManager()
                            .getLaunchIntentForPackage(app.getPackageName()));
                } else {
                    Toast.makeText(activity, "尚未安装此程序,请重新安装",
                            Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    public static void contentBtnDownload(final Button button, final App app,
            final Activity activity, final int progress,
            final ContentViewHolder viewHolder) {
        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (isSdEnable(activity)) {
                    downloadApk(app, activity);
                    // btnText = Settings.WAIT_DOWNLOAD;
                    // ((Button) v).setText(btnText); //字体已经在setCancle里面设置了
                    v.setBackgroundResource(R.drawable.content_cancle);
                    contentNameMap.put(app.getTitle(),
                            SettingsUtils.TEXT_WAIT_DOWNLOAD);
                    setContentBtnIcon(button, app, activity, progress,
                            viewHolder);
                }
            }
        });
    }

    public static void contentSetCancle(final Button button, final App app,
            final Activity activity, final int progress,
            final ContentViewHolder viewHolder) {
        // button.setText(Settings.CANCLE);
        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(SettingsUtils.CANCLE_INTENT);
                intent.putExtra(SettingsUtils.APK_NAME, app.getTitle());
                activity.sendBroadcast(intent);
                deleteFile(app.getTitle());
                button.setText("");
                setNullOnclick(button);
                contentNameMap.put(app.getTitle(),
                        contentStateMap.get(app.getTitle()));
                setContentBtnIcon(button, app, activity, progress, viewHolder);

            }
        });
    }

    /**
     * 作用:通用列表下载中所使用的状态和下载管理所用的状态
     */
    public static void setDownloadingBtnIcon(int downloadProgress, Button button) {
        if (downloadProgress <= 10) {
            button.setBackgroundResource(R.drawable.downloading1);
        } else if (downloadProgress > 10 && downloadProgress <= 20) {
            button.setBackgroundResource(R.drawable.downloading2);
        } else if (downloadProgress > 20 && downloadProgress <= 30) {
            button.setBackgroundResource(R.drawable.downloading3);
        } else if (downloadProgress > 30 && downloadProgress <= 40) {
            button.setBackgroundResource(R.drawable.downloading4);
        } else if (downloadProgress > 40 && downloadProgress <= 50) {
            button.setBackgroundResource(R.drawable.downloading5);
        } else if (downloadProgress > 50 && downloadProgress <= 60) {
            button.setBackgroundResource(R.drawable.downloading6);
        } else if (downloadProgress > 60 && downloadProgress <= 70) {
            button.setBackgroundResource(R.drawable.downloading7);
        } else if (downloadProgress > 70 && downloadProgress <= 80) {
            button.setBackgroundResource(R.drawable.downloading8);
        } else if (downloadProgress > 80 && downloadProgress < 100) {
            button.setBackgroundResource(R.drawable.downloading9);
        } else if (downloadProgress >= 100) {
            button.setBackgroundResource(R.drawable.item_btn_install);
        }
    }

    private void setLongClickNull(View view) {
        view.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return false;
            }
        });
    }

    public static int getProgress(String nameString) {
        if (progressMap.containsKey(nameString)) {
            return progressMap.get(nameString).getProgress();
        } else {
            return 0;
        }
    }

    private void setBtnText(String setText, String btnTextString,
            Map<String, String> map, String appName) {
        btnText = setText;
        contentNameMap.put(appName, setText);

    }

    Handler updateHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            // imageButton.setText(Settings.ALINSTALL);
            // setNullOnclick(imageButton);
            justNotifyView();
        }
    };

    public static void registerService(App app, Handler handler) {
        FocusContentViewActivity.installReceiver = new InstallReceiver(app,
                handler);
    }

    public static void unregisterInstallService(Activity activity) {
        if (FocusContentViewActivity.installReceiver != null) {
            activity.unregisterReceiver(FocusContentViewActivity.installReceiver);
            FocusContentViewActivity.installReceiver = null;
        }
    }

    protected void setName(TextView textView, String msg) {
        String titleName = msg;
        // titleName = titleName.length() > 8 ? titleName.substring(0,7)+"..."
        // : titleName;
        textView.setText(titleName);
    }

    protected void setDetail(TextView textView, String msg) {
        textView.setText(msg);
    }

    protected void setSize(TextView textView, String msg) {
        textView.setText(msg);
    }

    protected void setVersion(TextView textView, String msg) {
        textView.setText(msg);
    }

    protected void setSafe(ImageView imageView, int resid, int safe) {
        if (safe == 1) {
            imageView.setVisibility(View.VISIBLE);
        } else {
            imageView.setVisibility(View.GONE);
        }

    }
}
