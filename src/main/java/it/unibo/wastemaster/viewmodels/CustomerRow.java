package it.unibo.wastemaster.viewmodels;

import it.unibo.wastemaster.core.models.Customer;

public class CustomerRow {
    private final String name;
    private final String surname;
    private final String email;
    private final String street;
    private final String civic;
    private final String city;
    private final String postalCode;

    public CustomerRow(Customer customer) {
        this.name = customer.getName();
		this.surname = customer.getSurname();
		this.email = customer.getEmail();
		this.street = customer.getLocation().getStreet();
		this.civic = customer.getLocation().getCivicNumber();
		this.city = customer.getLocation().getCity();
		this.postalCode = customer.getLocation().getPostalCode();
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public String getEmail() {
        return email;
    }

    public String getStreet() {
        return street;
    }

    public String getCivic() {
        return civic;
    }

    public String getCity() {
        return city;
    }

    public String getPostalCode() {
        return postalCode;
    }
}