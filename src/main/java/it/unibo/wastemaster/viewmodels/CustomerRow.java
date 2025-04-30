package it.unibo.wastemaster.viewmodels;

public class CustomerRow {
    private final String name;
    private final String surname;
    private final String email;
    private final String street;
    private final String civic;
    private final String city;
    private final String postalCode;

    public CustomerRow(String name, String surname, String email,
            String street, String civic,
            String city, String postalCode) {
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.street = street;
        this.civic = civic;
        this.city = city;
        this.postalCode = postalCode;
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