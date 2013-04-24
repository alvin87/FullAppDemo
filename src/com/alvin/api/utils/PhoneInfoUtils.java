package com.alvin.api.utils;

import cn.com.pcgroup.common.android.config.Env;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.telephony.TelephonyManager;

public class PhoneInfoUtils {
    private static String TAG = PhoneInfoUtils.class.getSimpleName();
    public static String imei, phoneNum, email, phoneVersion, os, appVersion,
            resolution, uuid;
    private static boolean isFirst = true;

    public static String getResolution() {
        return resolution;
    }

    public static void setResolution(String resolution) {
        PhoneInfoUtils.resolution = resolution;
    }

    public static String getImei() {
        return imei;
    }

    public static String getPhoneNum() {
        return phoneNum;
    }

    public static String getEmail() {
        return email;
    }

    public static String getPhoneVersion() {
        return phoneVersion;
    }

    public static String getOs() {
        return os;
    }

    public static String getAppVersion() {
        return appVersion;
    }

    public static void setImei(String imei) {
        PhoneInfoUtils.imei = imei;
    }

    public static void setPhoneNum(String phoneNum) {
        PhoneInfoUtils.phoneNum = phoneNum;
    }

    public static void setEmail(String email) {
        PhoneInfoUtils.email = email;
    }

    public static void setPhoneVersion(String phoneVersion) {
        PhoneInfoUtils.phoneVersion = phoneVersion;
    }

    public static void setOs(String os) {
        PhoneInfoUtils.os = os;
    }

    public static void setAppVersion(String appVersion) {
        PhoneInfoUtils.appVersion = appVersion;
    }

    public static void setUuid(String uuid) {
        PhoneInfoUtils.uuid = uuid;
    }

    public static void setPhoneInfo(Context context) {
        if (isFirst) {
            TelephonyManager telephonyManager = (TelephonyManager) context
                    .getSystemService(Context.TELEPHONY_SERVICE);
            imei = telephonyManager.getDeviceId();
            if (imei == null) {
                imei = SettingsUtils.NULL_IMEI;
            }
            phoneNum = telephonyManager.getLine1Number();
            if (phoneNum == null) {
                phoneNum = "00" + SettingsUtils.NULL_PHONENUM;
            }
            if (phoneNum.contains("+")) {
                phoneNum = phoneNum.substring(1);
            }

            email = getEmail(context);
            if (email == null) {
                email = SettingsUtils.NULL_EMAIL;
            }
            phoneVersion = android.os.Build.MODEL; // 手机型号
            if (phoneVersion == null) {
                phoneVersion = SettingsUtils.NULL_PV;
            } else {
                String tempString[] = phoneVersion.split(" ");
                phoneVersion = "";
                for (int i = 0; i < tempString.length; i++) {
                    phoneVersion += tempString[i];
                }

            }
            os = android.os.Build.VERSION.RELEASE; // android系统版本号
            try {
                PackageInfo packageInfo = context.getPackageManager()
                        .getPackageInfo(context.getPackageName(),
                                PackageManager.GET_CONFIGURATIONS);
                appVersion = Env.versionName;
                LogOutputUtils.e(TAG, "appv " + appVersion);
            } catch (NameNotFoundException e) {
                e.printStackTrace();
            }
            isFirst = false;
        }

    }

    private static Account getAccount(AccountManager accountManager) {
        Account[] accounts = accountManager.getAccountsByType("com.google");
        Account account;
        if (accounts.length > 0) {
            account = accounts[0];
        } else {
            account = null;
        }
        return account;
    }

    private static String getEmail(Context context) {
        AccountManager accountManager = AccountManager.get(context);
        Account account = getAccount(accountManager);

        if (account == null) {
            return null;
        } else {
            return account.name;
        }
    }

    public static String getUuid() {
        UUID uuid = new UUID();
        String encode = null;
        try {
            encode = uuid.getUUID(imei, "0" + phoneNum);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return encode;
    }
}
