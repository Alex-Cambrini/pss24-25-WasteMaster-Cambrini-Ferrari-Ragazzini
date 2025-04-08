package it.unibo.wastemaster.core.models;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Waste {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int wasteId;

    @Enumerated(EnumType.STRING)
    private WasteType type;

    private Boolean isRecyclable;
    private Boolean isDangerous;

    public enum WasteType {
        PLASTIC, 
        GLASS, 
        PAPER, 
        ORGANIC, 
        HAZARDOUS, 
        UNSORTED
    }

    public Waste() {
    }

    public Waste(WasteType type, Boolean isRecyclable, Boolean isDangerous) {
        this.type = type;
        this.isRecyclable = isRecyclable;
        this.isDangerous = isDangerous;
    }

    public int getWasteId() {
        return wasteId;
    }

    public WasteType getType() {
        return type;
    }

    public void setType(WasteType type) {
        this.type = type;
    }

    public Boolean getIsRecyclable() {
        return isRecyclable;
    }

    public void setIsRecyclable(Boolean isRecyclable) {
        this.isRecyclable = isRecyclable;
    }

    public Boolean getIsDangerous() {
        return isDangerous;
    }

    public void setIsDangerous(Boolean isDangerous) {
        this.isDangerous = isDangerous;
    }

    @Override
    public String toString() {
        return "Waste{" +
                "wasteId=" + wasteId +
                ", type=" + type +
                ", isRecyclable=" + isRecyclable +
                ", isDangerous=" + isDangerous +
                '}';
    }
}
