package com.chenqiao.downloader;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Observable;

public class DataChanger extends Observable {

    private static DataChanger mInstance;

    private LinkedHashMap<String, DownloadEntry> mOperatedEntries;
    private DataChanger(){
        mOperatedEntries = new LinkedHashMap<>();
    }

    public static synchronized DataChanger getInstance(){
        if (mInstance == null){
            mInstance = new DataChanger();
        }

        return mInstance;
    }

    public void postStatus(DownloadEntry entry){

        mOperatedEntries.put(entry.id, entry);
        setChanged();
        notifyObservers(entry);
    }

    public ArrayList<DownloadEntry> queryAllRecoverableEntries(){

        ArrayList<DownloadEntry> mRecoverableEntries = null;
        for (Map.Entry<String, DownloadEntry> entryEntry : mOperatedEntries.entrySet()){
            if (entryEntry.getValue().status == DownloadEntry.DownloadSatus.paused){
                if (mRecoverableEntries == null){
                    mRecoverableEntries = new ArrayList<>();
                }
                mRecoverableEntries.add(entryEntry.getValue());
            }
        }
        return mRecoverableEntries;
    }
}
