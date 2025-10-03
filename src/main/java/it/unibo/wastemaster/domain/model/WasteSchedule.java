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
import jakarta.validation.constraints.NotNull;
import java.time.DayOfWeek;

/**
 * Represents a weekly collection schedule for a specific type of waste.
 */
@Entity
public final class WasteSchedule {

    /**
     * Unique identifier for the schedule, auto-generated.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int scheduleId;

    /**
     * The waste associated with this schedule. Cannot be null.
     */
    @ManyToOne
    @JoinColumn(name = "waste_id", nullable = false)
    @NotNull(message = "Waste must not be null")
    private Waste waste;

    /**
     * Day of the week when the waste is collected. Cannot be null.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull(message = "dayOfWeek must not be null")
    private DayOfWeek dayOfWeek;

    /**
     * Default constructor.
     */
    public WasteSchedule() {
    }

    /**
     * Constructs a schedule with specified waste and day.
     *
     * @param waste the associated waste
     * @param dayOfWeek the day of collection
     */
    public WasteSchedule(final Waste waste, final DayOfWeek dayOfWeek) {
        this.waste = waste;
        this.dayOfWeek = dayOfWeek;
    }

    /**
     * @return the ID of the schedule
     */
    public int getScheduleId() {
        return scheduleId;
    }

    /**
     * @return the associated waste
     */
    public Waste getWaste() {
        return waste;
    }

    /**
     * Sets the associated waste.
     *
     * @param waste the waste to set
     */
    public void setWaste(final Waste waste) {
        this.waste = waste;
    }

    /**
     * @return the day of collection
     */
    public DayOfWeek getDayOfWeek() {
        return dayOfWeek;
    }

    /**
     * Sets the collection day.
     *
     * @param dayOfWeek the day to set
     */
    public void setDayOfWeek(final DayOfWeek dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }
}
