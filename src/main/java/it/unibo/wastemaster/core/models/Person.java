package it.unibo.wastemaster.core.models;

import jakarta.persistence.ManyToOne;
import jakarta.persistence.MappedSuperclass;
import it.unibo.wastemaster.core.utils.ValidateUtils;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;

@MappedSuperclass
public abstract class Person {
    @Column(nullable = false)
    protected String name;
    
    @Column(nullable = false)
    protected String surname;

    @ManyToOne(cascade = CascadeType.PERSIST)
    private Location address;

    @Column(nullable = false)
    protected String email;

    @Column(nullable = false)
    protected String phone;

    protected boolean isDeleted = false;

    public Person(String name, String surname, Location address, String email, String phone) {
        ValidateUtils.validateString(name, "Name cannot be null or empty");
        ValidateUtils.validateString(surname, "Surname cannot be null or empty");
        ValidateUtils.validateNotNull(address, "Address cannot be null");
        ValidateUtils.validateString(email, "Email cannot be null or empty");
        ValidateUtils.validateString(phone, "Phone cannot be null or empty");
    
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

    public boolean isDeleted() {
        return isDeleted;
    }

    public void delete() {
        isDeleted = true;
    }

    public void restore() {
        isDeleted = false;
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
