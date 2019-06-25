package com.chenqiao.downloader;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

public class DownloadService extends Service {


    private HashMap<String, DownloadTask> mDownloadTasks = new HashMap<>();
    private ExecutorService mExecutor;
    private LinkedBlockingQueue<DownloadEntry> nQueue = new LinkedBlockingQueue<>();

    private static Handler mHandler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            DownloadEntry downloadEntry = (DownloadEntry) msg.obj;


            if (downloadEntry != null){
                switch (downloadEntry.status){
                    case idle:
                        break;
                    case cancelled:
                        break;
                    case completed:
                        break;
                    case paused:
                        break;
                    default:
                        break;
                }
                DataChanger.getInstance().postStatus(downloadEntry);
            }

        }
    };
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mExecutor = Executors.newCachedThreadPool();
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
//        if (action == Constants.KEY_DOWNLOAD_ACTION_ADD){
////            downloadEntry.status = DownloadEntry.DownloadSatus.downloading;
////            DataChanger.getInstance().postStatus(downloadEntry);
//            startDownload(downloadEntry);
//        }

        switch (action) {
            case Constants.KEY_DOWNLOAD_ACTION_ADD:
                startDownload(downloadEntry);
                break;
            case Constants.KEY_DOWNLOAD_ACTION_PAUSE:
                pauseDownload(downloadEntry);
                break;
            case Constants.KEY_DOWNLOAD_ACTION_RESUME:
                resumeDownload(downloadEntry);
                break;
            case Constants.KEY_DOWNLOAD_ACTION_CANCEL:
                cancelDownload(downloadEntry);
                break;
            default:
                break;
        }

    }

    private void cancelDownload(DownloadEntry downloadEntry) {
        DownloadTask downloadTask = mDownloadTasks.remove(downloadEntry.id);
        if (downloadTask != null){
            downloadTask.cancel();
        }
    }

    private void resumeDownload(DownloadEntry downloadEntry) {
        startDownload(downloadEntry);
    }

    private void pauseDownload(DownloadEntry downloadEntry) {
        DownloadTask downloadTask = mDownloadTasks.remove(downloadEntry.id);
        if (downloadTask != null){
            downloadTask.pause();
        }

    }

    private void startDownload(DownloadEntry downloadEntry) {

        DownloadTask downloadTask = new DownloadTask(downloadEntry, mHandler);
//        downloadTask.start();
        mDownloadTasks.put(downloadEntry.id, downloadTask);
        mExecutor.execute(downloadTask);

    }
}
