package it.unibo.wastemaster.core.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;
import java.time.DayOfWeek;

@Entity
public class WasteSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int scheduleId;

    @ManyToOne
    @JoinColumn(name = "waste_id", nullable = false)
    @NotNull(message = "Waste must not be null")
    private Waste waste;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull(message = "dayOfWeek must not be null")
    private DayOfWeek dayOfWeek;

    
    // DayOfWeek enum (java.time.DayOfWeek) mapping:
     
    // monday    -> 1
    // tuesday   -> 2
    // wednesday -> 3
    // thursday  -> 4
    // friday    -> 5
    // saturday  -> 6
    // sunday    -> 7
    


    public WasteSchedule() {
    }

    public WasteSchedule(Waste waste, DayOfWeek dayOfWeek) {
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

    public DayOfWeek getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(DayOfWeek dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    @Override
    public String toString() {
        return "WasteSchedule{" +
                "scheduleId=" + scheduleId +
                ", dayOfWeek=" + (dayOfWeek != null ? dayOfWeek.name() : "N/A") +
                ", waste=" + (waste != null ? waste.toString() : "N/A") +
                '}';
    }
}