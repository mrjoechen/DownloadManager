package com.chenqiao.downloader;

import java.util.Observable;

public class DataChanger extends Observable {

    private static DataChanger mInstance;
    private DataChanger(){

    }

    public static synchronized DataChanger getInstance(){
        if (mInstance == null){
            mInstance = new DataChanger();
        }

        return mInstance;
    }

    public void postStatus(DownloadEntry entry){
        setChanged();
        notifyObservers(entry);
    }
}
