package it.unibo.wastemaster.core.models;

import jakarta.persistence.ManyToOne;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.CascadeType;

@MappedSuperclass
public abstract class Person {
    protected String name;
    protected String surname;
    @ManyToOne(cascade = CascadeType.PERSIST)
    private Location address;
    protected String email;
    protected String phone;

    public Person(String name, String surname, Location address, String email, String phone) {
        this.name = name;
        this.surname = surname;
        this.address = address;
        this.email = email;
        this.phone = phone;
    }

    public Person() {
    };

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public Location getAddress() {
        return address;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public void setAddress(Location address) {
        this.address = address;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getInfo() {
        return String.format("Name: %s, Surname: %s, Address: %s, Email: %s, Phone: %s", name, surname, address, email,
                phone);
    }
}
