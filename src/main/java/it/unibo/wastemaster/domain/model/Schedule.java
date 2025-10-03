package it.unibo.wastemaster.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

/**
 * Abstract base class for waste collection schedules, associated with a specific
 * customer and type of waste. Provides common attributes like status, creation date,
 * category (one-time or recurring), and linked collections.
 * <p>
 * Subclasses must implement {@link #getCollectionDate()} to define when the waste
 * collection occurs. This class uses single table inheritance with a discriminator
 * column "schedule_type".
 */
@Entity
@jakarta.persistence.Inheritance(
        strategy = jakarta.persistence.InheritanceType.SINGLE_TABLE)
@jakarta.persistence.DiscriminatorColumn(name = "schedule_type",
        discriminatorType = jakarta.persistence.DiscriminatorType.STRING)
public abstract class Schedule {

    /**
     * Unique identifier for the schedule.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    /**
     * Customer associated with the schedule. Cannot be null.
     */
    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    @NotNull(message = "Customer cannot be null")
    private Customer customer;

    /**
     * Waste type to be collected. Cannot be null.
     */
    @ManyToOne
    @JoinColumn(name = "waste_id", nullable = false)
    @NotNull(message = "WasteType cannot be null")
    private Waste waste;

    /**
     * Status of the schedule. Cannot be null.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull(message = "Status cannot be null")
    private ScheduleStatus status;

    /**
     * Date when the schedule was created. Cannot be null.
     */
    @Column(nullable = false)
    @NotNull(message = "CreationDate cannot be null")
    private LocalDate creationDate;

    /**
     * Collections linked to this schedule.
     */
    @Valid
    @OneToMany(mappedBy = "schedule")
    private List<Collection> collections;

    /**
     * Category of the schedule (one-time or recurring). Cannot be null.
     */
    @Enumerated(EnumType.STRING)
    @NotNull(message = "Schedule category cannot be null")
    @Column(nullable = false)
    private ScheduleCategory scheduleCategory;

    /**
     * Default constructor required by JPA.
     */
    protected Schedule() {
    }

    /**
     * Constructs a Schedule with the given customer and waste. Initializes status to
     * ACTIVE and creationDate to now.
     *
     * @param customer the customer associated with the schedule
     * @param waste the type of waste to be collected
     */
    protected Schedule(final Customer customer, final Waste waste) {
        this.customer = customer;
        this.waste = waste;
        this.status = ScheduleStatus.ACTIVE;
        this.creationDate = LocalDate.now();
    }

    /**
     * Abstract method to get the scheduled collection date. Must be implemented by
     * subclasses.
     *
     * @return the collection date
     */
    public abstract LocalDate getCollectionDate();

    /**
     * Gets the schedule's unique ID.
     *
     * @return schedule ID
     */
    public int getScheduleId() {
        return id;
    }

    /**
     * Gets the associated customer.
     *
     * @return customer
     */
    public Customer getCustomer() {
        return customer;
    }

    /**
     * Sets the associated customer.
     *
     * @param customer new customer
     */
    public void setCustomer(final Customer customer) {
        this.customer = customer;
    }

    /**
     * Gets the waste type.
     *
     * @return waste
     */
    public Waste getWaste() {
        return waste;
    }

    /**
     * Sets the waste type.
     *
     * @param waste new waste type
     */
    public void setWaste(final Waste waste) {
        this.waste = waste;
    }

    /**
     * Gets the schedule status.
     *
     * @return schedule status
     */
    public ScheduleStatus getScheduleStatus() {
        return status;
    }

    /**
     * Sets the schedule status.
     *
     * @param status new schedule status
     */
    public void setScheduleStatus(final ScheduleStatus status) {
        this.status = status;
    }

    /**
     * Gets the creation date of the schedule.
     *
     * @return creation date
     */
    public LocalDate getCreationDate() {
        return creationDate;
    }

    /**
     * Sets the creation date of the schedule.
     *
     * @param creationDate new creation date
     */
    public void setCreationDate(final LocalDate creationDate) {
        this.creationDate = creationDate;
    }

    /**
     * Gets the list of collections associated with this schedule.
     *
     * @return list of collections
     */
    public List<Collection> getCollections() {
        return collections;
    }

    /**
     * Sets the list of collections associated with this schedule.
     *
     * @param collections new list of collections
     */
    public void setCollections(final List<Collection> collections) {
        this.collections = collections;
    }

    /**
     * Gets the schedule category.
     *
     * @return schedule category
     */
    public ScheduleCategory getScheduleCategory() {
        return scheduleCategory;
    }

    /**
     * Sets the schedule category.
     *
     * @param scheduleCategory new schedule category
     */
    public void setScheduleCategory(final ScheduleCategory scheduleCategory) {
        this.scheduleCategory = scheduleCategory;
    }

    /**
     * Returns a string representation of the schedule. Includes category, customer name,
     * waste type, status, creation date and collection IDs.
     *
     * @return string describing the schedule
     */
    @Override
    public String toString() {
        return String.format("""
                        %s Schedule {Customer: %s, WasteType: %s,
                        Status: %s, CreationDate: %s, CollectionIDs: %s}
                        """,
                scheduleCategory != null ? scheduleCategory.name() : "Unknown",
                customer != null ? customer.getName() : "N/A",
                waste != null ? waste.getWasteName() : "N/A",
                status != null ? status.name() : "N/A",
                creationDate != null ? creationDate : "N/A",
                collections != null && !collections.isEmpty() ? collections.stream()
                        .map(c -> String.valueOf(c.getCollectionId()))
                        .reduce((a, b) -> a + ", " + b).orElse("N/A") : "None");
    }

    /**
     * Enum representing schedule categories.
     */
    public enum ScheduleCategory {
        /**
         * One-time schedule category.
         */
        ONE_TIME,

        /**
         * Recurring schedule category.
         */
        RECURRING
    }

    /**
     * Enum representing possible statuses of a schedule.
     */
    public enum ScheduleStatus {
        /**
         * Schedule is active and ongoing.
         */
        ACTIVE,

        /**
         * Schedule has been cancelled.
         */
        CANCELLED,

        /**
         * Schedule is temporarily paused.
         */
        PAUSED,

        /**
         * Schedule is completed.
         */
        COMPLETED
    }
}
