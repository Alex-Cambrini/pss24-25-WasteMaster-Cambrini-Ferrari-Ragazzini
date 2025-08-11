package it.unibo.wastemaster.core.models;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import it.unibo.wastemaster.core.AbstractDatabaseTest;
import it.unibo.wastemaster.core.utils.ValidateUtils;
import it.unibo.wastemaster.domain.model.Account;
import it.unibo.wastemaster.domain.model.Employee;
import it.unibo.wastemaster.domain.model.Location;
import jakarta.validation.ConstraintViolation;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AccountTest extends AbstractDatabaseTest {

    private Employee employee;
    private Account account;

    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();
        Location address = new Location("Via Roma", "12", "Bologna", "40100");
        employee = new Employee("Luca", "Bianchi", address, "luca.bianchi@email.com",
                "0511234567", Employee.Role.OFFICE_WORKER, Employee.Licence.B);
        account = new Account("hashed_password_123", employee);
    }

    @Test
    void testAccountGettersAndSetters() {
        Employee newEmployee = new Employee("Maria", "Rossi",
                new Location("Via Verdi", "3", "Milano", "20100"),
                "maria.rossi@email.com", "027654321", Employee.Role.ADMINISTRATOR,
                Employee.Licence.C);

        Account a = new Account();
        a.setPasswordHash("new_hashed");
        a.setEmployee(newEmployee);

        assertEquals("new_hashed", a.getPasswordHash());
        assertEquals(newEmployee, a.getEmployee());
    }

    @Test
    void testPersistence() {
        getEmployeeDAO().insert(employee);
        getAccountDAO().insert(account);

        Optional<Account> retrievedOpt = getAccountDAO().findById(account.getId());
        assertTrue(retrievedOpt.isPresent());

        Account retrieved = retrievedOpt.get();
        assertEquals("hashed_password_123", retrieved.getPasswordHash());
        assertEquals(employee.getEmployeeId(), retrieved.getEmployee().getEmployeeId());

        int id = retrieved.getId();
        getAccountDAO().delete(retrieved);

        Optional<Account> deletedOpt = getAccountDAO().findById(id);
        assertTrue(deletedOpt.isEmpty());
    }

    @Test
    void testAccountValidation() {
        Account invalid = new Account("", null);

        Set<ConstraintViolation<Account>> violations =
                ValidateUtils.VALIDATOR.validate(invalid);

        assertTrue(
                violations.stream().anyMatch(
                        v -> v.getPropertyPath().toString().equals("passwordHash")),
                "Expected violation on passwordHash");

        assertTrue(
                violations.stream()
                        .anyMatch(v -> v.getPropertyPath().toString().equals("employee")),
                "Expected violation on employee");
    }
}
