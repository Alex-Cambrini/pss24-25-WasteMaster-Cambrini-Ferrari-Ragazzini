package it.unibo.wastemaster.core.dao;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import it.unibo.wastemaster.core.AbstractDatabaseTest;
import it.unibo.wastemaster.core.models.Employee;
import it.unibo.wastemaster.core.models.Location;
import it.unibo.wastemaster.core.models.Employee.Licence;
import it.unibo.wastemaster.core.models.Employee.Role;

public class EmployeeDAOTest extends AbstractDatabaseTest {

    private Location location;

    @BeforeEach
    public void setUp() {
        super.setUp();
        location = new Location("Via Test", "1", "Roma", "00100");
    }

    @Test
    public void testExistsByEmail() {
        String existingEmail = "test@example.com";
        String nonExistingEmail = "nonexistent@example.com";

        Employee employee = new Employee("Giulia", "Neri", location, existingEmail, "1234567890",
                Role.ADMINISTRATOR, Licence.NONE);

        employeeDAO.insert(employee);
        assertTrue(employeeDAO.existsByEmail(existingEmail), "Should return true for existing email");
        assertFalse(employeeDAO.existsByEmail(nonExistingEmail), "Should return false for non-existing email");
    }

    @Test
    public void testFindByEmail() {
        String existingEmail = "test@example.com";
        String nonExistingEmail = "nonexistent@example.com";

        Employee employee = new Employee("Giulia", "Neri", location, existingEmail, "1234567890",
                Role.ADMINISTRATOR, Licence.NONE);
        employeeDAO.insert(employee);

        Employee foundEmployee = employeeDAO.findByEmail(existingEmail);
        assertNotNull(foundEmployee, "Employee should be found for existing email");
        assertEquals(existingEmail, foundEmployee.getEmail(), "Email should match");

        Employee notFoundEmployee = employeeDAO.findByEmail(nonExistingEmail);
        assertNull(notFoundEmployee, "Employee should not be found for non-existing email");
    }

    @Test
    public void testFindEmployeeDetails() {
        Employee e1 = new Employee("Marco", "Verdi", location, "marco@example.com", "1111111111", Role.OPERATOR,
                Licence.C);
        Employee e2 = new Employee("Luca", "Bianchi", location, "luca@example.com", "2222222222", Role.OPERATOR,
                Licence.C1);
        Employee e3 = new Employee("Laura", "Rossi", location, "laura@example.com", "3333333333", Role.ADMINISTRATOR,
                Licence.NONE);

        employeeDAO.insert(e1);
        employeeDAO.insert(e2);
        employeeDAO.insert(e3);
        e3.delete();
        employeeDAO.update(e3);

        var result = employeeDAO.findEmployeeDetails();

        assertTrue(result.stream().anyMatch(e -> e.getEmail().equals("marco@example.com")), "Marco should be present");
        assertTrue(result.stream().anyMatch(e -> e.getEmail().equals("luca@example.com")), "Luca should be present");
        assertFalse(result.stream().anyMatch(e -> e.getEmail().equals("laura@example.com")),
                "Laura (deleted) should not be present");
    }
}