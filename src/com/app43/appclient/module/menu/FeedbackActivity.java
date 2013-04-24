package com.app43.appclient.module.menu;

import cn.com.pcgroup.common.android.config.Env;
import cn.com.pcgroup.common.android.utils.NetworkUtils;

import com.alvin.api.components.DialogAndToast;
import com.alvin.api.utils.SettingsUtils;
import com.alvin.api.utils.UMengAnalyseUtils;
import com.app43.appclient.R;
import com.app43.appclient.module.abstracts.activity.SendDataNoMenuActivity;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.webkit.URLUtil;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class FeedbackActivity extends SendDataNoMenuActivity {
    private TelephonyManager tm = null;
    private TextView suggestionTextView = null;
    private ProgressBar suggestionProgressBar = null;
    protected Handler backHandler = new Handler();
    protected Runnable backRunnable = new Runnable() {
        public void run() {
            onBackPressed();
        }
    };

    @Override
    protected void handleViews(String jsonString) {

    }

    @Override
    protected Handler initHandle() {
        return null;
    }

    @Override
    public void setupViews() {
        setContentView(R.layout.feedback_activity);
        tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        suggestionTextView = (TextView) findViewById(R.id.more_suggestion_content);
        suggestionProgressBar = (ProgressBar) findViewById(R.id.more_suggestion_refresh_loadprogress);
        setBackListener();
        setSubmitListener();
    }

    protected void setBackListener() {
        ImageButton backImageButton = (ImageButton) (findViewById(R.id.more_suggestion_activity_back));
        backImageButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                FeedbackActivity.this.onBackPressed();
            }
        });
    }

    /**
     * 提交意见监听
     */
    protected void setSubmitListener() {
        ImageButton submitImageButton = (ImageButton) (findViewById(R.id.more_suggestion_activity_submit));
        submitImageButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                sendSuggestion();
            }
        });
    }

    /**
     * 发送意见信息
     */
    protected void sendSuggestion() {

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(suggestionTextView.getWindowToken(), 0);

        // 网络不通，提示并返回
        if (!NetworkUtils.isNetworkAvailable(getApplicationContext())) {
            DialogAndToast.showToast(FeedbackActivity.this, "未找到网络连接！");
            return;
        }

        // 提交内容非空，非法的判断
        if (suggestionTextView.getText() == null
                || (suggestionTextView.getText() != null && ""
                        .equals(suggestionTextView.getText().toString().trim()))) {
            DialogAndToast.showToast(FeedbackActivity.this, "提交内容为空,请填写内容！");
            return;
        }

        if (suggestionTextView.getText() != null
                && suggestionTextView.getText().length() > 1000) {
            DialogAndToast.showToast(FeedbackActivity.this, "意见内容请不要超过1000字！");
            return;
        }

        suggestionProgressBar.setVisibility(View.VISIBLE);
        SubmitSuggestionAsyncTask task = new SubmitSuggestionAsyncTask();
        task.execute("");

    }

    class SubmitSuggestionAsyncTask extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... params) {

            String result = "";
            // String feedString = "uuid"+tm.getDeviceId() + "&content"
            // + suggestionTextView.getText() + "&ov"
            // + android.os.Build.VERSION.RELEASE + "&av" + Env.versionName
            // + "&pv"+android.os.Build.MODEL;
            // feedString = feedString.trim();
            // Log.e("ds", feedString);
            // String url = Settings.URL_SUGGESST + feedString;
            // 要提交的URL和数据
            StringBuilder sb = new StringBuilder(SettingsUtils.URL_SUGGESST
                    + "pppp/ppp/ppp");
            List<NameValuePair> paramList = new ArrayList<NameValuePair>();
            paramList.add(new BasicNameValuePair("uuid", tm.getDeviceId()));
            paramList.add(new BasicNameValuePair("pv", "Android-"
                    + android.os.Build.MODEL));
            paramList.add(new BasicNameValuePair("ov",
                    android.os.Build.VERSION.RELEASE));
            paramList.add(new BasicNameValuePair("av", Env.versionName));
            paramList.add(new BasicNameValuePair("content", suggestionTextView
                    .getText() + ""));

            // 检查URL是否正确
            if (!URLUtil.isNetworkUrl(sb.toString())) {
                return "参数有误，地址无效！";
            }

            // if (!URLUtil.isNetworkUrl(url)) {
            // return "参数有误，地址无效！";
            // }
            HttpPost httpPost = null;
            HttpParams httpParams = null;
            HttpClient httpClient = null;
            HttpResponse httpResponse = null;

            try {

                // httpPost = new HttpPost(url);
                httpPost = new HttpPost(sb.toString());
                httpPost.setEntity(new UrlEncodedFormEntity(paramList,
                        HTTP.UTF_8));

                // 超时控制
                httpParams = new BasicHttpParams();
                HttpConnectionParams.setConnectionTimeout(httpParams, 30000);
                HttpConnectionParams.setSoTimeout(httpParams, 30000);

                httpClient = new DefaultHttpClient(httpParams);
                httpResponse = httpClient.execute(httpPost);

                // 响应返回Code判断
                if (httpResponse.getStatusLine().getStatusCode() == 200) {
                    // System.out.println("返回值:"+EntityUtils.toString(httpResponse.getEntity()));
                    result = "提交成功，感谢您的宝贵意见！";
                } else {
                    DialogAndToast.showToast(FeedbackActivity.this,
                            "服务器暂时无法访问，请重新提交！");
                }
            } catch (UnknownHostException ex) {
                ex.printStackTrace();
                result = "服务器暂时无法访问，请稍候再试！";
            } catch (Exception ex) {
                ex.printStackTrace();
                result = "提交错误，请重新提交！";
            } finally {
                if (httpClient != null
                        && httpClient.getConnectionManager() != null) {
                    httpClient.getConnectionManager().shutdown();
                }
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            suggestionProgressBar.setVisibility(View.INVISIBLE);
            DialogAndToast.showToast(FeedbackActivity.this, result);
            if ("提交成功，感谢您的宝贵意见！".equals(result)) {
                backHandler.postDelayed(backRunnable, 2000);
            }
            super.onPostExecute(result);
        }
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
