package it.unibo.wastemaster.core.models;

import java.util.Date;

public class Collection {
    private int id;
    private Customer customer;
    private Date date;
    private String status;
    private String type;
    private int cancelLimitDays;

    public Collection(int id, Customer customer, Date date, String status, String type) {
        this.id = id;
        this.customer = customer;
        this.date = date;
        this.status = status;
        this.type = type;
        this.cancelLimitDays = cancelLimitDays;
    }

    public int getCancelPreNoticeDays() {
        return cancelLimitDays;
    }

    public int getId() {
        return id;
    }

    public Customer getCustomer() {
        return customer;
    }

    public Date getDate() {
        return date;
    }

    public String getStatus() {
        return status;
    }

    public String getType() {
        return type;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void scheduleCollection() {
        this.status = "Scheduled";
    }

    public void updateStatus(String newStatus) {
        this.status = newStatus;
    }

    public void cancelCollection() {
        this.status = "Cancelled";
    }
}
