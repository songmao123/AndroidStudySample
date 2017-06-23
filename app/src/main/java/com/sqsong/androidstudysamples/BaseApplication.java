package com.sqsong.androidstudysamples;

import android.app.Application;

/**
 * Created by 青松 on 2017/6/22.
 */

public class BaseApplication extends Application {
    private static BaseApplication sInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
    }

    public static BaseApplication getInstance() {
        return sInstance;
    }
}
