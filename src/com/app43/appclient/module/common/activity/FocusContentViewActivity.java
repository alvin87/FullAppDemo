package com.app43.appclient.module.common.activity;

/**
 * 针对焦点图app的终端页 
 * 
 */
import cn.com.pcgroup.common.android.share.Share;
import cn.com.pcgroup.common.android.utils.DisplayUtils;
import cn.com.pcgroup.common.android.utils.NetworkUtils;

import com.alvin.api.utils.AsyncImageLoader;
import com.alvin.api.utils.AsyncImageLoader.ImageCallback;
import com.alvin.api.utils.LogOutputUtils;
import com.alvin.api.utils.SettingsUtils;
import com.alvin.api.utils.UMengAnalyseUtils;
import com.app43.appclient.R;
import com.app43.appclient.bean.App;
import com.app43.appclient.module.abstracts.activity.BaseActivity;
import com.app43.appclient.module.abstracts.activity.BaseListViewActivity;
import com.app43.appclient.module.abstracts.activity.SendDataBaseActivity;
import com.app43.appclient.module.adapter.BaseOnClickListItemAdapter.ProgressData;
import com.app43.appclient.module.adapter.ProgressListViewAdapter;
import com.app43.appclient.module.adapter.ProgressListViewAdapter.InstallReceiver;
import com.app43.appclient.module.receive.ProgressChangeReceive;
import com.app43.appclient.module.receive.ProgressChangeReceive.DataChange;
import com.app43.download.test.ClickAdapter;

