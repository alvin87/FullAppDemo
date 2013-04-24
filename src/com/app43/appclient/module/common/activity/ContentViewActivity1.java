package com.app43.appclient.module.common.activity;

/**
 * 通用列表app的终端页
 */
import android.widget.Toast;

public class ContentViewActivity1 extends FocusContentViewActivity1 {
    protected String pageUrl = "";
    protected int pageNo = -1;
    protected boolean isTurnPage = false;// 是否翻页了
    Toast toast;

    // @Override
    // public void onCreate(Bundle savedInstanceState) {
    // TAG = "ContentViewActivity";
    // super.onCreate(savedInstanceState);
    // }
    //
    // @Override
    // protected void setPageUrl(Bundle bundle) {
    // pageNo = bundle.getInt(Settings.PAGENO);
    // pageUrl = bundle.getString(Settings.PAGEURL);
    // }
    //
    // @Override
    // protected void setPreNext() {
    // pageUpImage.setOnClickListener(new View.OnClickListener() {
    // public void onClick(View v) {
    // // TODO 上一个app
    // // plusScore();
    // isTurnPage = true;
    // position -= 1;
    // if (position >= 0) {
    // contentUrl = urlHead + serverAppList.get(position).getId();
    // articleWebView.loadUrl(contentUrl);
    // } else {
    // if (toast != null) {
    // toast.cancel();
    // }
    // // toast.makeText(ContentViewActivity.this, "已经是第一个",
    // // toast.LENGTH_SHORT).show();
    // showText(ContentViewActivity1.this, "已经是第一个");
    // position = 0;
    // }
    // canNextPage = true;
    // appid = (Integer) serverAppList.get(position).getId();
    // // setFavImage(checkIsFav(appid));
    // }
    // });
    // pageDownImage.setOnClickListener(new View.OnClickListener() {
    // public void onClick(View v) {
    // // TODO 下一个app
    // isTurnPage = true;
    // position += 1;
    // if (position < serverAppList.size()) {
    // contentUrl = urlHead + serverAppList.get(position).getId();
    // articleWebView.loadUrl(contentUrl);
    // appid = (Integer) serverAppList.get(position).getId();
    // // setFavImage(checkIsFav(appid));
    // // plusScore();
    // } else {
    // // 如果是翻到下一页则在handView获取appid
    // if (canNextPage) {
    // pageNo += 1;
    // canNextPage = false;
    // ContentViewActivity1.this
    // .sendJsonData(pageUrl, pageNo, getJsonHandler,
    // ContentViewActivity1.this, false);
    // } else {
    // position = serverAppList.size();
    // showText(ContentViewActivity1.this, "正在获取,请稍后...");
    // }
    // }
    // }
    // });
    // }
    //
    // /**
    // * 下一页时调用
    // */
    // Handler getJsonHandler = new Handler() {
    // public void handleMessage(Message msg) {
    // // LogOutput.e(TAG, ""+msg.what);
    // String jsonString = ParseJsonUtils
    // .getJsonStrByFile((FileInputStream) msg.obj);
    // if (jsonString == null) {
    // LogOutput.i(TAG, "parse Json null");
    // showError();
    // return;
    // }
    // switch (msg.what) {
    // case Settings.JSON_SUCCESS:
    // handleView(jsonString);
    // break;
    // case Settings.JSON_NET_ERROR:
    // showError();
    // break;
    // default:
    // break;
    // }
    // };
    // };
    //
    // @Override
    // public void handleView(String jsonString) {
    // Map<String, Object> inforMap = new HashMap<String, Object>();
    // try {
    // inforMap = ParseJsonUtils.getNormalApp(inforMap, jsonString);
    // if (inforMap != null) {
    // if (inforMap.containsKey(Settings.JSON_APPS)) {
    // canNextPage = true;
    // List<App> artList = (List<App>) inforMap
    // .get(Settings.JSON_APPS);
    // for (App itemInfo : artList) {
    // // 判断重复添加
    // serverAppList.add(itemInfo);
    // }
    // }
    // } else {
    // LogOutput.i(TAG, "informap is null");
    // }
    //
    // // LogOutput.e(TAG, "serverAppList"+serverAppList.size());
    // if (serverAppList != null && serverAppList.size() > 0
    // && position < serverAppList.size()) {
    // contentUrl = urlHead + serverAppList.get(position).getId();
    // if (!canNextPage) {// 证明此时点击到下一页
    // articleWebView.loadUrl(contentUrl);
    // }
    // appid = (Integer) serverAppList.get(position).getId();
    // setFavImage(checkIsFav(appid));
    // }
    //
    // } catch (Exception e) {
    // e.printStackTrace();
    // LogOutput.i(TAG, "handleView error");
    // }
    // }
    //
    // @Override
    // public void finish() {
    // Intent intent = new Intent();
    // intent.putExtra(Settings.POSITION, position);
    // intent.putParcelableArrayListExtra(Settings.SERVERAPPLIST,
    // serverAppList);
    // // LogOutput.e("listAdapter" ,
    // // "name"+serverAppList.get(0).getName()+"isTurnPage"+isTurnPage);
    // intent.putExtra(Settings.PAGEURL, pageUrl);
    // intent.putExtra(Settings.PAGENO, pageNo);
    // intent.putExtra(Settings.ISTURN_NEXT, isTurnPage);
    // setResult(Settings.ACTIVITY_RESULT, intent);
    // super.finish();
    // }
}
