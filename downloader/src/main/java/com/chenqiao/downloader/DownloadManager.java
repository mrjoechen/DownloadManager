package com.chenqiao.downloader;

import android.content.Context;
import android.content.Intent;

public class DownloadManager {


    private static DownloadManager mInstance;
    private final Context context;

    private DownloadManager(Context context){
        this.context = context;
    }

    public synchronized DownloadManager getInstance(Context context){
        if (mInstance == null){
            mInstance = new DownloadManager(context);
        }

        return mInstance;
    }

    public void add(DownloadEntry downloadEntry){
        Intent intent = new Intent(context, DownloadService.class);
        intent.putExtra(Constants.KEY_DOWNLOAD_ENTRY, downloadEntry);
        intent.putExtra(Constants.KEY_DOWNLOAD_ACTION, Constants.KEY_DOWNLOAD_ACTION_ADD);
        context.startService(intent);
    }

    public void pause(DownloadEntry downloadEntry){
        Intent intent = new Intent(context, DownloadService.class);
        intent.putExtra(Constants.KEY_DOWNLOAD_ENTRY, downloadEntry);
        intent.putExtra(Constants.KEY_DOWNLOAD_ACTION, Constants.KEY_DOWNLOAD_ACTION_PAUSE);
        context.startService(intent);
    }

    public void resume(DownloadEntry downloadEntry){
        Intent intent = new Intent(context, DownloadService.class);
        intent.putExtra(Constants.KEY_DOWNLOAD_ENTRY, downloadEntry);
        intent.putExtra(Constants.KEY_DOWNLOAD_ACTION, Constants.KEY_DOWNLOAD_ACTION_RESUME);
        context.startService(intent);
    }

    public void cancel(DownloadEntry downloadEntry){
        Intent intent = new Intent(context, DownloadService.class);
        intent.putExtra(Constants.KEY_DOWNLOAD_ENTRY, downloadEntry);
        intent.putExtra(Constants.KEY_DOWNLOAD_ACTION, Constants.KEY_DOWNLOAD_ACTION_CANCEL);
        context.startService(intent);
    }

    public void addObserver(DataWatcher dataWatcher){
        DataChanger.getInstance().addObserver(dataWatcher);
    }

    public void removeObserver(DataWatcher dataWatcher){
        DataChanger.getInstance().deleteObserver(dataWatcher);

    }

}
