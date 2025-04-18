package it.unibo.wastemaster.core.models;

import it.unibo.wastemaster.core.utils.ValidateUtils;
import jakarta.persistence.Column;
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
    @Column(nullable = false)
    private WasteType type;

    @Column(nullable = false)
    private Boolean isRecyclable;

    @Column(nullable = false)
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
        ValidateUtils.validateNotNull(type, "Waste type must not be null");
		ValidateUtils.validateNotNull(isRecyclable, "isRecyclable must not be null");
		ValidateUtils.validateNotNull(isDangerous, "isDangerous must not be null");
        
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
