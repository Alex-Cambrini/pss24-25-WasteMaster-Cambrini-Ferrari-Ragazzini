package it.unibo.wastemaster.core.models;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "reservation_extra")
public class ReservationExtra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int reservationId;

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @Temporal(TemporalType.TIMESTAMP)
    private Date reservationDate;

    @Enumerated(EnumType.STRING)
    private ReservationStatus status;

    @Enumerated(EnumType.STRING)
    private Waste.WasteType wasteType;

    @OneToOne
    @JoinColumn(name = "collection_id", unique = true)
    private Collection collection;

    public enum ReservationStatus {
        PENDING,
        CONFIRMED,
        CANCELLED
    }

    public ReservationExtra(Customer customer, Date reservationDate, ReservationStatus status, Waste.WasteType wasteType) {
        this.customer = customer;
        this.reservationDate = reservationDate;
        this.status = status;
        this.wasteType = wasteType;
    }

    public ReservationExtra() {}
    
    // Getter e Setter
    public int getReservationId() {
        return reservationId;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Date getReservationDate() {
        return reservationDate;
    }

    public void setReservationDate(Date reservationDate) {
        this.reservationDate = reservationDate;
    }

    public ReservationStatus getStatus() {
        return status;
    }

    public void setStatus(ReservationStatus status) {
        this.status = status;
    }

    public Waste.WasteType getWasteType() {
        return wasteType;
    }

    public void setWasteType(Waste.WasteType wasteType) {
        this.wasteType = wasteType;
    }

    public Collection getCollection() {
        return collection;
    }

    public void setCollection(Collection collection) {
        this.collection = collection;
    }

    @Override
    public String toString() {
        return String.format(
            "ReservationExtra {ID: %d, Customer: %s, Date: %s, Status: %s, Waste Type: %s, Collection ID: %s}",
            reservationId,
            customer != null ? customer.getName() : "N/A",
            reservationDate != null ? reservationDate.toString() : "N/A",
            status,
            wasteType,
            collection != null ? collection.getCollectionId() : "N/A"
        );
    }
}
