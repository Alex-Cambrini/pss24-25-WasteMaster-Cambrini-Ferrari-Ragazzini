package it.unibo.wastemaster.presentationdto;

import it.unibo.wastemaster.domain.model.Employee;
import it.unibo.wastemaster.domain.model.Employee.Licence;
import it.unibo.wastemaster.domain.model.Employee.Role;
import it.unibo.wastemaster.domain.model.Location;
import java.time.format.DateTimeFormatter;

/**
 * Presentation DTO class representing an employee row to be shown in UI tables.
 * It wraps employee and location details for display.
 */
public final class EmployeeRow {

    private final String name;
    private final String surname;
    private final String email;
    private final Role role;
    private final Licence licence;
    private final Employee employee;
    private final String creationDate;

    /**
     * Constructs an EmployeeRow instance using a given employee entity.
     *
     * @param employee the employee entity
     */
    public EmployeeRow(final Employee employee) {
        this.employee = employee;
        this.name = employee.getName();
        this.surname = employee.getSurname();
        this.email = employee.getEmail();
        this.role = employee.getRole();
        this.licence = employee.getLicence();
        this.creationDate = employee.getCreatedDate()
                .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }

    /**
     * Gets the employee's name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the employee's surname.
     *
     * @return the surname
     */
    public String getSurname() {
        return surname;
    }

    /**
     * Gets the employee's email address.
     *
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Gets the employee's role.
     *
     * @return the role
     */
    public Role getRole() {
        return role;
    }

    /**
     * Gets the employee's licence.
     *
     * @return the licence
     */
    public Licence getLicence() {
        return licence;
    }

    /**
     * Gets the full location as a formatted string.
     *
     * @return the full location string
     */
    public String getFullLocation() {
        Location loc = employee.getLocation();
        return loc.getStreet() + " " + loc.getCivicNumber() + ", "
                + loc.getCity() + " (" + loc.getPostalCode() + ")";
    }

    /**
     * Gets the employee's account creation date as a formatted string.
     *
     * @return the creation date
     */
    public String getCreationDate() {
        return creationDate;
    }
}
