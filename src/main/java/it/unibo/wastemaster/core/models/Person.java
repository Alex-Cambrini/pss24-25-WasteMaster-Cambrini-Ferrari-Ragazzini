package it.unibo.wastemaster.core.models;

import jakarta.persistence.ManyToOne;
import jakarta.persistence.MappedSuperclass;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;

@MappedSuperclass
public abstract class Person {
    @NotNull (message = "name cannot be null")
    @NotBlank (message = "name must not be blank")
    @Column(nullable = false)
    protected String name;

    @NotNull (message = "surname cannot be null")
    @NotBlank (message = "surname must not be blank")
    @Column(nullable = false)
    protected String surname;

    @Valid
    @ManyToOne(cascade = CascadeType.PERSIST)
    @NotNull (message = "address cannot be null")
    @JoinColumn(nullable = false)
    private Location location;

    @NotNull (message = "email cannot be null")
    @NotBlank (message = "email must not be blank")
    @Email(message = "Invalid email. Incorrect format.")
    @Column(nullable = false)
    protected String email;

    @NotNull (message = "phone cannot be null")
    @NotBlank (message = "phone must not be blank")
    @Pattern(regexp = "^[+]?[0-9]{10,15}$")
    @Column(nullable = false)
    protected String phone;

    @NotNull (message = "isDeleted cannot be null")
    @Column(nullable = false)
    protected boolean isDeleted = false;

    public Person(String name, String surname, Location location, String email, String phone) {
        this.name = name;
        this.surname = surname;
        this.location = location;
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

    public Location getLocation() {
        return location;
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

    public void setLocation(Location location) {
        this.location = location;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getInfo() {
        return String.format("Name: %s, Surname: %s, Address: %s, Email: %s, Phone: %s", name, surname, location, email,
                phone);
    }
}
