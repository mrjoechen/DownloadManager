package com.chenqiao.downloader;

import android.util.Log;

public class Trace {

    public static final String TAG = "DownloadManager-Trace";
    private static final boolean Debug = BuildConfig.DEBUG;

    public static void d(String msg){
        if (Debug){
            Log.d(TAG, msg);
        }
    }

    public static void e(String msg){
        if (Debug){
            Log.e(TAG, msg);
        }
    }
}
