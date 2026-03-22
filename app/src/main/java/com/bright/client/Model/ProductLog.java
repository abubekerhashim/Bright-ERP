package com.bright.client.Model;

public class ProductLog {

    private double afterQty;
    private double beforeQty;
    private double qty;
    private long timestamp;
    private String orderId;
    private String prodId;
    private String source;
    private String type;


    public ProductLog() {
    }

    public ProductLog(double afterQty, double beforeQty, double qty, long timestamp, String orderId, String prodId, String source, String type) {
        this.afterQty = afterQty;
        this.beforeQty = beforeQty;
        this.qty = qty;
        this.timestamp = timestamp;
        this.orderId = orderId;
        this.prodId = prodId;
        this.source = source;
        this.type = type;
    }

    public double getAfterQty() {
        return afterQty;
    }

    public void setAfterQty(double afterQty) {
        this.afterQty = afterQty;
    }

    public double getBeforeQty() {
        return beforeQty;
    }

    public void setBeforeQty(double beforeQty) {
        this.beforeQty = beforeQty;
    }

    public double getQty() {
        return qty;
    }

    public void setQty(double qty) {
        this.qty = qty;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getProdId() {
        return prodId;
    }

    public void setProdId(String prodId) {
        this.prodId = prodId;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
