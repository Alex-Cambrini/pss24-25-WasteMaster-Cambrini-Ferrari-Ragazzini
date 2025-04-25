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

@Entity
@Table(name = "location")
public class Location {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotNull(message = "Street cannot be null")
    @NotBlank(message = "Street must not be blank")
    @Column(nullable = false)
    private String street;

    @NotNull(message = "Civic number cannot be null")
    @NotBlank(message = "Civic number must not be blank")
    @Column(nullable = false)
    private String civicNumber;

    @NotNull(message = "City cannot be null")
    @NotBlank(message = "City must not be blank")
    @Column(nullable = false)
    private String city;

    @NotNull(message = "Postal code cannot be null")
    @NotBlank(message = "Postal code must not be blank")
    @Pattern(regexp = "^[0-9]{5}$", message = "Postal code must be exactly 5 digits")
    @Column(nullable = false)
    private String postalCode;

    // Default constructor for JPA
    public Location() {
    };

    public Location(String street, String civicNumber, String city, String postalCode) {
        this.street = street;
        this.civicNumber = civicNumber;
        this.city = city;
        this.postalCode = postalCode;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getCivicNumber() {
        return civicNumber;
    }

    public void setCivicNumber(String civicNumber) {
        this.civicNumber = civicNumber;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    @Override
    public String toString() {
        return street + ", " + civicNumber + ", " + city + ", " + postalCode;
    }
}
