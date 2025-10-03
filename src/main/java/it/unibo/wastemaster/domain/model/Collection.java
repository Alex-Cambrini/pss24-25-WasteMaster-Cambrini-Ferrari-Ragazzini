package it.unibo.wastemaster.domain.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * The type Collection.
 */
@Entity
@Table(name = "collections")
public final class Collection {

    /**
     * The constant CANCEL_LIMIT_DAYS.
     */
    public static final int CANCEL_LIMIT_DAYS = 2;

    /**
     * Unique identifier of the collection.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int collectionId;

    /**
     * Customer associated with the collection.
     */
    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "customer_id", nullable = false)
    @NotNull(message = "The customer cannot be null")
    private Customer customer;

    /**
     * Scheduled date of the collection (today or future).
     */
    @FutureOrPresent(message = "The date must be today or in the future")
    @NotNull(message = "The date cannot be null")
    @Column(nullable = false)
    private LocalDate date;

    /**
     * Type of waste to be collected.
     */
    @ManyToOne
    @NotNull(message = "The waste type cannot be null")
    @JoinColumn(name = "waste_id", nullable = false)
    private Waste waste;

    /**
     * Current status of the collection.
     */
    @Enumerated(EnumType.STRING)
    @NotNull(message = "The collection status cannot be null")
    @Column(nullable = false)
    private CollectionStatus collectionStatus;

    /**
     * Number of days allowed for cancellation.
     */
    @Min(value = 0, message = "Cancellation days must be >= 0")
    @Column(nullable = false)
    private int cancelLimitDays;

    /**
     * Schedule associated with the collection.
     */
    @ManyToOne
    @JoinColumn(name = "schedule_id", nullable = false)
    @NotNull(message = "Schedule cannot be null")
    private Schedule schedule;

    /**
     * True if the collection has been billed, false otherwise.
     */
    @Column(nullable = false)
    private boolean isBilled = false;

    /**
     * Trip associated with the collection (optional).
     */
    @ManyToOne
    @JoinColumn(name = "trip_id")
    private Trip trip;

    /**
     * Default no-argument constructor required by JPA.
     */
    public Collection() {
    }

    /**
     * Instantiates a new Collection.
     *
     * @param schedule the schedule
     */
    public Collection(final Schedule schedule) {
        this.schedule = schedule;
        if (schedule != null) {
            this.date = schedule.getCollectionDate();
            this.waste = schedule.getWaste();
            this.customer = schedule.getCustomer();
        }
        this.collectionStatus = CollectionStatus.ACTIVE;
        this.cancelLimitDays = CANCEL_LIMIT_DAYS;
    }

    /**
     * Gets collection id.
     *
     * @return the collection id
     */
    public int getCollectionId() {
        return collectionId;
    }

    /**
     * Gets collection date.
     *
     * @return the collection date
     */
    public LocalDate getCollectionDate() {
        return date;
    }

    /**
     * Sets collection date.
     *
     * @param date the date
     */
    public void setCollectionDate(final LocalDate date) {
        this.date = date;
    }

    /**
     * Gets customer.
     *
     * @return the customer
     */
    public Customer getCustomer() {
        return customer;
    }

    /**
     * Gets waste.
     *
     * @return the waste
     */
    public Waste getWaste() {
        return waste;
    }

    /**
     * Sets waste.
     *
     * @param waste the waste
     */
    public void setWaste(final Waste waste) {
        this.waste = waste;
    }

    /**
     * Gets collection status.
     *
     * @return the collection status
     */
    public CollectionStatus getCollectionStatus() {
        return collectionStatus;
    }

    /**
     * Sets collection status.
     *
     * @param collectionStatus the collection status
     */
    public void setCollectionStatus(final CollectionStatus collectionStatus) {
        this.collectionStatus = collectionStatus;
    }

    /**
     * Gets cancel limit days.
     *
     * @return the cancel limit days
     */
    public int getCancelLimitDays() {
        return cancelLimitDays;
    }

    /**
     * Sets cancel limit days.
     *
     * @param cancelLimitDays the cancel limit days
     */
    public void setCancelLimitDays(final int cancelLimitDays) {
        this.cancelLimitDays = cancelLimitDays;
    }

    /**
     * Gets is billed.
     *
     * @return the is billed
     */
    public Boolean getIsBilled() {
        return isBilled;
    }

    /**
     * Sets is billed.
     *
     * @param isBilled the is billed
     */
    public void setIsBilled(final Boolean isBilled) {
        this.isBilled = isBilled;
    }

    /**
     * Gets trip.
     *
     * @return the trip
     */
    public Trip getTrip() {
        return trip;
    }

    /**
     * Sets trip.
     *
     * @param trip the trip
     */
    public void setTrip(final Trip trip) {
        this.trip = trip;
    }

    /**
     * Gets schedule.
     *
     * @return the schedule
     */
    public Schedule getSchedule() {
        return schedule;
    }

    /**
     * Returns a string representation of the collection, including key details:
     * collection ID, customer name, collection date, waste type, collection status,
     * cancel limit days, schedule ID, and schedule category.
     * Null values are replaced with "N/A".
     *
     * @return formatted string describing the collection
     */
    @Override
    public String toString() {
        return String.format("""
                Collection {ID: %d, Customer: %s, Date: %s, Waste: %s, Status: %s,
                Cancel Limit Days: %d, Schedule ID: %s, Schedule Category: %s}""",
                collectionId, customer != null ? customer.getName() : "N/A", date,
                waste != null ? waste.getWasteName() : "N/A", collectionStatus,
                cancelLimitDays, schedule != null ? schedule.getScheduleId() : "N/A",
                schedule != null ? schedule.getScheduleCategory() : "N/A");
    }

    /**
     * The enum Collection status.
     */
    public enum CollectionStatus {
        /**
         * Active collection status.
         */
        ACTIVE,
        /**
         * Completed collection status.
         */
        COMPLETED,
        /**
         * Cancelled collection status.
         */
        CANCELLED
    }
}
