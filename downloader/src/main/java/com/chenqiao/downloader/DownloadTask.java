package com.chenqiao.downloader;


import android.os.Handler;
import android.os.Message;

public class DownloadTask implements Runnable{

    //todo check if support range

    private final DownloadEntry downloadEntry;
    private final Handler mHandler;

    private boolean isPaused;
    private boolean isCancelled;

    public DownloadTask(DownloadEntry downloadEntry, Handler handler){
        this.downloadEntry = downloadEntry;
        this.mHandler = handler;
    }

    public void pause() {
        isPaused = true;

    }

    public void cancel(){
        isCancelled = true;

    }

    public void start() {
        downloadEntry.status = DownloadEntry.DownloadSatus.downloading;
//        DataChanger.getInstance().postStatus(downloadEntry);

        Message message = mHandler.obtainMessage();
        message.obj = downloadEntry;
        mHandler.sendMessage(message);
        downloadEntry.totalLength = 1024 * 100;

        for (int i=downloadEntry.currentlength; i<downloadEntry.totalLength; ){

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (isCancelled || isPaused){
                downloadEntry.status = isPaused ? DownloadEntry.DownloadSatus.paused : DownloadEntry.DownloadSatus.cancelled;
//                DataChanger.getInstance().postStatus(downloadEntry);
                message = mHandler.obtainMessage();
                message.obj = downloadEntry;
                mHandler.sendMessage(message);
                // todo if cancelled ,delete th temp file
                return;
            }

            i+=1024;
            downloadEntry.currentlength += 1024;
//            DataChanger.getInstance().postStatus(downloadEntry);
            message = mHandler.obtainMessage();
            message.obj = downloadEntry;
            mHandler.sendMessage(message);
        }
        downloadEntry.status = DownloadEntry.DownloadSatus.completed;
//        DataChanger.getInstance().postStatus(downloadEntry);
        message = mHandler.obtainMessage();
        message.obj = downloadEntry;
        mHandler.sendMessage(message);

    }

    @Override
    public void run() {
        start();
    }
}
