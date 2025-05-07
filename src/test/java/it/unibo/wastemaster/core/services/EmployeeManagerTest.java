package it.unibo.wastemaster.core.services;

import it.unibo.wastemaster.core.AbstractDatabaseTest;
import it.unibo.wastemaster.core.models.Employee;
import it.unibo.wastemaster.core.models.Location;
import it.unibo.wastemaster.core.models.Employee.LicenceType;
import it.unibo.wastemaster.core.models.Employee.Role;
import jakarta.validation.ConstraintViolationException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EmployeeManagerTest extends AbstractDatabaseTest {
    private Location location;
    private Employee employee;
    private String email;

    @BeforeEach
    public void setUp() {
        super.setUp();
        em.getTransaction().begin();
        email = "test@test.it";
        location = new Location("Via Roma", "10", "Bologna", "40100");
        employee = new Employee("Mario", "Rossi", location, email, "1234567890", Role.ADMINISTRATOR, LicenceType.NONE);

    }

    @Test
    void testAddEmployee() {
        Employee saved = employeeManager.addEmployee(employee);
        assertNotNull(saved.getEmployeeId());

        // Add employee with duplicate email
        Employee duplicate = new Employee("Luca", "Bianchi", location, email, "1234567890", Role.ADMINISTRATOR,
                LicenceType.NONE);
        assertThrows(
                IllegalArgumentException.class,
                () -> employeeManager.addEmployee(duplicate),
                "A employee with this email already exists");
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
        assertEquals(saved.getLocation().getCivicNumber(), found.getLocation().getCivicNumber());
        assertEquals(saved.getLocation().getCity(), found.getLocation().getCity());
        assertEquals(saved.getLocation().getPostalCode(), found.getLocation().getPostalCode());
        assertEquals(saved.getRole(), found.getRole());
        assertEquals(saved.getLicenceType(), found.getLicenceType());

        // not found
        Employee notFound = employeeManager.getEmployeeById(-1);
        assertNull(notFound);
    }

    @Test
    void testUpdateEmployee() {
        Employee saved = employeeManager.addEmployee(employee);
        String newPhone = "0000000000";
        String newName = "Francesco";
        LicenceType newLicenceType = LicenceType.C1;
        Role newRole = Role.OPERATOR;

        saved.setPhone(newPhone);
        saved.setName(newName);
        saved.setLicenceType(LicenceType.C1);
        saved.setRole(Role.OPERATOR);
        employeeManager.updateEmployee(saved);
        Employee updated = employeeManager.getEmployeeById(saved.getEmployeeId());
        assertEquals(newPhone, updated.getPhone());
        assertEquals(newName, updated.getName());
        assertEquals(newLicenceType, updated.getLicenceType());
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

        Employee nonExistentEmployee = new Employee("Non", "Existent", location, "nonexistent@test.it",
                "1234567890", Role.ADMINISTRATOR, LicenceType.NONE);
        assertFalse(employeeManager.softDeleteEmployee(nonExistentEmployee));
    }

    @Test
    void testAddEmployeInvalid() {
        // Null role
        assertThrows(ConstraintViolationException.class,
                () -> employeeManager.addEmployee(new Employee("Mario", "Rossi",
                        new Location("Via Roma", "10", "Bologna", "40100"),
                        email, "1234567890", null, LicenceType.NONE)));

        // Null licence type
        assertThrows(ConstraintViolationException.class,
                () -> employeeManager.addEmployee(new Employee("Mario", "Rossi",
                        new Location("Via Roma", "10", "Bologna", "40100"),
                        email, "1234567890", Role.ADMINISTRATOR, null)));

    }

    @Test
    void testGetEmployeeInvalidId() {
        assertNull(employeeManager.getEmployeeById(-5));
        assertNull(employeeManager.getEmployeeById(0));
        assertNull(employeeManager.getEmployeeById(99999));
    }

    @Test
    void testUpdateEmployeeInvalid() {
               // Test: Employee null
                assertThrows(IllegalArgumentException.class, () -> employeeManager.updateEmployee(null));

                // Test: Employee not persisted (not saved in DB)
                Employee notPersisted = new Employee("Franco", "Neri",
                                new Location("Via Roma", "10", "Bologna", "40100"), "test2@test.it", "1234567890", Role.ADMINISTRATOR, LicenceType.NONE);
                assertThrows(IllegalArgumentException.class, () -> employeeManager.updateEmployee(notPersisted));
        }

    @Test
    void testUpdateEmployeeNoChange() {
        employeeManager.addEmployee(employee);
        assertDoesNotThrow(() -> employeeManager.updateEmployee(employee));
    }
}
