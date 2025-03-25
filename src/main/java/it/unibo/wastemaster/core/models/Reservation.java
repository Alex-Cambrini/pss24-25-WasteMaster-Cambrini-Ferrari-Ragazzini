package it.unibo.wastemaster.core.models;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.util.Date;

@Entity
@Table(name = "reservation")
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int reservationId;
    
    @ManyToOne
    private Customer customer;

    private Date bookingDate;
    private Date pickupDate;

    @Enumerated(EnumType.STRING)
    private ReservationStatus status;

    public enum ReservationStatus {
        PENDING,
        CONFIRMED,
        CANCELLED
    }

    public Reservation(Customer customer, Date bookingDate, Date pickupDate, ReservationStatus status) {
        this.customer = customer;
        this.bookingDate = bookingDate;
        this.pickupDate = pickupDate;
        this.status = status;
    }

    public int getReservationId() {
        return reservationId;
    }

    public Customer getCustomer() {
        return customer;
    }

    public Date getBookingDate() {
        return bookingDate;
    }

    public Date getPickupDate() {
        return pickupDate;
    }

    public ReservationStatus getStatus() {
        return status;
    }

    public void setStatus(ReservationStatus status) {
        this.status = status;
    }

    public void confirmReservation() {
        this.status = ReservationStatus.CONFIRMED;
    }

    public void cancelReservation() {
        this.status = ReservationStatus.CANCELLED;
    }

    public String getInfo() {
        return String.format("Reservation ID: %d, Customer: %s, Booking Date: %s, Pickup Date: %s, Status: %s",
                reservationId, customer.getName(), bookingDate.toString(), pickupDate.toString(), status);
    }

}
