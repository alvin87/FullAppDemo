package com.alvin.api.utils;

import com.feedback.NotificationType;
import com.feedback.UMFeedbackService;

import android.app.Activity;
import android.content.Context;

/**
 * 主要用于友盟统计 项目名称：app43 类名称：UMengAnalyse 类描述： 创建人：APP43 创建时间：2012-2-20 上午10:23:25
 * 修改人：APP43 修改时间：2012-2-20 上午10:23:25 修改备注：
 * 
 * @version
 * 
 */
public class UMengAnalyseUtils {

    /**
     * 作用: 统计事件
     * 
     * @param
     */
    public static void onEvents(Context context, String eventId) {
        //MobclickAgent.onEvent(context, eventId);
    }

    /**
     * 作用: 统计事件及标签
     * 
     * @param
     */
    public static void onEvents(Context context, String eventId, String lable) {
        //MobclickAgent.onEvent(context, eventId, lable);
        MossAnalyseUtils.onEvents(MossAnalyseUtils.EVENT_USER, eventId, lable);
    }

    /**
     * 作用: 统计事件点击次数
     * 
     * @param
     */
    public static void onEvents(Context context, String eventId, int aac) {
        //MobclickAgent.onEvent(context, eventId, aac);
    }

    /**
     * 作用: 统计事件标签的点击次数
     * 
     * @param
     */

    public static void onEvents(Context context, String eventId, String lable,
            int aac) {
       // MobclickAgent.onEvent(context, eventId, lable, aac);
    }

    public static void onError(Context context) {
        //MobclickAgent.onError(context);
    }

    public static void update(Context context) {
        //MobclickAgent.update(context);
    }

    public static void update(Context context, long id) {
        //MobclickAgent.update(context, id);
    }

    public static void update(Context context, String msg) {
        //MobclickAgent.update(context, msg);
    }

    public static void onResume(Context context) {
        //MobclickAgent.onResume(context);
    }

    public static void onResume(Context context, String msg, String lable) {
        //MobclickAgent.onResume(context, msg, lable);
    }

    public static void onPause(Context context) {
        //MobclickAgent.onPause(context);
    }

    public static void setDebugMode(boolean isDebug) {
        //MobclickAgent.setDebugMode(isDebug);
    }

    public static void updateOnlineConfig(Activity activity) {
        //MobclickAgent.updateOnlineConfig(activity);
    }

    public static void openUmengFeedbackSdk(Context context) {
        UMFeedbackService.openUmengFeedbackSDK(context);
    }

    public static void enableNewReplyNotification(Context context,
            NotificationType type) {
        UMFeedbackService.enableNewReplyNotification(context, type);
    }

    private static String DOWNLOAD = "download_";
    private static String VIEW = "view_";
    public static String goto1 = "直接进入精品推荐1";
    public static String goto2 = "直接进入精品推荐2";
    public static String download = "一键下载";
    public static String manager_download = "下载管理";
    public static String manager_install = "安装管理";

    // --------统计的字段 带#为已添加

    // 下载量
    public static String DOWNLOAD_RECOMMENDS = DOWNLOAD + "recommend";// 精品推荐
    // #
    public static String DOWNLOAD_FOCUSS = DOWNLOAD + "focus";// 焦点图 #
    public static String DOWNLOAD_GUESSS = DOWNLOAD + "guess";// 猜你喜欢#
    public static String DOWNLOAD_HOTS = DOWNLOAD + "hot";// 热门排行 #
    public static String DOWNLOAD_TOTALs = DOWNLOAD + "total";// 总下载#
    public static String DOWNLOAD_FAVs = DOWNLOAD + "fav";// 收藏夹下载量#

    // 查看量
    public static String VIEW_GUESSs = VIEW + "guess";// 猜你喜欢 #
    public static String VIEW_FOCUSs = VIEW + "focus";// 焦点图 #
    public static String VIEW_APP_MANAGERs = VIEW + "app_manager";// 应用管理标签查看量#
    public static String VIEW_HOTs = VIEW + "hot";// 热门排行 #
    public static String VIEW_RECOMMENDs = VIEW + "recommend";// 精品内容推荐 #
    public static String VIEW_TABs = VIEW + "tabhost";// tabhost查看量 #
    public static String VIEW_TOTALs = VIEW + "total";// 总应用查看量 #
    public static String VIEW_FAVs = VIEW + "fav";// 收藏夹查看量 #
    public static String VIEW_RECOMMEND_LABLEs = VIEW + "recommend_lable";// 精品推荐标签查看量#
    public static String VIEW_SHORTUCT = VIEW + "shortcut";// 快捷方式点击 #

    // 新建的
    public static String VIEW_RECOMMEND_MUST = VIEW + "recommend_must";// 装机必备查看量#
    public static String VIEW_USERINFO_BUTTON = VIEW + "userinfo_button";// 用户信息收集页跳转的按钮#
    public static String DOWNLOAD_USERINFO = DOWNLOAD + "userinfo";// 新手引导下载量#
    public static String DOWNLOAD_MUST = DOWNLOAD + "must";// 装机必备下载量#
    // 用户信息收集点击量标签分布（信息项） 待做
    public static String VIEW_USERINFO_LABLE = VIEW + "userinfo_lable";
    public static int activity2Content = -1;// 用来标记是哪个activity跳转到终端页

    // 1:精品推荐,2:热门应用 3:猜你喜欢 4:装机必备 5:收藏夹 6:焦点图

    public static int lableClick = 0;// 只为了友盟统计标签0:全部,1:游戏,2:软件,3:装机
}