import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FocusContentViewActivity extends SendDataBaseActivity {
    protected String TAG = FocusContentViewActivity.class.getSimpleName();
    protected TextView titleTextView;
    protected ProgressBar progressBar;// 下载菊花圈

    protected int appid = -1;// 当前app的id
    protected ArrayList<App> serverAppList = new ArrayList<App>();
    protected int position = -1; // 当前app在列表中的位置
    protected boolean isAlreadyFav = false;// 是否已经收藏
    int activityFlag = -1; // 判别是那个Activity传递的标记

    String flag;// 程序状态:请安装还是请更新还是下载
    ProgressChangeReceive progressChangeReceive;
    Button downloadButton;
    App app;
    int contentProgress;
    public static InstallReceiver installReceiver;// 终端页使用的安装apk广播接收
    ContentViewHolder viewHolder;

    TextView nameTextView, kindTextView, sizeTextView, downloadTimesTextView,
            summaryTextView, detailTextView, moreTextView, verNameTextView,
            companyTextView, pushDateTextView, safeTextView;
    // BigImageGallery gallery;
    Button moreButton;
    ImageView iconImageView;
    boolean isMore = true;
    String gallString = "http://i3.3conline.com/images/piclib/201111/01/batch/1/115722/1320140337832fmgbtltot7_medium.jpg,"
            + "http://i1.3conline.com/images/piclib/201111/01/batch/1/115722/1320140337832migxz41op3_medium.jpg,"
            + "http://i3.3conline.com/images/piclib/201111/01/batch/1/115722/1320140337832wen4qffuls_medium.jpg,"
            + "http://i4.3conline.com/images/piclib/201111/01/batch/1/115722/13201403378328p1om32nwu_medium.jpg,"
            + "http://i1.3conline.com/images/piclib/201111/01/batch/1/115722/1320140337832hl8axegdsj_medium.jpg";

    // GalleryListAdapter galleryListAdapter;
    List<String> galleryList = new ArrayList<String>();
    List<ImageView> pointImageList = new ArrayList<ImageView>();
    int safe;
    TableRow tableRow;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        BaseListViewActivity.isChange = BaseListViewActivity.noChange;
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            // TODO flag可以不用的
            flag = bundle.getString(SettingsUtils.CONTENT_TEXT);
        }
        setInitData();
        super.onCreate(savedInstanceState);
        setUpDataChange();
        plusScores();
    }

    private void setUpDataChange() {
        DataChange dataChange = new DataChange() {
            @Override
            public void cancle(String apkName) {
                // 如果是取消按钮的广播就直接更新

                ProgressListViewAdapter.dbDelete(apkName,
                        "终端页接收到ClickAdapter的广播点击取消下载线程");
                // setAppManaActivity(apkName);
                ProgressListViewAdapter.removeProgreMap(apkName);
            }

            @Override
            public void OnDataChange(String sApkName, int iProgress,
                    Long lCurrentTime) {
                LogOutputUtils.i(TAG + ":onreceive", sApkName + "size:"
                        + serverAppList.size());

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
                    ProgressListViewAdapter.removeProgreMap(tempName);
                    ProgressChangeReceive.removeCount(tempName,
                            FocusContentViewActivity.this);

                    LogOutputUtils.e(TAG, "下载完成" + tempName);
                    ProgressListViewAdapter.dbMap = BaseActivity.dbApp_download
                            .getAllDownloadApk();
                } else {
                    ProgressData progressData = new ProgressData();
                    progressData.setProgress(iProgress);
                    progressData.setApkName(sApkName);
                    ProgressListViewAdapter.progressMap.put(
                            progressData.getApkName(), progressData);
                }

                updateProgress(downloadButton, viewHolder.progressBar,
                        iProgress, true, viewHolder.textView);
            }
        };

        progressChangeReceive = new ProgressChangeReceive(dataChange);
        IntentFilter filter = new IntentFilter();
        filter.addAction(SettingsUtils.PROGRESS_BROADCAST_RECEIVE);
        this.registerReceiver(progressChangeReceive, filter);
    }

    /**
     * 作用:更新进度
     */
    private void updateProgress(Button button, ProgressBar progressBar,
            int progress, boolean isLoading, TextView textView) {
        LogOutputUtils.e(TAG, "flag_text:"
                + ProgressListViewAdapter.contentNameMap.get(app.getTitle()));

        ProgressListViewAdapter.setContentBtnIcon(button, app, this, progress,
                viewHolder);
    }

    protected void setInitData() {
        Bundle bundle = this.getIntent().getExtras();
        if (bundle != null) {
            position = bundle.getInt(SettingsUtils.POSITION);
            serverAppList = bundle
                    .getParcelableArrayList(SettingsUtils.SERVERAPPLIST);
            appid = (Integer) serverAppList.get(position).getId();
            app = serverAppList.get(position);
            contentProgress = ProgressListViewAdapter.getProgress(app
                    .getTitle());
            activityFlag = bundle.getInt(SettingsUtils.ACTIVITY_FLAG);

            gallString = app.getGallery_url();
            galleryList = getAppList(gallString);
            LogOutputUtils.e(TAG, "pro:" + contentProgress);
            LogOutputUtils.e(TAG, "appdetail : " + app.getApp_detail());

            UMengAnalyseUtils.onEvents(this, UMengAnalyseUtils.VIEW_TOTALs,
                    app.getTitle());
            // LogOutput.e(TAG, "serverAppList:" +
            // serverAppList.get(position).getId());
            // LogOutput.e(TAG, "serverAppList:" +
            // serverAppList.get(position).getName());
        }
    }

    // 用户行为分析规则
    protected void plusScores() {
        // Editor editor;
        long score;
        // SharedPreferences sharedPreferences = getSharedPreferences(
        // Settings.USERINFO_COLLECTION, MODE_PRIVATE);
        if (position < serverAppList.size()) {
            // score = sharedPreferences.getLong(""
            // + serverAppList.get(position).getCategory_id(), 0);

            Map<Long, Long> map = dbUser_behavior.getAllBehavior();
            if (map.containsKey(serverAppList.get(position).getCategory_id())) {
                score = map.get(serverAppList.get(position).getCategory_id());
            } else {
                score = 0;
            }

            // LogOutput.e(TAG, "getCategory_id"
            // + serverAppList.get(position).getCategory_id() + "score"
            // + score);

            score += 1;
            dbUser_behavior.inserts(serverAppList.get(position)
                    .getCategory_id(), score);
            // editor = sharedPreferences.edit();
            // editor.putLong("" + serverAppList.get(position).getCategory_id(),
            // score);
            // editor.commit();
        }
    }

    protected void setTitle() {
        titleTextView.setText(serverAppList.get(position).getTitle());
    }

    @Override
    public void setupViews() {

        Display display = getWindowManager().getDefaultDisplay();
        int ScreenDipHeight = DisplayUtils.convertPX2DIP(this,
                display.getHeight());
        int tabContentHeight = ScreenDipHeight
                - SettingsUtils.CONTENTVIEW_TOPBOTTOM + 1; // 设置tab的Activity的高度
        viewHolder = new ContentViewHolder();
        setContentView(R.layout.recommend_content_activity);
        ScrollView scrollView = (ScrollView) findViewById(R.id.contentView_scrollView);
        scrollView.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.FILL_PARENT, DisplayUtils
                        .convertDIP2PX(this, tabContentHeight)));
        titleTextView = (TextView) this
                .findViewById(R.id.recommend_content_title);
        progressBar = (ProgressBar) this
                .findViewById(R.id.content_loadprogress);
        viewHolder.favImageView = (ImageView) findViewById(R.id.content_favourite);
        viewHolder.progressBar = (ProgressBar) findViewById(R.id.content_progressbar);
        downloadButton = (Button) findViewById(R.id.content_btn_download);
        viewHolder.textView = (TextView) findViewById(R.id.content_tv_progress);
        viewHolder.delImageView = (ImageView) findViewById(R.id.content_btn_del);
        viewHolder.linearLayoutRightProgress = (LinearLayout) findViewById(R.id.content_bottom_right_progress);
        kindTextView = (TextView) findViewById(R.id.contentView_app_text_kind_x);
        sizeTextView = (TextView) findViewById(R.id.contentView_app_text_size_x);
        downloadTimesTextView = (TextView) findViewById(R.id.contentView_app_text_download_x);
        summaryTextView = (TextView) findViewById(R.id.contentView_app_dp_text);
        detailTextView = (TextView) findViewById(R.id.contentView_app_intro_text);
        tableRow = (TableRow) findViewById(R.id.content_row);
        moreButton = (Button) findViewById(R.id.contentView_app_btn_more);
        iconImageView = (ImageView) findViewById(R.id.contentView_app_icon);
        safeTextView = (TextView) findViewById(R.id.contentView_safe);
        verNameTextView = (TextView) findViewById(R.id.content_verName);
        pushDateTextView = (TextView) findViewById(R.id.content_pushDate);
        companyTextView = (TextView) findViewById(R.id.content_company);

        safe = app.getSafe();
        if (safe == 1) {
            safeTextView.setVisibility(View.VISIBLE);
        } else {
            safeTextView.setVisibility(View.GONE);
        }

        // LogOutput.e(TAG, "公司" + app.getCompany());
        verNameTextView.setText(app.getVerName());
        pushDateTextView.setText(app.getPush_date());
        companyTextView.setText(app.getCompany());

        BigImageActivity.addHeaderPointImage(galleryList.size(),
                R.id.content_pointImage, this, pointImageList);

        // gallery.setOnItemClickListener(new OnItemClickListener() {
        // @Override
        // public void onItemClick(AdapterView<?> parent, View view,
        // int position, long id) {
        // showPic(gallString, position);
        // }
        // });
        //
        // gallery.setOnItemSelectedListener(new OnItemSelectedListener() {
        // public void onItemSelected(AdapterView<?> paramAdapterView,
        // View paramView, int paramInt, long paramLong) {
        // for (int i = 0; i < pointImageList.size(); i++) {
        // if ((paramInt % pointImageList.size()) == i) {
        // pointImageList.get(i).setImageResource(
        // R.drawable.information_focus_cirlce_current);
        // } else {
        // pointImageList.get(i).setImageResource(
        // R.drawable.information_focus_cirlce);
        // }
        // }
        // }
        //
        // public void onNothingSelected(AdapterView<?> paramAdapterView) {
        // }
        // });
        int width = DisplayUtils.convertDIP2PX(this,
                SettingsUtils.CONTENT_GALLERY_WIDTH);
        int heigh = DisplayUtils.convertDIP2PX(this,
                SettingsUtils.CONTENT_GALLERY_HEIGHT);
        int padding = DisplayUtils.convertDIP2PX(this,
                SettingsUtils.CONTENT_GALLERY_PADDING);
        // galleryListAdapter = new GalleryListAdapter(this, width, heigh,
        // padding, R.drawable.no_content, Settings.CONTENT_SCALTYPE);
        // gallery.setHorizontalFadingEdgeEnabled(false);
        // gallery.setAdapter(galleryListAdapter);
        // gallery.setSelection(300);
        // galleryListAdapter.updateImage(galleryList, progressBar);

        setSrcollImage(galleryList, tableRow);

        setImage(app.getContentUrl(), iconImageView);

        kindTextView.setText(app.getCategory_title());
        sizeTextView.setText(app.getSize());
        summaryTextView.setText("    " + app.getSummary());
        downloadTimesTextView.setText(String.valueOf(app.getDownload_times())
                + " 次");
        String detail = "    " + app.getApp_detail();
        if (app.getApp_detail().length() > 80) {
            detailTextView.setMaxLines(3);
            moreButton.setVisibility(View.VISIBLE);
            clickMoreBtn(moreButton, detailTextView, detail);
        } else {
            moreButton.setVisibility(View.GONE);
        }
        LogOutputUtils.e(TAG, detail);
        detailTextView.setText(detail);

        setTitle();
        setFavImage(checkIsFav(appid));
        updateProgress(downloadButton, viewHolder.progressBar, contentProgress,
                false, viewHolder.textView);
        viewHolder.favImageView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                // TODO 收藏夹按钮
                // addOrCancleFavorites(articleId);
                if (isAlreadyFav) {
                    if (checkIsFav(appid)) {
                        if (dbFavourite.delete(appid)) {
                            viewHolder.favImageView
                                    .setBackgroundResource(R.drawable.content_fav_default);
                            Toast.makeText(FocusContentViewActivity.this,
                                    "取消收藏", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(FocusContentViewActivity.this,
                                    "取消收藏失败,请重新操作", Toast.LENGTH_LONG).show();
                        }
                    }
                } else {
                    if (appid > 0) {
                        if (dbFavourite.insert(appid)) {
                            viewHolder.favImageView
                                    .setBackgroundResource(R.drawable.content_fav_current);
                            Toast.makeText(FocusContentViewActivity.this,
                                    "收藏成功", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(FocusContentViewActivity.this,
                                    "收藏失败,请重新操作", Toast.LENGTH_LONG).show();
                        }
                    }
                }
                isAlreadyFav = !isAlreadyFav;
            }
        });
    }

    public void shareWeibo(String fileUrl) {
        // 网络不通，提示并返回
        if (!NetworkUtils.isNetworkAvailable(getApplicationContext())) {
            Toast.makeText(FocusContentViewActivity.this, "未找到网络连接！",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (fileUrl == null || "".equals(fileUrl)) {
            Toast.makeText(FocusContentViewActivity.this, "正在加载中,请稍后再试.",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        showWeibo(fileUrl);
    }

    /**
     * 跳转到微博分享页面 分享Activity有bug
     */

    public void showWeibo(String fileUrl) {
        // System.out.println("分享页面");
        // Share.share(this, "《" + "标题" + "》 " + "网页地址" + " 分享来自"
        // + getString(R.string.pcgroup_topic));

        Intent intent = new Intent();
        intent.putExtra("share_content", " Test");
        Share.share(this, intent);
    }

    public void jsReturn(String token, String data) {
        System.out.println("webView调用");
        if ("token".equals(token)) {
            shareWeibo(data);
        }
    }

    @Override
    protected void onDestroy() {
        ProgressListViewAdapter.unRegisterReceive(this, progressChangeReceive);
        progressChangeReceive.reuseRegisCount();
        ProgressListViewAdapter.unregisterInstallService(this);
        super.onDestroy();
        // if (dbFavourite != null) {
        // dbFavourite.close();
        // }
        // if(dbUser_behavior!=null){
        // dbUser_behavior.close();
        // }
    }

    /**
     * 是否已经是收藏夹里面的app
     */
    protected void setFavImage(boolean isFav) {
        if (isFav) {
            isAlreadyFav = true;
            viewHolder.favImageView
                    .setBackgroundResource(R.drawable.content_fav_current);
        } else {
            isAlreadyFav = false;
            viewHolder.favImageView
                    .setBackgroundResource(R.drawable.content_fav_default);
        }
    }

    /**
     * 检测是否已经是收藏了的app
     * 
     * @param id
     * @return
     */
    public boolean checkIsFav(int id) {
        boolean flag = false;
        int[] favIds = dbFavourite.getAppID();
        for (int i = 0; i < favIds.length; i++) {
            // LogOutput.e("FocusContentViewActivity", "id:" + id + "||fav:" +
            // favIds[i]);
            if (id == favIds[i]) {
                return true;
            }
        }
        return flag;
    }

    /**
     * 屏蔽菜单键
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            onBackPressed();
            return true;
        }
        return false;
    }

    @Override
    protected void handleViews(String jsonString) {
    }

    @Override
    protected Handler initHandle() {
        return null;
    }

    @Override
    public void finish() {
        if (activityFlag == SettingsUtils.FOUCS_ACTIVITY_REQ_CODE) {
            Intent intent = new Intent();
            intent.putExtra(SettingsUtils.POSITION, position);
            intent.putParcelableArrayListExtra(SettingsUtils.SERVERAPPLIST,
                    serverAppList);
            intent.putExtra(SettingsUtils.CONTENT_CLICK_ISCHANGE,
                    BaseListViewActivity.isChange);
            // LogOutput.e("focusAdapter" ,
            // "name"+serverAppList.get(position).getName());
            setResult(SettingsUtils.ACTIVITY_RESULT, intent);
        }
        super.finish();
    }

    public void showPic(String images, int position) {
        Intent intent = new Intent();
        intent.setClass(this, BigImageActivity.class);
        intent.putExtra(SettingsUtils.BIG_IMAGE_URL, images);
        intent.putExtra(SettingsUtils.BIG_IMAGE_POSITION, position);
        startActivity(intent);
    }

    public static class ContentViewHolder {
        public static LinearLayout linearLayoutRightProgress;
        public static ImageView favImageView, delImageView;
        public static TextView textView;
        public static ProgressBar progressBar;
    }

    protected void setImage(String imgUrl, final ImageView imageView) {
        Drawable drawable = AsyncImageLoader.loadBitmap(imgUrl);
        if (drawable != null) {
            imageView.setImageDrawable(drawable);
        } else {
            progressBar.setVisibility(View.VISIBLE);
            AsyncImageLoader.loadDrawable(imgUrl, new ImageCallback() {
                public void imageLoaded(Drawable imageDrawable, String imageUrl) {
                    if (null != imageDrawable) {
                        imageView.setImageDrawable(imageDrawable);

                    }
                    progressBar.setVisibility(View.GONE);
                }
            });
        }
    }

    protected void clickMoreBtn(final Button button, final TextView textView,
            final String introduce) {
        button.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (isMore) {
                    button.setBackgroundResource(R.drawable.content_less);
                    textView.setMaxLines(1000);
                    textView.setText(introduce);
                    isMore = !isMore;
                } else {
                    button.setBackgroundResource(R.drawable.content_more);
                    textView.setMaxLines(3);
                    textView.setText(introduce);
                    isMore = !isMore;
                }

            }
        });
    }

    private List<String> getAppList(String imageUrls) {

        List<String> list = new ArrayList<String>();

        if (imageUrls != null && imageUrls.contains(",")) {
            String[] url = imageUrls.split(",");
            for (int i = 0; i < url.length; i++) {
                list.add(url[i]);
            }
        } else {
            list.add(imageUrls);
        }
        return list;
    }

    private void setSrcollImage(List<String> imageList, TableRow tableRow) {
        for (int i = 0; i < imageList.size(); i++) {
            ImageView imageView = new ImageView(this);
            int width = DisplayUtils.convertDIP2PX(this,
                    SettingsUtils.CONTENT_GALLERY_WIDTH);
            int heigh = DisplayUtils.convertDIP2PX(this,
                    SettingsUtils.CONTENT_GALLERY_HEIGHT);
            imageView.setLayoutParams(new TableRow.LayoutParams(width, heigh));
            imageView.setBackgroundResource(R.drawable.no_content);
            setImage(imageList.get(i), imageView);
            final int index = i;
            imageView.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    showPic(gallString, index);
                }
            });
            tableRow.addView(imageView);
        }

    }
}