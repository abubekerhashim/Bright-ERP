package com.bright.client.Model;

public class Asset {

    private String id;
    private String name;
    private String category;
    private String location;
    private Double regDate;
    private String status;
    private String assignedTo;
    private String assigneeName;
    private String detail;
    private String imageUrl;
    private String type;

    private String pendingUserId;
    private String pendingUserName;


    // 🔹 Assignment fields (NEW)
    private String assignmentStatus;   // pending | accepted | rejected
    private Long assignedAt;
    private String assignedBy;
    private String assignedById;

    public Asset() {
        // Required empty constructor
    }


    public Asset(String id, String name, String category, String location, Double regDate, String status, String assignedTo, String assigneeName, String detail, String imageUrl, String type, String pendingUserId, String pendingUserName, String assignmentStatus, Long assignedAt, String assignedBy, String assignedById) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.location = location;
        this.regDate = regDate;
        this.status = status;
        this.assignedTo = assignedTo;
        this.assigneeName = assigneeName;
        this.detail = detail;
        this.imageUrl = imageUrl;
        this.type = type;
        this.pendingUserId = pendingUserId;
        this.pendingUserName = pendingUserName;
        this.assignmentStatus = assignmentStatus;
        this.assignedAt = assignedAt;
        this.assignedBy = assignedBy;
        this.assignedById = assignedById;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Double getRegDate() {
        return regDate;
    }

    public void setRegDate(Double regDate) {
        this.regDate = regDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(String assignedTo) {
        this.assignedTo = assignedTo;
    }

    public String getAssigneeName() {
        return assigneeName;
    }

    public void setAssigneeName(String assigneeName) {
        this.assigneeName = assigneeName;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPendingUserId() {
        return pendingUserId;
    }

    public void setPendingUserId(String pendingUserId) {
        this.pendingUserId = pendingUserId;
    }

    public String getPendingUserName() {
        return pendingUserName;
    }

    public void setPendingUserName(String pendingUserName) {
        this.pendingUserName = pendingUserName;
    }

    public String getAssignmentStatus() {
        return assignmentStatus;
    }

    public void setAssignmentStatus(String assignmentStatus) {
        this.assignmentStatus = assignmentStatus;
    }

    public Long getAssignedAt() {
        return assignedAt;
    }

    public void setAssignedAt(Long assignedAt) {
        this.assignedAt = assignedAt;
    }

    public String getAssignedBy() {
        return assignedBy;
    }

    public void setAssignedBy(String assignedBy) {
        this.assignedBy = assignedBy;
    }

    public String getAssignedById() {
        return assignedById;
    }

    public void setAssignedById(String assignedById) {
        this.assignedById = assignedById;
    }
}
