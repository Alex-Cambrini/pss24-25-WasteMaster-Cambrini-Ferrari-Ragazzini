package it.unibo.wastemaster.domain.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "customer")
public class Customer extends Person {

    /**
     * Unique identifier for the customer, auto-generated.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer customerId;

    /**
     * Constructs a new Customer with the specified details.
     *
     * @param name the customer's first name
     * @param surname the customer's last name
     * @param location the customer's location
     * @param email the customer's email address
     * @param phone the customer's phone number
     */
    public Customer(final String name, final String surname, final Location location,
                    final String email, final String phone) {
        super(name, surname, location, email, phone, LocalDateTime.now());
    }

    /**
     * Default no-argument constructor required by JPA.
     */
    public Customer() {
    }

    /**
     * Returns the unique identifier of this customer.
     *
     * @return the customer ID
     */
    public Integer getCustomerId() {
        return customerId;
    }


    @Override
    public String toString() {
        return String.format("%s %s (%s)", getName(), getSurname(), getEmail());
    }
}
