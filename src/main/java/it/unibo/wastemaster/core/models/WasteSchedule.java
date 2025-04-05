package it.unibo.wastemaster.core.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class WasteSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int scheduleId;

    @ManyToOne
    @JoinColumn(name = "waste_id")
    private Waste waste;

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


    public WasteSchedule(Waste waste, int dayOfWeek) {
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
        this.dayOfWeek = dayOfWeek;
    }
}
