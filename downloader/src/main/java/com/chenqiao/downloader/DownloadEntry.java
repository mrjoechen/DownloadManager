package com.chenqiao.downloader;

import java.io.Serializable;

public class DownloadEntry implements Serializable {

    public String id;
    public String name;
    public String url;

    public enum DownloadSatus {
        idle, waiting, downloading, paused, resumed, cancelled, completed
    }

    public DownloadSatus status;

    public int currentlength;
    public int totalLength;

    @Override
    public String toString() {
        return "DownloadEntry: "+ url + " is " + status.name() + " with " + currentlength +"/"+totalLength;
    }

    @Override
    public boolean equals(Object obj) {
        return obj.hashCode() == this.hashCode();
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
