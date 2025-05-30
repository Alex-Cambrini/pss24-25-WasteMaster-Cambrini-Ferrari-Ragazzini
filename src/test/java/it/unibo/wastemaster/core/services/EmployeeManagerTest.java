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
    private String rawPassword;

    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();
        getEntityManager().getTransaction().begin();
        email = "test@test.it";
        rawPassword = "MfE&475DK4GRHAi";
        location = new Location("Via Roma", "10", "Bologna", "40100");
        employee = new Employee("Mario", "Rossi", location, email, "1234567890",
                Role.ADMINISTRATOR, Licence.NONE);
    }

    @Test
    void testAddEmployeeNullOrEmptyEmail() {
        Employee nullEmailEmployee = new Employee("Mario", "Rossi", location, null,
                "1234567890", Employee.Role.ADMINISTRATOR, Employee.Licence.NONE);
        assertThrows(IllegalArgumentException.class,
                () -> getEmployeeManager().addEmployee(nullEmailEmployee, rawPassword));

        Employee emptyEmailEmployee = new Employee("Mario", "Rossi", location, "",
                "1234567890", Employee.Role.ADMINISTRATOR, Employee.Licence.NONE);
        assertThrows(IllegalArgumentException.class,
                () -> getEmployeeManager().addEmployee(emptyEmailEmployee, rawPassword));
    }

    @Test
    void testAddEmployeeSuccessAndFailures() {
        Employee saved = assertDoesNotThrow(
                () -> getEmployeeManager().addEmployee(employee, rawPassword));
        assertNotNull(saved);
        assertEquals(employee.getEmail(), saved.getEmail());

        Employee duplicateEmail = new Employee("Test2", "User2", location, email,
                "0987654321", Employee.Role.OPERATOR, Employee.Licence.B);
        assertThrows(IllegalArgumentException.class,
                () -> getEmployeeManager().addEmployee(duplicateEmail, rawPassword));

        Employee employee2 = new Employee("Test3", "User3", location, "test3@test.it",
                "1111111111", Employee.Role.OPERATOR, Employee.Licence.B);
        assertThrows(IllegalArgumentException.class,
                () -> getEmployeeManager().addEmployee(employee2, null));

        assertThrows(IllegalArgumentException.class,
                () -> getEmployeeManager().addEmployee(null, rawPassword));
    }

    @Test
    void testAddEmployeeInvalid() {
        Employee invalidRoleEmployee = new Employee("Mario", "Rossi", location,
                "email@example.com", "1234567890", null, Employee.Licence.NONE);

        Employee invalidLicenceEmployee = new Employee("Mario", "Rossi", location,
                "email@example.com", "1234567890", Employee.Role.ADMINISTRATOR, null);

        assertThrows(IllegalArgumentException.class,
                () -> getEmployeeManager().addEmployee(invalidRoleEmployee, rawPassword));
        assertThrows(IllegalArgumentException.class, () -> getEmployeeManager()
                .addEmployee(invalidLicenceEmployee, rawPassword));
    }

    @Test
    void testGetEmployeeById() {
        Employee saved = getEmployeeManager().addEmployee(employee, rawPassword);
        int savedId = saved.getEmployeeId();

        Employee found = getEmployeeManager().getEmployeeById(savedId);
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

        assertNull(getEmployeeManager().getEmployeeById(-1));
    }

    @Test
    void testUpdateEmployee() {
        Employee saved = getEmployeeManager().addEmployee(employee, rawPassword);
        String newPhone = "0000000000";
        String newName = "Francesco";
        Licence newLicence = Licence.C1;
        Role newRole = Role.OPERATOR;

        saved.setPhone(newPhone);
        saved.setName(newName);
        saved.setLicence(Licence.C1);
        saved.setRole(Role.OPERATOR);
        getEmployeeManager().updateEmployee(saved);

        Employee updated = getEmployeeManager().getEmployeeById(saved.getEmployeeId());
        assertEquals(newPhone, updated.getPhone());
        assertEquals(newName, updated.getName());
        assertEquals(newLicence, updated.getLicence());
        assertEquals(newRole, updated.getRole());
    }

    @Test
    void testSoftDeleteEmploye() {
        Employee saved = getEmployeeManager().addEmployee(employee, rawPassword);
        int savedId = saved.getEmployeeId();

        assertNotNull(saved);
        assertFalse(saved.isDeleted());

        boolean result = getEmployeeManager().softDeleteEmployee(saved);
        assertTrue(result);

        Employee deletedEmployee = getEmployeeManager().getEmployeeById(savedId);
        assertNotNull(deletedEmployee);
        assertTrue(deletedEmployee.isDeleted());

        assertEquals(savedId, deletedEmployee.getEmployeeId());

        assertFalse(getEmployeeManager().softDeleteEmployee(null));

        Employee nonExistentEmployee = new Employee("Non", "Existent", location,
                "nonexistent@test.it", "1234567890", Role.ADMINISTRATOR, Licence.NONE);
        assertFalse(getEmployeeManager().softDeleteEmployee(nonExistentEmployee));
    }

    @Test
    void testGetEmployeeInvalidId() {
        final int invalidIdNegative = -5;
        final int invalidIdZero = 0;
        final int invalidIdTooLarge = 99999;

        assertNull(getEmployeeManager().getEmployeeById(invalidIdNegative));
        assertNull(getEmployeeManager().getEmployeeById(invalidIdZero));
        assertNull(getEmployeeManager().getEmployeeById(invalidIdTooLarge));
    }

    @Test
    void testUpdateEmployeeInvalid() {
        assertThrows(IllegalArgumentException.class,
                () -> getEmployeeManager().updateEmployee(null));

        Employee notPersisted = new Employee("Franco", "Neri",
                new Location("Via Roma", "10", "Bologna", "40100"), "test2@test.it",
                "1234567890", Role.ADMINISTRATOR, Licence.NONE);
        assertThrows(IllegalArgumentException.class,
                () -> getEmployeeManager().updateEmployee(notPersisted));
    }

    @Test
    void testUpdateEmployeeNoChange() {
        getEmployeeManager().addEmployee(employee, rawPassword);
        assertDoesNotThrow(() -> getEmployeeManager().updateEmployee(employee));
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

        assertTrue(getEmployeeManager().canDriveVehicle(eB, vB));
        assertFalse(getEmployeeManager().canDriveVehicle(eB, vC1));
        assertFalse(getEmployeeManager().canDriveVehicle(eB, vC));

        assertTrue(getEmployeeManager().canDriveVehicle(eC1, vC1));
        assertTrue(getEmployeeManager().canDriveVehicle(eC1, vB));
        assertFalse(getEmployeeManager().canDriveVehicle(eC1, vC));

        assertTrue(getEmployeeManager().canDriveVehicle(eC, vC));
        assertTrue(getEmployeeManager().canDriveVehicle(eC, vC1));
        assertTrue(getEmployeeManager().canDriveVehicle(eC, vB));

        assertThrows(IllegalArgumentException.class,
                () -> getEmployeeManager().canDriveVehicle(null, vB));
        assertThrows(IllegalArgumentException.class,
                () -> getEmployeeManager().canDriveVehicle(eC, null));
    }
}
