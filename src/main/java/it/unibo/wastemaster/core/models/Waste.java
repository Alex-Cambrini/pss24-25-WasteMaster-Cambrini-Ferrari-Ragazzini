package it.unibo.wastemaster.core.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;

@Entity
public class Waste {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer wasteId;

    @Column(nullable = false)
    @NotNull(message = "Waste type must not be null")
    private String name;

    @Column(nullable = false)
    @NotNull(message = "isRecyclable must not be null")
    private Boolean isRecyclable;

    @Column(nullable = false)
    @NotNull(message = "isDangerous must not be null")
    private Boolean isDangerous;

    @Column(nullable = false)
    private boolean deleted = false;

    public Waste() {
    }

    public Waste(String name, Boolean isRecyclable, Boolean isDangerous) {
        this.name = name;
        this.isRecyclable = isRecyclable;
        this.isDangerous = isDangerous;
    }

    public Integer getWasteId() {
        return wasteId;
    }

    public String getWasteName() {
        return name;
    }

    public void setWasteName(String name) {
        this.name = name;
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

    public boolean isDeleted() {
        return deleted;
    }

    public void delete() {
        this.deleted = true;
    }

    @Override
    public String toString() {
        return "Waste Type: " + name + "\n" +
                "Recyclable: " + (isRecyclable ? "Yes" : "No") + "\n" +
                "Dangerous: " + (isDangerous ? "Yes" : "No");
    }
}