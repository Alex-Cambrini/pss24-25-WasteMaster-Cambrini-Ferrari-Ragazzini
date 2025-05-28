package it.unibo.wastemaster.core.services;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import it.unibo.wastemaster.core.AbstractDatabaseTest;
import it.unibo.wastemaster.core.models.Employee;
import it.unibo.wastemaster.core.models.Employee.Licence;
import it.unibo.wastemaster.core.models.Employee.Role;
import it.unibo.wastemaster.core.models.Location;
import it.unibo.wastemaster.core.models.Vehicle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class EmployeeManagerTest extends AbstractDatabaseTest {
    private Location location;
    private Employee employee;
    private String email;

    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();
        em.getTransaction().begin();
        email = "test@test.it";
        location = new Location("Via Roma", "10", "Bologna", "40100");
        employee = new Employee("Mario", "Rossi", location, email, "1234567890",
                Role.ADMINISTRATOR, Licence.NONE);
    }

    @Test
    void testAddEmployeeInvalid() {
        Employee invalidRoleEmployee = new Employee("Mario", "Rossi", location,
                "email@example.com", "1234567890", null, Employee.Licence.NONE);

        Employee invalidLicenceEmployee = new Employee("Mario", "Rossi", location,
                "email@example.com", "1234567890", Employee.Role.ADMINISTRATOR, null);

        assertThrows(IllegalArgumentException.class,
                () -> employeeManager.addEmployee(invalidRoleEmployee));
        assertThrows(IllegalArgumentException.class,
                () -> employeeManager.addEmployee(invalidLicenceEmployee));
    }

    @Test
    void testGetEmployeeById() {
        Employee saved = employeeManager.addEmployee(employee);
        int savedId = saved.getEmployeeId();

        Employee found = employeeManager.getEmployeeById(savedId);
        assertNotNull(found);
        assertEquals(saved.getName(), found.getName());
        assertEquals(saved.getSurname(), found.getSurname());
        assertEquals(saved.getEmail(), found.getEmail());
        assertEquals(saved.getPhone(), found.getPhone());
        assertEquals(saved.getLocation().getStreet(), found.getLocation().getStreet());
        assertEquals(saved.getLocation().getCivicNumber(),
                found.getLocation().getCivicNumber());
        assertEquals(saved.getLocation().getCity(), found.getLocation().getCity());
        assertEquals(saved.getLocation().getPostalCode(),
                found.getLocation().getPostalCode());
        assertEquals(saved.getRole(), found.getRole());
        assertEquals(saved.getLicence(), found.getLicence());

        assertNull(employeeManager.getEmployeeById(-1));
    }

    @Test
    void testUpdateEmployee() {
        Employee saved = employeeManager.addEmployee(employee);
        String newPhone = "0000000000";
        String newName = "Francesco";
        Licence newLicence = Licence.C1;
        Role newRole = Role.OPERATOR;

        saved.setPhone(newPhone);
        saved.setName(newName);
        saved.setLicence(Licence.C1);
        saved.setRole(Role.OPERATOR);
        employeeManager.updateEmployee(saved);

        Employee updated = employeeManager.getEmployeeById(saved.getEmployeeId());
        assertEquals(newPhone, updated.getPhone());
        assertEquals(newName, updated.getName());
        assertEquals(newLicence, updated.getLicence());
        assertEquals(newRole, updated.getRole());
    }

    @Test
    void testSoftDeleteEmploye() {
        Employee saved = employeeManager.addEmployee(employee);
        int savedId = saved.getEmployeeId();

        assertNotNull(saved);
        assertFalse(saved.isDeleted());

        boolean result = employeeManager.softDeleteEmployee(saved);
        assertTrue(result);

        Employee deletedEmployee = employeeManager.getEmployeeById(savedId);
        assertNotNull(deletedEmployee);
        assertTrue(deletedEmployee.isDeleted());

        assertEquals(savedId, deletedEmployee.getEmployeeId());

        assertFalse(employeeManager.softDeleteEmployee(null));

        Employee nonExistentEmployee = new Employee("Non", "Existent", location,
                "nonexistent@test.it", "1234567890", Role.ADMINISTRATOR, Licence.NONE);
        assertFalse(employeeManager.softDeleteEmployee(nonExistentEmployee));
    }

    @Test
    void testGetEmployeeInvalidId() {
        final int invalidIdNegative = -5;
        final int invalidIdZero = 0;
        final int invalidIdTooLarge = 99999;

        assertNull(employeeManager.getEmployeeById(invalidIdNegative));
        assertNull(employeeManager.getEmployeeById(invalidIdZero));
        assertNull(employeeManager.getEmployeeById(invalidIdTooLarge));
    }

    @Test
    void testUpdateEmployeeInvalid() {
        assertThrows(IllegalArgumentException.class,
                () -> employeeManager.updateEmployee(null));

        Employee notPersisted = new Employee("Franco", "Neri",
                new Location("Via Roma", "10", "Bologna", "40100"), "test2@test.it",
                "1234567890", Role.ADMINISTRATOR, Licence.NONE);
        assertThrows(IllegalArgumentException.class,
                () -> employeeManager.updateEmployee(notPersisted));
    }

    @Test
    void testUpdateEmployeeNoChange() {
        employeeManager.addEmployee(employee);
        assertDoesNotThrow(() -> employeeManager.updateEmployee(employee));
    }

    @Test
    void testCanDriveVehicle() {
        final int year = 2022;
        final int capacity = 3;

        Vehicle vB = new Vehicle("AA111AA", "Fiat", "Ducato", year,
                Vehicle.RequiredLicence.B, Vehicle.VehicleStatus.IN_SERVICE, capacity);
        Vehicle vC1 = new Vehicle("BB222BB", "Iveco", "Daily", year,
                Vehicle.RequiredLicence.C1, Vehicle.VehicleStatus.IN_SERVICE, capacity);
        Vehicle vC = new Vehicle("CC333CC", "MAN", "TGE", year, Vehicle.RequiredLicence.C,
                Vehicle.VehicleStatus.IN_SERVICE, capacity);

        Employee eB = new Employee("Luca", "Bianchi", location, "b@example.com", "123",
                Employee.Role.OPERATOR, Employee.Licence.B);
        Employee eC1 = new Employee("Giulia", "Neri", location, "c1@example.com", "456",
                Employee.Role.OPERATOR, Employee.Licence.C1);
        Employee eC = new Employee("Marco", "Verdi", location, "c@example.com", "789",
                Employee.Role.OPERATOR, Employee.Licence.C);

        assertTrue(employeeManager.canDriveVehicle(eB, vB));
        assertFalse(employeeManager.canDriveVehicle(eB, vC1));
        assertFalse(employeeManager.canDriveVehicle(eB, vC));

        assertTrue(employeeManager.canDriveVehicle(eC1, vC1));
        assertTrue(employeeManager.canDriveVehicle(eC1, vB));
        assertFalse(employeeManager.canDriveVehicle(eC1, vC));

        assertTrue(employeeManager.canDriveVehicle(eC, vC));
        assertTrue(employeeManager.canDriveVehicle(eC, vC1));
        assertTrue(employeeManager.canDriveVehicle(eC, vB));

        assertThrows(IllegalArgumentException.class,
                () -> employeeManager.canDriveVehicle(null, vB));
        assertThrows(IllegalArgumentException.class,
                () -> employeeManager.canDriveVehicle(eC, null));
    }
}
