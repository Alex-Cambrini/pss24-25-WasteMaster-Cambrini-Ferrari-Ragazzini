package it.unibo.wastemaster.core.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Entity
public class WasteSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int scheduleId;

    @ManyToOne
    @JoinColumn(name = "waste_id", nullable = false)
    @NotNull(message = "Waste must not be null")
    private Waste waste;

    @Column(nullable = false)
    @Min(value = 1, message = "dayOfWeek must be between 1 (Sunday) and 7 (Saturday)")
	@Max(value = 7, message = "dayOfWeek must be between 1 (Sunday) and 7 (Saturday)")
    private int dayOfWeek;

    /*
     * 1 = Domenica
     * 
     * 2 = Lunedì
     * 
     * 3 = Martedì
     * 
     * 4 = Mercoledì
     * 
     * 5 = Giovedì
     * 
     * 6 = Venerdì
     * 
     * 7 = Sabato
     */

    public WasteSchedule() {
    }

    public WasteSchedule(Waste waste, int dayOfWeek) {
        validateDayOfWeek(dayOfWeek);

        this.waste = waste;
        this.dayOfWeek = dayOfWeek;
    }

    public int getScheduleId() {
        return scheduleId;
    }

    public Waste getWaste() {
        return waste;
    }

    public void setWaste(Waste waste) {
        this.waste = waste;
    }

    public int getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(int dayOfWeek) {
        validateDayOfWeek(dayOfWeek);
        this.dayOfWeek = dayOfWeek;
    }

    private void validateDayOfWeek(int dayOfWeek) {
        if (dayOfWeek < 1 || dayOfWeek > 7) {
            throw new IllegalArgumentException("dayOfWeek must be between 1 (Sunday) and 7 (Saturday)");
        }
    }

    @Override
    public String toString() {
        return "WasteSchedule{" +
                "scheduleId=" + scheduleId +
                ", dayOfWeek=" + dayOfWeek +
                ", waste=" + (waste != null ? waste.toString() : "N/A") +
                '}';
    }
}
