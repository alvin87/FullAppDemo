package com.app43.appclient.module.common.activity;

/**
 * 针对焦点图app的终端页   webview,以后可能用到
 * 
 */
import com.app43.appclient.bean.App;
import com.app43.appclient.module.abstracts.activity.SendDataBaseActivity;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.params.HttpParams;

import android.os.Handler;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class FocusContentViewActivity1 extends SendDataBaseActivity {
    protected String TAG = "FocusContentViewActivity";
    protected TextView articlePageNumText;
    protected WebView articleWebView;
    protected final String mimeType = "text/html";
    protected final String encoding = "utf-8";
    protected String articleStr;
    protected String favouriteStr;
    protected ImageView favouriteCircleImg;
    protected TextView favouriteTotal, titleTextView;
    protected FrameLayout favouriteFrameLayout;
    protected ImageView pageUpImage;
    protected ImageView pageDownImage;
    protected ImageView favImage;

    protected int pageIndex = 0;
    protected ProgressBar articleLoadingProgressBar;
    protected TextView defaultMessage;

    protected boolean allowGetPage;
    protected String cmtSettingStr;

    protected String ACCESS_ARTICLE_INT = "intf-mrobot-article";
    protected HttpGet request;
    protected HttpParams params;
    protected HttpResponse response;
    protected HttpClient httpClient;
    protected boolean isJumpCommentsActivity;
    protected boolean isFullScreen = false;
    protected FrameLayout topLayout;
    protected RelativeLayout bottomLayout;
    protected FrameLayout centerLayout;
    protected String[] pageName;
    protected int appid = -1;// 当前app的id
    protected String contentUrl = "", downloadUrl = "";
    protected String urlHead = "http://mrobot.pconline.com.cn/v2/cms/articles/";
    protected ArrayList<App> serverAppList = new ArrayList<App>();
    protected int position = -1; // 当前app在列表中的位置
    protected boolean isAlreadyFav = false;// 是否已经收藏
    int activityFlag = -1; // 判别是那个Activity传递的标记
    boolean canNextPage = true;// 用于判断是否可以点击下一页,避免在等待下一页数据时,用户猛点下一页

    @Override
    protected void handleViews(String jsonString) {

    }

    @Override
    public void setupViews() {

    }

    @Override
    protected Handler initHandle() {
        return null;
    }

    // @Override
    // public void onCreate(Bundle savedInstanceState) {
    //
    // setInitData();
    // Thread update = new Thread(updateThread);
    // update.start();
    // super.onCreate(savedInstanceState);
    // // plusScore();
    // }
    //
    // protected void setInitData() {
    // Bundle bundle = this.getIntent().getExtras();
    // if (bundle != null) {
    // position = bundle.getInt(Settings.POSITION);
    // serverAppList = bundle
    // .getParcelableArrayList(Settings.SERVERAPPLIST);
    // appid = (Integer) serverAppList.get(position).getId();
    //
    // // TODO 修改url
    // contentUrl = urlHead + serverAppList.get(position).getId();
    // activityFlag = bundle.getInt(Settings.ACTIVITY_FLAG);
    // setPageUrl(bundle);
    // // LogOutput.e(TAG, "serverAppList:" +
    // // serverAppList.get(position).getId());
    // // LogOutput.e(TAG, "serverAppList:" +
    // // serverAppList.get(position).getName());
    // }
    // }
    //
    // // 用户行为分析规则
    // protected void plusScores() {
    // Editor editor;
    // long score;
    // SharedPreferences sharedPreferences = getSharedPreferences(
    // Settings.USERINFO_COLLECTION, MODE_PRIVATE);
    // if (position < serverAppList.size()) {
    // score = sharedPreferences.getLong(""
    // + serverAppList.get(position).getCategory_id(), 0);
    // LogOutput.e(TAG, "getCategory_id"
    // + serverAppList.get(position).getCategory_id() + "score"
    // + score);
    //
    // score += 1;
    // dbUser_behavior.inserts(serverAppList.get(position)
    // .getCategory_id(), score);
    // editor = sharedPreferences.edit();
    // editor.putLong("" + serverAppList.get(position).getCategory_id(),
    // score);
    // editor.commit();
    // }
    // }
    //
    // /**
    // * setPageUrl(除了焦点图进来都要设置列表URL)
    // */
    // protected void setPageUrl(Bundle bundle) {
    // }
    //
    // protected void setupWebView() {
    // articleWebView.setWebViewClient(new WebViewClient() {
    // public boolean shouldOverrideUrlLoading(WebView view, String url) {
    // try {
    // // 新网页也是通过此webview打开
    // articleWebView.loadUrl(url);
    // articleWebView.clearFocus();
    // return true;
    // } catch (Exception e) {
    // e.printStackTrace();
    // }
    // return false;
    // }
    //
    // public void onPageStarted(WebView webView, String url,
    // Bitmap favicon) {
    // super.onPageStarted(webView, url, favicon);
    // allowGetPage = false;
    // plusScores();
    // CopyOfFocusContentViewActivity.this.articleLoadingProgressBar
    // .setVisibility(View.VISIBLE);
    // }
    //
    // public void onPageFinished(WebView webView, String url) {
    // super.onPageFinished(webView, url);
    // allowGetPage = true;
    // setTitle();
    // setFavImage(checkIsFav(appid));
    // CopyOfFocusContentViewActivity.this.articleLoadingProgressBar
    // .setVisibility(View.INVISIBLE);
    // }
    //
    // public void onReceivedError(WebView view, int errorCode,
    // String description, String failingUrl) {
    // CopyOfFocusContentViewActivity.this.articleLoadingProgressBar
    // .setVisibility(View.INVISIBLE);
    // articleWebView.setVisibility(View.INVISIBLE);
    // defaultMessage.setVisibility(View.VISIBLE);
    // }
    // });
    // }
    //
    // protected void setTitle() {
    // if (canNextPage) {
    // titleTextView.setText(serverAppList.get(position).getTitle());
    // }
    //
    // }
    //
    // @Override
    // public void setupViews() {
    //
    // Display display = getWindowManager().getDefaultDisplay();
    // int ScreenDipHeight = DisplayUtils.convertPX2DIP(this,
    // display.getHeight());
    // int tabContentHeight = ScreenDipHeight -
    // Settings.CONTENTVIEW_TOPBOTTOM+1; // 设置tab的Activity的高度
    // // LinearLayout layout = (LinearLayout) this.getLayoutInflater()
    // // .from(this).inflate(R.layout.recommend_content_activity, null);
    // setContentView(R.layout.recommend_content_activity);
    // ScrollView scrollView = (ScrollView)
    // findViewById(R.id.contentView_scrollView);
    // scrollView.setLayoutParams(new LinearLayout.LayoutParams(
    // LinearLayout.LayoutParams.FILL_PARENT, DisplayUtils
    // .convertDIP2PX(this, tabContentHeight)));
    // titleTextView = (TextView) this
    // .findViewById(R.id.recommend_content_title);
    // setTitle();
    // defaultMessage = (TextView) this
    // .findViewById(R.id.recommend_article_default_message);
    // articlePageNumText = (TextView) this
    // .findViewById(R.id.recommend_article_current_page_num);
    // articleLoadingProgressBar = (ProgressBar) this
    // .findViewById(R.id.article_loading_progress);
    // topLayout = (FrameLayout) this.findViewById(R.id.recommend_article_top);
    // centerLayout = (FrameLayout) this
    // .findViewById(R.id.recommend_article_center_layout);
    // bottomLayout = (RelativeLayout) this
    // .findViewById(R.id.recommend_article_bottom);
    // articleWebView = (WebView) this
    // .findViewById(R.id.recommend_article_webview);
    //
    // articleWebView.setScrollbarFadingEnabled(true);
    // articleWebView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
    // WebSettings webSettings = articleWebView.getSettings();
    // webSettings.setBuiltInZoomControls(true);
    // webSettings.setJavaScriptEnabled(true);
    // articleWebView.addJavascriptInterface(this, "client");
    // pageUpImage = (ImageView)
    // findViewById(R.id.recommend_article_pageup_img);
    // pageDownImage = (ImageView)
    // findViewById(R.id.recommend_article_pagedown_img);
    // favouriteFrameLayout = (FrameLayout) this
    // .findViewById(R.id.recommend_article_comments_layout);
    // favImage = (ImageView) findViewById(R.id.recommend_article_comments_msg);
    // setFavImage(checkIsFav(appid));
    // // 分享到微博
    // findViewById(R.id.infomation_acticle_bottom_share).setOnClickListener(
    // new OnClickListener() {
    // public void onClick(View arg0) {
    // // 需要网页有用到这个js方法才可以调用.
    // // System.out.println(">>s<<");
    // // articleWebView
    // // .loadUrl("javascript:window.webview.jsReturn('token', firstPic())");
    // showWeibo("");
    // }
    // });
    // favouriteFrameLayout.setOnClickListener(new View.OnClickListener() {
    // public void onClick(View v) {
    // // TODO 收藏夹按钮
    // // addOrCancleFavorites(articleId);
    // if (isAlreadyFav) {
    // if (checkIsFav(appid)) {
    // if (dbFavourite.delete(appid)) {
    // favImage.setBackgroundResource(R.drawable.fav_default);
    // Toast.makeText(CopyOfFocusContentViewActivity.this,
    // "删除成功", Toast.LENGTH_SHORT).show();
    // } else {
    // Toast.makeText(CopyOfFocusContentViewActivity.this,
    // "删除失败,请重新操作", Toast.LENGTH_LONG).show();
    // }
    // }
    // } else {
    // if (appid > 0) {
    // if (dbFavourite.insert(appid)) {
    // favImage.setBackgroundResource(R.drawable.fav_current);
    // Toast.makeText(CopyOfFocusContentViewActivity.this,
    // "收藏成功", Toast.LENGTH_SHORT).show();
    // } else {
    // Toast.makeText(CopyOfFocusContentViewActivity.this,
    // "收藏失败,请重新操作", Toast.LENGTH_LONG).show();
    // }
    // }
    // }
    // isAlreadyFav = !isAlreadyFav;
    // }
    // });
    // setupWebView();
    // setPreNext();
    // }
    //
    // protected void setPreNext() {
    // pageUpImage.setOnClickListener(new View.OnClickListener() {
    // public void onClick(View v) {
    // // TODO 上一个app
    // // prevPage();
    // position -= 1;
    // if (position < 0) {
    // position = serverAppList.size() - 1;
    // } else {
    // // plusScore();
    // }
    // appid = (Integer) serverAppList.get(position).getId();
    // setFavImage(checkIsFav(appid));
    // contentUrl = urlHead + serverAppList.get(position).getId();
    // articleWebView.loadUrl(contentUrl);
    //
    // }
    // });
    //
    // pageDownImage.setOnClickListener(new View.OnClickListener() {
    // public void onClick(View v) {
    // // TODO 下一个app
    // // nextPage();
    //
    // position += 1;
    // if (position >= serverAppList.size()) {
    // position = 0;
    // }
    // // plusScore();
    // appid = (Integer) serverAppList.get(position).getId();
    // setFavImage(checkIsFav(appid));
    // contentUrl = urlHead + serverAppList.get(position).getId();
    // articleWebView.loadUrl(contentUrl);
    // }
    // });
    //
    // }
    //
    // /**
    // * 获取app详情
    // *
    // * @return 1 成功， -1 网络故障，-2 获取数据失败
    // */
    // public int getArticleInfor() {
    // int status = 0;
    // StringBuffer textBuff = new StringBuffer();
    // // LogOutput.e(TAG, "contentUrlRequest:" + contentUrl);
    // request = new HttpGet(contentUrl);
    // request.addHeader("User-Agent", Env.client + " " + Env.versionName);
    // request.addHeader("Accept-Encoding", "gzip, deflate");
    // params = new BasicHttpParams();
    // HttpConnectionParams.setConnectionTimeout(params, 10 * 1000);// 连接超时设置
    // HttpConnectionParams.setSoTimeout(params, 20 * 1000);// 获取获取数据超时
    // httpClient = new DefaultHttpClient(params);
    // InputStream is = null;
    // try {
    // response = httpClient.execute(request);
    // if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
    // LogOutput.i(TAG, "response article information succeed");
    // is = response.getEntity().getContent();
    // Header contentEncoding = response
    // .getFirstHeader("Content-Encoding");
    // Header xPage = response.getFirstHeader("X-Page");
    // if (contentEncoding != null
    // && contentEncoding.getValue().equalsIgnoreCase("gzip")) {
    // is = new GZIPInputStream(is);
    // }
    // BufferedReader bufferReader = new BufferedReader(
    // new InputStreamReader(is));
    // String lineStr;
    // while ((lineStr = bufferReader.readLine()) != null) {
    // textBuff.append(lineStr + "\r\n");
    // }
    // articleStr = textBuff.toString();
    // // LogOutput.e(TAG, "articleStrResponse:" + articleStr);
    // status = 1;
    // } else {
    // status = -1;
    // }
    // if (is != null) {
    // is.close();
    // }
    // } catch (Exception e) {
    // status = -2;
    // LogOutput.i("FocusContentViewActivity",
    // "online return articleInfor fail");
    // e.printStackTrace();
    // }
    //
    // return status;
    // }
    //
    // /**
    // * 获取文章信息
    // */
    // protected Runnable updateThread = new Runnable() {
    // public void run() {
    // int status = getArticleInfor();
    // if (status == 1) {
    // // if(null!=articleStr && !"".equals(articleStr)){
    //
    // Message message1 = new Message();
    // message1.what = 0;
    // message1.obj = articleStr;
    // handler.sendMessage(message1);
    // // }
    // } else {
    // Message message = new Message();
    // message.what = 8;
    // message.obj = status;
    // handler.sendMessage(message);
    // }
    // }
    // };
    //
    // // 处理消息
    // protected Handler handler = new Handler() {
    // public void handleMessage(Message msg) {
    // super.handleMessage(msg);
    // switch (msg.what) {
    // case 0:
    // articleWebView.loadDataWithBaseURL(null, articleStr, mimeType,
    // encoding, null);
    // // articleWebView.loadUrl("file:///android_asset/appContent.html");
    // break;
    // case 2:// 网络连接失败
    // articleLoadingProgressBar.setVisibility(View.INVISIBLE);
    // articleWebView.setVisibility(View.INVISIBLE);
    // defaultMessage.setVisibility(View.VISIBLE);
    // //
    // displayToastMessage(ContentViewActivity.this.getResources().getString(R.string.hit_network_failure));
    // break;
    // case 3:// 解析json失败
    // showError();
    // break;
    // case 8:// 获取文章infor失败
    // switch (((Integer) msg.obj).intValue()) {
    // case -1:
    // articleLoadingProgressBar.setVisibility(View.INVISIBLE);
    // articleWebView.setVisibility(View.INVISIBLE);
    // defaultMessage.setVisibility(View.VISIBLE);
    // break;
    // case -2:
    // showError();
    // break;
    // }
    // break;
    // }
    // }
    // };
    //
    // public void shareWeibo(String fileUrl) {
    // // 网络不通，提示并返回
    // if (!NetworkUtils.isNetworkAvailable(getApplicationContext())) {
    // Toast.makeText(CopyOfFocusContentViewActivity.this, "未找到网络连接！",
    // Toast.LENGTH_SHORT).show();
    // return;
    // }
    //
    // if (fileUrl == null || "".equals(fileUrl)) {
    // Toast.makeText(CopyOfFocusContentViewActivity.this, "正在加载中,请稍后再试.",
    // Toast.LENGTH_SHORT).show();
    // return;
    // }
    // showWeibo(fileUrl);
    // }
    //
    // /**
    // * 跳转到微博分享页面 分享Activity有bug
    // */
    //
    // public void showWeibo(String fileUrl) {
    // // System.out.println("分享页面");
    // // Share.share(this, "《" + "标题" + "》 " + "网页地址" + " 分享来自"
    // // + getString(R.string.pcgroup_topic));
    //
    // Intent intent = new Intent();
    // intent.putExtra("share_content", " Test");
    // Share.share(this, intent);
    // }
    //
    // public void jsReturn(String token, String data) {
    // System.out.println("webView调用");
    // if ("token".equals(token)) {
    // shareWeibo(data);
    // }
    // }
    //
    // @Override
    // protected void onDestroy() {
    // super.onDestroy();
    // // if (dbFavourite != null) {
    // // dbFavourite.close();
    // // }
    // // if(dbUser_behavior!=null){
    // // dbUser_behavior.close();
    // // }
    // }
    //
    // /**
    // * 是否已经是收藏夹里面的app
    // */
    // protected void setFavImage(boolean isFav) {
    // if (isFav) {
    // isAlreadyFav = true;
    // favImage.setBackgroundResource(R.drawable.fav_current);
    // } else {
    // isAlreadyFav = false;
    // favImage.setBackgroundResource(R.drawable.fav_default);
    // }
    // }
    //
    // /**
    // * 检测是否已经是收藏了的app
    // *
    // * @param id
    // * @return
    // */
    // public boolean checkIsFav(int id) {
    // boolean flag = false;
    // int[] favIds = dbFavourite.getAppID();
    // for (int i = 0; i < favIds.length; i++) {
    // // LogOutput.e("FocusContentViewActivity", "id:" + id + "||fav:" +
    // // favIds[i]);
    // if (id == favIds[i]) {
    // return true;
    // }
    // }
    // return flag;
    // }
    //
    // /**
    // * 屏蔽菜单键
    // */
    // @Override
    // public boolean onKeyDown(int keyCode, KeyEvent event) {
    // if (keyCode == KeyEvent.KEYCODE_BACK) {
    // onBackPressed();
    // return true;
    // }
    // return false;
    // }
    //
    // @Override
    // protected void handleView(String jsonString) {
    // }
    //
    // @Override
    // public void finish() {
    // if (activityFlag == Settings.FOUCS_ACTIVITY_REQ_CODE) {
    // Intent intent = new Intent();
    // intent.putExtra(Settings.POSITION, position);
    // intent.putParcelableArrayListExtra(Settings.SERVERAPPLIST,
    // serverAppList);
    // // LogOutput.e("focusAdapter" ,
    // // "name"+serverAppList.get(position).getName());
    // setResult(Settings.ACTIVITY_RESULT, intent);
    // }
    // super.finish();
    // }
    //
    // public void showPic(String images, int position) {
    // Intent intent = new Intent();
    // intent.setClass(this, BigImageActivity.class);
    // intent.putExtra(Settings.BIG_IMAGE_URL, images);
    // intent.putExtra(Settings.BIG_IMAGE_POSITION, position);
    // startActivity(intent);
    // }
}