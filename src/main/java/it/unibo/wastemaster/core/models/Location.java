package it.unibo.wastemaster.core.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

/**
 * Represents a location entity with street address details. Maps to the "location" table
 * in the database.
 */
@Entity
@Table(name = "location")
public class Location {

    /** Unique identifier for the location, auto-generated. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    /** Street name. Cannot be null or blank. */
    @NotNull(message = "Street cannot be null")
    @NotBlank(message = "Street must not be blank")
    @Column(nullable = false)
    private String street;

    /** Civic number (house/building number). Cannot be null or blank. */
    @NotNull(message = "Civic number cannot be null")
    @NotBlank(message = "Civic number must not be blank")
    @Column(nullable = false)
    private String civicNumber;

    /** City name. Cannot be null or blank. */
    @NotNull(message = "City cannot be null")
    @NotBlank(message = "City must not be blank")
    @Column(nullable = false)
    private String city;

    /** Postal code. Must be exactly 5 digits. Cannot be null or blank. */
    @NotNull(message = "Postal code cannot be null")
    @NotBlank(message = "Postal code must not be blank")
    @Pattern(regexp = "^\\d{5}$", message = "Postal code must be exactly 5 digits")
    @Column(nullable = false)
    private String postalCode;

    /**
     * Default no-argument constructor required by JPA.
     */
    public Location() { }

    /**
     * Constructs a Location with the specified details.
     * 
     * @param street street name.
     * @param civicNumber civic number.
     * @param city city name.
     * @param postalCode postal code (5 digits).
     */
    public Location(final String street, final String civicNumber, final String city,
            final String postalCode) {
        this.street = street;
        this.civicNumber = civicNumber;
        this.city = city;
        this.postalCode = postalCode;
    }

    /**
     * Returns the unique identifier of the location.
     * 
     * @return location ID.
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the unique identifier of the location.
     * 
     * @param id new location ID.
     */
    public void setId(final int id) {
        this.id = id;
    }

    /**
     * Returns the street name.
     * 
     * @return street name.
     */
    public String getStreet() {
        return street;
    }

    /**
     * Sets the street name.
     * 
     * @param street new street name.
     */
    public void setStreet(final String street) {
        this.street = street;
    }

    /**
     * Returns the civic number.
     * 
     * @return civic number.
     */
    public String getCivicNumber() {
        return civicNumber;
    }

    /**
     * Sets the civic number.
     * 
     * @param civicNumber new civic number.
     */
    public void setCivicNumber(final String civicNumber) {
        this.civicNumber = civicNumber;
    }

    /**
     * Returns the city name.
     * 
     * @return city name.
     */
    public String getCity() {
        return city;
    }

    /**
     * Sets the city name.
     * 
     * @param city new city name.
     */
    public void setCity(final String city) {
        this.city = city;
    }

    /**
     * Returns the postal code.
     * 
     * @return postal code.
     */
    public String getPostalCode() {
        return postalCode;
    }

    /**
     * Sets the postal code.
     * 
     * @param postalCode new postal code.
     */
    public void setPostalCode(final String postalCode) {
        this.postalCode = postalCode;
    }

    /**
     * Returns a string representation of the location in the format: "street civicNumber,
     * city, postalCode".
     * 
     * @return formatted string with location details.
     */
    @Override
    public String toString() {
        return street + " " + civicNumber + ", " + city + ", " + postalCode;
    }
}
