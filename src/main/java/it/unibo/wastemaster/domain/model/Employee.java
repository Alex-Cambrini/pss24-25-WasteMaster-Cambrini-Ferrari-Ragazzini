package it.unibo.wastemaster.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

/**
 * Represents an employee entity extending the Person class. Maps to the "employee" table
 * in the database.
 */
@Entity
@Table(name = "employee")
public class Employee extends Person {

    /**
     * Unique identifier for the employee, auto-generated.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer employeeId;

    /**
     * Role of the employee. Cannot be null.
     */
    @NotNull(message = "Role cannot be null")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    /**
     * Licence held by the employee. Cannot be null.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull(message = "Licence cannot be null")
    private Licence licence;

    /**
     * Constructs an Employee with the specified details.
     *
     * @param name employee's first name.
     * @param surname employee's last name.
     * @param address employee's location.
     * @param email employee's email address.
     * @param phone employee's phone number.
     * @param role employee's role.
     * @param licence employee's licence.
     */
    public Employee(final String name, final String surname, final Location address,
                    final String email, final String phone, final Role role,
                    final Licence licence) {
        super(name, surname, address, email, phone);
        this.role = role;
        this.licence = licence;
    }

    /**
     * Default no-argument constructor required by JPA.
     */
    public Employee() {
        super();
    }

    /**
     * Returns the unique identifier of this employee.
     *
     * @return employee ID.
     */
    public Integer getEmployeeId() {
        return employeeId;
    }

    /**
     * Returns the employee's role.
     *
     * @return role.
     */
    public Role getRole() {
        return role;
    }

    /**
     * Sets the employee's role.
     *
     * @param role new role.
     */
    public void setRole(final Role role) {
        this.role = role;
    }

    /**
     * Returns the employee's licence.
     *
     * @return licence.
     */
    public Licence getLicence() {
        return licence;
    }

    /**
     * Sets the employee's licence.
     *
     * @param licence new licence.
     */
    public void setLicence(final Licence licence) {
        this.licence = licence;
    }

    /**
     * Returns a short string suitable for display in a ComboBox,
     * showing ID, name, surname, and licence.
     *
     * @return compact string for ComboBox display.
     */
    @Override
    public String toString() {
        return String.format("ID: %d | %s %s | Licence: %s",
                employeeId, getName(), getSurname(), licence);
    }

    /**
     * Enum representing the employee's licence category.
     */
    public enum Licence {
        /**
         * No licence.
         */
        NONE("None"),
        /**
         * Licence for vehicles up to 3.5 tons.
         */
        B("Fino a 3.5 t"),
        /**
         * Licence for vehicles between 3.5 and 7.5 tons.
         */
        C1("3.5 t - 7.5 t"),
        /**
         * Licence for vehicles over 7.5 tons.
         */
        C("Oltre 7.5 t");

        private final String licenceDescription;

        Licence(final String licenceDescription) {
            this.licenceDescription = licenceDescription;
        }

        /**
         * Returns the human-readable description of the licence.
         *
         * @return licence description.
         */
        public String getLicenceDescription() {
            return licenceDescription;
        }
    }

    /**
     * Enum representing possible roles of an employee.
     */
    public enum Role {
        /**
         * Administrator role.
         */
        ADMINISTRATOR,
        /**
         * Office worker role.
         */
        OFFICE_WORKER,
        /**
         * Operator role.
         */
        OPERATOR
    }
}
