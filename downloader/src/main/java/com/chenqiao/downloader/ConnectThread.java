package com.chenqiao.downloader;

public class ConnectThread implements Runnable {

    private final DownloadEntry entry;

    public ConnectThread(DownloadEntry entry){
        this.entry = entry;
    }

    @Override
    public void run() {

    }
}
