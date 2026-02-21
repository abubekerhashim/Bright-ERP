package com.bright.client.Model;

public class Product {

    private String prodId;
    private String name;
    private String model;
    private String category;
    private String catId;
    private String color;
    private String imageUrl;
    private String unit;
    private double costPrice, sellPrice;
    private boolean status;
    private int totalQty;

    public Product() {

    }

    public Product(String prodId, String name, String model, String category, String catId, String color, String imageUrl, String unit, double costPrice, double sellPrice, boolean status, int totalQty) {
        this.prodId = prodId;
        this.name = name;
        this.model = model;
        this.category = category;
        this.catId = catId;
        this.color = color;
        this.imageUrl = imageUrl;
        this.unit = unit;
        this.costPrice = costPrice;
        this.sellPrice = sellPrice;
        this.status = status;
        this.totalQty = totalQty;
    }

    public String getProdId() {
        return prodId;
    }

    public void setProdId(String prodId) {
        this.prodId = prodId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCatId() {
        return catId;
    }

    public void setCatId(String catId) {
        this.catId = catId;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public double getCostPrice() {
        return costPrice;
    }

    public void setCostPrice(double costPrice) {
        this.costPrice = costPrice;
    }

    public double getSellPrice() {
        return sellPrice;
    }

    public void setSellPrice(double sellPrice) {
        this.sellPrice = sellPrice;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public int getTotalQty() {
        return totalQty;
    }

    public void setTotalQty(int totalQty) {
        this.totalQty = totalQty;
    }
}
