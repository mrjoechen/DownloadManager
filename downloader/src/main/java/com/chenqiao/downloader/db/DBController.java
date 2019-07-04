package com.chenqiao.downloader.db;

import android.content.Context;

import com.chenqiao.downloader.Constants;
import com.chenqiao.downloader.DownloadEntry;

import java.util.List;

public class DBController {

    private Context mContext;

    private DBController(){

    }

    public static DBController getInstance(){
        return Holder.Instance;
    }

    private static class Holder{
        static final DBController Instance = new DBController();
    }

    public synchronized void newOrUpdate(DownloadEntry entry){

    }
    public synchronized List<DownloadEntry> queryAll(){

        return null;
    }

    public synchronized DownloadEntry queryById(String id){

        return null;
    }

    public synchronized void remove(DownloadEntry entry){

    }

}
