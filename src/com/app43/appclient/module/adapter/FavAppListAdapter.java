package com.app43.appclient.module.adapter;

import com.alvin.api.utils.LogOutputUtils;
import com.alvin.api.utils.SettingsUtils;
import com.alvin.api.utils.UMengAnalyseUtils;
import com.app43.appclient.R;
import com.app43.appclient.bean.App;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// 程序列表适配器
public class FavAppListAdapter extends ProgressListViewAdapter {
    boolean gotoDisplayDel = false;// 要为删除按钮
    private Handler handler;
    private Map<Integer, Boolean> selectMap = new HashMap<Integer, Boolean>();// 利用一个映射表来记录选择的item
    private Map<Integer, Integer> appIdPositionMap = new HashMap<Integer, Integer>();// app的id与列表所在位置的映射,<position,appIds>

    public FavAppListAdapter(Activity activity) {
        super(activity, FavAppListAdapter.class.getSimpleName());
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        super.getView(position, convertView, parent);
        ViewHolder holder = null; // 使用viewholder加载图片速度提高50%
        final int localId = position;
        appIdPositionMap.put(localId, serverAppList.get(localId).getId());
        Map<String, Object> map = new HashMap<String, Object>();
        map = setupViews(convertView, R.layout.recommend_app_list_item, holder);
        convertView = (View) map.get(SettingsUtils.VIEW);// 获取converView
        holder = (ViewHolder) map.get(SettingsUtils.VIEWHOLDE);// 获取vviewHold
        if (isServerAppFinish && isLocalAppFinish && isInstallAppFinish) {

            if (position <= serverAppList.size() - 1) {
                // 设置图片
                setImage(serverAppList, position, holder, firstPosition,
                        lastPosition, scrollStop, defaultDrawable);

                // holder.name.setText(serverAppList.get(position).getTitle());
                // // 简介
                // holder.summary.setText(serverAppList.get(position)
                // .getPackageName() + "localId:" + localId);

                setSafe(holder.safeImageView, R.drawable.list_safe,
                        serverAppList.get(localId).getSafe());
                // 名字

                setName(holder.name, serverAppList.get(position).getTitle());
                // 简介
                setDetail(holder.summary, serverAppList.get(position)
                        .getSummary());
                // 大小
                setSize(holder.size, serverAppList.get(localId).getSize());

                // 安装或者删除收藏夹
                if (gotoDisplayDel) {
                    holder.checkBox.setVisibility(View.GONE);
                    progressBtnVisible(holder);
                } else {
                    // 按钮为"完成"
                    progressBtnGone(holder);
                    holder.checkBox.setVisibility(View.VISIBLE);
                    holder.checkBox.setChecked(selectMap.get(localId));
                    // LogOutput.e(TAG, "localid:" + localId + "--" +
                    // map.get(localId));
                    holder.checkBox.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            boolean isSel = selectMap.get(localId);
                            selectMap.put(localId, !isSel);
                            LogOutputUtils.i(TAG, "setOnChecked localid:" + localId);
                            if (localId < serverAppList.size()) {
                                // LogOutput.e(TAG, "delId:"
                                // + FavouriteActivity.appID[localId]);
                                sendHandleMessage(handler,
                                        appIdPositionMap.get(localId),
                                        selectMap.get(localId));
                            }
                        }
                    });
                }
                setButtonText(localId, holder);
            }
            // item点击监听
            convertView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO WebView跳转
                    // 点击跳转
                    UMengAnalyseUtils.onEvents(activity, UMengAnalyseUtils.VIEW_FAVs,
                            serverAppList.get(localId).getTitle());
                    onClickItemToContent(contentNameMap.get(serverAppList.get(
                            localId).getTitle()), pageUrl, pageNo, localId,
                            serverAppList, activity);
                }
            });

        } else {
            holder.name.setText("");
            holder.icon.setImageDrawable(defaultDrawable);
            holder.size.setText("");
            holder.summary.setText("");
        }
        if (progressBar != null) {
            progressBar.setVisibility(View.INVISIBLE);
            progressBar = null;
        }

        return convertView;
    }

    /*
     * 更新列表 扫描本地已下载,本地已安装软件
     */
    @Override
    public void updateList(ArrayList<App> sApps, boolean serverFinish,
            List<App> lApps, boolean localFinish, List<App> iApps,
            boolean installFinish, ProgressBar pb, boolean del,
            Handler handler, String pageUrl, int pageNo, int[] apps, String who) {
        LogOutputUtils.e("favAppListAdapter", "who:" + who + "||   serverFinish:"
                + serverFinish + "isLocalAppFinish:" + isLocalAppFinish
                + "isInstallAppFinish" + isInstallAppFinish);
        progressBar = pb;
        serverAppList = sApps;
        localAppList = lApps;
        installAppList = iApps;
        // LogOutput.e(TAG, "本地列表循环");
        // for (int i = 0; i < installAppList.size(); i++) {
        //
        // }
        isServerAppFinish = serverFinish;
        isLocalAppFinish = localFinish;
        isInstallAppFinish = installFinish;
        gotoDisplayDel = del;
        setMap();
        this.pageNo = pageNo;
        this.pageUrl = pageUrl;
        this.handler = handler;
        notifyDataSetChanged();
    }

    private void setMap() {
        for (int i = 0; i < serverAppList.size(); i++) {
            if (!selectMap.containsKey(i)) {
                selectMap.put(i, false);
            }
        }
    }

    private void progressBtnVisible(ViewHolder holder) {
        holder.progressBar.setVisibility(View.VISIBLE);
        holder.imageButton.setVisibility(View.VISIBLE);
    }

    private void progressBtnGone(ViewHolder holder) {
        holder.progressBar.setVisibility(View.GONE);
        holder.imageButton.setVisibility(View.GONE);
    }

    private void sendHandleMessage(Handler handler, int appId, boolean isDel) {
        Message message = handler.obtainMessage();
        message.obj = appId;
        if (isDel) {
            message.what = 1;
        } else {
            message.what = 0;
        }
        handler.sendMessage(message);
    }
}
