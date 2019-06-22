package com.chenqiao.downloader;

import java.io.Serializable;

public class DownloadEntry implements Serializable {

    public String id;
    public String name;
    public String url;

    public enum DownloadSatus{waiting, downloading, pause, resume, cancel}
    public DownloadSatus downloadSatus;

    public int currentlength;
    public int totalLength;


}
