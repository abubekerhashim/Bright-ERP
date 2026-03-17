package com.bright.client.Model;

public class BankAccount {

    private String accountId;
    private String accountName;
    private String accountNumber;
    private Double balance;
    private String bankName;
    private String branch;
    private long timestamp;
    private boolean status;

    public BankAccount() {
    }

    public BankAccount(String accountId, String accountName, String accountNumber, Double balance, String bankName, String branch, long timestamp, boolean status) {
        this.accountId = accountId;
        this.accountName = accountName;
        this.accountNumber = accountNumber;
        this.balance = balance;
        this.bankName = bankName;
        this.branch = branch;
        this.timestamp = timestamp;
        this.status = status;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}
