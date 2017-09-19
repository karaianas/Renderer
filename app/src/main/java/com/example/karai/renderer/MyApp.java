package com.example.karai.renderer;

/**
 * Created by karai on 9/18/2017.
 */

import android.app.Application;
import android.content.Context;

public class MyApp extends Application {

    private static Context context;

    public void onCreate() {
        super.onCreate();
        MyApp.context = getApplicationContext();
    }

    public static Context getAppContext() {
        return MyApp.context;
    }
}
