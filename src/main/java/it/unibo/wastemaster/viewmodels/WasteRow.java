package it.unibo.wastemaster.viewmodels;

import it.unibo.wastemaster.core.models.Waste;
import it.unibo.wastemaster.core.models.WasteSchedule;
import java.time.DayOfWeek;

/**
 * ViewModel class that represents a waste with its collection schedule.
 */
public final class WasteRow {

    private final Waste waste;
    private final String name;
    private final boolean recyclable;
    private final boolean dangerous;
    private final DayOfWeek dayOfWeek;

    /**
     * Constructs a WasteRow with waste and its schedule.
     *
     * @param finalWaste the waste object
     * @param finalSchedule the optional schedule
     */
    public WasteRow(final Waste finalWaste, final WasteSchedule finalSchedule) {
        this.waste = finalWaste;
        this.name = finalWaste.getWasteName();
        this.recyclable = finalWaste.getIsRecyclable();
        this.dangerous = finalWaste.getIsDangerous();
        this.dayOfWeek = finalSchedule != null ? finalSchedule.getDayOfWeek() : null;
    }

    /**
     * Returns the waste entity.
     *
     * @return the Waste object
     */
    public Waste getWaste() {
        return waste;
    }

    /**
     * Returns the name of the waste.
     *
     * @return the waste name
     */
    public String getName() {
        return name;
    }

    /**
     * Indicates if the waste is recyclable.
     *
     * @return true if recyclable, false otherwise
     */
    public boolean isRecyclable() {
        return recyclable;
    }

    /**
     * Indicates if the waste is dangerous.
     *
     * @return true if dangerous, false otherwise
     */
    public boolean isDangerous() {
        return dangerous;
    }

    /**
     * Returns the scheduled day of the week for collection.
     *
     * @return the DayOfWeek, or null if not scheduled
     */
    public DayOfWeek getDayOfWeek() {
        return dayOfWeek;
    }
}
