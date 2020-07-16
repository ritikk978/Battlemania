//For onesignal, check internet availability and font family
package com.official.gold.gaming.tournamentpubg.utils;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import com.android.volley.RequestQueue;
import com.official.gold.gaming.tournamentpubg.ui.activities.NoInternetActivity;
import com.onesignal.OneSignal;
import java.util.List;

public class MyApp extends Application {

    boolean internet = false;
    private static Context mContext;
    RequestQueue mQueue;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;

        //for change font family
        FontsOverride.setDefaultFont(this, "MONOSPACE", "font/Poppins-Regular.ttf");

        //for one signal notification
        OneSignal.startInit(this)
                //.inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .init();

        //check internet availability
        final Handler tipsHanlder = new Handler();
        Runnable tipsRunnable = new Runnable() {
            @Override
            public void run() {
                tipsHanlder.postDelayed(this, 1000);

                if (isAppRunning(getApplicationContext(), "com.official.gold.gaming.tournamentpubg")) {

                    // App is running
                    if (isAppOnForeground(getApplicationContext(), "com.official.gold.gaming.tournamentpubg")) {

                        //run in foreground
                        if (!isNetworkAvailable()) {
                            if (internet == false) {
                                internet = true;
                                Intent intent = new Intent(getApplicationContext(), NoInternetActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        } else {
                            internet = false;
                            //internet available
                        }
                    } else {
                        //run in background
                    }
                } else {
                    // App is not running
                }
            }
        };
        tipsHanlder.post(tipsRunnable);
    }

    public static Context getContext() {
        return mContext;
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static boolean isAppRunning(final Context context, final String packageName) {
        final ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        final List<ActivityManager.RunningAppProcessInfo> procInfos = activityManager.getRunningAppProcesses();
        if (procInfos != null) {
            for (final ActivityManager.RunningAppProcessInfo processInfo : procInfos) {
                if (processInfo.processName.equals(packageName)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isAppOnForeground(Context context, String appPackageName) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        if (appProcesses == null) {
            return false;
        }
        final String packageName = appPackageName;
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND && appProcess.processName.equals(packageName)) {
                return true;
            }
        }
        return false;
    }
}
