package com.bright.client.Model;

public class Warehouse {

    private String code;
    private String name;
    private String location;
    private boolean status;

    public Warehouse() {
        // Required for Firebase
    }

    public Warehouse(String code, String name, String location, boolean status) {
        this.code = code;
        this.name = name;
        this.location = location;
        this.status = status;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}
