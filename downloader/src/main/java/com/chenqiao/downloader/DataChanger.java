package com.chenqiao.downloader;

import android.content.Context;

import com.chenqiao.downloader.db.DBController;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Observable;

public class DataChanger extends Observable {

    private static DataChanger mInstance;
    private final Context mContext;

    private LinkedHashMap<String, DownloadEntry> mOperatedEntries;
    private DataChanger(Context context){
        mContext = context;
        mOperatedEntries = new LinkedHashMap<>();
    }

    public static synchronized DataChanger getInstance(Context context){
        if (mInstance == null){
            mInstance = new DataChanger(context);
        }

        return mInstance;
    }

    public void postStatus(DownloadEntry entry){

        mOperatedEntries.put(entry.id, entry);
        DBController.getInstance().newOrUpdate(entry);
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

    public DownloadEntry queryDownloadEntryById(String id){
        return mOperatedEntries.get(id);
    }

    public void addToOperatedEntryMap(String key, DownloadEntry entry){
        mOperatedEntries.put(key, entry);
    }
}
