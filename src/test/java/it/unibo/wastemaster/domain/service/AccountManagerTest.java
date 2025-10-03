package it.unibo.wastemaster.domain.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import it.unibo.wastemaster.domain.model.Account;
import it.unibo.wastemaster.domain.model.Employee;
import it.unibo.wastemaster.domain.model.Location;
import it.unibo.wastemaster.infrastructure.AbstractDatabaseTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AccountManagerTest extends AbstractDatabaseTest {

    private Employee employee;

    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();
        Location location = new Location("Via Roma", "12", "Bologna", "40100");
        employee = new Employee(
                "Luca",
                "Bianchi",
                location,
                "luca.bianchi@email.com",
                "0511234567",
                Employee.Role.OFFICE_WORKER,
                Employee.Licence.B
        );

        getLocationDAO().insert(location);
        getEmployeeDAO().insert(employee);
    }

    @Test
    void testCreateAccountSuccess() {
        String validPassword = "Password123";
        Account account = getAccountManager().createAccount(employee, validPassword);

        assertNotNull(account.getId());
        assertEquals(employee.getEmployeeId(), account.getEmployee().getEmployeeId());
        assertNotNull(account.getPasswordHash());
        assertNotEquals(validPassword, account.getPasswordHash());
    }

    @Test
    void testCreateAccountFailsWithBlankPassword() {
        String blankPassword = "   ";

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                getAccountManager().createAccount(employee, blankPassword)
        );

        assertEquals("Password cannot be blank.", ex.getMessage());
    }

    @Test
    void testCreateAccountFailsWithWeakPassword() {
        String weakPassword = "abc123";

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                getAccountManager().createAccount(employee, weakPassword)
        );

        assertTrue(ex.getMessage().contains("Password must be at least 8 characters"));
    }

    @Test
    void testCreateAccountFailsWithNullPassword() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                getAccountManager().createAccount(employee, null)
        );

        assertEquals("Password cannot be blank.", ex.getMessage());
    }
}
