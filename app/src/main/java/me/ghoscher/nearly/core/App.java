package me.ghoscher.nearly.core;

import android.app.Application;

/**
 * Created by Hisham on 29/4/2015.
 */
public class App extends Application {

    private static App instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        // Android bug fix
        try {
            Class.forName("android.os.AsyncTask");
        } catch (Throwable ignore) {
            // ignored
        }

        // TODO integrate and initialize Crashlytics
    }

    public static App getInstance() {
        return instance;
    }
}
