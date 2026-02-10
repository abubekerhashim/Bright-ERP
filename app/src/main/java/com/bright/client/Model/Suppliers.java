package com.bright.client.Model;

public class Suppliers {

    private String supplierId;
    private String name;
    private String accName;
    private String accNum;
    private String address;
    private String categories;
    private String userId;
    private String registeredByName;
    private String registeredByPhone;
    private boolean status;

    public Suppliers() {
    }

    public Suppliers(String supplierId, String name, String accName, String accNum, String address, String categories, String userId, String registeredByName, String registeredByPhone, boolean status) {
        this.supplierId = supplierId;
        this.name = name;
        this.accName = accName;
        this.accNum = accNum;
        this.address = address;
        this.categories = categories;
        this.userId = userId;
        this.registeredByName = registeredByName;
        this.registeredByPhone = registeredByPhone;
        this.status = status;
    }

    public String getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(String supplierId) {
        this.supplierId = supplierId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAccName() {
        return accName;
    }

    public void setAccName(String accName) {
        this.accName = accName;
    }

    public String getAccNum() {
        return accNum;
    }

    public void setAccNum(String accNum) {
        this.accNum = accNum;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCategories() {
        return categories;
    }

    public void setCategories(String categories) {
        this.categories = categories;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getRegisteredByName() {
        return registeredByName;
    }

    public void setRegisteredByName(String registeredByName) {
        this.registeredByName = registeredByName;
    }

    public String getRegisteredByPhone() {
        return registeredByPhone;
    }

    public void setRegisteredByPhone(String registeredByPhone) {
        this.registeredByPhone = registeredByPhone;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}
