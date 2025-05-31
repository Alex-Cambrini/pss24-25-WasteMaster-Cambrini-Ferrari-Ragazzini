package it.unibo.wastemaster.core.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Represents a user account linked to an employee.
 * Stores login credentials and the associated employee reference.
 */
@Entity
@Table(name = "account")
public final class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    @NotBlank
    @Column(nullable = false)
    private String passwordHash;

    @NotNull(message = "Employee cannot be null")
    @OneToOne
    @JoinColumn(name = "employee_id", nullable = false, unique = true)
    private Employee employee;

    /**
     * Default constructor required by JPA.
     */
    public Account() {
    }

    /**
     * Creates a new Account with the given password hash and employee.
     *
     * @param passwordHash the hashed password
     * @param employee the associated employee
     */
    public Account(final String passwordHash, final Employee employee) {
        this.passwordHash = passwordHash;
        this.employee = employee;
    }

    /**
     * Returns the account ID.
     *
     * @return the ID
     */
    public Integer getId() {
        return id;
    }

    /**
     * Returns the hashed password.
     *
     * @return the password hash
     */
    public String getPasswordHash() {
        return passwordHash;
    }

    /**
     * Sets the hashed password.
     *
     * @param passwordHash the new hashed password
     */
    public void setPasswordHash(final String passwordHash) {
        this.passwordHash = passwordHash;
    }

    /**
     * Returns the associated employee.
     *
     * @return the employee
     */
    public Employee getEmployee() {
        return employee;
    }

    /**
     * Sets the associated employee.
     *
     * @param employee the employee to associate
     */
    public void setEmployee(final Employee employee) {
        this.employee = employee;
    }
}
