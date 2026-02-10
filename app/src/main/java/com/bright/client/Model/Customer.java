package com.bright.client.Model;

public class Customer {

    private String customerId;
    private String name;
    private String accName;
    private String accNum;
    private String address;
    private String type;
    private String registeredByName;
    private String registeredByPhone;
    private String userId;
    private boolean status;

    public Customer() {
    }

    public Customer(String customerId, String name, String accName, String accNum, String address, String type, String registeredByName, String registeredByPhone, String userId, boolean status) {
        this.customerId = customerId;
        this.name = name;
        this.accName = accName;
        this.accNum = accNum;
        this.address = address;
        this.type = type;
        this.registeredByName = registeredByName;
        this.registeredByPhone = registeredByPhone;
        this.userId = userId;
        this.status = status;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}
