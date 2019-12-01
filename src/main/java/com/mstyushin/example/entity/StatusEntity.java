package com.mstyushin.example.entity;

public class StatusEntity extends BaseEntity {
    private String currentStatus;

    public StatusEntity(String currentStatus) {
        this.currentStatus = currentStatus;
    }

    public String getCurrentStatus() {
        return currentStatus;
    }

    public void setCurrentStatus(String currentStatus) {
        this.currentStatus = currentStatus;
    }
}
