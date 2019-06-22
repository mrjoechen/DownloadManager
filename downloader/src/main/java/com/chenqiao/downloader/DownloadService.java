package com.chenqiao.downloader;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import java.io.Serializable;

public class DownloadService extends Service {


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        DownloadEntry downloadEntry = (DownloadEntry) intent.getSerializableExtra(Constants.KEY_DOWNLOAD_ENTRY);
        int action = intent.getIntExtra(Constants.KEY_DOWNLOAD_ACTION, -1);

        doAction(action, downloadEntry);

        return super.onStartCommand(intent, flags, startId);
    }

    private void doAction(int action, DownloadEntry downloadEntry) {

        //check action, do related action
        if (action == Constants.KEY_DOWNLOAD_ACTION_ADD){
            downloadEntry.downloadSatus = DownloadEntry.DownloadSatus.downloading;
            DataChanger.getInstance().postStatus(downloadEntry);
        }

    }
}
