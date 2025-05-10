package it.unibo.wastemaster.core.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "customer")
public class Customer extends Person {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer customerId;

    public Customer(String name, String surname, Location location, String email, String phone) {
        super(name, surname, location, email, phone);
    }

    public Customer() {
    }

    public Integer getCustomerId() {
        return customerId;
    }

    @Override
    public String toString() {
        return String.format("Customer {ID: %d, Name: %s %s, Email: %s, Phone: %s, Location: %s}",
                customerId,
                getName(),
                getSurname(),
                getEmail(),
                getPhone(),
                getLocation() != null ? getLocation().toString() : "N/A");
    }
}
