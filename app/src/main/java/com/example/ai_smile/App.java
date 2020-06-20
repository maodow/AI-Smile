package com.example.ai_smile;

import android.app.Application;
import android.content.Context;
import androidx.multidex.MultiDex;

/**
 * Created by Administrator on 20120/4/12.
 *
 */

public class App extends Application {

    private static App mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    public static Application getApplicationInstance() {
        if (mInstance != null)
            return mInstance;
        return null;
    }

}
