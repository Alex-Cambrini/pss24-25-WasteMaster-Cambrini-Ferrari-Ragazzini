package it.unibo.wastemaster.core.models;

import java.util.Date;
public class Collection {
    private int id;
    private Customer customer;
    private Date date;
    private String status;
    private String type;
    private int cancelLimitDays;
    private Integer scheduleId;  // Riferimento alla programmazione periodica (ID nel DB, null se non periodica)

    public Collection(int id, Customer customer, Date date, String status, String type, int cancelLimitDays, Integer scheduleId) {
        this.id = id;
        this.customer = customer;
        this.date = date;
        this.status = status;
        this.type = type;
        this.cancelLimitDays = cancelLimitDays;
        this.scheduleId = scheduleId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getCancelLimitDays() {
        return cancelLimitDays;
    }

    public void setCancelLimitDays(int cancelLimitDays) {
        this.cancelLimitDays = cancelLimitDays;
    }

    public Integer getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(Integer scheduleId) {
        this.scheduleId = scheduleId;
    }
}
