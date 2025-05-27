package it.unibo.wastemaster.viewmodels;

import java.time.DayOfWeek;

import it.unibo.wastemaster.core.models.Waste;
import it.unibo.wastemaster.core.models.WasteSchedule;

public class WasteRow {
    private final Waste waste;
    private final String name;
    private final boolean recyclable;
    private final boolean dangerous;
    private final DayOfWeek dayOfWeek;

    public WasteRow(Waste waste, WasteSchedule schedule) {
        this.waste = waste;
        this.name = waste.getWasteName();
        this.recyclable = waste.getIsRecyclable();
        this.dangerous = waste.getIsDangerous();
        this.dayOfWeek = schedule != null ? schedule.getDayOfWeek() : null;
    }

    public Waste getWaste() {
        return waste;
    }

    public String getName() {
        return name;
    }

    public boolean isRecyclable() {
        return recyclable;
    }

    public boolean isDangerous() {
        return dangerous;
    }

    public DayOfWeek getDayOfWeek() {
        return dayOfWeek;
    }
}