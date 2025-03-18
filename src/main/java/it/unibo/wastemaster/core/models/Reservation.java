package it.unibo.wastemaster.core.models;

import java.util.Date;

public class Reservation {
    private int id;
    private Customer customer;
    private Date date;
    private String status;

    public Reservation(int id, Customer customer, Date date, String status) {
        this.id = id;
        this.customer = customer;
        this.date = date;
        this.status = status;
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

    public void setStatus(String status) {
        this.status = status;
    }

    public void confirmReservation() {
        this.status = "Confirmed";
    }

    public void cancelReservation() {
        this.status = "Cancelled";
    }
}
