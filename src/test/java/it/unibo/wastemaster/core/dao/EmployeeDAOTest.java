package it.unibo.wastemaster.core.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import it.unibo.wastemaster.core.AbstractDatabaseTest;
import it.unibo.wastemaster.core.models.Employee;
import it.unibo.wastemaster.core.models.Employee.Licence;
import it.unibo.wastemaster.core.models.Employee.Role;
import it.unibo.wastemaster.core.models.Location;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for EmployeeDAO.
 */
class EmployeeDAOTest extends AbstractDatabaseTest {

    private Location location;

    /**
     * Initializes shared test data.
     */
    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();
        location = new Location("Via Test", "1", "Roma", "00100");
    }

    /**
     * Verifies that the DAO detects existing and non-existing emails.
     */
    @Test
    void testExistsByEmail() {
        String existingEmail = "test@example.com";
        String nonExistingEmail = "nonexistent@example.com";

        Employee employee = new Employee("Giulia", "Neri", location,
                existingEmail, "1234567890", Role.ADMINISTRATOR,
                Licence.NONE);

        getEmployeeDAO().insert(employee);

        assertTrue(getEmployeeDAO().existsByEmail(existingEmail),
                "Should return true for existing email");
        assertFalse(getEmployeeDAO().existsByEmail(nonExistingEmail),
                "Should return false for non-existing email");
    }

    /**
     * Verifies correct retrieval by email.
     */
    @Test
    void testFindByEmail() {
        String existingEmail = "test@example.com";
        String nonExistingEmail = "nonexistent@example.com";

        Employee employee = new Employee("Giulia", "Neri", location,
                existingEmail, "1234567890", Role.ADMINISTRATOR,
                Licence.NONE);
        getEmployeeDAO().insert(employee);

        Employee foundEmployee = getEmployeeDAO().findByEmail(existingEmail);
        assertNotNull(foundEmployee,
                "Employee should be found for existing email");
        assertEquals(existingEmail, foundEmployee.getEmail(),
                "Email should match");

        Employee notFoundEmployee =
                getEmployeeDAO().findByEmail(nonExistingEmail);
        assertNull(notFoundEmployee,
                "Employee should not be found for non-existing email");
    }

    /**
     * Verifies that deleted employees are not returned by findEmployeeDetails.
     */
    @Test
    void testFindEmployeeDetails() {
        Employee e1 = new Employee("Marco", "Verdi", location,
                "marco@example.com", "1111111111", Role.OPERATOR,
                Licence.C);
        Employee e2 = new Employee("Luca", "Bianchi", location,
                "luca@example.com", "2222222222", Role.OPERATOR,
                Licence.C1);
        Employee e3 = new Employee("Laura", "Rossi", location,
                "laura@example.com", "3333333333", Role.ADMINISTRATOR,
                Licence.NONE);

        getEmployeeDAO().insert(e1);
        getEmployeeDAO().insert(e2);
        getEmployeeDAO().insert(e3);
        e3.delete();
        getEmployeeDAO().update(e3);

        List<Employee> result = getEmployeeDAO().findEmployeeDetails();

        assertTrue(result.stream()
                        .anyMatch(e -> e.getEmail().equals("marco@example.com")),
                "Marco should be present");
        assertTrue(result.stream()
                        .anyMatch(e -> e.getEmail().equals("luca@example.com")),
                "Luca should be present");
        assertFalse(result.stream()
                        .anyMatch(e -> e.getEmail().equals("laura@example.com")),
                "Laura (deleted) should not be present");
    }
}
