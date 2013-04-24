package com.alvin.api.utils;

import cn.com.pcgroup.common.android.config.Env;

import android.content.Context;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

public class SettingsUtils {

    // 首页用户信息收集SharedPreferences getSharePre统一用USERINFO_COLLECTION
    public static final String USERINFO_COLLECTION = "userinfo_collection";
    public static final String ISFIRSTIN = "isFirstIn";// 是否第一次进入程序
    public static final String APPID = "appId";// 跳转到终端页绑定app
    public static final String CONTENTURL = "contentUrl";// 终端页url
    public static final String DOWNLOADURL = "downloadUrl";// apk下载url
    public static final String SERVERAPPLIST = "serverAppList";// activity间传递的对象
    public static final String POSITION = "position"; // app位置
    public static final String PAGEURL = "pageUrl"; // 在终端页翻页url
    public static final String PAGENO = "pageNo";// 在终端页翻页页数
    public static final String ISTURN_NEXT = "isTurnNest";// 是否按了下一个
    public static final String ACTIVITY_FLAG = "activityFlag";// Activity传递标记
    public static final String NETWORKSTATE = "networkState";// 网络状态
    public static final String DELETE_POSITION = "deletePosition";
    public static final String VIEWHOLDE = "viewHold";
    public static final String VIEW = "view";
    public static final String NOTIFICATION_ONCLIAK = "notification_onclick";//从通知栏点击进来
    public static final String CONTENT_TEXT = "content_flag";// 跳转到终端页所带的状态
    public static final String CONTENT_PROGRESS = "content_progress";// 跳转到终端页的进度
    public static final String SHORTUCT = "shortuct";// 快捷方式
    public static final int NOTIFICATION_INDEX = 3;// 点击通知跳转到应用管理
    public static final int TOPBOTTOM = 87; // 顶部状态栏和底部的高
    public static final int CONTENTVIEW_TOPBOTTOM = 118; // 顶部状态栏和底部的高
    // Activity请求代码
    public static final int FOUCS_ACTIVITY_REQ_CODE = 0x0001;
    public static final int LISTVIEW_ACTIVITY_REQ_CODE = 0x0002;
    public static final int FAV_LISTVIEW_ACTIVITY_REQ_CODE = 0x0003;
    // Json收发数据类型
    public final static int JSON_REQUEST = 0x0004; // 发送请求
    public final static int JSON_SUCCESS = 0x0005; // 请求成功
    public final static int JSON_NET_ERROR = 0x0006; // 接收失败
    // Activity请求代码
    public final static int LauncherUserInfo_Activity_REQUEST_CODE = 0x0007; // 发送请求
    public final static int LauncherUserInfo_Activity_RESPONE_CODE = 0x0008; // 发送请求
    public static final int ACTIVITY_RESULT = 0x0009;

    // 数据库版本控制
    public static final int DATABASEVERSION = 1;
    public final static String USER_BEHAVIOR = "user_behavior";
    public final static String FAVOURITE_APP = "favourite_app";
    public final static String APP_DOWNLOAD = "app_download";

    public final static int CATEID_GAME = 3;
    public final static int CATEID_INPUT = 4;
    public final static int CATEID_BROWSER = 5;
    public final static int CATEID_VIDEO = 6;
    public final static int CATEID_CAMERA = 7;
    public final static int CATEID_ENTERTAINMENT = 8;
    public final static int CATEID_SOCIAL = 9;
    public final static int CATEID_LIFE = 10;
    public final static int CATEID_SHOPPING = 11;
    public final static int CATEID_MONENY = 12;
    public final static int CATEID_NEWS = 13;
    public final static int CATEID_READDING = 14;
    public final static int CATEID_OFFICE = 15;
    public final static int CATEID_TRIP = 16;
    public final static int CATEID_NAVIGATION = 17;
    public final static int CATEID_SYSTEM = 18;
    public final static int CATEID_TOOLS = 19;
    public final static int CATEID_SAFE = 20;
    public final static int CATEID_SHOOTING = 21;
    public final static int CATEID_RPG = 22;
    public final static int CATEID_SPEED = 23;
    public final static int CATEID_SPORT = 24;
    public final static int CATEID_INTELLECT = 25;
    public final static int CATEID_BUSINESS = 26;
    public final static int CATEID_SIMULATE = 27;
    public final static int CATEID_CARDS = 28;
    public final static int CATEID_NETWORKGAME = 29;

    // 缓存过期时间
    public final static int TEXTCACHETIME = 1 * 60; // titl缓存时间 单位分钟
    public final static int ImageCacheTime = 14 * 24 * 60 * 60; // 图片缓存时间 单位天

    // 终端页大图数组
    public final static String BIG_IMAGE_URL = "bigImageUrl";
    public final static String BIG_IMAGE_POSITION = "bigImagePosition";

    // 用户行为加分规则
    public final static int READ_APP = 1;
    public final static int DOWNLOAD_APP = 2;

