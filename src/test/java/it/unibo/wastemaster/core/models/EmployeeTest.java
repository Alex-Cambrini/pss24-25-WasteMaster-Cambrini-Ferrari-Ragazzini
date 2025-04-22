package it.unibo.wastemaster.core.models;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import it.unibo.wastemaster.core.AbstractDatabaseTest;
import it.unibo.wastemaster.core.models.Employee.LicenceType;
import it.unibo.wastemaster.core.models.Employee.Role;
import it.unibo.wastemaster.core.utils.ValidateUtils;
import jakarta.validation.ConstraintViolation;


public class EmployeeTest extends AbstractDatabaseTest {
    private Employee employee;
    private Location location;

    @BeforeEach
    public void setUp() {
        location = new Location("Via Roma", "10", "Bologna", "40100");
        employee = new Employee("Mario", "Rossi", location, "mario.rossi@example.com", "1234567890", Role.OPERATOR,
                LicenceType.C1);
    }

    @Test
    public void testPersonGettersAndSetters() {
        employee.setName("Luigi");
        employee.setSurname("Verdi");
        Location newLocation = new Location("Via Milano", "20", "Modena", "41100");
        employee.setAddress(newLocation);
        employee.setEmail("luigi.verdi@example.com");
        employee.setPhone("0987654321");

        assertEquals("Luigi", employee.getName());
        assertEquals("Verdi", employee.getSurname());
        assertEquals(newLocation, employee.getAddress());
        assertEquals("luigi.verdi@example.com", employee.getEmail());
        assertEquals("0987654321", employee.getPhone());
    }

    @Test
    public void testSoftDeleteAndRestore() {
        assertEquals(false, employee.isDeleted());

        employee.delete();
        assertEquals(true, employee.isDeleted());

        employee.restore();
        assertEquals(false, employee.isDeleted());
    }

    @Test
    public void testValidEmployee() {
        Set<ConstraintViolation<Employee>> violations = ValidateUtils.VALIDATOR.validate(employee);
        assertTrue(violations.isEmpty());
    }

    @Test
    public void testEmployeeValidation() {
        Set<ConstraintViolation<Employee>> violations;
    
        violations = ValidateUtils.VALIDATOR.validate(new Employee(null, "Rossi", location, "mario.rossi@example.com", "1234567890", Role.OPERATOR, LicenceType.C1));
        assertTrue(violations.size() > 0);
    
        violations = ValidateUtils.VALIDATOR.validate(new Employee("", "Rossi", location, "mario.rossi@example.com", "1234567890", Role.OPERATOR, LicenceType.C1));
        assertTrue(violations.size() > 0);
    
        violations = ValidateUtils.VALIDATOR.validate(new Employee("Mario", null, location, "mario.rossi@example.com", "1234567890", Role.OPERATOR, LicenceType.C1));
        assertTrue(violations.size() > 0);
    
        violations = ValidateUtils.VALIDATOR.validate(new Employee("Mario", "", location, "mario.rossi@example.com", "1234567890", Role.OPERATOR, LicenceType.C1));
        assertTrue(violations.size() > 0);
    
        violations = ValidateUtils.VALIDATOR.validate(new Employee("Mario", "Rossi", null, "mario.rossi@example.com", "1234567890", Role.OPERATOR, LicenceType.C1));
        assertTrue(violations.size() > 0);
    
        violations = ValidateUtils.VALIDATOR.validate(new Employee("Mario", "Rossi", location, null, "1234567890", Role.OPERATOR, LicenceType.C1));
        assertTrue(violations.size() > 0);

        violations = ValidateUtils.VALIDATOR.validate(new Employee("Mario", "Rossi", location, "mario.rossiexample.com", "1234567890", Role.OPERATOR, LicenceType.C1));
        assertTrue(violations.size() > 0);
    
        violations = ValidateUtils.VALIDATOR.validate(new Employee("Mario", "Rossi", location, "", "1234567890", Role.OPERATOR, LicenceType.C1));
        assertTrue(violations.size() > 0);
    
        violations = ValidateUtils.VALIDATOR.validate(new Employee("Mario", "Rossi", location, "mario.rossi@example.com", null, Role.OPERATOR, LicenceType.C1));
        assertTrue(violations.size() > 0);
    
        violations = ValidateUtils.VALIDATOR.validate(new Employee("Mario", "Rossi", location, "mario.rossi@example.com", "", Role.OPERATOR, LicenceType.C1));
        assertTrue(violations.size() > 0);
    
        violations = ValidateUtils.VALIDATOR.validate(new Employee("Mario", "Rossi", location, "mario.rossi@example.com", "1234567890", null, LicenceType.C1));
        assertTrue(violations.size() > 0);
    
        violations = ValidateUtils.VALIDATOR.validate(new Employee("Mario", "Rossi", location, "mario.rossi@example.com", "1234567890", Role.OPERATOR, null));
        assertTrue(violations.size() > 0);
    }
    
    @Test
    public void testEmployeeGettersAndSetters() {
        Employee emptyEmployee = new Employee();
    
        emptyEmployee.setName("Giovanni");
        emptyEmployee.setSurname("Bianchi");
        emptyEmployee.setAddress(location);
        emptyEmployee.setEmail("giovanni.bianchi@example.com");
        emptyEmployee.setPhone("0987654321");
        emptyEmployee.setRole(Role.OPERATOR);
        emptyEmployee.setLicenceType(LicenceType.C1);
    
        assertEquals("Giovanni", emptyEmployee.getName());
        assertEquals("Bianchi", emptyEmployee.getSurname());
        assertEquals(location, emptyEmployee.getAddress());
        assertEquals("giovanni.bianchi@example.com", emptyEmployee.getEmail());
        assertEquals("0987654321", emptyEmployee.getPhone());
        assertEquals(Role.OPERATOR, emptyEmployee.getRole());
        assertEquals(LicenceType.C1, emptyEmployee.getLicenceType());
    
        assertTrue(emptyEmployee.getEmployeeId() >= 0);
        assertEquals(Role.OPERATOR, emptyEmployee.getRole());
        assertEquals(LicenceType.C1, emptyEmployee.getLicenceType());
        assertEquals("3.5 t - 7.5 t", LicenceType.C1.getLicenceDescription());
    
        emptyEmployee.setRole(Role.ADMINISTRATOR);
        emptyEmployee.setLicenceType(LicenceType.C);
    
        assertEquals(Role.ADMINISTRATOR, emptyEmployee.getRole());
        assertEquals(LicenceType.C, emptyEmployee.getLicenceType());
    }
    

    @Test
    public void testGetInfo() {
        String expectedInfoWithLicence = String.format(
                "Name: %s, Surname: %s, Address: %s, Email: %s, Phone: %s, EmployeeId: %d, Role: %s, Licence: %s",
                "Mario",
                "Rossi",
                location,
                "mario.rossi@example.com",
                "1234567890",
                0,
                Employee.Role.OPERATOR,
                Employee.LicenceType.C1);
        assertEquals(expectedInfoWithLicence, employee.getInfo());

        employee.setLicenceType(null);
        String expectedInfoWithoutLicence = String.format(
                "Name: %s, Surname: %s, Address: %s, Email: %s, Phone: %s, EmployeeId: %d, Role: %s, Licence: %s",
                "Mario",
                "Rossi",
                location,
                "mario.rossi@example.com",
                "1234567890",
                0,
                Employee.Role.OPERATOR,
                "N/A");
        assertEquals(expectedInfoWithoutLicence, employee.getInfo());
    }
}
