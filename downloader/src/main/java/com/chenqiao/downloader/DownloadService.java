package com.chenqiao.downloader;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

import com.chenqiao.downloader.db.DBController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

public class DownloadService extends Service {


    public static final int NOTIFY_DOWNLOADING = 1;
    public static final int NOTIFY_UPDATING = 2;
    public static final int NOTIFY_PAUSED_OR_CANCELLED = 3;
    public static final int NOTIFY_COMPLETED = 4;


    private HashMap<String, DownloadTask> mDownloadTasks = new HashMap<>();
    private ExecutorService mExecutor;
    private LinkedBlockingQueue<DownloadEntry> mWaitingQueue = new LinkedBlockingQueue<>();
    private ArrayList<DownloadEntry> mPausedEntries = new ArrayList<>();

    private Handler mHandler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

//            DownloadEntry downloadEntry = (DownloadEntry) msg.obj;
//            if (downloadEntry != null){
//                switch (downloadEntry.status){
//                    case cancelled:
//                    case completed:
//                    case paused:
//                        checkNext();
//                        break;
//                    case idle:
//                        break;
//                    default:
//                        break;
//                }
//                DataChanger.getInstance().postStatus(downloadEntry);
//            }

            switch (msg.what){
                case NOTIFY_PAUSED_OR_CANCELLED:
                case NOTIFY_COMPLETED:
                    checkNext();
                    break;
            }

            DataChanger.getInstance(DownloadService.this).postStatus((DownloadEntry) msg.obj);

        }
    };
    private DataChanger mDataChanger;

    private void checkNext() {
        DownloadEntry poll = mWaitingQueue.poll();
        if (poll != null){
            startDownload(poll);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //
        mExecutor = Executors.newCachedThreadPool();
//        Executors.newFixedThreadPool(3);
        mDataChanger = DataChanger.getInstance(getApplicationContext());

        List<DownloadEntry> downloadEntries = DBController.getInstance().queryAll();
        if (downloadEntries != null){
            for (DownloadEntry entry : downloadEntries){
                if (entry.status == DownloadEntry.DownloadSatus.downloading || entry.status == DownloadEntry.DownloadSatus.waiting){
                    entry.status = DownloadEntry.DownloadSatus.paused;
                    //可以配置是否恢复下载
                    addDownload(entry);
                }
                mDataChanger.addToOperatedEntryMap(entry.id, entry);
            }
        }

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
                addDownload(downloadEntry);
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


    private void addDownload(DownloadEntry entry){
        if (mDownloadTasks.size() >= Constants.MAX_DOWNLOAD_SIZE){
            mWaitingQueue.offer(entry);
            entry.status = DownloadEntry.DownloadSatus.waiting;
            DataChanger.getInstance(getApplicationContext()).postStatus(entry);
        }else {
            startDownload(entry);
        }
    }


    private void cancelDownload(DownloadEntry downloadEntry) {
        DownloadTask downloadTask = mDownloadTasks.remove(downloadEntry.id);
        if (downloadTask != null){
            downloadTask.cancel();
        }else {
            mWaitingQueue.remove(downloadEntry);
            downloadEntry.status = DownloadEntry.DownloadSatus.cancelled;
            DataChanger.getInstance(getApplicationContext()).postStatus(downloadEntry);

        }
    }

    private void resumeDownload(DownloadEntry downloadEntry) {
        addDownload(downloadEntry);
    }

    private void pauseDownload(DownloadEntry downloadEntry) {
        DownloadTask downloadTask = mDownloadTasks.remove(downloadEntry.id);
        if (downloadTask != null){
            downloadTask.pause();
        }else {
            mWaitingQueue.remove(downloadEntry);
            downloadEntry.status = DownloadEntry.DownloadSatus.paused;
            DataChanger.getInstance(getApplicationContext()).postStatus(downloadEntry);
        }

    }

    private void startDownload(DownloadEntry downloadEntry) {

        DownloadTask downloadTask = new DownloadTask(downloadEntry, mHandler);
//        downloadTask.start();
        mDownloadTasks.put(downloadEntry.id, downloadTask);
        mExecutor.execute(downloadTask);
    }

    private void pauseAll(){
        while (mWaitingQueue.iterator().hasNext()){
            DownloadEntry entry = mWaitingQueue.poll();
            entry.status = DownloadEntry.DownloadSatus.paused;
            DataChanger.getInstance(getApplicationContext()).postStatus(entry);
        }

        for (Map.Entry<String, DownloadTask> entry : mDownloadTasks.entrySet()){
            entry.getValue().pause();
        }
        mDownloadTasks.clear();
    }

    private void recoverAll(){

        ArrayList<DownloadEntry> downloadEntries = DataChanger.getInstance(getApplicationContext()).queryAllRecoverableEntries();
        if (downloadEntries != null){
            for (DownloadEntry entry : downloadEntries){
                addDownload(entry);
            }
        }
    }
}