    // 下载服务
    public final static String DOWNLOADSERVICE = "com.download.service";// 开启下载服务标记
    public final static String SERVICEDOWNLOAD = "com.service.download";
    public final static String POSITIONDOWN = "position";// 所选下载的item位置
    public final static String PROGRESS = "progress";// 下载进度
    public final static String APP = "app";// 下载app对象
    public final static String INSTALL_RECOMMEND = "install_recommed";// 下载app对象
    public final static String APK_NAME = "apkName";// 下载apk的名字
    public final static String PROGRESS_BROADCAST_RECEIVE = "com.progress.broadcastReceive";
    public final static String CLICK_CANCLE_RECEIVE = "cancleDownload";// 点击取消下载按钮发送广播
    public final static String CANCLE_INTENT = "cancleIntent";// BroadcastThread发送取消广播给视图更新
    // public static String APK_DOWNLOAD_PATH = getRootPath();// apk存放路径
    // 在LuancherActivity设置
    public final static String TEXT_INSTALL_APK = "请安装";
    public final static String TEXT_ALINSTALL = "打开";
    public final static String TEXT_DOWNLOAD_APK = "请下载";
    public final static String TEXT_WAIT_DOWNLOAD = "请等待";
    // public final static String ALDOWNLOADED = "已下载";
    public final static String TEXT_UPDATED_APK = "请更新";
    // public final static String NOSIZE = "正在下载...";// 不知道文件大小的显示
    public final static int PROGRESS_ERROE = 404;// 进度显示错误
    public final static int CANCLE_DOWNLOAD = 707;// 从下载线程接口给广播进度线程判断是否取消下载
    public final static String ADD_TASK = "addTask";// 添加下载任务
    public final static String REMOVE_TASK = "remove_task";// 取消下载任务
    public final static String TASK_FLAG = "taskFlag";// 是删除还是取消任务
    public final static String INSTALL_DEL = "点击安装,长按删除程序";
    public final static String DEL = "点击打开,长按删除程序";
    public final static String CANCLE = "取消";
    public final static String SELNULL = "亲,您还没选择任何下载";
    public final static String CONTENT_CLICK_ISCHANGE = "content_click_ischange";
    public final static String CURRENT_TIME = "currentTime";
    public final static String NOTIFYID = "notifyId";
    // ListView.getView()标记
    public static final String FLAG_TOLOADING = "toLoading";// 重新打开程序有没下载完的apk则重新添加任务到OnclickAdapter
    public static final String FLAG_TODOWNLOAD = "toDownload";// 去下载
    public static final String FLAG_TOINSTALL = "toInstalled";// 去安装
    public static final String FLAG_INSTALLED = "installed";// 显示已安装

    // json列表对象
    public final static String JSON_FOCUS = "focus";
    public final static String JSON_TOTAL = "total";
    public final static String JSON_APPS = "apps";
    public final static String JSON_NECESSARY = "necessary";
    public final static String JSON_INTERESTED = "interest";
    public final static String JSON_CATEGORYS_ID = "id";
    public final static String JSON_CATEGORIES = "categories";
    public final static String JSON_CATEGORY_TITLE = "title";
    public final static String JSON_SEX = "sex";
    public final static String JSON_OCCUPATION = "Occupation";
    public final static String JSON_INTEREST_LABLE = "Interest";// 收集信息标签页
    public final static String JSON_USERINFO_ID = "id";
    public final static String JSON_USERINFO_TITLE = "title";

    public final static String ID = "id";
    public final static String NAME = "name";
    public final static String PACKAGE = "package";
    public final static String VERSION_CODE = "version_code";
    public final static String VEWSION_NAME = "version_name";
    public final static String CATEGORY_ID = "category_id";
    public final static String CATEGORY_NAME = "category_name";
    public final static String SIZE = "size";
    public final static String SUMMARY = "summary";
    public final static String IMAGE = "image";
    public final static String URL = "url";
    public final static String DOWNLOAD = "download";

    public final static String CATEGORIES = "categories";
    public final static String SIMILAR = "similar";

    // apkDownload
    public final static String NAME_APK = "name"; // 程序名
    public final static String PACKAGE_APK = "packageName"; // 包名
    public final static String CATEGORY_NAME_APK = "category_name"; // 分类名称
    public final static String CATEGORY_ID_APK = "category_id"; // 分类ID
    public final static String SIZE_APK = "size"; // 程序大小
    public final static String VERSION_APK = "version"; // 版本名
    public final static String DOWNDATE_APK = "downDate"; // 下载日期
    public final static String STATE_APK = "state";// 下载状态:1,完成;0,未完成.
    public final static String PROGRESS_APK = "progress";// 下载进度

