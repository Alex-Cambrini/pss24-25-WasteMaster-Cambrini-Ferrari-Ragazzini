package it.unibo.wastemaster.core.models;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import it.unibo.wastemaster.core.AbstractDatabaseTest;
import it.unibo.wastemaster.core.utils.ValidateUtils;
import it.unibo.wastemaster.domain.model.Employee;
import it.unibo.wastemaster.domain.model.Employee.Licence;
import it.unibo.wastemaster.domain.model.Employee.Role;
import it.unibo.wastemaster.domain.model.Location;
import jakarta.validation.ConstraintViolation;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class EmployeeTest extends AbstractDatabaseTest {

    private Employee employee;
    private Location location;

    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();
        location = new Location("Via Roma", "10", "Bologna", "40100");
        employee = new Employee("Mario", "Rossi", location, "mario.rossi@example.com",
                "1234567890", Role.OPERATOR, Licence.C1);
    }

    @Test
    void testPersonGettersAndSetters() {
        employee.setName("Luigi");
        employee.setSurname("Verdi");
        Location newLocation = new Location("Via Milano", "20", "Modena", "41100");
        employee.setLocation(newLocation);
        employee.setEmail("luigi.verdi@example.com");
        employee.setPhone("0987654321");

        assertEquals("Luigi", employee.getName());
        assertEquals("Verdi", employee.getSurname());
        assertEquals(newLocation, employee.getLocation());
        assertEquals("luigi.verdi@example.com", employee.getEmail());
        assertEquals("0987654321", employee.getPhone());
    }

    @Test
    void testSoftDeleteAndRestore() {
        assertFalse(employee.isDeleted());

        employee.delete();
        assertTrue(employee.isDeleted());

        employee.restore();
        assertFalse(employee.isDeleted());
    }

    @Test
    void testValidEmployee() {
        Set<ConstraintViolation<Employee>> violations =
                ValidateUtils.VALIDATOR.validate(employee);
        assertTrue(violations.isEmpty());
    }

    @Test
    void testEmployeeValidation() {
        Set<ConstraintViolation<Employee>> violations;

        Employee nullNameEmployee = new Employee(null, "Rossi", location,
                "mario.rossi@example.com", "1234567890", Role.OPERATOR, Licence.C1);
        violations = ValidateUtils.VALIDATOR.validate(nullNameEmployee);
        assertFalse(violations.isEmpty());

        Employee emptyNameEmployee = new Employee("", "Rossi", location,
                "mario.rossi@example.com", "1234567890", Role.OPERATOR, Licence.C1);
        violations = ValidateUtils.VALIDATOR.validate(emptyNameEmployee);
        assertFalse(violations.isEmpty());

        Employee nullSurnameEmployee = new Employee("Mario", null, location,
                "mario.rossi@example.com", "1234567890", Role.OPERATOR, Licence.C1);
        violations = ValidateUtils.VALIDATOR.validate(nullSurnameEmployee);
        assertFalse(violations.isEmpty());

        Employee emptySurnameEmployee = new Employee("Mario", "", location,
                "mario.rossi@example.com", "1234567890", Role.OPERATOR, Licence.C1);
        violations = ValidateUtils.VALIDATOR.validate(emptySurnameEmployee);
        assertFalse(violations.isEmpty());

        Employee nullLocationEmployee = new Employee("Mario", "Rossi", null,
                "mario.rossi@example.com", "1234567890", Role.OPERATOR, Licence.C1);
        violations = ValidateUtils.VALIDATOR.validate(nullLocationEmployee);
        assertFalse(violations.isEmpty());

        Employee nullEmailEmployee = new Employee("Mario", "Rossi", location, null,
                "1234567890", Role.OPERATOR, Licence.C1);
        violations = ValidateUtils.VALIDATOR.validate(nullEmailEmployee);
        assertFalse(violations.isEmpty());

        Employee invalidEmailEmployee = new Employee("Mario", "Rossi", location,
                "mario.rossiexample.com", "1234567890", Role.OPERATOR, Licence.C1);
        violations = ValidateUtils.VALIDATOR.validate(invalidEmailEmployee);
        assertFalse(violations.isEmpty());

        Employee emptyEmailEmployee = new Employee("Mario", "Rossi", location, "",
                "1234567890", Role.OPERATOR, Licence.C1);
        violations = ValidateUtils.VALIDATOR.validate(emptyEmailEmployee);
        assertFalse(violations.isEmpty());

        Employee nullPhoneEmployee = new Employee("Mario", "Rossi", location,
                "mario.rossi@example.com", null, Role.OPERATOR, Licence.C1);
        violations = ValidateUtils.VALIDATOR.validate(nullPhoneEmployee);
        assertFalse(violations.isEmpty());

        Employee emptyPhoneEmployee = new Employee("Mario", "Rossi", location,
                "mario.rossi@example.com", "", Role.OPERATOR, Licence.C1);
        violations = ValidateUtils.VALIDATOR.validate(emptyPhoneEmployee);
        assertFalse(violations.isEmpty());

        Employee nullRoleEmployee = new Employee("Mario", "Rossi", location,
                "mario.rossi@example.com", "1234567890", null, Licence.C1);
        violations = ValidateUtils.VALIDATOR.validate(nullRoleEmployee);
        assertFalse(violations.isEmpty());

        Employee nullLicenceEmployee = new Employee("Mario", "Rossi", location,
                "mario.rossi@example.com", "1234567890", Role.OPERATOR, null);
        violations = ValidateUtils.VALIDATOR.validate(nullLicenceEmployee);
        assertFalse(violations.isEmpty());
    }

    @Test
    void testEmployeeGettersAndSetters() {
        Employee emptyEmployee = new Employee();

        emptyEmployee.setName("Giovanni");
        emptyEmployee.setSurname("Bianchi");
        emptyEmployee.setLocation(location);
        emptyEmployee.setEmail("giovanni.bianchi@example.com");
        emptyEmployee.setPhone("0987654321");
        emptyEmployee.setRole(Role.OPERATOR);
        emptyEmployee.setLicence(Licence.C1);

        assertEquals("Giovanni", emptyEmployee.getName());
        assertEquals("Bianchi", emptyEmployee.getSurname());
        assertEquals(location, emptyEmployee.getLocation());
        assertEquals("giovanni.bianchi@example.com", emptyEmployee.getEmail());
        assertEquals("0987654321", emptyEmployee.getPhone());
        assertEquals(Role.OPERATOR, emptyEmployee.getRole());
        assertEquals(Licence.C1, emptyEmployee.getLicence());

        assertEquals("3.5 t - 7.5 t", Licence.C1.getLicenceDescription());

        emptyEmployee.setRole(Role.ADMINISTRATOR);
        emptyEmployee.setLicence(Licence.C);

        assertEquals(Role.ADMINISTRATOR, emptyEmployee.getRole());
        assertEquals(Licence.C, emptyEmployee.getLicence());
    }

    @Test
    void testEmployeePersistence() {
        getEmployeeDAO().insert(employee);
        int employeeId = employee.getEmployeeId();

        Optional<Employee> foundOpt = getEmployeeDAO().findById(employeeId);
        assertTrue(foundOpt.isPresent());

        Employee found = foundOpt.get();
        assertEquals(employee.getName(), found.getName());
        assertEquals(employee.getEmail(), found.getEmail());

        getEmployeeDAO().delete(employee);

        Optional<Employee> deletedOpt = getEmployeeDAO().findById(employeeId);
        assertTrue(deletedOpt.isEmpty());
    }

    @Test
    void testToString() {
        String output = employee.toString();
        assertNotNull(output);
        assertTrue(output.contains("Employee"));
        assertTrue(output.contains(employee.getName()));
        assertTrue(output.contains(employee.getSurname()));
        assertTrue(output.contains(employee.getEmail()));
        assertTrue(output.contains(employee.getPhone()));
        assertTrue(output.contains(employee.getRole().name()));
        assertTrue(output.contains(employee.getLicence().name()));
        assertTrue(output.contains(location.toString()));
    }
}
