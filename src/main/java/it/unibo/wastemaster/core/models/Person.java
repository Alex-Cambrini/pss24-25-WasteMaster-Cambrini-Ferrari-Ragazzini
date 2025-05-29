package it.unibo.wastemaster.core.models;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MappedSuperclass;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

/**
 * Abstract superclass representing a person with common attributes. Provides basic
 * personal information and soft-delete functionality.
 */
@MappedSuperclass
public abstract class Person {

    /**
     * The person's first name. Cannot be null or blank.
     */
    @NotNull(message = "name cannot be null")
    @NotBlank(message = "name must not be blank")
    @Column(nullable = false)
    private String name;

    /**
     * The person's surname. Cannot be null or blank.
     */
    @NotNull(message = "surname cannot be null")
    @NotBlank(message = "surname must not be blank")
    @Column(nullable = false)
    private String surname;

    /**
     * The person's location. Cannot be null. Validated entity associated with the person.
     */
    @Valid
    @ManyToOne(cascade = CascadeType.PERSIST)
    @NotNull(message = "address cannot be null")
    @JoinColumn(nullable = false)
    private Location location;

    /**
     * The person's email address. Cannot be null or blank. Must be a valid email format.
     */
    @NotNull(message = "email cannot be null")
    @NotBlank(message = "email must not be blank")
    @Email(message = "Invalid email. Incorrect format.")
    @Column(nullable = false)
    private String email;

    /**
     * The person's phone number. Cannot be null or blank. Must match pattern allowing
     * optional + and 10-15 digits.
     */
    @NotNull(message = "phone cannot be null")
    @NotBlank(message = "phone must not be blank")
    @Pattern(regexp = "^[+]?\\d{10,15}$",
            message = "Phone must be between 10 and 15 digits, optional '+' prefix")
    @Column(nullable = false)
    private String phone;

    /**
     * Soft delete flag. Indicates if the person is marked as deleted.
     */
    @NotNull(message = "isDeleted cannot be null")
    @Column(nullable = false)
    private boolean isDeleted = false;

    /**
     * Constructs a new Person with the given parameters.
     *
     * @param name the first name (final)
     * @param surname the surname (final)
     * @param location the location (final)
     * @param email the email address (final)
     * @param phone the phone number (final)
     */
    protected Person(final String name, final String surname, final Location location,
                     final String email, final String phone) {
        this.name = name;
        this.surname = surname;
        this.location = location;
        this.email = email;
        this.phone = phone;
    }

    /**
     * Default constructor required by JPA.
     */
    protected Person() {
    }

    /**
     * @return the person's first name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the person's first name.
     *
     * @param name the new first name (final)
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * @return the person's surname
     */
    public String getSurname() {
        return surname;
    }

    /**
     * Sets the person's surname.
     *
     * @param surname the new surname (final)
     */
    public void setSurname(final String surname) {
        this.surname = surname;
    }

    /**
     * @return the person's location
     */
    public Location getLocation() {
        return location;
    }

    /**
     * Sets the person's location.
     *
     * @param location the new location (final)
     */
    public void setLocation(final Location location) {
        this.location = location;
    }

    /**
     * @return the person's email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the person's email.
     *
     * @param email the new email (final)
     */
    public void setEmail(final String email) {
        this.email = email;
    }

    /**
     * @return the person's phone number
     */
    public String getPhone() {
        return phone;
    }

    /**
     * Sets the person's phone number.
     *
     * @param phone the new phone number (final)
     */
    public void setPhone(final String phone) {
        this.phone = phone;
    }

    /**
     * @return true if the person is marked as deleted, false otherwise
     */
    public boolean isDeleted() {
        return isDeleted;
    }

    /**
     * Marks the person as deleted (soft delete).
     */
    public void delete() {
        isDeleted = true;
    }

    /**
     * Restores the person by unmarking the deleted flag.
     */
    public void restore() {
        isDeleted = false;
    }

    /**
     * Returns a string representation of the person.
     *
     * @return formatted string with person details
     */
    @Override
    public String toString() {
        return String.format("""
                        Person {Name: %s, Surname: %s, Address: %s,
                            Email: %s, Phone: %s, Deleted: %s }
                        """, name, surname, location != null ? location.toString() : "N"
                        + "/A", email,
                phone, isDeleted ? "Yes" : "No");
    }
}
