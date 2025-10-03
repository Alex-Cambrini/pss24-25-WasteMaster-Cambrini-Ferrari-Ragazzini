package it.unibo.wastemaster.domain.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import it.unibo.wastemaster.domain.model.Collection;
import it.unibo.wastemaster.domain.model.Customer;
import it.unibo.wastemaster.domain.model.Employee;
import it.unibo.wastemaster.domain.model.Invoice;
import it.unibo.wastemaster.domain.model.Location;
import it.unibo.wastemaster.domain.model.OneTimeSchedule;
import it.unibo.wastemaster.domain.model.Trip;
import it.unibo.wastemaster.domain.model.Vehicle;
import it.unibo.wastemaster.domain.model.Waste;
import it.unibo.wastemaster.infrastructure.AbstractDatabaseTest;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class NotificationManagerTest extends AbstractDatabaseTest {

    private Location location;
    private Vehicle vehicle;
    private Employee operator;
    private Waste waste;

    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();
        location = new Location("Via Roma", "10", "Bologna", "40100");
        getLocationDAO().insert(location);

        vehicle = new Vehicle("AB123CD", "Iveco", "Daily", 2020,
                Vehicle.RequiredLicence.C1, Vehicle.VehicleStatus.IN_SERVICE, 3);
        getVehicleDAO().insert(vehicle);

        operator = new Employee("John", "Doe", location,
                "john.doe@example.com", "+391234567890",
                Employee.Role.OPERATOR, Employee.Licence.C1);
        getEmployeeDAO().insert(operator);

        waste = new Waste("Organico", true, false);
        getWasteDAO().insert(waste);
    }

    @Test
    void testFindLast5InsertedCustomers() {
        for (int i = 1; i <= 6; i++) {
            Customer c = new Customer("Nome" + i, "Cognome" + i, location,
                    "mail" + i + "@mail.com", "3912345678" + i);
            getCustomerDAO().insert(c);
        }

        List<Customer> last5 = getCustomerRepository().findLast5Inserted();
        assertEquals(5, last5.size());

        List<String> expected =
                List.of("Cognome6", "Cognome5", "Cognome4", "Cognome3", "Cognome2");
        List<String> actual = last5.stream().map(Customer::getSurname).toList();

        assertEquals("Cognome6", actual.get(0));

        assertTrue(actual.containsAll(expected));
    }

    @Test
    void testFindLast5InsertedTrips() {
        for (int i = 1; i <= 6; i++) {
            Customer customer = new Customer("Nome" + i, "Cognome" + i, location,
                    "mail" + i + "@mail.com", "3912345678" + i);
            getCustomerDAO().insert(customer);

            OneTimeSchedule schedule =
                    new OneTimeSchedule(customer, waste, LocalDate.now().plusDays(i));
            getOneTimeScheduleDAO().insert(schedule);

            Collection collection = new Collection(schedule);
            getCollectionDAO().insert(collection);

            LocalDateTime departureTime = LocalDateTime.now().plusHours(i);
            LocalDateTime expectedReturnTime = departureTime.plusHours(2);

            getTripManager().createTrip(
                    "40100",
                    vehicle,
                    new ArrayList<>(List.of(operator)),
                    departureTime,
                    expectedReturnTime,
                    new ArrayList<>(List.of(collection))
            );
        }

        List<Trip> last5 = getTripRepository().findLast5Modified();
        assertEquals(5, last5.size());
        assertNotNull(last5.get(0).getTripId());
    }

    @Test
    void testFindLast5InvoicesEvent() {
        Customer customer =
                new Customer("Mario", "Rossi", location, "mario.rossi@example.com",
                        "39123456789");
        getCustomerDAO().insert(customer);

        for (int i = 1; i <= 6; i++) {
            Waste waste = new Waste("Tipo" + i, true, false);
            getWasteDAO().insert(waste);

            OneTimeSchedule schedule =
                    new OneTimeSchedule(customer, waste, LocalDate.now().plusDays(i));
            getOneTimeScheduleDAO().insert(schedule);

            Collection collection = new Collection(schedule);
            collection.setCollectionStatus(Collection.CollectionStatus.COMPLETED);
            collection.setCollectionDate(LocalDate.now().plusDays(i));
            getCollectionDAO().insert(collection);

            Invoice invoice = new Invoice();
            invoice.setCustomer(customer);
            invoice.setCollections(List.of(collection));
            invoice.setAmount(100.0 + i);
            invoice.setPaymentStatus(Invoice.PaymentStatus.PAID);
            invoice.setIssueDate(LocalDate.now().plusDays(i).atStartOfDay());
            invoice.setPaymentDate(LocalDateTime.now().plusDays(i));
            invoice.setLastModified(LocalDateTime.now().plusDays(i));
            getInvoiceDAO().insert(invoice);
        }

        List<Invoice> last5 = getInvoiceRepository().findLast5InvoicesEvent();
        assertEquals(5, last5.size());
        assertNotNull(last5.get(0).getInvoiceId());
    }
}
