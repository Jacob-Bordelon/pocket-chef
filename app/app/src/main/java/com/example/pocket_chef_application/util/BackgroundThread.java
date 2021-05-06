package com.example.pocket_chef_application.util;


import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Process;
import android.util.Log;

public class BackgroundThread extends HandlerThread {
    private static final String TAG = "BackgroundThread";
    private Handler handler;

    public BackgroundThread() {
        super("BackgroundThread", Process.THREAD_PRIORITY_BACKGROUND);
    }

    @Override
    protected void onLooperPrepared() {
        handler = new Handler();
    }

    public Handler getHandler() {
        return handler;
    }
}
