package com.alvin.api.components;

import com.app43.appclient.R;
import com.app43.appclient.module.launch.LauncherUserInfoActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.widget.Toast;

public class DialogAndToast {

    public static String POSITIVE = "确定";
    public static String NEGATIVE = "取消";
    public static String PROGRESS_DIALOG_MSG = "正在加载,请稍后...";

    /**
     * 作用:选择对话框
     */
    public static void gotoDialog(final Context context, String positiveString,
            String negativeString,
            DialogInterface.OnClickListener positiveOnClickListener,
            OnClickListener negativeClickListener) {

        AlertDialog.Builder builder = new Builder(context);

        builder.setMessage(context.getResources().getString(
                R.string.user_info_goto_recommend));

        builder.setTitle(context.getResources().getString(
                R.string.user_info_goto_tips));

        builder.setPositiveButton(positiveString, positiveOnClickListener);
        builder.setNegativeButton(negativeString, negativeClickListener);

        builder.show();
    }

    /**
     * 作用:progress对话框
     */
    public static ProgressDialog getProgressDialog(Context context, String msg) {
        ProgressDialog progressDialog;
        progressDialog = new ProgressDialog(context);
        progressDialog.setTitle(null);
        progressDialog.setIcon(null);
        progressDialog.setMessage(msg);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        return progressDialog;
    }

    public static void showToast(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_LONG);
    }

    public static void showError(Context context) {
        Toast loadToast = Toast.makeText(context,
                context.getString(R.string.hit_network_failure),
                Toast.LENGTH_SHORT);
        loadToast.show();
    }
}
