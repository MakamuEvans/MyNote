package com.elm.mycheck.login.model;

import android.content.Context;

import com.orm.SugarApp;
import com.orm.SugarContext;

/**
 * Created by elm on 7/11/17.
 */

public class Sugar extends SugarApp{

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        android.support.multidex.MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        SugarContext.init(this);
    }

    @Override
    public void onTerminate() {
        SugarContext.terminate();
        super.onTerminate();
    }
}
