package com.bright.client.Model;

public class Employee {

   private String employeeId, firstName, middleName, lastName, imageUrl, position, userId, gender;
   private String password;
   private int rateCount;
   private double rateAverage;
   private String city, subCity, houseNo;
   private long regDate;
   private boolean suspend;

    public Employee() {
    }

    public Employee(String employeeId, String firstName, String middleName, String lastName, String imageUrl, String position, String userId, String gender, String password, int rateCount, double rateAverage, String city, String subCity, String houseNo, long regDate, boolean suspend) {
        this.employeeId = employeeId;
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.imageUrl = imageUrl;
        this.position = position;
        this.userId = userId;
        this.gender = gender;
        this.password = password;
        this.rateCount = rateCount;
        this.rateAverage = rateAverage;
        this.city = city;
        this.subCity = subCity;
        this.houseNo = houseNo;
        this.regDate = regDate;
        this.suspend = suspend;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getRateCount() {
        return rateCount;
    }

    public void setRateCount(int rateCount) {
        this.rateCount = rateCount;
    }

    public double getRateAverage() {
        return rateAverage;
    }

    public void setRateAverage(double rateAverage) {
        this.rateAverage = rateAverage;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getSubCity() {
        return subCity;
    }

    public void setSubCity(String subCity) {
        this.subCity = subCity;
    }

    public String getHouseNo() {
        return houseNo;
    }

    public void setHouseNo(String houseNo) {
        this.houseNo = houseNo;
    }

    public long getRegDate() {
        return regDate;
    }

    public void setRegDate(long regDate) {
        this.regDate = regDate;
    }

    public boolean isSuspend() {
        return suspend;
    }

    public void setSuspend(boolean suspend) {
        this.suspend = suspend;
    }
}
