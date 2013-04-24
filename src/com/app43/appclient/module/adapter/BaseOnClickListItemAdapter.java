package com.app43.appclient.module.adapter;

import com.alvin.api.utils.LogOutputUtils;
import com.alvin.api.utils.SettingsUtils;
import com.alvin.api.utils.UMengAnalyseUtils;
import com.app43.appclient.bean.App;
import com.app43.appclient.module.common.activity.ContentViewActivity;
import com.app43.appclient.module.common.activity.FocusContentViewActivity;
import com.app43.appclient.module.hot.GameListActivity;
import com.app43.appclient.module.hot.HotListActivity;
import com.app43.appclient.module.menu.FavouriteActivity;
import com.app43.appclient.module.tabframe.GuessInterestActivity;
import com.app43.appclient.module.tabframe.HotGridActivity;
import com.app43.appclient.module.tabframe.RecommendAppActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * 跳转到没有上一页下一页的终端页.所以不用传serverlist 项目名称：app43 类名称：BaseOnClickListItemAdapter
 * 类描述： 创建人：APP43 创建时间：2012-2-19 下午2:02:41 修改人：APP43 修改时间：2012-2-19 下午2:02:41
 * 修改备注：
 * 
 * @version
 * 
 */
public abstract class BaseOnClickListItemAdapter extends BaseAdapter {
    protected String TAG;
    public Activity activity;
    public int appcount = 0;

    public BaseOnClickListItemAdapter(Activity activitys, String tag) {
        TAG = tag;
        activity = activitys;

    }

    public void setAppcount(int appcount) {
        this.appcount = appcount;
    }

    @Override
    public int getCount() {
        return appcount;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return null;
    }

    public void notifyView(int firstPosition, int lastPosition,
            List<App> serverAppList, String apkName) {

        if (serverAppList != null && !serverAppList.isEmpty()) {
            for (int i = firstPosition; i < lastPosition; i++) {

                LogOutputUtils.i(TAG + "}}onreceive i:", "lastPo:" + lastPosition
                        + "server.Name" + serverAppList.get(i).getTitle()
                        + "|| intent.title:" + apkName + "qeual:"
                        + (serverAppList.get(i).getTitle().equals(apkName)));
                if (serverAppList.get(i).getTitle().equals(apkName)) {
                    notifyDataSetChanged();
                    break;
                }
            }
        }
    }

    public void justNotifyView() {
        notifyDataSetChanged();
    }

    /**
     * 通用listView点击Item跳转到终端页
     */
    public void onClickItemToContent(String stateText, String pageUrl,
            int pageNo, int localId, ArrayList<App> serverAppList,
            Activity activitys) {
        LogOutputUtils.e(TAG + "click",
                "getGallery_url : "
                        + serverAppList.get(localId).getGallery_url());
        if (activity instanceof GuessInterestActivity) {
            UMengAnalyseUtils.activity2Content = 3;
        } else if (activity instanceof RecommendAppActivity) {
            UMengAnalyseUtils.activity2Content = 1;
            if (UMengAnalyseUtils.lableClick == 3) {
                UMengAnalyseUtils.activity2Content = 4;
            }
        } else if (activity instanceof HotListActivity
                || activity instanceof HotGridActivity
                || activity instanceof GameListActivity) {
            UMengAnalyseUtils.activity2Content = 2;
        } else if (activity instanceof FavouriteActivity) {
            UMengAnalyseUtils.activity2Content = 5;
        } 

        Intent intent = new Intent();
        this.activity = activitys;
        intent.setClass(activity, ContentViewActivity.class);
        // 需序列化serverList传递到contentActivitySERVERAPPLIST
        intent.putParcelableArrayListExtra(SettingsUtils.SERVERAPPLIST,
                serverAppList);
        intent.putExtra(SettingsUtils.CONTENT_TEXT, stateText);
        intent.putExtra(SettingsUtils.POSITION, localId);
        intent.putExtra(SettingsUtils.PAGENO, pageNo);
        intent.putExtra(SettingsUtils.PAGEURL, pageUrl);
        intent.putExtra(SettingsUtils.ACTIVITY_FLAG,
                SettingsUtils.LISTVIEW_ACTIVITY_REQ_CODE);
        // LogOutput.e("onclick" , "name"+serverAppList.get(localId).getName());
        activity.startActivityForResult(intent,
                SettingsUtils.LISTVIEW_ACTIVITY_REQ_CODE);
    };

    /**
     * category跳转到相应Activity
     * 
     * @param pageUrl
     * @param pageNo
     * @param activitys
     */
    public void onClickItemToList(String pageUrl, int pageNo,
            Activity fromActivity, Activity toActivity) {
        Intent intent = new Intent();
        intent.setClass(fromActivity, toActivity.getClass());
        intent.putExtra(SettingsUtils.PAGEURL, pageUrl);
        intent.putExtra(SettingsUtils.PAGENO, pageNo);
        fromActivity.startActivity(intent);
    }

    /**
     * 
     * 作用:通用列表adapter
     * 
     * @param
     * 
     * @return String DOM对象
     */
    public void updateList(ArrayList<App> sApps, boolean serverFinish,
            List<App> lApps, boolean localFinish, List<App> iApps,
            boolean installFinish, ProgressBar pb, String pageUrl, int pageNo,
            String who) {
    }

    /**
     * 
     * 作用: 收藏夹adapter用
     * 
     * @param
     * 
     * @return String DOM对象
     */
    public void updateList(ArrayList<App> sApps, boolean serverFinish,
            List<App> lApps, boolean localFinish, List<App> iApps,
            boolean installFinish, ProgressBar pb, boolean del,
            Handler handler, String pageUrl, int pageNo, int apps[], String who) {
    }

    public static class ViewHolder {
        public ImageView icon = null;
        public TextView name = null, size = null, summary = null, state = null;
        public String iconurl, downName;// 回调映射时对应的item
        public Button imageButton;

        public int groupPosition, childPosition;
        // , delButton
        ;
        public CheckBox checkBox;
        public ProgressBar progressBar;// 进度条
        public TextView version;
        public ImageView safeImageView;
    }

    public static class ProgressData {
        public String apkName;
        public int progress;

        public String getApkName() {
            return apkName;
        }

        public void setApkName(String apkName) {
            this.apkName = apkName;
        }

        public int getProgress() {
            return progress;
        }

        public void setProgress(int progress) {
            this.progress = progress;
        }
    }
}
