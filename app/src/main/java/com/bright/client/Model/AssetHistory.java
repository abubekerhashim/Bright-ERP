package com.bright.client.Model;

public class AssetHistory {

    private String action;
    private String from;
    private String to;
    private long timestamp;

    public AssetHistory() {
        // Required for Firebase
    }

    public String getAction() {
        return action;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
