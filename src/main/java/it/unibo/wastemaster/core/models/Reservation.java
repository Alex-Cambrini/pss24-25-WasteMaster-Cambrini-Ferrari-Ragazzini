package it.unibo.wastemaster.core.models;

import java.util.Date;

public class Reservation {
    private int reservationId;
    private Customer customer;
    private Date bookingDate;
    private Date pickupDate;
    private ReservationStatus status;

    public enum ReservationStatus {
        PENDING,
        CONFIRMED,
        CANCELLED
    }

    public Reservation(int reservationId, Customer customer, Date bookingDate, Date pickupDate, ReservationStatus status) {
        this.reservationId = reservationId;
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


    //test
    // public static void main(String[] args) {
    //     Customer customer = new Customer(1, "John", "Doe", new Location(1, "Street", "10", "City", "Country"), "john@example.com", "1234567890", 123);
    //     Date bookingDate = new Date();
    //     Date pickupDate = new Date(System.currentTimeMillis() + 86400000L);

    //     Reservation reservation = new Reservation(1, customer, bookingDate, pickupDate, ReservationStatus.PENDING);

    //     if (reservation.getReservationId() == 1 && reservation.getCustomer().equals(customer) && reservation.getBookingDate().equals(bookingDate)
    //             && reservation.getPickupDate().equals(pickupDate) && reservation.getStatus() == ReservationStatus.PENDING) {
    //         System.out.println("ok");
    //     } else {
    //         System.out.println("error");
    //     }

    //     reservation.confirmReservation();
    //     if (reservation.getStatus() == ReservationStatus.CONFIRMED) {
    //         System.out.println("ok");
    //     } else {
    //         System.out.println("error");
    //     }

    //     reservation.cancelReservation();
    //     if (reservation.getStatus() == ReservationStatus.CANCELLED) {
    //         System.out.println("ok");
    //     } else {
    //         System.out.println("error");
    //     }

    //     String info = reservation.getInfo();
    //     System.out.println("Expected output of getInfo:");
    //     System.out.println("Reservation ID: 1, Customer: John, Booking Date: " + bookingDate.toString() + ", Pickup Date: " + pickupDate.toString() + ", Status: CANCELLED");

    //     System.out.println("Actual output of getInfo:");
    //     System.out.println(info);
    // }
}
