package com.chenqiao.downloader;

import java.util.Observable;
import java.util.Observer;

public abstract class DataWatcher implements Observer {

    @Override
    public void update(Observable o, Object entry) {

        if (entry instanceof DownloadEntry){
            notifyUpdate((DownloadEntry)entry);
        }
    }

    public abstract void notifyUpdate(DownloadEntry entry);
}
