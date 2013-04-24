package com.app43.appclient.module.tabframe;

/**
 * 
 *  listView滑动网络问题 ,数据库app扫描对比没有做
 */

import com.alvin.api.utils.UMengAnalyseUtils;
import com.app43.appclient.module.abstracts.activity.BaseListViewWithExitActivity;

public class BAppManagerActivity extends BaseListViewWithExitActivity {
    //
    // // TODO 需重写抽象的方法如HotActivity
    // protected ArrayList<App> focusList = new ArrayList<App>();
    // protected List<App> oldFocusList = new ArrayList<App>(); //
    // 因为从第二页起没有focus对象
    // // 焦点图会为空,所以要把旧的对象赋值
    // protected View headerView; // 列表头部view
    // protected Gallery focusGallery;
    // protected ProgressBar progressBar;// 焦点图的菊花
    // protected FrameLayout headerLayout;
    // protected ImageListAdapter imageAdapter;
    // protected LinearLayout titleLayout;
    //
    // protected static int FOCUS_POSITION = 300;
    //
    // static Bundle instanceState;
    // protected static int titleFlag = 0;// 0:全部,1:游戏 2:软件 3:装机必备
    //
    // @Override
    // public void onCreate(Bundle savedInstanceState) {
    // // LogOutput.e(TAG, "onCreate");
    // instanceState = savedInstanceState;
    // String tempUrl = "";
    //
    // switch (titleFlag) {
    // case 0:
    // tempUrl = Settings.URL_RECOMMEND_ALL;
    // break;
    // case 1:
    // tempUrl = Settings.URL_RECOMMEND_GAME;
    // break;
    // case 2:
    // tempUrl = Settings.URL_RECOMMEND_SOFTWAVE;
    // break;
    // case 3:
    // tempUrl = Settings.URL_RECOMMEND_MUST;
    // break;
    //
    // default:
    // break;
    // }
    // LogOutput.i(TAG, "titleFlag:" + titleFlag + "||  tempurl:" + tempUrl);
    // setInitData("RecommendAppActivity",
    // R.layout.recommend_app_list_activity,
    // R.string.recommend_content_home_title, tempUrl);
    // super.onCreate(savedInstanceState);
    // }
    //
    // /**
    // * 添加listview头
    // */
    // protected void addHeaderView() {
    // headerView = mInflater
    // .inflate(R.layout.recommend_app_list_header, null);
    // headerLayout = (FrameLayout) headerView
    // .findViewById(R.id.show_picture_layout);
    // focusGallery = (Gallery) headerView.findViewById(R.id.show_picture);
    // progressBar = (ProgressBar) headerView
    // .findViewById(R.id.header_gallery_loadprogress);
    // focusGallery.setFadingEdgeLength(0);
    // listView.addHeaderView(headerView);
    //
    // headerLayout.setVisibility(View.VISIBLE);
    // focusGallery.setVisibility(View.VISIBLE);
    //
    // }
    //
    // @Override
    // protected void setupListView() {
    // listView = (ListView) findViewById(R.id.commend_app_listview);
    // listView.setScrollbarFadingEnabled(true);
    // listView.setOnScrollListener(new AbsListView.OnScrollListener() {
    // // 添加滚动条滚到最底部，加载余下的元素
    // public void onScrollStateChanged(AbsListView view, int scrollState) {
    // LogOutput.e(TAG, "footerVisible:" + footerViewIsVisible
    // + "||lastItem:" + lastItem + "||aListAdapter:"
    // + aListAdapter.getCount() + "\n" + "||serverAppList:"
    // + serverAppList.size());
    // if (footerViewIsVisible && aListAdapter.getCount() == lastItem) {
    // if (null != serverAppList && serverAppList.size() > 0) {
    // loadListItemAsy();
    // }
    // }
    // }
    //
    // public void onScroll(AbsListView view, int firstVisibleItem,
    // int visibleItemCount, int totalItemCount) {
    // lastItem = visibleItemCount + firstVisibleItem - 2;
    // LogOutput.e(TAG, "firstVisibleItem" + firstVisibleItem
    // + "||visibleItemCount" + visibleItemCount);
    // int firstPosition = firstVisibleItem == 0 ? 0
    // : firstVisibleItem - 1;
    // int lastPosition = lastItem;
    // if (positionListener != null)
    // positionListener
    // .positionChange(firstPosition, lastPosition);
    // }
    // });
    // addHeaderView();
    // addFooterView(); // 加载listview底部
    // }
    //
    // /**
    // * 配置整个view
    // */
    // @Override
    // public void setupViews() {
    // super.setupViews();
    // titleLayout = (LinearLayout)
    // findViewById(R.id.information_title_view_layout);
    // updateNaviView();
    // }
    //
    // @Override
    // public void handleView(String jsonString) {
    // Map<String, Object> inforMap = new HashMap<String, Object>();
    // try {
    // inforMap = ParseJsonUtils.getNormalApp(inforMap, jsonString);
    // inforMap = ParseJsonUtils.getFocusApp(inforMap, jsonString);
    // if (inforMap != null) {
    // if (inforMap.containsKey(Settings.JSON_APPS)) {
    // List<App> artList = (List<App>) inforMap
    // .get(Settings.JSON_APPS);
    // for (App itemInfo : artList) {
    // // 判断重复添加
    // serverAppList.add(itemInfo);
    // }
    // }
    // if (inforMap.containsKey(Settings.JSON_FOCUS)
    // && inforMap.get(Settings.JSON_FOCUS) != null) {
    // focusList = (ArrayList<App>) inforMap
    // .get(Settings.JSON_FOCUS);
    // // oldFocusList = focusList;
    // if (null != focusList && focusList.size() > 0) {
    // // 选中处理
    //
    // focusGallery
    // .setOnItemClickListener(new OnItemClickListener() {
    // @Override
    // public void onItemClick(
    // AdapterView<?> parent, View view,
    // int position, long id) {
    // // TODO 跳转webview
    // position = position % focusList.size();
    // ;
    // Intent intent = new Intent();
    // intent.setClass(
    // BAppManagerActivity.this,
    // FocusContentViewActivity.class);
    // intent.putParcelableArrayListExtra(
    // Settings.SERVERAPPLIST,
    // focusList);
    // intent.putExtra(Settings.POSITION,
    // position);
    // intent.putExtra(
    // Settings.ACTIVITY_FLAG,
    // Settings.FOUCS_ACTIVITY_REQ_CODE);
    // BAppManagerActivity.this
    // .startActivityForResult(
    // intent,
    // Settings.FOUCS_ACTIVITY_REQ_CODE);
    // }
    // });
    // imageAdapter = new ImageListAdapter(this, false);
    // focusGallery.setAdapter(imageAdapter);
    // focusGallery.setSelection(FOCUS_POSITION);
    // imageAdapter.updateImage(focusList, progressBar);
    // }
    // }
    // } else {
    // LogOutput.i(TAG, "informap is null");
    // }
    // // LogOutput.e(TAG, "serverAppList"+serverAppList.size());
    // // LogOutput.e(TAG, "aListAdapter"+aListAdapter.getCount());
    // if (serverAppList != null && serverAppList.size() > 0) {
    // isServerAppFinish = true;
    // if (aListAdapter.getCount() < serverAppList.size()) {
    // aListAdapter.setAppcount(serverAppList.size());
    // footLayout.setVisibility(View.VISIBLE);
    // footerViewIsVisible = true;
    // } else {
    // footLayout.setVisibility(View.GONE);
    // footerViewIsVisible = false;
    // }
    // // LogOutput.e(TAG, "isLocalAppFinish"+isLocalAppFinish);
    // // LogOutput.e(TAG, "isInstallAppFinish"+isInstallAppFinish);
    // // LogOutput.e(TAG, "isServerAppFinish"+isServerAppFinish);
    // if (isServerAppFinish && isLocalAppFinish && isInstallAppFinish) {
    // aListAdapter.updateList(serverAppList, isServerAppFinish,
    // localAppList, isLocalAppFinish, installAppList,
    // isInstallAppFinish, listProgressBar, downloadUrl,
    // pageNo);
    // }
    // }
    // } catch (JSONException e) {
    // e.printStackTrace();
    // LogOutput.i(TAG, "parse Json error");
    // // TODO 当翻到空的页数时有异常,待接口整理好有最大页数则可以避免
    // // http://mrobot.conline.com.cn/v2/cms/channels/999?pageNo=9为空的
    // footLayout.setVisibility(View.GONE);
    // footerViewIsVisible = false;
    // }
    // };
    //
    // /**
    // * 加载Title
    // */
    // protected int scrollWidth;
    // protected int navigationWidth;
    // protected int scrollRightDistance;
    //
    // public void updateNaviView() {
    // // LogOutput.e(TAG, "updateNaviView");
    // final List<RecommendTitle> recommendTitles = new
    // ArrayList<RecommendTitle>();
    // TITLES = setTitles(this);
    // DisplayMetrics dm;
    // dm = new DisplayMetrics();
    // getWindowManager().getDefaultDisplay().getMetrics(dm);
    // scrollWidth = (int) (dm.widthPixels - (this.getResources()
    // .getDimension(R.dimen.information_navi_arr_width)
    // + this.getResources().getDimension(
    // R.dimen.infromation_navi_arr_margin_left) + this
    // .getResources().getDimension(
    // R.dimen.information_navi_arr_margin_right)) * 2);
    // navigationWidth = scrollWidth / TITLES.size();
    // scrollRightDistance = navigationWidth * TITLES.size();
    // // 初始化Title
    // for (int i = 0; i < TITLES.size(); i++) {
    // final int index = i;
    // final RecommendTitle textView = new RecommendTitle(this);
    // textView.setText(TITLES.get(index).getTitle());
    // ViewGroup.MarginLayoutParams p = new ViewGroup.MarginLayoutParams(
    // navigationWidth, LinearLayout.LayoutParams.FILL_PARENT);
    // textView.setTextSize(this.getResources().getDimension(
    // R.dimen.title_text_size));
    // textView.setTextColor(Color.WHITE);
    // textView.setGravity(Gravity.CENTER);
    // textView.setTitleUrl(TITLES.get(index).getTitleUrl());
    // titleLayout.addView(textView, p);
    // recommendTitles.add(textView);
    // }
    // // 去掉列表头
    // headerLayout.setVisibility(View.GONE);
    // focusGallery.setVisibility(View.GONE);
    // progressBar.setVisibility(View.GONE);
    // recommendTitles.get(titleFlag).setBackgroundResource(
    // R.drawable.information_top_navi_current);
    // recommendTitles.get(titleFlag).setTextColor(
    // getResources().getColor(R.color.navi_text));
    // recommendTitles.get(titleFlag).setSelected(true);
    // for (int i = 0; i < recommendTitles.size(); i++) {
    // final int tempFlag = i;
    // RecommendTitle textView = recommendTitles.get(i);
    // textView.setOnClickListener(new View.OnClickListener() {
    // public void onClick(View v) {
    // titleFlag = tempFlag;
    // RecommendTitle tv = (RecommendTitle) v;
    // setTitleView(recommendTitles, tv);
    // downloadUrl = tv.getTitleUrl();
    // footerViewIsVisible = true;
    // pageNo = 1;
    // serverAppList.clear();
    // aListAdapter.setAppcount(0);// 设置count为0,则没有数据显示
    // if (footerViewIsVisible) {
    // footLayout.setVisibility(View.GONE);
    // footerViewIsVisible = false;
    // }
    // // 如果选中则高亮
    // if (tv.getText() != TITLES.get(0).getTitle()) {
    // headerLayout.setVisibility(View.GONE);
    // focusGallery.setVisibility(View.GONE);
    // progressBar.setVisibility(View.GONE);
    // } else {
    // headerLayout.setVisibility(View.VISIBLE);
    // focusGallery.setVisibility(View.VISIBLE);
    // }
    // focusGallery.setAdapter(imageAdapter);
    // listView.setAdapter(aListAdapter);
    // listProgressBar.setVisibility(View.VISIBLE);
    // // System.out.println("listscroll: change column");
    // sendJsonData(tv.getTitleUrl(), pageNo, getJsonHandler,
    // BAppManagerActivity.this, false);
    // listView.setSelection(0);
    // }
    // });
    // }
    // }
    //
    // /**
    // * 设置选中title背景
    // *
    // * @param v
    // */
    // public void setTitleView(List<RecommendTitle> titleList, RecommendTitle
    // v) {
    // for (int i = 0; i < titleList.size(); i++) {
    // if (titleList.get(i).getText().equals(v.getText())) {
    // titleList.get(i).setBackgroundResource(
    // R.drawable.information_top_navi_current);
    // titleList.get(i).setTextColor(
    // getResources().getColor(R.color.navi_text));
    // titleList.get(i).setSelected(true);
    // } else {
    // titleList.get(i).setBackgroundResource(0);
    // titleList.get(i).setTextColor(Color.WHITE);
    // titleList.get(i).setSelected(false);
    // }
    // }
    // }
    //
    // public List<RecommendTitle> TITLES;
    //
    // public List<RecommendTitle> setTitles(Context mContext) {
    // List<RecommendTitle> titles = new ArrayList<RecommendTitle>();
    // RecommendTitle title = new RecommendTitle(mContext);
    // title.setTitle("下载管理");
    // title.setTitleUrl("http://mrobot.pconline.com.cn/v2/cms/channels/999");
    // titles.add(title);
    // title = new RecommendTitle(mContext);
    // title.setTitle("安装管理");
    // title.setTitleUrl("http://mrobot.pconline.com.cn/v2/cms/channels/3");
    // titles.add(title);
    // return titles;
    // }
    //
    // @Override
    // protected void onActivityResult(int requestCode, int resultCode, Intent
    // data) {
    // super.onActivityResult(requestCode, resultCode, data);
    // // LogOutput.e(TAG,
    // // "onActivityResult"+data.getExtras().getInt(Settings.POSITION));
    // if (resultCode == Settings.ACTIVITY_RESULT) {
    // if (requestCode == Settings.FOUCS_ACTIVITY_REQ_CODE) {
    // // LogOutput.e(TAG,
    // // "onActivityResult"+data.getExtras().getInt(Settings.POSITION));
    // imageAdapter.updateImage(focusList, progressBar);
    // focusGallery.setSelection(data.getExtras().getInt(
    // Settings.POSITION)
    // + focusList.size() * 100);
    // aListAdapter.updateList(serverAppList, isServerAppFinish,
    // localAppList, isLocalAppFinish, installAppList,
    // isInstallAppFinish, listProgressBar, downloadUrl,
    // pageNo);
    // }
    // }
    // }

    @Override
    protected void onResume() {
        super.onResume();
        UMengAnalyseUtils.onResume(this);
        if (!insideActivity) {
            isInstallAppFinish = false;
            readInstallApp();
            // onCreate(instanceState);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        insideActivity = false;
        UMengAnalyseUtils.onPause(this);
    }
    // @Override
    // protected void onPause() {
    // super.onPause();
    // new InstallAppUtils.readInstallApp(this, installAppHandler).start();
    // }
}
