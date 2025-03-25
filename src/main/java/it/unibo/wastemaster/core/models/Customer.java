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
    private int customerId;

    public Customer(String name, String surname, Location address, String email, String phone) {
        super(name, surname, address, email, phone); 
    }

    public Customer() {}

    public int getCustomerId() {
        return customerId;
    }

    @Override
    public String getInfo() {
        return super.getInfo() + String.format(", CustomerId: %d", customerId);
    }
}
