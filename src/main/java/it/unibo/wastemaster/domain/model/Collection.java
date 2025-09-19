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
 * Represents a waste collection linked to a customer and a schedule. Contains
 * information
 * about the waste type, scheduled date, collection status, and other relevant
 * details for
 * managing the collection.
 */
@Entity
@Table(name = "collections")
public class Collection {

    /**
     * Number of days allowed to cancel a collection.
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

    /** True if the collection has been billed, false otherwise. */
    private Boolean isBilled = false;

    /**
     * Trip associated with the collection (optional).
     */
    @ManyToOne
    @JoinColumn(name = "trip_id")
    private Trip trip;

    /**
     * Default constructor required by JPA.
     */
    public Collection() {
    }

    /**
     * Constructs a Collection based on a Schedule. Sets the date, waste type, and
     * customer based on the schedule.
     *
     * @param schedule the schedule linked to this collection
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
     * @return the unique collection identifier
     */
    public int getCollectionId() {
        return collectionId;
    }

    /**
     * @return the scheduled collection date
     */
    public LocalDate getCollectionDate() {
        return date;
    }

    /**
     * Sets the collection date.
     *
     * @param date the new collection date
     */
    public void setCollectionDate(final LocalDate date) {
        this.date = date;
    }

    /**
     * @return the customer associated with the collection
     */
    public Customer getCustomer() {
        return customer;
    }

    /**
     * @return the waste type
     */
    public Waste getWaste() {
        return waste;
    }

    /**
     * Sets the waste type.
     *
     * @param waste the new waste type
     */
    public void setWaste(final Waste waste) {
        this.waste = waste;
    }

    /**
     * @return the current status of the collection
     */
    public CollectionStatus getCollectionStatus() {
        return collectionStatus;
    }

    /**
     * Sets the collection status.
     *
     * @param collectionStatus the new status
     */
    public void setCollectionStatus(final CollectionStatus collectionStatus) {
        this.collectionStatus = collectionStatus;
    }

    /**
     * @return the cancellation limit days
     */
    public int getCancelLimitDays() {
        return cancelLimitDays;
    }

    /**
     * Sets the cancellation limit days.
     *
     * @param cancelLimitDays the new cancellation limit
     */
    public void setCancelLimitDays(final int cancelLimitDays) {
        this.cancelLimitDays = cancelLimitDays;
    }

    /**
     * Returns whether the collection has been billed.
     *
     * @return true if billed, false otherwise
     */
    public Boolean getIsBilled() {
        return isBilled;
    }

    /**
     * Sets whether the collection has been billed.
     *
     * @param isBilled true if billed, false otherwise
     */
    public void setIsBilled(final Boolean isBilled) {
        this.isBilled = isBilled;
    }

    /**
     * @return the schedule linked to the collection
     */
    public Schedule getSchedule() {
        return schedule;
    }

    /**
     * Returns a string representation of the collection.
     * <p>
     * Subclasses overriding this method should call super.toString() and append
     * their
     * additional information to maintain consistency.
     *
     * @return string representation of the collection
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
     * Possible statuses of the collection.
     */
    public enum CollectionStatus {
        ACTIVE, // raccolta attiva
        COMPLETED, // raccolta terminata
        CANCELLED // raccolta annullata
    }

}
