package com.app43.appclient.module.tabframe;

import com.alvin.api.utils.UMengAnalyseUtils;
import com.app43.appclient.R;
import com.app43.appclient.module.abstracts.activity.BaseGridActivity;
import com.app43.appclient.module.news.NewsContentActivity;

public class NewsGridActivity extends BaseGridActivity {

    public NewsGridActivity() {
        super(false);
    }

    protected void setupData() {
        imagesId = new int[] { R.drawable.new1, R.drawable.new2,
                R.drawable.new3, R.drawable.new4, R.drawable.new5,
                R.drawable.new6, R.drawable.new7, R.drawable.new8,
                R.drawable.new9 };
        textId = new int[] { R.string.new_1, R.string.new_2, R.string.new_3,
                R.string.new_4, R.string.new_5, R.string.new_6, R.string.new_7,
                R.string.new_8, R.string.new_9 };
        urls = new String[] {
                "http://fand.app43.com/html/android/tutorial/1_tonghua.htm",
                "http://fand.app43.com/html/android/tutorial/1_tonghua.htm",
                "http://fand.app43.com/html/android/tutorial/3_xinxi.htm",
                "http://fand.app43.com/html/android/tutorial/4_wangluo.htm",
                "http://fand.app43.com/html/android/tutorial/5_lianjie.htm",
                "http://fand.app43.com/html/android/tutorial/6_tongzhi.htm",
                "http://fand.app43.com/html/android/tutorial/7_zhaoxiang.htm",
                "http://fand.app43.com/html/android/tutorial/8_yinyue.htm",
                "http://fand.app43.com/html/android/tutorial/9_xitong.htm" };
        titleId = R.string.tab_news_name;
        tarActivity = new NewsContentActivity();
    }

    @Override
    protected void onResume() {
        super.onResume();
        UMengAnalyseUtils.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        UMengAnalyseUtils.onPause(this);
    }
}
