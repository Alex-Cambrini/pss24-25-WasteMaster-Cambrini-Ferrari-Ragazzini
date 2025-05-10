package it.unibo.wastemaster.core.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;

@Entity
public class Waste {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int wasteId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull(message = "Waste type must not be null")
    private WasteType type;

    @Column(nullable = false)
    @NotNull(message = "isRecyclable must not be null")
    private Boolean isRecyclable;

    @Column(nullable = false)
    @NotNull(message = "isDangerous must not be null")
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
        return String.format(
                "Waste {ID: %d, Type: %s, Recyclable: %s, Dangerous: %s}",
                wasteId,
                type != null ? type.name() : "N/A",
                isRecyclable != null ? isRecyclable.toString() : "N/A",
                isDangerous != null ? isDangerous.toString() : "N/A");
    }
}
