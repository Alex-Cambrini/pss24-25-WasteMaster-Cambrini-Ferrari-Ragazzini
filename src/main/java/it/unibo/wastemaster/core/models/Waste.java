package it.unibo.wastemaster.core.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;

/**
 * Entity representing a type of waste.
 */
@Entity
public class Waste {

    /**
     * Unique identifier for the waste.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer wasteId;

    /**
     * Name of the waste type. Cannot be null.
     */
    @Column(nullable = false)
    @NotNull(message = "Waste type must not be null")
    private String name;

    /**
     * Indicates if the waste is recyclable. Cannot be null.
     */
    @Column(nullable = false)
    @NotNull(message = "isRecyclable must not be null")
    private Boolean isRecyclable;

    /**
     * Indicates if the waste is dangerous. Cannot be null.
     */
    @Column(nullable = false)
    @NotNull(message = "isDangerous must not be null")
    private Boolean isDangerous;

    /**
     * Flag indicating if the waste is deleted.
     */
    @Column(nullable = false)
    private boolean deleted = false;

    /**
     * Default constructor required by JPA.
     */
    public Waste() {
    }

    /**
     * Constructs a Waste with specified name, recyclability, and danger status.
     *
     * @param name the name of the waste
     * @param isRecyclable whether the waste is recyclable
     * @param isDangerous whether the waste is dangerous
     */
    public Waste(final String name, final Boolean isRecyclable,
                 final Boolean isDangerous) {
        this.name = name;
        this.isRecyclable = isRecyclable;
        this.isDangerous = isDangerous;
    }

    /**
     * Gets the waste ID.
     *
     * @return waste ID
     */
    public Integer getWasteId() {
        return wasteId;
    }

    /**
     * Gets the name of the waste.
     *
     * @return waste name
     */
    public String getWasteName() {
        return name;
    }

    /**
     * Sets the name of the waste.
     *
     * @param name new waste name
     */
    public void setWasteName(final String name) {
        this.name = name;
    }

    /**
     * Returns whether the waste is recyclable.
     *
     * @return true if recyclable, false otherwise
     */
    public Boolean getIsRecyclable() {
        return isRecyclable;
    }

    /**
     * Sets the recyclability status.
     *
     * @param isRecyclable new recyclability status
     */
    public void setIsRecyclable(final Boolean isRecyclable) {
        this.isRecyclable = isRecyclable;
    }

    /**
     * Returns whether the waste is dangerous.
     *
     * @return true if dangerous, false otherwise
     */
    public Boolean getIsDangerous() {
        return isDangerous;
    }

    /**
     * Sets the dangerous status.
     *
     * @param isDangerous new dangerous status
     */
    public void setIsDangerous(final Boolean isDangerous) {
        this.isDangerous = isDangerous;
    }

    /**
     * Checks if the waste is marked as deleted.
     *
     * @return true if deleted, false otherwise
     */
    public boolean isDeleted() {
        return deleted;
    }

    /**
     * Marks the waste as deleted.
     */
    public void delete() {
        this.deleted = true;
    }

    /**
     * Returns string representation of the waste.
     *
     * @return string describing waste name, recyclability and danger status.
     */
    @Override
    public String toString() {
        return "Waste Name: " + name + "\n" + "Recyclable: "
                + (isRecyclable ? "Yes" : "No") + "\n" + "Dangerous: "
                + (isDangerous ? "Yes" : "No");
    }
}