    private final static String URL_HEAD = "http://wwwand.app43.com/index.php/android/api/";
    private final static String URL_TOP = "topapps/";
    // ----每个接口地址
    // public static final String URL_USERINFO =
    // "http://wwwand.app43.com/index.php/android/api/userapps";// 装机推荐
    // wwwand.app43.com/index.php/android/api/init/
    public static final String URL_USERINFO = URL_HEAD + "init?";// 装机推荐
    public static final String URL_USERINFO_INDEX = "http://wwwand.app43.com/index.php/android/api/index";
    public static final String URL_RECOMMEND_ALL = URL_HEAD + "recommendApp/0/";// 精品推荐_all
    public static final String URL_RECOMMEND_GAME = URL_HEAD
            + "recommendApp/2/";// 精品推荐_游戏
    public static final String URL_RECOMMEND_SOFTWAVE = URL_HEAD
            + "recommendApp/1/";// 精品推荐_软件
    public static final String URL_RECOMMEND_MUST = URL_HEAD + "necessary/";// 精品推荐_必备
    public static final String URL_HOT_1 = URL_HEAD + URL_TOP + "3/";// 游戏
    public static final String URL_HOT_2 = URL_HEAD + URL_TOP + "5/";// 浏览器
    public static final String URL_HOT_3 = URL_HEAD + URL_TOP + "19/";// 工具
    public static final String URL_HOT_4 = URL_HEAD + URL_TOP + "4/";// 输入法
    public static final String URL_HOT_5 = URL_HEAD + URL_TOP + "10/";// 生活
    public static final String URL_HOT_6 = URL_HEAD + URL_TOP + "15/";// 办公
    public static final String URL_HOT_7 = URL_HEAD + URL_TOP + "14/";// 阅读
    public static final String URL_HOT_8 = URL_HEAD + URL_TOP + "8/";// 娱乐
    public static final String URL_HOT_9 = URL_HEAD + URL_TOP + "9/";// 社交
    public static final String URL_HOT_10 = URL_HEAD + URL_TOP + "16/";// 旅游
    public static final String URL_HOT_11 = URL_HEAD + URL_TOP + "13/";// 新闻
    public static final String URL_HOT_12 = URL_HEAD + URL_TOP + "20/";// 安全
    public static final String URL_HOT_13 = URL_HEAD + URL_TOP + "7/";// 摄影
    public static final String URL_HOT_14 = URL_HEAD + URL_TOP + "18/";// 系统设置
    public static final String URL_HOT_15 = URL_HEAD + URL_TOP + "6/";// 影音
    public static final String URL_HOT_16 = URL_HEAD + URL_TOP + "17/";// 导航
    public static final String URL_HOT_17 = URL_HEAD + URL_TOP + "11/";// 购物
    public static final String URL_HOT_18 = URL_HEAD + URL_TOP + "12/";// 理财
    public static final String URL_GAME_1 = URL_HEAD + URL_TOP + "23/";// 赛车
    public static final String URL_GAME_2 = URL_HEAD + URL_TOP + "21/";// 动作
    public static final String URL_GAME_3 = URL_HEAD + URL_TOP + "22/";// 角色
    public static final String URL_GAME_4 = URL_HEAD + URL_TOP + "24/";// 体育
    public static final String URL_GAME_5 = URL_HEAD + URL_TOP + "27/";// 策略
    public static final String URL_GAME_6 = URL_HEAD + URL_TOP + "25/";// 休闲
    public static final String URL_GAME_7 = URL_HEAD + URL_TOP + "26/";// 经营
    public static final String URL_GAME_8 = URL_HEAD + URL_TOP + "28/";// 棋牌
    public static final String URL_GAME_9 = URL_HEAD + URL_TOP + "29/";// 网络
    public static final String URL_GUESS = URL_HEAD + "interest/";// 猜你喜欢
    public static final String URL_FAVOURITE = URL_HEAD + "collection/";// 收藏夹
    public static final String URL_SUGGESST = URL_HEAD + "suggestion?";// 意见反馈
    public static final String URL_ABOUT = "http://www.app43.com/aboutme_android.html";// 关于我们

    public static Context context;

    public static final int FOCUS_GALLERY_HEIGHT = 100;
    public static final int FOCUS_GALLERY_WIDTH = 200;
    public static final int FOCUS_GALLERY_PADDING = 3;

    public static final int CONTENT_GALLERY_HEIGHT = 260;
    public static final int CONTENT_GALLERY_WIDTH = 180;
    public static final int CONTENT_GALLERY_PADDING = 3;

    public static final ScaleType FOCUS_SCALTYPE = ImageView.ScaleType.FIT_CENTER;
    public static final ScaleType CONTENT_SCALTYPE = ImageView.ScaleType.FIT_CENTER;
    public static final ScaleType CONTENT_BIGIMG = ImageView.ScaleType.FIT_XY;

    public static final String NULL_IMEI = "000000000000000";
    public static final String NULL_PHONENUM = "00000000000";
    public static final String NULL_EMAIL = "0";
    public static final String NULL_PV = "0";
    public static final String NULL_OS = "0";

    public static final String uuid = "uuid";
    public static final String IMEI = "imei";
    public static final String PHONENUM = "phoneNum";
    public static final String EMAIL = "email";
    public static final String ID_LABLE = "lable";
    public static final String OS = "os";
    public static final String AV = "av";
    public static final String PV = "pv";

    public static String getRootPath() {
        return Env.externalFileDir.getAbsolutePath() + "/";
    }
}
