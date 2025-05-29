package it.unibo.wastemaster.viewmodels;

import it.unibo.wastemaster.core.models.Customer;

/**
 * ViewModel class representing a customer row to be shown in UI tables. It wraps relevant
 * customer and location details for display.
 */
public final class CustomerRow {

    private final String name;
    private final String surname;
    private final String email;
    private final String street;
    private final String civic;
    private final String city;
    private final String postalCode;

    /**
     * Constructs a CustomerRow instance using a given customer entity.
     *
     * @param customer the customer entity
     */
    public CustomerRow(final Customer customer) {
        this.name = customer.getName();
        this.surname = customer.getSurname();
        this.email = customer.getEmail();
        this.street = customer.getLocation().getStreet();
        this.civic = customer.getLocation().getCivicNumber();
        this.city = customer.getLocation().getCity();
        this.postalCode = customer.getLocation().getPostalCode();
    }

    /**
     * Gets the customer's name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the customer's surname.
     *
     * @return the surname
     */
    public String getSurname() {
        return surname;
    }

    /**
     * Gets the customer's email address.
     *
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Gets the street of the customer's location.
     *
     * @return the street
     */
    public String getStreet() {
        return street;
    }

    /**
     * Gets the civic number of the customer's location.
     *
     * @return the civic number
     */
    public String getCivic() {
        return civic;
    }

    /**
     * Gets the city where the customer resides.
     *
     * @return the city
     */
    public String getCity() {
        return city;
    }

    /**
     * Gets the postal code of the customer's location.
     *
     * @return the postal code
     */
    public String getPostalCode() {
        return postalCode;
    }

    /**
     * Gets the full location as a formatted string.
     *
     * @return the full location string
     */
    public String getFullLocation() {
        return street + " " + civic + ", " + city + " (" + postalCode + ")";
    }
}
