package com.app43.appclient.module.launch;

/**
 * 
 * java可以向javascript传json对象,但不能传string数组;
 * javascript可以向java传string数组(是字符串格式,以逗号隔开),但不能传json对象;
 */

import cn.com.pcgroup.common.android.utils.NetworkUtils;

import com.alvin.api.components.DialogAndToast;
import com.alvin.api.utils.LogOutputUtils;
import com.alvin.api.utils.PhoneInfoUtils;
import com.alvin.api.utils.SettingsUtils;
import com.alvin.api.utils.UMengAnalyseUtils;
import com.app43.appclient.R;
import com.app43.appclient.bean.UserInfoBean;
import com.app43.appclient.module.abstracts.activity.SendDataWithExitNomenuBaseActivity;
import com.app43.appclient.module.utils.ParseJsonUtils;

import org.json.JSONException;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class LauncherUserInfoActivity extends
        SendDataWithExitNomenuBaseActivity {
    private final static String TAG = LauncherUserInfoActivity.class
            .getSimpleName();
    String email = null, imei = null, phoneNum = null, os = null, appV = null,
            pv = null, id_lable = "", uuid = null;
    Map<String, Boolean> selectMap = new HashMap<String, Boolean>();
    Map<String, List<UserInfoBean>> dataMap = new HashMap<String, List<UserInfoBean>>();
    TextView sureBtn, sex1, sex0, work1, work2, work0, interest1, interest2,
            interest3, interest4, interest5, interest6, interest7, interest8,
            interest0;
    ProgressDialog progressDialog;
    ProgressBar progressBar;
    String url = SettingsUtils.URL_USERINFO_INDEX;
    List<UserInfoBean> sexUserInfoBeans, workUserInfoBeans, interUserInfoBeans;
    LinearLayout sexLayout, workLayout, allTextLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        progressDialog = DialogAndToast.getProgressDialog(this,
                DialogAndToast.PROGRESS_DIALOG_MSG);

        imei = PhoneInfoUtils.getImei();

        phoneNum = PhoneInfoUtils.getPhoneNum();

        email = PhoneInfoUtils.getEmail();

        pv = PhoneInfoUtils.getPhoneVersion();

        os = PhoneInfoUtils.os;
        appV = PhoneInfoUtils.getAppVersion();
        super.onCreate(savedInstanceState);
        sendJsonData(url, -1, getJsonHandler, this, true);
        // 设置非第一次进入程序
    }

    @Override
    protected Handler initHandle() {
        return normallHandle(TAG);
    }

    protected Handler getJsonHandler = initHandle();

    @Override
    public void setupViews() {
        setContentView(R.layout.user_info_activity);
        allTextLayout = (LinearLayout) findViewById(R.id.user_info_alltext);
        sexLayout = (LinearLayout) findViewById(R.id.userinfo_sexLayout);
        workLayout = (LinearLayout) findViewById(R.id.userinfo_workLayout);
        TextView infoText = (TextView) findViewById(R.id.user_info_text);
        progressBar = (ProgressBar) findViewById(R.id.user_info_loading_progress);
        sureBtn = (Button) findViewById(R.id.userinfo_sure);
        progressBar.setVisibility(View.VISIBLE);
        allTextLayout.setVisibility(View.INVISIBLE);
        sureBtn.setVisibility(View.INVISIBLE);

        infoText.setText("        "
                + getResources().getString(R.string.user_info_text));

        setLableBtn();

        sureBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (progressDialog != null) {
                    progressDialog.show();
                }

                // 跳转到装机推荐列表
                new Thread(new Runnable() {

                    @Override
                    public void run() {

                        try {
                            uuid = PhoneInfoUtils.getUuid();
                            id_lable = "";

                            // 获取所选标签
                            Iterator<Entry<String, Boolean>> iterator = selectMap
                                    .entrySet().iterator();
                            while (iterator.hasNext()) {
                                Entry<String, Boolean> entry = iterator.next();
                                if (entry.getValue()) {
                                    if (iterator.hasNext()) {
                                        id_lable = id_lable + entry.getKey()
                                                + ",";
                                    } else {
                                        id_lable = id_lable + entry.getKey();
                                    }
                                    addDb(Integer.valueOf(entry.getKey()));
                                }
                            }
                            dbUser_behavior.getAllBehavior();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (progressDialog != null) {
                            progressDialog.dismiss();
                        }

                        // 发送intent
                        Intent intent = new Intent();
                        intent.setClass(LauncherUserInfoActivity.this,
                                InstallRecommendNewActivity.class);
                        intent.putExtra(SettingsUtils.uuid, uuid);
                        intent.putExtra(SettingsUtils.IMEI, imei);
                        intent.putExtra(SettingsUtils.PHONENUM, phoneNum);
                        intent.putExtra(SettingsUtils.EMAIL, email);
                        intent.putExtra(SettingsUtils.ID_LABLE, id_lable);
                        intent.putExtra(SettingsUtils.OS, os);
                        intent.putExtra(SettingsUtils.AV, appV);
                        intent.putExtra(SettingsUtils.PV, pv);
                        startActivityForResult(
                                intent,
                                SettingsUtils.LauncherUserInfo_Activity_REQUEST_CODE);
                    }
                }).start();

            }
        });
    }

    // 从装机推荐列表返回结果
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SettingsUtils.LauncherUserInfo_Activity_REQUEST_CODE
                && resultCode == SettingsUtils.LauncherUserInfo_Activity_RESPONE_CODE) {
            this.finish();
        }
    }

    @Override
    public void handleViews(String jsonString) {
        try {
            progressBar.setVisibility(View.INVISIBLE);
            dataMap = ParseJsonUtils.getUserInfoBeanMap(jsonString,
                    SettingsUtils.JSON_SEX, SettingsUtils.JSON_OCCUPATION,
                    SettingsUtils.JSON_INTEREST_LABLE);
            Iterator<Entry<String, List<UserInfoBean>>> iterator = dataMap
                    .entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, List<UserInfoBean>> entry = iterator.next();
                List<UserInfoBean> list = entry.getValue();
                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i).getId().equals("1")
                            || list.get(i).getId().equals("4")) {
                        selectMap.put(list.get(i).getId(), true);
                    } else {
                        selectMap.put(list.get(i).getId(), false);
                    }

                }
            }
            sexUserInfoBeans = dataMap.get(SettingsUtils.JSON_SEX);
            workUserInfoBeans = dataMap.get(SettingsUtils.JSON_OCCUPATION);
            interUserInfoBeans = dataMap.get(SettingsUtils.JSON_INTEREST_LABLE);
            if (interUserInfoBeans.get(0) != null) {
                allTextLayout.setVisibility(View.VISIBLE);
                sureBtn.setVisibility(View.VISIBLE);
                setInterestAction(interest0, interUserInfoBeans.get(0));
            }
            if (interUserInfoBeans.get(1) != null) {
                setInterestAction(interest1, interUserInfoBeans.get(1));
            }
            if (interUserInfoBeans.get(2) != null) {
                setInterestAction(interest2, interUserInfoBeans.get(2));
            }
            if (interUserInfoBeans.get(3) != null) {
                setInterestAction(interest3, interUserInfoBeans.get(3));
            }
            if (interUserInfoBeans.get(4) != null) {
                setInterestAction(interest4, interUserInfoBeans.get(4));
            }
            if (interUserInfoBeans.get(5) != null) {
                setInterestAction(interest5, interUserInfoBeans.get(5));
            }
            if (interUserInfoBeans.get(6) != null) {
                setInterestAction(interest6, interUserInfoBeans.get(6));
            }
            if (interUserInfoBeans.get(7) != null) {
                setInterestAction(interest7, interUserInfoBeans.get(7));
            }
            if (interUserInfoBeans.get(8) != null) {
                setInterestAction(interest8, interUserInfoBeans.get(8));
            }

            setSexBtnAction();
            setWorkBtnAction();
        } catch (JSONException e) {
            e.printStackTrace();
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

    private void setLableBtn() {
        sex1 = (TextView) findViewById(R.id.userinfo_sex1);
        sex0 = (TextView) findViewById(R.id.userinfo_sex0);
        work1 = (TextView) findViewById(R.id.userinfo_work1);
        work2 = (TextView) findViewById(R.id.userinfo_work2);
        work0 = (TextView) findViewById(R.id.userinfo_work0);
        interest1 = (TextView) findViewById(R.id.userinfo_interest1);
        interest2 = (TextView) findViewById(R.id.userinfo_interest2);
        interest3 = (TextView) findViewById(R.id.userinfo_interest3);
        interest4 = (TextView) findViewById(R.id.userinfo_interest4);
        interest5 = (TextView) findViewById(R.id.userinfo_interest5);
        interest6 = (TextView) findViewById(R.id.userinfo_interest6);
        interest7 = (TextView) findViewById(R.id.userinfo_interest7);
        interest8 = (TextView) findViewById(R.id.userinfo_interest8);
        interest0 = (TextView) findViewById(R.id.userinfo_interest0);
        setBtn(sex0);
        setBtn(sex1);
        setBtn(work0);
        setBtn(work1);
        setBtn(work2);
        setBtn(interest0);
        setBtn(interest1);
        setBtn(interest2);
        setBtn(interest3);
        setBtn(interest4);
        setBtn(interest5);
        setBtn(interest6);
        setBtn(interest7);
        setBtn(interest8);

    }

    private void setInterestAction(TextView button,
            final UserInfoBean userInfoBean) {
        button.setText(userInfoBean.getTitle());
        if (selectMap.get(userInfoBean.getId())) {
            button.setBackgroundResource(R.drawable.userinfo_lable_current);
        } else {
            button.setBackgroundResource(R.drawable.userinfo_lable_default);
        }

        button.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                boolean isSelect = selectMap.get(userInfoBean.getId());
                selectMap.put(userInfoBean.getId(), !isSelect);
                if (selectMap.get(userInfoBean.getId())) {
                    v.setBackgroundResource(R.drawable.userinfo_lable_current);
                } else {
                    v.setBackgroundResource(R.drawable.userinfo_lable_default);
                }
            }
        });
    }

    private void setSexBtnAction() {

        sex0.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                UserInfoBean userInfoBean = sexUserInfoBeans.get(0);
                boolean isSelect = !selectMap.get(userInfoBean.getId());
                selectMap.put(userInfoBean.getId(), isSelect);
                if (isSelect) {
                    selectMap.put("2", false);
                }
                sexLayout.setBackgroundResource(R.drawable.userinfo_sex0);
            }
        });
        sex1.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                UserInfoBean userInfoBean = sexUserInfoBeans.get(1);
                boolean isSelect = !selectMap.get(userInfoBean.getId());
                selectMap.put(userInfoBean.getId(), isSelect);
                if (isSelect) {
                    selectMap.put("1", false);
                }
                sexLayout.setBackgroundResource(R.drawable.userinfo_sex1);
            }
        });
    }

    private void setWorkBtnAction() {
        work0.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                UserInfoBean userInfoBean = workUserInfoBeans.get(0);
                boolean isSelect = !selectMap.get(userInfoBean.getId());
                selectMap.put(userInfoBean.getId(), isSelect);
                if (isSelect) {
                    selectMap.put("4", false);
                    selectMap.put("5", false);
                }
                workLayout.setBackgroundResource(R.drawable.userinfo_work0);
            }
        });
        work1.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                UserInfoBean userInfoBean = workUserInfoBeans.get(1);
                boolean isSelect = !selectMap.get(userInfoBean.getId());
                selectMap.put(userInfoBean.getId(), isSelect);
                if (isSelect) {
                    selectMap.put("3", false);
                    selectMap.put("5", false);
                }
                workLayout.setBackgroundResource(R.drawable.userinfo_work1);
            }
        });
        work2.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                UserInfoBean userInfoBean = workUserInfoBeans.get(2);
                boolean isSelect = !selectMap.get(userInfoBean.getId());
                selectMap.put(userInfoBean.getId(), isSelect);
                if (isSelect) {
                    selectMap.put("3", false);
                    selectMap.put("4", false);
                }
                workLayout.setBackgroundResource(R.drawable.userinfo_work2);
            }
        });
    }

    private void addDb(int lable_id) {
        LogOutputUtils.e(TAG, "添加的ID" + lable_id);
        switch (lable_id) {
        case 1:
            dbUser_behavior.inserts(SettingsUtils.CATEID_INTELLECT, 10);
            break;
        case 2:
            dbUser_behavior.inserts(SettingsUtils.CATEID_CAMERA, 10);
            dbUser_behavior.inserts(SettingsUtils.CATEID_SHOOTING, 10);
            break;
        case 3:
            dbUser_behavior.inserts(SettingsUtils.CATEID_ENTERTAINMENT, 10);
            dbUser_behavior.inserts(SettingsUtils.CATEID_SOCIAL, 10);
            dbUser_behavior.inserts(SettingsUtils.CATEID_SHOOTING, 10);
            break;
        case 4:
            dbUser_behavior.inserts(SettingsUtils.CATEID_LIFE, 10);
            dbUser_behavior.inserts(SettingsUtils.CATEID_NEWS, 10);
            dbUser_behavior.inserts(SettingsUtils.CATEID_OFFICE, 10);
            break;
        case 5:
            dbUser_behavior.inserts(SettingsUtils.CATEID_ENTERTAINMENT, 10);
            break;
        case 6:
            dbUser_behavior.inserts(SettingsUtils.CATEID_LIFE, 10);
            dbUser_behavior.inserts(SettingsUtils.CATEID_SHOPPING, 10);
            dbUser_behavior.inserts(SettingsUtils.CATEID_TRIP, 10);
            break;
        case 7:
            dbUser_behavior.inserts(SettingsUtils.CATEID_LIFE, 5);
            dbUser_behavior.inserts(SettingsUtils.CATEID_NEWS, 10);
            dbUser_behavior.inserts(SettingsUtils.CATEID_READDING, 100);
            break;

        case 8:
            dbUser_behavior.inserts(SettingsUtils.CATEID_CAMERA, 10);
            dbUser_behavior.inserts(SettingsUtils.CATEID_SOCIAL, 10);
            dbUser_behavior.inserts(SettingsUtils.CATEID_SHOPPING, 10);
            break;
        case 9:
            dbUser_behavior.inserts(SettingsUtils.CATEID_LIFE, 10);
            dbUser_behavior.inserts(SettingsUtils.CATEID_SHOPPING, 10);
            dbUser_behavior.inserts(SettingsUtils.CATEID_MONENY, 10);
            break;
        case 10:
            dbUser_behavior.inserts(SettingsUtils.CATEID_NAVIGATION, 10);
            dbUser_behavior.inserts(SettingsUtils.CATEID_TRIP, 10);
            break;
        case 11:
            dbUser_behavior.inserts(SettingsUtils.CATEID_CAMERA, 20);
            dbUser_behavior.inserts(SettingsUtils.CATEID_TRIP, 10);
            dbUser_behavior.inserts(SettingsUtils.CATEID_LIFE, 10);
            break;
        case 12:
            dbUser_behavior.inserts(SettingsUtils.CATEID_SOCIAL, 20);
            break;
        case 13:
            dbUser_behavior.inserts(SettingsUtils.CATEID_SHOOTING, 10);
            dbUser_behavior.inserts(SettingsUtils.CATEID_RPG, 10);
            dbUser_behavior.inserts(SettingsUtils.CATEID_SPEED, 10);
            dbUser_behavior.inserts(SettingsUtils.CATEID_SPORT, 10);
            dbUser_behavior.inserts(SettingsUtils.CATEID_INTELLECT, 10);
            dbUser_behavior.inserts(SettingsUtils.CATEID_BUSINESS, 10);
            dbUser_behavior.inserts(SettingsUtils.CATEID_SIMULATE, 10);
            dbUser_behavior.inserts(SettingsUtils.CATEID_CARDS, 10);
            dbUser_behavior.inserts(SettingsUtils.CATEID_NETWORKGAME, 10);
            break;
        case 14:
            dbUser_behavior.inserts(SettingsUtils.CATEID_SHOPPING, 30);
            dbUser_behavior.inserts(SettingsUtils.CATEID_LIFE, 10);
            dbUser_behavior.inserts(SettingsUtils.CATEID_MONENY, 10);
            break;

        default:
            break;
        }
    }

    private void setBtn(View view) {
        view.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (!NetworkUtils
                        .isNetworkAvailable(LauncherUserInfoActivity.this)) {
                    DialogAndToast.showError(LauncherUserInfoActivity.this);
                }
            }
        });
    }
}
